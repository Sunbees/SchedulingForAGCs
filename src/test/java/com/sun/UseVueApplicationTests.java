package com.sun;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class UseVueApplicationTests {


    @Test
    public void test() {
        System.out.println(addStrings("1b", "2x"));
        System.out.println(addStrings("1z", "1"));
    }

    public String addStrings(String num1, String num2) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;
        int i = num1.length() - 1, j = num2.length() - 1;
        while (i >= 0 || j >= 0 || carry > 0) {
            int x = i >= 0 ? getInt(num1.charAt(i--)) : 0;
            int y = j >= 0 ? getInt(num2.charAt(j--)) : 0;
            int ans = x + y + carry;
            carry = ans / 36;
            sb.append(getChar(ans % 36));
        }

        return sb.reverse().toString();
    }

    private char getChar(int num) {
        if (num < 10) {
            return (char) ('0' + num);
        }
        return (char) ('a' + num - 10);
    }

    private int getInt(char c) {
        if (Character.isDigit(c)) {
            return c - '0';
        }
        return 10 + (c - 'a');
    }
}
