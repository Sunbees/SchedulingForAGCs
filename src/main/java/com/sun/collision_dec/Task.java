package com.sun.collision_dec;


public class Task {
    private int id;
    private double startTime;
    private double endTime;
    private Location start;
    private Location end;
    private int type;

    public Task(int id, double startTime, Location start, Location end, int type) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = 0.0f;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", start=" + start +
                ", end=" + end +
                ", type=" + type +
                '}';
    }
}
