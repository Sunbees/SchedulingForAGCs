package com.sun.collision_dec;


import com.sun.data.Data;
import com.sun.pojo.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class Collision {
    public static double nowTime;
    public static int safeDistance = 2;
    // 存储所有的待分配任务
    public static HashMap<Integer, Task> taskMap;
    public static ArrayList<Integer> priority;
    public static List<Crane> craneList;

    public static void init(List<Integer> pri) {
        nowTime = 0.0;
        //Data.initTest();
        Data.init();
        // craneList存储所有的AGC信息
        craneList = Data.craneList;

        taskMap = Data.taskMap;

        // priority列表存储每个任务的处理优先级，越靠前优先级越高，已分配AGC的任务会从中删除
        priority = new ArrayList<>(pri);
        //priority.add(0);
        //priority.add(1);
        //priority.add(2);
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
                if (nowTime < taskMap.get(current).getStartTime()) {
                    nowTime = taskMap.get(current).getStartTime();
                }
                Crane current_crane = selectCrane(craneList, current, taskMap, false, track);
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
            run(craneList, 999999, false, track);
        }

        return track;
    }

    public static void main(String[] args) {
        List<Integer> pri = new ArrayList<>();
        pri.add(1);
        pri.add(2);
        pri.add(3);
        pri.add(0);
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

    public double calRunningTime(List<Integer> pri, boolean isPrint) {
        init(pri);

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
        Location start1 = crane1.getLocation();
        Location terminal1 = crane1.getToDoList().peek();
        Location destination1 = calDestination(start1, terminal1, crane1, usedTime);

        Location start2 = crane2.getLocation();
        Location destination2 = null;
        if (crane2.isUsed()) {
            Location terminal2 = crane2.getToDoList().peek();
            destination2 = calDestination(start2, terminal2, crane2, usedTime);
        }
        Location start3 = null;
        if(crane3 != null) {
            start3 = crane3.getLocation();
        }
        Location destination3 = null;
        if (crane3 != null &&crane3.isUsed()) {
            Location terminal3 = crane3.getToDoList().peek();
            destination3 = calDestination(start3, terminal3, crane3, usedTime);
        }

        // 处理crane1
        if (isPrint) {
            System.out.print(crane1.getId() + "(" + crane1.getTaskNo() + ")" + ":" + crane1.getLocation() + "->");

        }
        crane1.setLocation(destination1);
        if (isPrint) {
            System.out.println(crane1.getLocation());
        }
        if (isTrack) {
            track.getPath().get(crane1.getId()).add(destination1.clone());
            track.getTaskNo().get(crane1.getId()).add(crane1.getTaskNo());
        }

        if (crane1.getToDoList().peek().equals(destination1)) {
            if (destination1.getZ() == 20) {
                crane1.getToDoList().poll();
            } else {
                crane1.getToDoList().peek().setZ(20);
            }
            if (crane1.getToDoList().isEmpty()) {
                crane1.setUsed(false);
                taskMap.get(crane1.getTaskNo()).setEndTime(nowTime);
            }
        }
        // 检测crane1和crane2是否会发生碰撞
        if (crane2.isUsed()) {
            if (sign(destination1.getX() - start1.getX()) == 0) {
                if (isBetweenWithSafe(start2.getX(), start1.getX(), destination2.getX(), crane1, crane2)) {
                    if ((crane2.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                            (crane2.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                        destination2.setX(destination1.getX() + 2 * safeDistance * sign(start2.getX() - destination1.getX()));
                    } else {
                        destination2.setX(destination1.getX() + 1 * safeDistance * sign(start2.getX() - destination1.getX()));
                    }
                }
            } else if (((start1.getX() - destination1.getX()) * (start2.getX() - destination2.getX())) <= 0 && (isBetweenWithSafe(start1.getX(), destination2.getX(), destination1.getX(), crane1, crane2) || isBetweenWithSafe(start1.getX(), start2.getX(), destination1.getX(), crane1, crane2))) {
                if ((crane2.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                        (crane2.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                    if (sign(destination1.getX() - start1.getX()) != 0)
                        destination2.setX(destination1.getX() + 2 * safeDistance * sign(destination1.getX() - start1.getX()));
                } else {
                    if (sign(destination1.getX() - start1.getX()) != 0)
                        destination2.setX(destination1.getX() + 1 * safeDistance * sign(destination1.getX() - start1.getX()));
                }
            }
            if (isPrint) {
                System.out.print(crane2.getId() + "(" + crane2.getTaskNo() + ")" + ":" + crane2.getLocation() + "->");
            }
            crane2.setLocation(destination2);
            if (isPrint) {
                System.out.println(crane2.getLocation());
            }
            if (isTrack) {
                track.getPath().get(crane2.getId()).add(destination2.clone());
                track.getTaskNo().get(crane2.getId()).add(crane2.getTaskNo());
            }
            if (crane2.getToDoList().peek().equals(destination2)) {
                if (destination2.getZ() == 20) {
                    crane2.getToDoList().poll();
                } else {
                    crane2.getToDoList().peek().setZ(20);
                }
                if (crane2.getToDoList().isEmpty()) {
                    crane2.setUsed(false);
                    taskMap.get(crane2.getTaskNo()).setEndTime(nowTime);
                }
            }
        } else if (Math.abs(start1.getX() - destination1.getX()) >= 1e-3 && isBetweenWithSafe(start1.getX(), crane2.getLocation().getX(), destination1.getX(), crane1, crane2)) {
            if (isPrint) {
                System.out.print(crane2.getId() + ": " + crane2.getLocation() + "->");
            }
            if ((crane2.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                    (crane2.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                crane2.getLocation().setX(destination1.getX() + 2 * safeDistance * sign(destination1.getX() - start1.getX())); // 在两端的AGC发生碰撞时，考虑到中间的AGC，所以要避让两倍的安全距离
                if (isPrint) {
                    System.out.println(crane2.getLocation());
                }
                if (isTrack) {
                    track.getPath().get(crane2.getId()).add(crane2.getLocation().clone());
                    track.getTaskNo().get(crane2.getId()).add(-1);
                }
            } else {
                crane2.getLocation().setX(destination1.getX() + 1 * safeDistance * sign(destination1.getX() - start1.getX())); // 在两端的AGC发生碰撞时，考虑到中间的AGC，所以要避让两倍的安全距离
                if (isPrint) {
                    System.out.println(crane2.getLocation());
                }
                if (isTrack) {
                    track.getPath().get(crane2.getId()).add(crane2.getLocation().clone());
                    track.getTaskNo().get(crane2.getId()).add(-1);
                }
            }
        } else {
            if (isTrack) {
                track.getPath().get(crane2.getId()).add(start2.clone());
                track.getTaskNo().get(crane2.getId()).add(-1);
            }
        }
        // 先处理crane3
        if (crane3 != null &&crane3.isUsed()) {
            // 检测crane3和crane2是否会发生碰撞
            if (crane2.isUsed() && sign(destination2.getX() - start2.getX()) == 0) {
                if (isBetweenWithSafe(start3.getX(), start2.getX(), destination3.getX(), crane2, crane3)) {
                    if ((crane3.getId().equals("AGC-left") && crane2.getId().equals("AGC-right")) ||
                            (crane3.getId().equals("AGC-right") && crane2.getId().equals("AGC-left"))) {
                        destination3.setX(destination2.getX() + 2 * safeDistance * sign(start3.getX() - destination2.getX()));
                    } else {
                        destination3.setX(destination2.getX() + 1 * safeDistance * sign(start3.getX() - destination2.getX()));
                    }
                }

            } else if (crane2.isUsed() && ((start2.getX() - destination2.getX()) * (start3.getX() - destination3.getX())) <= 0 && (isBetweenWithSafe(start2.getX(), destination3.getX(), destination2.getX(), crane2, crane3) || isBetweenWithSafe(start2.getX(), start3.getX(), destination2.getX(), crane2, crane3))) {
                if ((crane3.getId().equals("AGC-left") && crane2.getId().equals("AGC-right")) ||
                        (crane3.getId().equals("AGC-right") && crane2.getId().equals("AGC-left"))) {
                    if (sign(destination2.getX() - start2.getX()) != 0)
                        destination3.setX(destination2.getX() + 2 * safeDistance * sign(destination2.getX() - start2.getX()));
                } else {
                    if (sign(destination2.getX() - start2.getX()) != 0)
                        destination3.setX(destination2.getX() + safeDistance * sign(destination2.getX() - start2.getX()));
                }
            }
            // 检测crane3和crane1是否会发生碰撞
            if (sign(destination1.getX() - start1.getX()) == 0) {
                if (isBetweenWithSafe(start3.getX(), start1.getX(), destination3.getX(), crane1, crane3)) {
                    if ((crane3.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                            (crane3.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                        destination3.setX(destination1.getX() + 2 * safeDistance * sign(start3.getX() - destination1.getX()));
                    } else {
                        destination3.setX(destination1.getX() + 1 * safeDistance * sign(start3.getX() - destination1.getX()));
                    }
                }
            } else if ((start1.getX() - destination1.getX()) * (start3.getX() - destination3.getX()) <= 0 && (isBetweenWithSafe(start1.getX(), destination3.getX(), destination1.getX(), crane1, crane3) || isBetweenWithSafe(start1.getX(), start3.getX(), destination1.getX(), crane1, crane3))) {
                if ((crane3.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                        (crane3.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                    if (sign(destination1.getX() - start1.getX()) != 0)
                        destination3.setX(destination1.getX() + 2 * safeDistance * sign(destination1.getX() - start1.getX()));
                } else {
                    if (sign(destination1.getX() - start1.getX()) != 0)
                        destination3.setX(destination1.getX() + 1 * safeDistance * sign(destination1.getX() - start1.getX()));
                }
            }
            if (isPrint) {
                System.out.print(crane3.getId() + "(" + crane3.getTaskNo() + ")" + ":" + crane3.getLocation() + "->");
            }
            crane3.setLocation(destination3);
            if (isPrint) {
                System.out.println(crane3.getLocation());
            }
            if (isTrack) {
                track.getPath().get(crane3.getId()).add(destination3.clone());
                track.getTaskNo().get(crane3.getId()).add(crane3.getTaskNo());
            }
            if (crane3.getToDoList().peek().equals(destination3)) {
                if (destination3.getZ() == 20) {
                    crane3.getToDoList().poll();
                } else {
                    crane3.getToDoList().peek().setZ(20);
                }
                if (crane3.getToDoList().isEmpty()) {
                    crane3.setUsed(false);
                    taskMap.get(crane3.getTaskNo()).setEndTime(nowTime);
                }
            }
        } else if (crane3 != null &&Math.abs(start1.getX() - destination1.getX()) >= 1e-3 && isBetweenWithSafe(start1.getX(), crane3.getLocation().getX(), destination1.getX(), crane1, crane3)) {
            if (isPrint) {
                System.out.print(crane3.getId() + ": " + crane3.getLocation() + "->");
            }
            if ((crane3.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                    (crane3.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
                crane3.getLocation().setX(destination1.getX() + 2 * safeDistance * sign(destination1.getX() - start1.getX()));
                if (isPrint) {
                    System.out.println(crane3.getLocation());
                }
                if (isTrack) {
                    track.getPath().get(crane3.getId()).add(crane3.getLocation().clone());
                    track.getTaskNo().get(crane3.getId()).add(-1);
                }
            } else if(crane3 != null){
                crane3.getLocation().setX(destination1.getX() + 1 * safeDistance * sign(destination1.getX() - start1.getX()));
                if (isPrint) {
                    System.out.println(crane3.getLocation());
                }
                if (isTrack) {
                    track.getPath().get(crane3.getId()).add(crane3.getLocation().clone());
                    track.getTaskNo().get(crane3.getId()).add(-1);
                }
            }
        } else if (crane3 != null &&crane2.isUsed() && Math.abs(start2.getX() - destination2.getX()) >= 1e-3 && isBetweenWithSafe(start2.getX(), crane3.getLocation().getX(), destination2.getX(), crane2, crane3)) { // 若已经与crane1之间有了冲突，不用考虑更低优先级的crane2
            //if (isPrint) {
            //    System.out.print(crane3.getId() + ": " + crane3.getLocation() + "->");
            //}
            if ((crane3.getId().equals("AGC-left") && crane2.getId().equals("AGC-right")) ||
                    (crane3.getId().equals("AGC-right") && crane2.getId().equals("AGC-left"))) {
                //crane3.getLocation().setX(destination2.getX() + 2 * safeDistance * sign(destination2.getX() - start2.getX()));
                //if (isPrint) {
                //    System.out.println(crane3.getLocation());
                //}
                if (isTrack) {
                    track.getPath().get(crane3.getId()).add(crane3.getLocation().clone());
                    track.getTaskNo().get(crane3.getId()).add(-1);
                }
            } else {
                if (isPrint) {
                    System.out.print(crane3.getId() + ": " + crane3.getLocation() + "->");
                }
                crane3.getLocation().setX(destination2.getX() + 1 * safeDistance * sign(destination2.getX() - start2.getX()));
                if (isPrint) {
                    System.out.println(crane3.getLocation());
                }
                if (isTrack) {
                    track.getPath().get(crane3.getId()).add(crane3.getLocation().clone());
                    track.getTaskNo().get(crane3.getId()).add(-1);
                }
            }
        } else if(crane3 != null){
            if (isTrack) {
                track.getPath().get(crane3.getId()).add(start3.clone());
                track.getTaskNo().get(crane3.getId()).add(-1);
            }
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
        Crane current_crane = selectCrane(craneList, next, taskMap, isPrint, track);
        if (current_crane != null) {
            current_crane.setUsed(true);
            current_crane.addTask(taskMap.get(next).getStart()).addTask(taskMap.get(next).getEnd());
            current_crane.setTaskNo(next);
            current_crane.setType(taskMap.get(next).getType());
            return true;
        }
        return false;
    }

    private static Crane selectCrane(List<Crane> craneList, int curTask, HashMap<Integer, Task> taskMap, boolean isPrint, Track track) {
        Location startPoint = taskMap.get(curTask).getStart();
        double startTime = taskMap.get(curTask).getStartTime();
        int taskType = taskMap.get(curTask).getType();
        double minTime = Integer.MAX_VALUE;
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
            for (int i = 0; i < allocatedList.size(); i++) {
                Crane last_crane = allocatedList.get(i);
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
        double tempTime = nowTime;
        int res = priority.get(0);
        if (taskMap.get(res).getStartTime() > tempTime) {
            tempTime = taskMap.get(res).getStartTime();
        }
        for (int i = 1; i < priority.size(); i++) {
            int p = priority.get(i);
            if (taskMap.get(p).getStartTime() <= tempTime) {
                if (taskMap.get(p).getType() > taskMap.get(res).getType()) {
                    res = p;
                }
            }
        }

        return res;
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
        if ((crane2.getId().equals("AGC-left") && crane1.getId().equals("AGC-right")) ||
                (crane2.getId().equals("AGC-right") && crane1.getId().equals("AGC-left"))) {
            if (x < z) {
                //x -= safeDistance;
                z += 2 * safeDistance;
            } else if (x > z) {
                //x += safeDistance;
                z -= 2 * safeDistance;
            } else {
                x += 2 * safeDistance;
                z -= 2 * safeDistance;
            }
        } else {
            if (x < z) {
                //x -= safeDistance;
                z += safeDistance;
            } else if (x > z) {
                //x += safeDistance;
                z -= safeDistance;
            } else {
                x += 2 * safeDistance;
                z -= 2 * safeDistance;
            }
        }

        return (y - x) * (y - z) < 0;
    }

    private static int sign(double x) {
        if (Math.abs(x) < 1e-6)
            return 0;
        return x > 0 ? 1 : -1;
    }

}
