package com.sun.data;

import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.pojo.*;
import com.sun.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Data {
    static public int TASK_NUM;
    static public Solution solution;
    static public List<Crane> craneList;
    static public HashMap<Integer, Task> taskMap;
    static public Cranes cranes;
    static public Tasks tasks;
    static public List<Stock> stocks;
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
        //for (int i = 0; i < 40; i++) {
        //    solution.addPriority(i);
        //}
        //TASK_NUM = solution.numOfTask();
        //TASK_NUM = solution.numOfTask();
        stocks = new ArrayList<>();
    }

    static public void initTest() {
        // 初始化AGC位置
        Location location_l = new Location(0, 0, 20);
        Location location_m = new Location(50, 0, 20);
        Location location_r = new Location(100, 100, 20);
        Crane craneL = new Crane("crane1-1", location_l, false, 1, 1, 2).addType(2).addType(1).addType(0);
        Crane craneM = new Crane("crane1-2", location_m, false, 1, 1, 2).addType(2).addType(1).addType(0);
        Crane craneR = new Crane("crane1-3", location_r, false, 1, 1, 2).addType(2).addType(1).addType(0);
        craneList = new ArrayList<>();
        craneList.add(craneL);
        craneList.add(craneM);
        craneList.add(craneR);

        /*
         * 定义任务：
         * type：任务的类型（吊机组、出入库、倒垛等，值越高优先级越高）
         * startTime：任务分配的时间
         * start_x,y,z：任务起始点
         * end_x,y,z：任务终点
         * */
        //int[] type = new int[]{1, 1, 2, 1};
        //double[] startTime = new double[]{10, 3, 5, 8};
        //double[] start_x = new double[]{80, 60, 55, 20};
        //double[] start_y = new double[]{20, 30, 50, 90};
        //double[] start_z = new double[]{5, 5, 5, 5};
        //
        //double[] end_x = new double[]{10, 20, 40, 75};
        //double[] end_y = new double[]{70, 50, 20, 10};
        //double[] end_z = new double[]{5, 5, 5, 5};

        // 测试三个任务的情况
        //int[] type = new int[]{1, 1, 2};
        //double[] startTime = new double[]{10, 3, 5};
        //double[] start_x = new double[]{80, 60, 55};
        //double[] start_y = new double[]{20, 30, 50};
        //double[] start_z = new double[]{5, 5, 5};
        //
        //double[] end_x = new double[]{10, 20, 40};
        //double[] end_y = new double[]{70, 50, 20};
        //double[] end_z = new double[]{5, 5, 5};

        // 测试两个任务的情况
        //int[] type = new int[]{1, 1};
        //double[] startTime = new double[]{10, 3};
        //double[] start_x = new double[]{80, 60};
        //double[] start_y = new double[]{20, 30};
        //double[] start_z = new double[]{5, 5};
        //
        //double[] end_x = new double[]{10, 20};
        //double[] end_y = new double[]{70, 50};
        //double[] end_z = new double[]{5, 5};

        // 7个任务的情况
        int[] type = new int[]{1, 1, 2, 1, 2, 1, 0};
        double[] startTime = new double[]{10, 3, 5, 8, 1, 4, 6};
        double[] start_x = new double[]{80, 60, 55, 20, 1, 50, 90};
        double[] start_y = new double[]{20, 30, 50, 90, 2, 10, 90};
        double[] start_z = new double[]{5, 5, 5, 5, 5, 5, 5};

        double[] end_x = new double[]{10, 20, 40, 75, 99, 100, 50};
        double[] end_y = new double[]{70, 50, 20, 10, 98, 30, 10};
        double[] end_z = new double[]{5, 5, 5, 5, 5, 5, 5};

        taskMap = new HashMap<>();
        // 把所有task插入taskMap
        for (int i = 0; i < startTime.length; i++) {
            Location startLocation = new Location(start_x[i], start_y[i], start_z[i]);
            Location endLocation = new Location(end_x[i], end_y[i], end_z[i]);
            Task task = new Task(i, startTime[i], startLocation, endLocation, type[i]);
            taskMap.put(i, task);
        }
    }

    public static void init() {
        int crane_num = 0;
        craneList = new ArrayList<>();
        taskMap = new HashMap<>();
        for (Crane2 crane : cranes.getCranes()) {
            String crane_Location = crane.getCrane_Location();
            String crane_Velocity = crane.getCrane_Velocity();
            String type = crane.getType();
            String[] locations = crane_Location.split(",");
            String[] velocities = crane_Velocity.split(",");
            String[] types = type.split(",");
            Location location = new Location(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]), Double.parseDouble(locations[2]));
            String craneName = crane_num == 0 ? "crane1-1" : crane_num == 1 ? "crane1-2" : "crane1-3";
            Crane craneBuilder = new Crane(craneName, location, false, Double.parseDouble(velocities[0]), Double.parseDouble(velocities[1]), Double.parseDouble(velocities[2]));
            for (int i = 0; i < types.length; i++) {
                int typeNum = Integer.parseInt(types[i]);
                craneBuilder = craneBuilder.addType(typeNum);
            }
            craneList.add(craneBuilder);
            ++crane_num;
        }
        //craneList.forEach(System.out::println);
        int task_num = 0;
        for (Task2 task : tasks.getTasks()) {
            double startTime = task.getStartTime();
            String[] startLocation = task.getStartLocation().split(",");
            String[] endLocation = task.getEndLocation().split(",");
            int type = task.getType();
            Location startL = new Location(Double.parseDouble(startLocation[0]), Double.parseDouble(startLocation[1]), Double.parseDouble(startLocation[2]));
            Location endL = new Location(Double.parseDouble(endLocation[0]), Double.parseDouble(endLocation[1]), Double.parseDouble(endLocation[2]));
            Task taskBuilder = new Task(task_num, startTime, startL, endL, type);
            taskMap.put(task_num++, taskBuilder);
        }
        //taskMap.values().forEach(System.out::println);
    }

    public static void initTestForDraw() throws IOException {
        taskMap = new HashMap<>();
        Util.createRandomTask(1, 25, "0,1", "0,1");
        Util.createRandomTask(2, 13, "0,1", "0,1");
        Util.createRandomTask(0, 2, "0,1", "0,1");

        // 初始化AGC位置
        Location location_l = new Location(0, 0, 6000);
        Location location_m = new Location(5000, 0, 6000);
        Location location_r = new Location(10000, 10000, 6000);
        Crane craneL = new Crane("crane1-1", location_l, false, 100, 100, 200).addType(2).addType(1).addType(0);
        Crane craneM = new Crane("crane1-2", location_m, false, 100, 100, 200).addType(2).addType(1).addType(0);
        Crane craneR = new Crane("crane1-3", location_r, false, 100, 100, 200).addType(2).addType(1).addType(0);
        craneList = new ArrayList<>();
        craneList.add(craneL);
        craneList.add(craneM);
        craneList.add(craneR);
        //Data.taskMap.values().forEach(System.out::println);
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

    public static void initData(Cranes cranesT, Tasks tasksT) {
        cranes = cranesT;
        tasks = tasksT;
        solution = new Solution();
        for (int i = 0; i < tasksT.getTasks().size(); i++) {
            solution.addPriority(i);
        }
        TASK_NUM = solution.numOfTask();
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
