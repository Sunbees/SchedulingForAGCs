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
    void contextLoads() throws IOException {
        //Util.writeCsvForPath("1_1","2020110200001","9570","27011","5710");
        //Util.writeCsvForPath("1_1","2020110200002","9571","27012","5711");
        //Util.writeCsvForPath("1_1","2020110200003","9571","27012","5711");
        //Util.writeCsvForPath("1_1","2020110200004","9571","27012","5711");

        //Util.createRandomTask(1, 25);
        //Util.createRandomTask(2, 13);
        Data.taskMap = new HashMap<>();
        Util.createRandomTask(0, 10, "0,1", "2,3");
        Data.taskMap.values().forEach(System.out::println);
        //Data.stocks.forEach(System.out::println);
    }


}
