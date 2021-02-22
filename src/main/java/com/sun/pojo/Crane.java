package com.sun.pojo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Crane {
    private Location location;
    private double v_x; // 大车运行速度，暂时空载重载一致
    private double v_y; // 小车运行速度，暂时空载重载一致
    private double v_z; // 吊具起落速度，暂时上升起落一致

    // 加速度定义

    private String id;
    private boolean isUsed;
    private int type = 0; //当前AGC执行任务的类型,2:吊机组、1:出入库、0:倒垛
    private List<Integer> typeList; // 当前AGC可执行的任务类型，同上
    private Queue<Location> toDoList;
    private int taskNo;

    public Crane(String id, Location location, boolean isUsed, double v_x, double v_y, double v_z) {
        this.location = location;
        this.v_x = v_x;
        this.v_y = v_y;
        this.v_z = v_z;
        this.id = id;
        this.isUsed = isUsed;
        this.typeList = new ArrayList<>();
        this.toDoList = new LinkedList<>();
    }

    public Crane addType(int type) {
        this.typeList.add(type);
        return this;
    }

    public Crane addTask(Location location) {
        this.toDoList.offer(location);
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getV_x() {
        return v_x;
    }

    public void setV_x(double v_x) {
        this.v_x = v_x;
    }

    public double getV_y() {
        return v_y;
    }

    public void setV_y(double v_y) {
        this.v_y = v_y;
    }

    public double getV_z() {
        return v_z;
    }

    public void setV_z(double v_z) {
        this.v_z = v_z;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Integer> typeList) {
        this.typeList = typeList;
    }

    public Queue<Location> getToDoList() {
        return toDoList;
    }

    public void setToDoList(Queue<Location> toDoList) {
        this.toDoList = toDoList;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(int taskNo) {
        this.taskNo = taskNo;
    }

    public double minTime() {
        double time_x = Math.abs(this.location.getX() - this.toDoList.peek().getX()) / this.v_x;
        double time_y = Math.abs(this.location.getY() - this.toDoList.peek().getY()) / this.v_y;
        double time_z = Math.abs(this.location.getZ() - this.toDoList.peek().getZ()) / this.v_z;
        double minTime = Integer.MAX_VALUE;
        if (time_x != 0) {
            minTime = Math.min(minTime, time_x);
        }
        if (time_y != 0) {
            minTime = Math.min(minTime, time_y);
        }
        if (time_z != 0) {
            minTime = Math.min(minTime, time_z);
        }
        return Math.round(minTime * 100) / 100;
    }

    @Override
    public String toString() {
        return "Crane{" +
                "location=" + location +
                ", v_x=" + v_x +
                ", v_y=" + v_y +
                ", v_z=" + v_z +
                ", id='" + id + '\'' +
                ", isUsed=" + isUsed +
                ", type=" + type +
                ", typeList=" + typeList +
                ", toDoList=" + toDoList +
                ", taskNo=" + taskNo +
                '}';
    }
}
