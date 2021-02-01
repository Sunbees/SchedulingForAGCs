package com.sun.allocate;

import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.data.Data;
import com.sun.pojo.Solution;
import com.sun.pojo.StockArea;
import com.sun.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class TestData {
    static public int TASK_NUM;
    static public Location startLocation;
    static public SolutionForAllocate solution;
    static public List<Crane> craneList;
    static public HashMap<Integer, Task> taskMap;
    static public HashMap<Integer, Location> stockLocationMap;
    static public HashMap<Integer, List<Integer>> typeStockMap;
    static public HashMap<Integer, List<Integer>> stockTypeStockAreaMap;
    static public List<Integer> countForEveryType;
    static public double[] velocity_1 = {1500, 200, 100};
    static public double[] velocity_2 = {1500, 200, 100};
    static public double[] velocity_3 = {1500, 200, 100};

    static public double[] location_1 = {60000, 3000, 6000};
    static public double[] location_2 = {80000, 3000, 6000};
    static public double[] location_3 = {220000, 3000, 6000};

    public static void init(Location start) throws IOException {
        stockLocationMap = new HashMap<>();
        typeStockMap = new HashMap<>();
        stockTypeStockAreaMap = new HashMap<>();
        List<Integer> l1 = new ArrayList<>();
        l1.add(0);
        l1.add(1);
        List<Integer> l2 = new ArrayList<>();
        l2.add(2);
        l2.add(3);
        List<Integer> l3 = new ArrayList<>();
        l3.add(4);
        l3.add(5);
        stockTypeStockAreaMap.put(0, l1);
        stockTypeStockAreaMap.put(1, l2);
        stockTypeStockAreaMap.put(2, l3);
        countForEveryType = new ArrayList<>();
        countForEveryType.add(5);
        countForEveryType.add(5);
        countForEveryType.add(5);
        //genStock();
        stockLocationMap.put(0, new Location(260399.0, 17622.0, 1000.0));
        stockLocationMap.put(1, new Location(236399.0, 17622.0, 1000.0));
        stockLocationMap.put(2, new Location(242399.0, 4622.0, 1000.0));
        stockLocationMap.put(3, new Location(222399.0, 33800.0, 1000.0));
        stockLocationMap.put(4, new Location(235399.0, 5622.0, 1000.0));
        stockLocationMap.put(5, new Location(179000.0, 17622.0, 1000.0));
        stockLocationMap.put(6, new Location(172000.0, 28900.0, 1000.0));
        stockLocationMap.put(7, new Location(196000.0, 9622.0, 1000.0));
        stockLocationMap.put(8, new Location(195000.0, 6622.0, 1000.0));
        stockLocationMap.put(9, new Location(187000.0, 31900.0, 1000.0));
        stockLocationMap.put(10, new Location(131800.0, 26900.0, 1000.0));
        stockLocationMap.put(11, new Location(147800.0, 13622.0, 1000.0));
        stockLocationMap.put(12, new Location(155800.0, 28900.0, 1000.0));
        stockLocationMap.put(13, new Location(126800.0, 22900.0, 1000.0));
        stockLocationMap.put(14, new Location(158800.0, 24900.0, 1000.0));
        typeStockMap.put(0, Arrays.asList(0, 1, 2, 3, 4));
        typeStockMap.put(1, Arrays.asList(5, 6, 7, 8, 9));
        typeStockMap.put(2, Arrays.asList(10, 11, 12, 13, 14));

        startLocation = start;

    }

    private static void genStock() throws IOException {
        List<String> list = new ArrayList<>();
        boolean isInitial = Data.stockAreas.size() == 0;
        String path = "./static/data/StoreLocation.csv";
        ArrayList<String[]> stockList = new ArrayList<>();
        Util.readCsv(path, stockList);
        stockList.forEach(e -> {
            list.add(e[0]);
            if (isInitial) {
                StockArea stockArea = new StockArea(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                Data.stockAreas.add(stockArea);
            }
        });
        List<StockArea> endList = new ArrayList<>(Data.stockAreas);
        for (int j = 0; j < countForEveryType.size(); j++) {
            int pre = stockLocationMap.size();
            for (int i = 0; i < countForEveryType.get(j); i++) {
                int index = (int) (Math.random() * stockTypeStockAreaMap.get(j).size());
                int stkNoE = stockTypeStockAreaMap.get(j).get(index);
                double xE = endList.get(stkNoE).getX() + (int) (Math.random() * endList.get(stkNoE).getWidth());
                double yE = endList.get(stkNoE).getY() - (int) (Math.random() * endList.get(stkNoE).getHeight());
                xE = Math.round(xE * 1000);
                yE = Math.round(yE * 1000);
                //System.out.println(xS);
                Location end = new Location(xE, yE, 1000);
                stockLocationMap.put(i + pre, end);
                if (!typeStockMap.containsKey(j)) {
                    typeStockMap.put(j, new ArrayList<>());
                }
                typeStockMap.get(j).add(i + pre);
            }
        }
    }

    // 初始化Data的solution
    public void initSolution(int n) {
        TASK_NUM = n;
        Data.solution = new Solution();
        for (int i = 0; i < n; i++) {
            Data.solution.addPriority(i);
        }
    }

    static {
        try {
            init(new Location(0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initForAllocate(List<Integer> allocationNo, int taskNum) throws IOException {
        // 初始化AGC位置
        Location location_l = new Location(location_1[0], location_1[1], location_1[2]);
        Location location_m = new Location(location_2[0], location_2[1], location_2[2]);
        Location location_r = new Location(location_3[0], location_3[1], location_3[2]);
        Crane craneL = new Crane("crane1-1", location_l, false, velocity_1[0], velocity_1[1], velocity_1[2]).addType(2).addType(1).addType(0);
        Crane craneM = new Crane("crane1-2", location_m, false, velocity_2[0], velocity_2[1], velocity_2[2]).addType(2).addType(1).addType(0);
        Crane craneR = new Crane("crane1-3", location_r, false, velocity_3[0], velocity_3[1], velocity_3[2]).addType(2).addType(1).addType(0);
        craneList = new ArrayList<>();
        craneList.add(craneL);
        craneList.add(craneM);
        craneList.add(craneR);

        Data.craneList = craneList;

        genTasks(allocationNo);

        Data.taskMap = taskMap;
    }


    public static void genTasks(List<Integer> allocationNo) {
        if (taskMap == null) {
            taskMap = new HashMap<>();
        }
        if (taskMap.size() > 0) {
            return;
        }
        for (int i = 0; i < allocationNo.size(); i++) {
            Location start = startLocation;
            Location end = stockLocationMap.get(allocationNo.get(i));
            Task task = new Task(i, 0, start, end, 1);
            taskMap.put(i, task);
        }
    }

    public static void main(String[] args) throws IOException {

        //test.init(3, new Location(0, 0, 0));
        //
        //for (int i = 0; i < 15; i++) {
        //    System.out.print(TestData.stockLocationMap.get(i) + "\t");
        //    System.out.println(TestData.stockTypeMap.get(i));
        //}
        List<Integer> allocationNo = Arrays.asList(0, 2, 5, 6, 7, 10, 11, 12, 14);
        //test.genTasks(allocationNo);
        //TestData.taskMap.values().forEach(System.out::println);

        TestData.initForAllocate(allocationNo, 9);

        Data.taskMap.values().forEach(System.out::println);

    }

    public void clearTaskMap() {
        taskMap.clear();
    }
}
