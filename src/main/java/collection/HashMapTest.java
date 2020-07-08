package collection;

import org.junit.Test;

import java.util.HashMap;

/**
 * @Author: LiQiongchao
 * @Date: 2020/7/7 22:52
 */
public class HashMapTest {

    @Test
    public void createHashMap () {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("qq", "11");
    }

    @Test
    public void moveBitTest () {
        int n = 10;
        n |= n >>> 1;
        n |= n >>> 2;
        System.out.println(n);
        System.out.println(2 >>> 1);
        System.out.println(2>>>2);
        System.out.println(2>>>4);
        // 1
        //0
        //0
    }

}
