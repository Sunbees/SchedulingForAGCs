package com.sun.data;

import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.pojo.*;
import com.sun.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data {
    static public int TASK_NUM;
    static public Solution solution;
    static public List<Crane> craneList;
    static public HashMap<Integer, Task> taskMap;
    static public Cranes cranes;
    static public Tasks tasks;
    static public List<StockArea> stockAreas;
    static public List<Order> orderList;
    static public int SafeDistance = 26000;
    static public double[] velocity_1 = {1500, 200, 100};
    static public double[] velocity_2 = {1500, 200, 100};
    static public double[] velocity_3 = {1500, 200, 100};

    static public double[] location_1 = {60000, 3000, 6000};
    static public double[] location_2 = {80000, 3000, 6000};
    static public double[] location_3 = {220000, 3000, 6000};

    static {
        solution = new Solution();
        //solution.addPriority(0)
        //        .addPriority(1).addPriority(2).addPriority(3);
        //for (int i = 0; i < 7; i++) {
        //    solution.addPriority(i);
        //}
        //TASK_NUM = solution.numOfTask();
        //TASK_NUM = solution.numOfTask();
        stockAreas = new ArrayList<>();
    }

    public static void initForDraw() {
        int crane_num = 0;
        craneList = new ArrayList<>();
        orderList = new ArrayList<>();
        Order.setNo(0);
        for (Crane2 crane : cranes.getCranes()) {
            String type = crane.getType();
            if(type!=null) {
                String[] types = type.split(",");
                double x = crane_num == 0 ? location_1[0] : crane_num == 1 ? location_2[0] : location_3[0];
                double y = crane_num == 0 ? location_1[1] : crane_num == 1 ? location_2[1] : location_3[1];
                double z = crane_num == 0 ? location_1[2] : crane_num == 1 ? location_2[2] : location_3[2];

                Location location = new Location(x, y, z);
                String craneName = crane_num == 0 ? "crane1-1" : crane_num == 1 ? "crane1-2" : "crane1-3";
                double[] velocity = crane_num == 0 ? velocity_1 : crane_num == 1 ? velocity_2 : velocity_3;
                Crane craneBuilder = new Crane(craneName, location, false, velocity[0], velocity[1], velocity[2]);
                for (int i = 0; i < types.length; i++) {
                    int typeNum = Integer.parseInt(types[i]);
                    craneBuilder = craneBuilder.addType(typeNum);
                }
                craneList.add(craneBuilder);
            }
            ++crane_num;
        }
    }

    public static void initDataForDraw(Tasks4Create tasks, Cranes cranesT) throws IOException {
        cranes = cranesT;
        taskMap = new HashMap<>();
        TASK_NUM = 0;
        solution = new Solution();
        for (Task4Create task : tasks.getTasks()) {
            if (task.getNum() > 0) {
                Util.createRandomTask(task.getType(), task.getNum(), task.getBegin(), task.getEnd());
                TASK_NUM += task.getNum();
            }
        }
        for (int i = 0; i < TASK_NUM; i++) {
            solution.addPriority(i);
        }
    }
}
