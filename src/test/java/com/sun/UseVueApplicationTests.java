package com.sun;

import com.sun.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

@SpringBootTest
class UseVueApplicationTests {
    @Autowired
    Util util;

    @Test
    public void test() throws IOException {
        //Util.writeCsvForOrder();
        //System.out.println(addStrings("1b", "2x"));
        //System.out.println(addStrings("1z", "1"));
        Map<String, Object> stringObjectMap = util.readOrderInfo();
        System.out.println("2");
        LIS(new int[]{2,1,5,3,6,4,8,9,7});
        //System.out.println(reverse("safwq123"));
    }

    public int[] LIS (int[] arr) {
        // write code here
        int n = arr.length;
        int[] dp = new int [n];
        int len = 0;
        dp[len] = arr[0];
        int[] maxIndex = new int[n];
        maxIndex[len] = 1;
        for(int i=1; i<n; i++) {
            if(arr[i] > dp[len]) {
                dp[++len] = arr[i];
                maxIndex[i] = len + 1;
            } else {
                int left = 0, right = len + 1;
                while(left < right) {
                    int mid = left + (right - left) / 2;
                    if(dp[mid] > arr[i]) {
                        right = mid;
                    } else if(dp[mid] < arr[i]) {
                        left = mid + 1;
                    } else {
                        left = mid + 1;
                    }
                }
                int temp = arr[i];
                arr[i] = dp[left];
                dp[left] = temp;
                maxIndex[i] = left + 1;
            }

        }
        int[] res = new int[len+1];
        for(int i=n-1; i>=0&&len>=0; i--) {
            if(maxIndex[i] == len+1) {
                res[len] = arr[i];
                --len;
            }
        }

        return res;
    }
}
