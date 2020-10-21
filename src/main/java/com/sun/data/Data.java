package com.sun.data;

import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.pojo.*;

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

    static {
        solution = new Solution();
        solution.addPriority(0)
                .addPriority(1).addPriority(2).addPriority(3);
        TASK_NUM = solution.numOfTask();
    }

    static public void initTest() {
        // 初始化AGC位置
        Location location_l = new Location(0, 0, 20);
        Location location_m = new Location(50, 0, 20);
        Location location_r = new Location(100, 100, 20);
        Crane craneL = new Crane("AGC-left", location_l, false, 1, 1, 2).addType(2).addType(1).addType(0);
        Crane craneM = new Crane("AGC-middle", location_m, false, 1, 1, 2).addType(2).addType(1).addType(0);
        Crane craneR = new Crane("AGC-right", location_r, false, 1, 1, 2).addType(2).addType(1).addType(0);
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
            String craneName = crane_num == 0 ? "AGC-left" : crane_num == 1 ? "AGC-middle" : "AGC-right";
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

    public static void initData(Cranes cranesT, Tasks tasksT) {
        cranes = cranesT;
        tasks = tasksT;
        solution = new Solution();
        for (int i = 0; i < tasksT.getTasks().size(); i++) {
            solution.addPriority(i);
        }
        TASK_NUM = solution.numOfTask();
    }
}
