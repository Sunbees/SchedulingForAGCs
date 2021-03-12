package com.sun.util;

import com.sun.init.Data;
import com.sun.pojo.Location;
import com.sun.pojo.Order;
import com.sun.pojo.Step;
import com.sun.pojo.Store;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;

public class Util {
    public static String csvPath = "C:\\Users\\SunQJ\\Documents\\agc";

    public static void readCsv(String readPath, ArrayList<String[]> Valueslist) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(readPath);
        InputStream inputStream = classPathResource.getInputStream();
        //File file = classPathResource.getFile();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
        if (Valueslist.size() > 0)
            Valueslist.remove(0);
    }

    public static void readCsv2(String readPath, ArrayList<String[]> Valueslist) throws IOException {
        //ClassPathResource classPathResource = new ClassPathResource(readPath);
        //InputStream inputStream = classPathResource.getInputStream();
        File file = new File(csvPath + readPath);
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
        if (Valueslist.size() > 0)
            Valueslist.remove(0);
    }

    public static Map<String, Object> readStoreLocation() {
        Map<String, Object> map = new HashMap<>();
        String path = "./source/StoreLocation.csv";
        ArrayList<String[]> valuesList = new ArrayList<>();
        try {
            readCsv(path, valuesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Store> list = new ArrayList<>();
        valuesList.forEach(e -> {
            Store store = new Store(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
            list.add(store);
        });
        map.put("data", list);
        return map;
    }

    public static Map<String, Object> readOrderInfo() {
        Map<String, Object> map = new HashMap<>();
        String path = "\\order.csv";
        ArrayList<String[]> valuesList = new ArrayList<>();
        try {
            readCsv2(path, valuesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Order> list = new ArrayList<>();
        valuesList.forEach(e -> {
            double[] start = Arrays.stream(e[4].substring(1, e[4].length() - 1).split("-")).mapToDouble(Double::parseDouble).toArray();
            double[] end = Arrays.stream(e[5].substring(1, e[5].length() - 1).split("-")).mapToDouble(Double::parseDouble).toArray();
            Order order = new Order(e[0], Integer.parseInt(e[1]), e[2], Integer.parseInt(e[3]), start, end, e[6], Double.parseDouble(e[7]));
            //System.out.println(order);
            list.add(order);
        });
        map.put("data", list);
        return map;
    }

    public static Map<String, Object> readPath() {
        Map<String, Object> map = new HashMap<>();
        String[] paths = new String[]{"\\20201102_1_1.csv", "\\20201102_1_2.csv", "\\20201102_1_3.csv"};
        String[] keys = new String[]{"path1", "path2", "path3"};
        for (int i = 0; i < 3; i++) {
            ArrayList<String[]> valuesList = new ArrayList<>();
            try {
                readCsv2(paths[i], valuesList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Step> list = new ArrayList<>();
            valuesList.forEach(e -> {
                Step step = new Step(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                //System.out.println(order);
                list.add(step);
            });
            map.put(keys[i], list);
        }
        return map;
    }

    public static Map<String, Object> readStockForNo() {
        Map<String, Object> map = new HashMap<>();
        String path = "./source/StoreLocation.csv";
        ArrayList<String[]> valuesList = new ArrayList<>();
        try {
            readCsv(path, valuesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> list = new ArrayList<>();
        int index = 0;
        valuesList.forEach(e -> {
            list.add(e[0]);
        });
        map.put("data", list);
        return map;
    }

    public static Map<String, Object> getCraneConfigInfo() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> craneList = new ArrayList<>();
        Map<String, Object> crane1 = new HashMap<>();
        crane1.put("craneName", "crane1");
        crane1.put("begin", arrayToString(Data.location_1));
        crane1.put("v1", Data.velocity_1[0]);
        crane1.put("v2", Data.velocity_1[1]);
        crane1.put("v3", Data.velocity_1[2]);
        craneList.add(crane1);

        Map<String, Object> crane2 = new HashMap<>();
        crane2.put("craneName", "crane2");
        crane2.put("begin", arrayToString(Data.location_2));
        crane2.put("v1", Data.velocity_2[0]);
        crane2.put("v2", Data.velocity_2[1]);
        crane2.put("v3", Data.velocity_2[2]);
        craneList.add(crane2);

        Map<String, Object> crane3 = new HashMap<>();
        crane3.put("craneName", "crane3");
        crane3.put("begin", arrayToString(Data.location_3));
        crane3.put("v1", Data.velocity_3[0]);
        crane3.put("v2", Data.velocity_3[1]);
        crane3.put("v3", Data.velocity_3[2]);
        craneList.add(crane3);

        map.put("craneList", craneList);
        map.put("distance", Data.SafeDistance);

        return map;
    }

    public static void writeCsvForOrder() throws IOException {
        //String writePath = "./source/order.csv";
        //ClassPathResource classPathResource = new ClassPathResource(writePath);
        //
        //
        //File file = classPathResource.getFile();
        File file = new File(csvPath+"\\order.csv");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            fileWriter.write(Arrays.toString(order.getStart()).replaceAll(", ", "-") + ",");
            fileWriter.write(Arrays.toString(order.getEnd()).replaceAll(", ", "-") + ",");
            fileWriter.write(order.getCoilNo() + ",");
            fileWriter.write(order.getStartTime() + ",\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public static void writeCsvForPath(List<Double> timeList, Map<String, List<Location>> path, Map<String, List<Integer>> taskNo) throws IOException {
        List<String> craneNoMap = new ArrayList<>();
        craneNoMap.add("1_1");
        craneNoMap.add("1_2");
        craneNoMap.add("1_3");

        for (String key : craneNoMap) {
            String key2 = "crane" + (key.equals("1_1") ? "1-1" : key.equals("1_2") ? "1-2" : "1-3");
            String writePath = "\\20201102_" + key + ".csv";
            //ClassPathResource classPathResource = new ClassPathResource(writePath);
            //File file = classPathResource.getFile();
            File file = new File(csvPath+writePath);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    public static String arrayToString(double[] nums) {
        StringBuilder sb = new StringBuilder();
        for (double num : nums) {
            sb.append(num);
            sb.append(',');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
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


    public static void main(String[] args) {
        readPath();
    }
}
