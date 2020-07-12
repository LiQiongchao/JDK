package collection;

import javafx.scene.chart.Chart;
import org.junit.Test;

import java.util.HashMap;

/**
 * @Author: LiQiongchao
 * @Date: 2020/7/7 22:52
 */
public class HashMapTest {

    @Test
    public void capacityCalTest() {
        int capacity = 16;
        System.out.println(capacity >> 2);
    }

    @Test
    public void hashCodeTest() {
        // 正整数是以原码形式存储，负数是以补码方式存储
        String aa = "abc";
        int hash = aa.hashCode();
        System.out.println(hash);
        System.out.println(Integer.toBinaryString(hash));
        System.out.println(Integer.toBinaryString(hash >>> 16));
        System.out.println(hashCode(hash));
    }

    private int hashCode(int hash) {
        int h = hash;
        return h ^ (h >>> 16);
    }

    @Test
    public void createHashMap () {
//        HashMap<Object, Object> map = new HashMap<>();
        HashMap map = new HashMap<String, String>(10, 0.75F);
        map.put("qq", "11");
    }

    @Test
    public void moveBitTest () {
        // 1010
        int n = 10;
        // 1010 | 101
        n |= n >>> 1;
        System.out.println(n); // 15
        // 1111 | 1111
        n |= n >>> 2;
        System.out.println(n); // 15
        System.out.println(2 >>> 1);
        System.out.println(2>>>2);
        System.out.println(2>>>4);
        // 1
        //0
        //0
    }

}
