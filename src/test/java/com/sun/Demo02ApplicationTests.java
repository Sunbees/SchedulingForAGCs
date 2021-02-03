package com.sun;

import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.data.Data;
import com.sun.pojo.Order;
import com.sun.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

@SpringBootTest
class Demo02ApplicationTests {

    @Test
    public void test() {
        strToInt("2147483648");

    }


    public int strToInt(String str) {
        int n = str.length();
        if(n == 0) {
            return 0;
        }
        char[] charArray = str.toCharArray();
        int i = 0;
        while(i<n && charArray[i] == ' ') {
            i++;
        }
        if(i == n) {
            return 0;
        }
        boolean isMinus = false;
        if(charArray[i] == '+' || charArray[i] == '-') {
            if(charArray[i] == '-') {
                isMinus = true;
            }
            i++;
        }
        int res = 0;
        while(i<n && charArray[i]>='0' && charArray[i]<='9') {
            int temp = charArray[i]-'0';
            if(!isMinus&&(res>Integer.MAX_VALUE/10 || (res==Integer.MAX_VALUE%10 && temp>Integer.MAX_VALUE%10))) {
                return Integer.MAX_VALUE;
            }
            else if(isMinus&&(-res<Integer.MIN_VALUE/10 || (-res==Integer.MIN_VALUE%10 && -temp<Integer.MIN_VALUE%10))) {
                return Integer.MIN_VALUE;
            }
            res = res * 10 + temp;
            ++i;
        }
        return isMinus? -res: res;
    }
}


