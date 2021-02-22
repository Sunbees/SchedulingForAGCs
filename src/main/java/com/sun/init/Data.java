package com.sun.init;

import com.sun.pojo.*;
import com.sun.solution.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sun.util.Util.readCsv;

public class Data {
    static public int TASK_NUM;
    static public Solution solution;
    static public List<Crane> craneList;
    static public HashMap<Integer, Task> taskMap;
    static public List<Store> stockAreas;
    static public List<Order> orderList;
    static public int SafeDistance = 26000;
    static public double[] velocity_1 = {1500, 200, 100};
    static public double[] velocity_2 = {1500, 200, 100};
    static public double[] velocity_3 = {1500, 200, 100};

    static public double[] location_1 = {60000, 3000, 6000};
    static public double[] location_2 = {80000, 3000, 6000};
    static public double[] location_3 = {220000, 3000, 6000};


    public static void initData(List<Integer> crane1Type, List<Integer> crane2Type, List<Integer> crane3Type, List<TaskCreate> tasks) {
        solution = new Solution();
        craneList = new ArrayList<>();
        orderList = new ArrayList<>();
        stockAreas = new ArrayList<>();
        taskMap = new HashMap<>();
        Order.setNo(0);
        if (crane1Type.size() > 0) {
            Crane crane1 = new Crane("crane1-1", new Location(location_1[0], location_1[1], location_1[2]), false, velocity_1[0], velocity_1[1], velocity_1[2]);
            for (int type : crane1Type) {
                crane1.addType(type);
            }
            craneList.add(crane1);
        }
        if (crane2Type.size() > 0) {
            Crane crane2 = new Crane("crane1-2", new Location(location_2[0], location_2[1], location_2[2]), false, velocity_2[0], velocity_2[1], velocity_2[2]);
            for (int type : crane2Type) {
                crane2.addType(type);
            }
            craneList.add(crane2);
        }
        if (crane3Type.size() > 0) {
            Crane crane3 = new Crane("crane1-3", new Location(location_3[0], location_3[1], location_3[2]), false, velocity_3[0], velocity_3[1], velocity_3[2]);
            for (int type : crane3Type) {
                crane3.addType(type);
            }
            craneList.add(crane3);
        }
        for (TaskCreate task : tasks) {
            if (task.getNum() > 0) {
                createRandomTask(task.getType(), task.getNum(), task.getBegin(), task.getEnd());
            }
        }
        TASK_NUM = taskMap.size();

        for (int i = 0; i < TASK_NUM; i++) {
            solution.addPriority(i);
        }
        //System.out.println("ok");
    }

    public static void initCrane() {
        for (Crane crane : craneList) {
            if (crane.getId().equals("crane1-1")) {
                crane.setLocation(new Location(location_1[0], location_1[1], location_1[2]));
            } else if (crane.getId().equals("crane1-2")) {
                crane.setLocation(new Location(location_2[0], location_2[1], location_2[2]));
            } else {
                crane.setLocation(new Location(location_3[0], location_3[1], location_3[2]));
            }
            crane.setUsed(false);
        }
    }

    private static void createRandomTask(int type, int num, List<Integer> begins, List<Integer> ends) {
        int stkNum = stockAreas.size();
        if (stkNum == 0) {
            String path = "./source/StoreLocation.csv";
            ArrayList<String[]> stockList = new ArrayList<>();
            try {
                readCsv(path, stockList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockList.forEach(e -> {
                Store stockArea = new Store(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                stockAreas.add(stockArea);
            });
            stkNum = stockAreas.size();
        }
        if (stkNum == 0) {
            return;
        }
        List<Store> beginList = new ArrayList<>();
        List<Store> endList = new ArrayList<>();
        for (int index : begins) {
            beginList.add(stockAreas.get(index));
        }
        for (int index : ends) {
            endList.add(stockAreas.get(index));
        }
        int beginNum = beginList.size();
        int endNum = endList.size();
        int preTaskNum = taskMap.size();
        for (int i = 0; i < num; i++) {
            int stkNoS = (int) (beginNum * Math.random());
            int stkNoE = (int) (endNum * Math.random());
            double xS = beginList.get(stkNoS).getX() + (int) (Math.random() * beginList.get(stkNoS).getWidth());
            double xE = endList.get(stkNoE).getX() + (int) (Math.random() * endList.get(stkNoE).getWidth());
            double yS = beginList.get(stkNoS).getY() - (int) (Math.random() * beginList.get(stkNoS).getHeight());
            double yE = endList.get(stkNoE).getY() - (int) (Math.random() * endList.get(stkNoE).getHeight());
            xS = Math.round(xS * 1000);
            xE = Math.round(xE * 1000);
            yS = Math.round(yS * 1000);
            yE = Math.round(yE * 1000);
            //System.out.println(xS);
            Location start = new Location(xS, yS, 1000);
            Location end = new Location(xE, yE, 1000);
            Task task = new Task(i + preTaskNum, 0, start, end, type);
            taskMap.put((Integer) (i + preTaskNum), task);
        }
    }

    public static void updateCraneList(int distance, List<double[]> crane1Info, List<double[]> crane2Info, List<double[]> crane3Info) {
        SafeDistance = distance;
        location_1 = crane1Info.get(0);
        velocity_1 = crane1Info.get(1);

        location_2 = crane2Info.get(0);
        velocity_2 = crane2Info.get(1);

        location_3 = crane3Info.get(0);
        velocity_3 = crane3Info.get(1);
    }
}
