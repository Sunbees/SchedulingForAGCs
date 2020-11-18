package com.sun.collision_dec;

import java.util.Objects;

public class Location {
    // 三个坐标
    private double x;
    private double y;
    private double z;

    protected Location clone() {
        return new Location(this.x, this.y, this.z);
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getLocationName() {
        return "[" + this.getX() + "-" + this.getY() + "-" + this.getZ() + "]";
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ')';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.x, x) == 0 &&
                Double.compare(location.y, y) == 0 &&
                Double.compare(location.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
