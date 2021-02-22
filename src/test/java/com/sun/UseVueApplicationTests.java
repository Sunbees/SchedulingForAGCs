package com.sun;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class UseVueApplicationTests {

    @Test
    void contextLoads() {
        int[] a = new int[]{1, 2, 3};
        System.out.println(Arrays.toString(a).replace(',', '-'));
    }


}
