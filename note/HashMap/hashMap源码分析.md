# HashMap 源码分析（详细）

本文分析基于 `JDK1.8` 

HashMap是使用数组 + 链表 + 红黑树 的结构来存储的。

## `hashCode` 方法

HashMap中处处用到了hashCode，先说一下hashCode方法。

HashCode#hash(Object key)

```java
    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     */
    static final int hash(Object key) {
        int h;
        // 用hashCode的高位与低位进行异或运算，让高位参与运算，防止hashCode过小时冲突问题
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

知识点补充：

* `^` 运算是相应位数的什不一样时取1，否则取0
* 正数在计算机底层是以原码形式存储，而负数是以补码形式存储
* `Integer#hashCode()` 值是本身；`Long#hashCode()` 与 `HashMap` 的相似 `(int)(value ^ (value >>> 32))` ；`String#hashCode()` 是 `h = 31 * h + val[i];` 把原本的 hash 乘以31后再加上每个字符串起来。



## `HashMap` 的构造器

### 无参构造器

```java
    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
   /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
```

指定了默认的加载因子，`Node` 的初始化在第一次放元素的时候。

### 有参构造器

有参构造器最后调用的都是一个。

```java
    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
```

注意的是 `tableSizeFor(initialCapacity)` 的方法，取比自己最大且最近的2的幂次方的数。

```java
    /**
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        // 先减1，防止该数刚好是2的幂次方
        int n = cap - 1;
        // 利用最高位的1把低全变成1，这样变变成全部的1了。比如：9
        // 1001 | 100 = 1101
        n |= n >>> 1;
        // 1101 | 10 = 1111，上次或后最差的情况会把前两位变成1，这一步操作后就能把前4位变成1
        n |= n >>> 2;
        // 1111 | 1 = 1111
        n |= n >>> 4;
        // 1111 | 0 = 1111
        n |= n >>> 8;
        // 1111 | 0 = 1111
        n |= n >>> 16;
        // 因为是以前面或后的结果再移动，所以移动16次刚好把int的数给处理完。
        // 再加1返回，刚好是2的幂次方
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```

举例说明：

```java
n = 0100 0000 0000 0000

0100 0000 0000 0000
 010 0000 0000 0000		n |= n >>> 1
0110 0000 0000 0000
  01 1000 0000 0000		n |= n >>> 2
0111 1000 0000 0000
     0111 1000 0000		n |= n >>> 4
```



## put方法

### `putVal()` 方法

```java
    /**
     * Implements Map.put and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
	// onlyIfAbsent key 存在时是否覆盖 value，true为不覆盖
	// evict HashMap中什么也没有做，在子类LinkHashMap中用来删除头节点。
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 第一次put时，会初始化数组
        if ((tab = table) == null || (n = tab.length) == 0)
            // 初始化 & 扩容 后面分析
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            // 当该位置为null时直接放入该位置
            tab[i] = newNode(hash, key, value, null);
        else {
            // 当该数组的位置不为null时，然后判断是链表还是红黑树
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                // 如果插入元素的key与position上key相等，就把赋值给临时变量e
                e = p;
            else if (p instanceof TreeNode)
                // 如果是红黑树节点，就放入树中
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // key不等于链表的头节点，也不是红黑树时，遍历链表，放到链表的最后面
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        // 放置链表最后面
                        p.next = newNode(hash, key, value, null);
                        // >= 7时，即链表上有8个元素的时候进行转换成红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        // 等于链表上存在的某一个节点时，退出，走下面的是否覆盖的操作
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                // 修改值并返回旧值
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            // 扩容
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```



### resize() 方法



















