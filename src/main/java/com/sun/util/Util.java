package com.sun.util;

import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.data.Data;
import com.sun.pojo.Order;
import com.sun.pojo.StockArea;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;

public class Util {
    public static void readCsv(String readPath, ArrayList<String[]> Valueslist) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(readPath);

        File file = classPathResource.getFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splitline = line.split(",");
            int Dim = splitline.length;
            //将splitline中的每个元素保存到double类型的数组中
            String[] temp = new String[Dim];
            for (int i = 0; i < Dim; i++) {
                temp[i] = splitline[i];
            }
            Valueslist.add(temp);
        }
        Valueslist.remove(0);
    }

    public static void writeCsvForLocation(String writePath, String[] names, String[] types, String[] x, String[] y, String[] widths, String[] heights) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(writePath);
        File file = classPathResource.getFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("name,type,x,y,width,height\n");
        for (int i = 0; i < names.length; i++) {
            fileWriter.write(names[i] + ",");
            fileWriter.write(types[i] + ",");
            fileWriter.write(x[i] + ",");
            fileWriter.write(y[i] + ",");
            fileWriter.write(widths[i] + ",");
            fileWriter.write(heights[i]);
            if (i < names.length - 1) {
                fileWriter.write("\n");
            }
        }
        fileWriter.flush();
        fileWriter.close();
        //System.out.println("hello world!");
    }

    public static void writeCsvForPath(List<Double> timeList, Map<String, List<Location>> path, Map<String, List<Integer>> taskNo) throws IOException {
        List<String> craneNoMap = new ArrayList<>();
        craneNoMap.add("1_1");
        craneNoMap.add("1_2");
        craneNoMap.add("1_3");

        for (String key : craneNoMap) {
            String key2 = "crane" + (key.equals("1_1") ? "1-1" : key.equals("1_2") ? "1-2" : "1-3");
            String writePath = "./static/data/20201102_" + key + ".csv";
            ClassPathResource classPathResource = new ClassPathResource(writePath);
            File file = classPathResource.getFile();
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!taskNo.containsKey(key2)) {
                continue;
            }
            FileWriter fileWriter = new FileWriter(file, true);
            if (file.length() == 0) {
                fileWriter.write("CraneNO,OrderNO,RecTime,CurX,CurY,CurZ,\n");
            }
            for (int i = 0; i < timeList.size(); i++) {
                fileWriter.write(key2 + ",");
                fileWriter.write(taskNo.get(key2).get(i) + ",");
                fileWriter.write(timeList.get(i) + ",");
                fileWriter.write(path.get(key2).get(i).getX() + ",");
                fileWriter.write(path.get(key2).get(i).getY() + ",");
                fileWriter.write(path.get(key2).get(i).getZ() + ",\n");
            }
            fileWriter.flush();
            fileWriter.close();
        }
    }

    public static void writeCsvForOrder() throws IOException {
        String writePath = "./static/data/order.csv";
        ClassPathResource classPathResource = new ClassPathResource(writePath);
        File file = classPathResource.getFile();
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file, true);
        if (file.length() == 0) {
            fileWriter.write("orderNo,type,crane,flag,start,end,coilNo,startTime,\n");
        }
        for (Order order : Data.orderList) {
            fileWriter.write(order.getOrderNo() + ",");
            fileWriter.write(order.getType() + ",");
            fileWriter.write(order.getCrane() + ",");
            fileWriter.write(order.getCrane().charAt(order.getCrane().length() - 1) + ",");
            fileWriter.write(order.getStart() + ",");
            fileWriter.write(order.getEnd() + ",");
            fileWriter.write(order.getCoilNo() + ",");
            fileWriter.write(order.getStartTime() + ",\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public static List<Task> createRandomTask(int type, int num, String beginS, String endS) throws IOException {
        int stkNum = Data.stockAreas.size();
        if (stkNum == 0) {
            String path = "./static/data/StoreLocation.csv";
            ArrayList<String[]> stockList = new ArrayList<>();
            readCsv(path, stockList);
            stockList.forEach(e -> {
                StockArea stockArea = new StockArea(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                Data.stockAreas.add(stockArea);
            });
            stkNum = Data.stockAreas.size();
        }
        if (stkNum == 0) {
            return null;
        }
        List<StockArea> beginList = new ArrayList<>();
        List<StockArea> endList = new ArrayList<>();
        for (String s : beginS.split(",")) {
            beginList.add(Data.stockAreas.get(Integer.parseInt(s)));
        }
        for (String s : endS.split(",")) {
            endList.add(Data.stockAreas.get(Integer.parseInt(s)));
        }
        int beginNum = beginList.size();
        int endNum = endList.size();
        List<Task> taskList = new ArrayList<>();
        int preTaskNum = Data.taskMap.size();
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
            Data.taskMap.put((Integer) (i + preTaskNum), task);
            taskList.add(task);
        }

        return taskList;
    }

    public static List<Set<Integer>> convertToSet(List<Integer> countForEveryType, List<Integer> allocateNo) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        Set<Integer> set3 = new HashSet<>();
        int pre = 0;
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            if (i == 0)
                set1.addAll(allocateNo.subList(pre, pre + count));
            else if (i == 1)
                set2.addAll(allocateNo.subList(pre, pre + count));
            else
                set3.addAll(allocateNo.subList(pre, pre + count));
            pre += count;
        }

        return Arrays.asList(set1, set2, set3);
    }
}
