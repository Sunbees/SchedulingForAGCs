package com.sun.solution;


//import com.sun.allocate.TestData;
import com.sun.allocate.TestData;
import com.sun.init.Data;
import com.sun.pojo.Crane;
import com.sun.pojo.Location;
import com.sun.pojo.Order;
import com.sun.pojo.Task;

import java.io.IOException;
import java.util.*;

import static com.sun.init.Data.initCrane;

public class Collision {
    public static double nowTime;
    public static int safeDistance = Data.SafeDistance;
    public static double startZ;
    // 存储所有的待分配任务
    public static HashMap<Integer, Task> taskMap;
    public static ArrayList<Integer> priority;
    public static List<Crane> craneList;


    public static void init(List<Integer> pri) {
        nowTime = 0.0;
        initCrane();
        //Data.initForDraw(); //改这个还原
        safeDistance = Data.SafeDistance;
        // craneList存储所有的AGC信息
        craneList = Data.craneList;
        startZ = craneList.get(0).getLocation().getZ();
        taskMap = new HashMap<>();
        try {
            for (Integer key : Data.taskMap.keySet()) {
                taskMap.put(key, Data.taskMap.get(key).clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // priority列表存储每个任务的处理优先级，越靠前优先级越高，已分配AGC的任务会从中删除
        priority = new ArrayList<>(pri);
        //priority.add(0);
        //priority.add(1);
        //priority.add(2);
    }

    public static void initForAllocate(List<Set<Integer>> allocationNo, int taskNum, List<Integer> pri) throws IOException {
        nowTime = 0.0;
        //List<Integer> allocationNo = Arrays.asList(0, 2, 5, 6, 7, 10, 11, 12, 14);
        //test.genTasks(allocationNo);
        //TestData.taskMap.values().forEach(System.out::println);

        TestData.initForAllocate(allocationNo, taskNum);
        safeDistance = Data.SafeDistance;
        // craneList存储所有的AGC信息
        craneList = Data.craneList;
        startZ = craneList.get(0).getLocation().getZ();
        taskMap = new HashMap<>();
        try {
            for (Integer key : Data.taskMap.keySet()) {
                taskMap.put(key, Data.taskMap.get(key).clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // priority列表存储每个任务的处理优先级，越靠前优先级越高，已分配AGC的任务会从中删除
        priority = new ArrayList<>(pri);
    }

    public static Track getTrack(List<Integer> pri) {
        init(pri);
        Track track = new Track();
        // 初始化track
        nowTime = 0.0;
        track.getTimeList().add(nowTime);
        for (Crane crane : Data.craneList) {
            ArrayList<Location> locations = new ArrayList<>();
            locations.add(crane.getLocation());
            track.getPath().put(crane.getId(), locations);
            track.getTaskNo().put(crane.getId(), new ArrayList<>());
            track.getTaskNo().get(crane.getId()).add(-1);
        }
        while (!priority.isEmpty() || numOfUsedCrane(craneList) > 0) {
            if (numOfUsedCrane(craneList) == 0) {
                // 选择分配AGC的任务
                int current = selectTask(priority);

                // 如果时间还没到，就把时间调整至这个任务的开始时间
                Task cTask = taskMap.get(current);
                if (nowTime < cTask.getStartTime()) {
                    nowTime = cTask.getStartTime();
                }
                Crane current_crane = selectCrane(craneList, current, taskMap, false, track);
                if (current_crane != null) {
                    priority.remove(Integer.valueOf(current));
                    current_crane.setUsed(true);
                    current_crane.addTask(cTask.getStart()).addTask(cTask.getEnd());
                    current_crane.setTaskNo(current);
                    current_crane.setType(cTask.getType());
                    Data.orderList.add(new Order(cTask.getId(), cTask.getType(), current_crane, cTask.getStart(), cTask.getEnd(), nowTime));
                }
            }

            // 有一个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 1) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, false, track)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            //有两个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 2) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, false, track)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            // 运行
            run(craneList, 1, false, track);
        }

        return track;
    }

    public static void main(String[] args) {
        List<Integer> pri = new ArrayList<>();
        //pri.add(5);
        //pri.add(1);
        //pri.add(2);
        //pri.add(3);
        //pri.add(0);
        //pri.add(4);
        //pri.add(6);
        for (int i = 0; i < 40; i++) {
            pri.add(i);
        }
        init(pri);

        while (!priority.isEmpty() || numOfUsedCrane(craneList) > 0) {
            if (numOfUsedCrane(craneList) == 0) {
                // 选择分配AGC的任务
                int current = selectTask(priority);

                // 如果时间还没到，就把时间调整至这个任务的开始时间
                if (nowTime < taskMap.get(current).getStartTime()) {
                    nowTime = taskMap.get(current).getStartTime();
                }
                Crane current_crane = selectCrane(craneList, current, taskMap, true, null);
                if (current_crane != null) {
                    priority.remove(Integer.valueOf(current));
                    current_crane.setUsed(true);
                    current_crane.addTask(taskMap.get(current).getStart()).addTask(taskMap.get(current).getEnd());
                    current_crane.setTaskNo(current);
                    current_crane.setType(taskMap.get(current).getType());
                }
            }

            // 有一个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 1) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, true, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            //有两个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 2) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, true, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            // 运行
            run(craneList, 999999, true, null);
        }
        for (Task value : taskMap.values()) {
            System.out.println("任务" + value.getId() + "的结束时间:" + value.getEndTime());
        }
    }

    public double calRunningTime(List<Integer> pri, boolean isPrint, List<Set<Integer>> allocationNo, int taskNum) throws IOException {
        //init(pri);
        initForAllocate(allocationNo, taskNum, pri);
        while (!priority.isEmpty() || numOfUsedCrane(craneList) > 0) {
            if (numOfUsedCrane(craneList) == 0) {
                // 选择分配AGC的任务
                int current = selectTask(priority);

                // 如果时间还没到，就把时间调整至这个任务的开始时间
                if (nowTime < taskMap.get(current).getStartTime()) {
                    nowTime = taskMap.get(current).getStartTime();
                }
                Crane current_crane = selectCrane(craneList, current, taskMap, isPrint, null);
                if (current_crane != null) {
                    priority.remove(Integer.valueOf(current));
                    current_crane.setUsed(true);
                    current_crane.addTask(taskMap.get(current).getStart()).addTask(taskMap.get(current).getEnd());
                    current_crane.setTaskNo(current);
                    current_crane.setType(taskMap.get(current).getType());
                }
            }

            // 有一个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 1) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, isPrint, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            //有两个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 2) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, isPrint, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            // 运行
            run(craneList, 999999, isPrint, null);
        }
        double endTime = 0.0;
        for (Task task : taskMap.values()) {
            if (task.getEndTime() > endTime) {
                endTime = task.getEndTime();
            }
        }
        return endTime;
    }

    public double calRunningTime(List<Integer> pri, boolean isPrint) throws IOException {
        init(pri);
        while (!priority.isEmpty() || numOfUsedCrane(craneList) > 0) {
            if (numOfUsedCrane(craneList) == 0) {
                // 选择分配AGC的任务
                int current = selectTask(priority);

                if(current==-1){
                    break;
                }

                // 如果时间还没到，就把时间调整至这个任务的开始时间
                if (nowTime < taskMap.get(current).getStartTime()) {
                    nowTime = taskMap.get(current).getStartTime();
                }
                Crane current_crane = selectCrane(craneList, current, taskMap, isPrint, null);
                if (current_crane != null) {
                    priority.remove(Integer.valueOf(current));
                    current_crane.setUsed(true);
                    current_crane.addTask(taskMap.get(current).getStart()).addTask(taskMap.get(current).getEnd());
                    current_crane.setTaskNo(current);
                    current_crane.setType(taskMap.get(current).getType());
                }
            }

            // 有一个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 1) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, isPrint, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            //有两个AGC被使用
            if (!priority.isEmpty() && numOfUsedCrane(craneList) == 2) {
                // 选择分配AGC的任务
                int next = selectTask(priority);
                // 是否能分配
                if (allocated(craneList, next, taskMap, isPrint, null)) {
                    priority.remove(Integer.valueOf(next));
                }
            }

            // 运行
            run(craneList, 999999, isPrint, null);
        }
        double endTime = 0.0;
        for (Task task : taskMap.values()) {
            if (task.getEndTime() > endTime) {
                endTime = task.getEndTime();
            }
        }
        return endTime;
    }

    private static void run(List<Crane> craneList, double maxTime, boolean isPrint, Track track) {
        PriorityQueue<Crane> priority = new PriorityQueue<>((e1, e2) -> {
            if (!e1.isUsed() && !e2.isUsed()) {
                return -1;
            } else if (!e1.isUsed() || !e2.isUsed()) {
                return e1.isUsed() ? -1 : 1;
            } else if (e1.getType() != e2.getType()) {
                return e2.getType() - e1.getType();
            } else {
                return (Math.abs(e1.getLocation().getX() - e1.getToDoList().peek().getX()) / e1.getV_x() - Math.abs(e2.getLocation().getX() - e2.getToDoList().peek().getX()) / e2.getV_x()) > 0 ? 1 : -1;
            }
        });
        for (Crane crane : craneList) {
            priority.offer(crane);
        }

        int crane_num = craneList.size();

        // 优先级 crane1 >= crane2 >= crane3
        Crane crane1 = priority.poll();
        Crane crane2 = priority.poll();
        Crane crane3 = priority.poll();

        boolean isTrack = false;
        if (track != null) {
            isTrack = true;
        }

        double usedTime = crane1.minTime();
        if (crane2 != null && crane2.isUsed()) {
            usedTime = Math.min(usedTime, crane2.minTime());
        }
        if (crane3 != null && crane3.isUsed()) {
            usedTime = Math.min(usedTime, crane3.minTime());
        }
        if (Math.abs(usedTime) < 1e-6) {
            usedTime = 1;
        }
        if (usedTime > maxTime) {
            nowTime += maxTime;
            usedTime = maxTime;
        } else {
            nowTime += usedTime;
        }

        if (isTrack) {
            track.getTimeList().add(nowTime);
        }
        Location start1 = crane1.getLocation().clone();
        Location terminal1 = crane1.getToDoList().peek().clone();
        Location destination1 = calDestination(start1, terminal1, crane1, usedTime).clone();

        Location start2 = null;
        Location destination2 = null;
        if (crane2 != null) {
            start2 = crane2.getLocation().clone();
            if (crane2.isUsed()) {
                Location terminal2 = crane2.getToDoList().peek().clone();
                destination2 = calDestination(start2, terminal2, crane2, usedTime).clone();
            }
        }
        Location start3 = null;
        Location destination3 = null;

        if (crane3 != null) {
            start3 = crane3.getLocation().clone();
            if (crane3.isUsed()) {
                Location terminal3 = crane3.getToDoList().peek().clone();
                destination3 = calDestination(start3, terminal3, crane3, usedTime).clone();
            }
        }

        handle(isPrint, crane1, destination1, track, isTrack);

        //检测crane1和crane2是否会发生碰撞
        if (crane2 != null) {
            if (!crane2.isUsed()) {
                destination2 = start2;
            }
            if ((crane2.getId().equals("crane1-1") && crane1.getId().equals("crane1-2")) ||
                    (crane2.getId().equals("crane1-2") && crane1.getId().equals("crane1-3"))) {
                if (destination2.getX() > crane1.getLocation().getX() - safeDistance) {
                    destination2.setX(crane1.getLocation().getX() - safeDistance);
                }
            } else if ((crane2.getId().equals("crane1-2") && crane1.getId().equals("crane1-1")) ||
                    (crane2.getId().equals("crane1-3") && crane1.getId().equals("crane1-2"))) {
                if (destination2.getX() < crane1.getLocation().getX() + safeDistance) {
                    destination2.setX(crane1.getLocation().getX() + safeDistance);
                }
            } else if (crane2.getId().equals("crane1-1") && crane1.getId().equals("crane1-3")) {
                if (crane_num == 3) {
                    if (destination2.getX() > crane1.getLocation().getX() - 2 * safeDistance) {
                        destination2.setX(crane1.getLocation().getX() - 2 * safeDistance);
                    }
                } else {
                    if (destination2.getX() > crane1.getLocation().getX() - safeDistance) {
                        destination2.setX(crane1.getLocation().getX() - safeDistance);
                    }
                }
            } else {
                if (crane_num == 3) {
                    if (destination2.getX() < crane1.getLocation().getX() + 2 * safeDistance) {
                        destination2.setX(crane1.getLocation().getX() + 2 * safeDistance);
                    }
                } else {
                    if (destination2.getX() < crane1.getLocation().getX() + safeDistance) {
                        destination2.setX(crane1.getLocation().getX() + safeDistance);
                    }
                }
            }
            handle(isPrint, crane2, destination2, track, isTrack);

        }
        // 最后处理crane3
        if (crane3 != null) {
            if (!crane3.isUsed()) {
                destination3 = start3;
            }
            if (crane3.getId().equals("crane1-1")) {
                Crane craneT = crane1.getId().equals("crane1-2") ? crane1 : crane2;
                if (destination3.getX() > craneT.getLocation().getX() - safeDistance) {
                    destination3.setX(craneT.getLocation().getX() - safeDistance);
                }
            } else if (crane3.getId().equals("crane1-2")) {
                Crane craneT1 = crane1.getId().equals("crane1-1") ? crane1 : crane2;
                Crane craneT3 = crane1.getId().equals("crane1-3") ? crane1 : crane2;
                if (destination3.getX() < craneT1.getLocation().getX() + safeDistance) {
                    destination3.setX(craneT1.getLocation().getX() + safeDistance);
                }
                if (destination3.getX() > craneT3.getLocation().getX() - safeDistance) {
                    destination3.setX(craneT3.getLocation().getX() - safeDistance);
                }
            } else {
                Crane craneT = crane1.getId().equals("crane1-2") ? crane1 : crane2;
                if (destination3.getX() < craneT.getLocation().getX() + safeDistance) {
                    destination3.setX(craneT.getLocation().getX() + safeDistance);
                }
            }
            handle(isPrint, crane3, destination3, track, isTrack);
        }

        if (isPrint) {
            System.out.println("nowTime:" + nowTime);
            System.out.println("============================");
        }

    }

    // 计算crane经过usedTime沿start到terminal到达的位置，无视碰撞
    private static Location calDestination(Location start, Location terminal, Crane crane, double usedTime) {
        double x = start.getX();
        x += sign(terminal.getX() - start.getX()) * usedTime * crane.getV_x();
        // x超过了terminal的距离
        if (!isBetween(terminal.getX(), x, start.getX())) {
            x = terminal.getX();
        }

        double y = start.getY();
        y += sign(terminal.getY() - start.getY()) * usedTime * crane.getV_y();
        // y超过了terminal的距离
        if (!isBetween(terminal.getY(), y, start.getY())) {
            y = terminal.getY();
        }

        double z = start.getZ();
        z += sign(terminal.getZ() - start.getZ()) * usedTime * crane.getV_z();
        if (!isBetween(terminal.getZ(), z, start.getZ())) {
            z = terminal.getZ();
        }
        return new Location(x, y, z);
    }


    private static boolean allocated(List<Crane> craneList, int next, HashMap<Integer, Task> taskMap, boolean isPrint, Track track) {
        if (next == -1) {
            return false;
        }
        Crane current_crane = selectCrane(craneList, next, taskMap, isPrint, track);
        if (current_crane != null) {
            current_crane.setUsed(true);
            Task cTask = taskMap.get(next);
            current_crane.addTask(cTask.getStart()).addTask(cTask.getEnd());
            current_crane.setTaskNo(next);
            current_crane.setType(cTask.getType());
            if (track != null) {
                Data.orderList.add(new Order(cTask.getId(), cTask.getType(), current_crane, cTask.getStart(), cTask.getEnd(), nowTime));
            }
            return true;
        }
        return false;
    }

    private static Crane selectCrane(List<Crane> craneList, int curTask, HashMap<Integer, Task> taskMap, boolean isPrint, Track track) {
        Location startPoint = taskMap.get(curTask).getStart();
        double startTime = taskMap.get(curTask).getStartTime();
        int taskType = taskMap.get(curTask).getType();
        List<Crane> allocatedList = new ArrayList<>();
        List<Crane> usedList = new ArrayList<>();

        // 填充allocatedList和usedList，并把距离任务起点最近的未使用AGC放在allocatedList的第一位
        for (Crane tempCrane : craneList) {
            if (!tempCrane.isUsed()) {
                if (tempCrane.getTypeList().contains(taskType)) {
                    allocatedList.add(tempCrane);
                }
            } else {
                usedList.add(tempCrane);
            }
        }
        allocatedList.sort((a, b) -> {
            double time_x1 = Math.abs(startPoint.getX() - a.getLocation().getX()) / a.getV_x();
            double time_y1 = Math.abs(startPoint.getY() - a.getLocation().getY()) / a.getV_y();
            double time_z1 = Math.abs(startPoint.getZ() - a.getLocation().getZ()) / a.getV_z();
            double time_a = Math.max(time_x1, Math.max(time_y1, time_z1));

            double time_x2 = Math.abs(startPoint.getX() - b.getLocation().getX()) / b.getV_x();
            double time_y2 = Math.abs(startPoint.getY() - b.getLocation().getY()) / b.getV_y();
            double time_z2 = Math.abs(startPoint.getZ() - b.getLocation().getZ()) / b.getV_z();
            double time_b = Math.max(time_x2, Math.max(time_y2, time_z2));
            if (time_a == time_b && time_x1 != time_x2) {
                return time_x1 - time_x2 > 0 ? 1 : -1;
            }
            return time_a - time_b == 0 ? 0 : time_a - time_b > 0 ? 1 : -1;

        });

        int usedNum = usedList.size();
        if (usedNum == 0) {
            return allocatedList.get(0);
        } else if (usedNum == 1 && !allocatedList.isEmpty()) { // 有一个AGC被使用
            while (startTime > nowTime) {
                double deltaTime = startTime - nowTime;
                run(craneList, deltaTime, isPrint, track);
            }
            Crane usedCrane = usedList.get(0);
            for (Crane last_crane : allocatedList) {
                if (!hasCollision(last_crane, usedCrane, startPoint)) {
                    return last_crane;
                }
            }
        } else if (usedNum == 2 && !allocatedList.isEmpty()) {
            Crane usedCrane1 = usedList.get(0);
            Crane usedCrane2 = usedList.get(1);
            Crane allocatedCrane = allocatedList.get(0);
            // 下一个任务还没开始
            while (startTime > nowTime) {
                double deltaTime = startTime - nowTime;
                run(craneList, deltaTime, isPrint, track); // 以最多间隔时间的长度运行一下各个AGC
            }
            if (!hasCollision(allocatedCrane, usedCrane1, startPoint) && !hasCollision(allocatedCrane, usedCrane2, startPoint)) {
                return allocatedCrane;
            }
        }

        return null;
    }

    // 检查待分配的Crane是否会和某一个已使用的Crane发生碰撞
    private static boolean hasCollision(Crane allocatedCrane, Crane usedCrane, Location startPoint) {
        if (isBetween(allocatedCrane.getLocation().getX(), usedCrane.getLocation().getX(), startPoint.getX())) {
            if ((usedCrane.getLocation().getX() - usedCrane.getToDoList().peek().getX()) * (allocatedCrane.getLocation().getX() - startPoint.getX()) < 0) {
                return true;
            }
        }
        return false;
    }

    private static int selectTask(ArrayList<Integer> priority) {
        if (priority.isEmpty()) {
            return -1;
        }
        Set<Integer> unUsedCraneType = new HashSet<>();
        for (Crane crane : craneList) {
            if (!crane.isUsed()) {
                unUsedCraneType.addAll(crane.getTypeList());
            }
        }
        double tempTime = nowTime;
        int res = -1;
        for (Integer p : priority) {
            if (unUsedCraneType.contains(taskMap.get(p).getType())) {
                res = p;
                break;
            }
        }
        if (res == -1) {
            return res;
        }

        if (taskMap.get(res).getStartTime() > tempTime) {
            tempTime = taskMap.get(res).getStartTime();
        }
        for (int i = 0; i < priority.size(); i++) {
            int p = priority.get(i);
            if (!unUsedCraneType.contains(taskMap.get(p).getType())) {
                continue;
            }
            if (taskMap.get(p).getStartTime() <= tempTime) {
                if (taskMap.get(p).getType() > taskMap.get(res).getType()) {
                    res = p;
                }
            }
        }

        return res;
    }

    public static void handle(boolean isPrint, Crane crane, Location destination, Track track, boolean isTrack) {
        if (isPrint) {
            System.out.print(crane.getId() + "(" + crane.getTaskNo() + ")" + ":" + crane.getLocation() + "->");
        }
        crane.setLocation(destination);
        if (isPrint) {
            System.out.println(crane.getLocation());
        }
        if (isTrack) {
            track.getPath().get(crane.getId()).add(destination.clone());
            track.getTaskNo().get(crane.getId()).add(crane.isUsed() ? crane.getTaskNo() : -1);
        }

        if (crane.isUsed() && crane.getToDoList().peek().equals(destination)) {
            if (Double.compare(destination.getZ(), startZ) == 0) {
                crane.getToDoList().poll();
            } else {
                crane.getToDoList().peek().setZ(startZ);
            }
            if (crane.getToDoList().isEmpty()) {
                crane.setUsed(false);
                taskMap.get(crane.getTaskNo()).setEndTime(nowTime);
            }
        }
    }

    private static int numOfUsedCrane(List<Crane> craneList) {
        int count = 0;
        for (Crane crane : craneList) {
            if (crane.isUsed()) {
                ++count;
            }
        }
        return count;
    }

    private static boolean isBetween(double x, double y, double z) {
        return (y - x) * (y - z) <= 0;
    }

    private static boolean isBetweenWithSafe(double x, double y, double z, Crane crane1, Crane crane2) {
        if (isBetween(x, y, z)) {
            return true;
        }
        int dis = safeDistance;
        if ((crane2.getId().equals("crane1-1") && crane1.getId().equals("crane1-3")) ||
                (crane2.getId().equals("crane1-3") && crane1.getId().equals("crane1-1"))) {
            dis = 2 * safeDistance;
        }
        return Math.abs(x - y) < dis || Math.abs(y - z) < dis;
    }

    private static int sign(double x) {
        if (Math.abs(x) < 1e-6)
            return 0;
        return x > 0 ? 1 : -1;
    }

}
