package com.sun;

import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Task;
import com.sun.data.Data;
import com.sun.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

@SpringBootTest
class Demo02ApplicationTests {

    @Test
    void contextLoads() throws IOException {
        //Util.writeCsvForPath("1_1","2020110200001","9570","27011","5710");
        //Util.writeCsvForPath("1_1","2020110200002","9571","27012","5711");
        //Util.writeCsvForPath("1_1","2020110200003","9571","27012","5711");
        //Util.writeCsvForPath("1_1","2020110200004","9571","27012","5711");

        //Util.createRandomTask(1, 25);
        //Util.createRandomTask(2, 13);
        Data.taskMap=new HashMap<>();
        Util.createRandomTask(0, 10,"0,1","2,3");
        Data.taskMap.values().forEach(System.out::println);
        //Data.stocks.forEach(System.out::println);
    }


    long[] sum;
    public int countRangeSum(int[] nums, int lower, int upper) {
        int n = nums.length;
        if(n==0) {
            return 0;
        }
        sum = new long[n];
        sum[0] = (long)nums[0];
        for(int i=1;i<n;i++) {
            sum[i] = sum[i-1] + (long)nums[i];
        }
        int res = 0;
        for(int i=0;i<n;i++) {
            for(int j=i;j<n;j++) {
                if(lower<= between(i,j) && between(i,j)<=upper) {
                    System.out.println(i+" "+j);
                    ++res;
                }
            }
        }
        return res;

    }
    public long between(int i, int j) {
        if(i==0) {
            return sum[j];
        }
        return (sum[j] - sum[i-1]);
    }
    @Test
    public void test(){
        int[] nums = new int[]  {-2147483647,0,-2147483647,2147483647};

        countRangeSum(nums,-564,3864);
        for (long l : sum) {
            System.out.println(l);
        }
    }

}
