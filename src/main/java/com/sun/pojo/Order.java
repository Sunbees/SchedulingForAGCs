package com.sun.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Order {
    public String orderNo;
    public int type;
    public String crane;
    public int flag;
    public double[] start;
    public double[] end;
    public String coilNo;
    public double startTime;
    private static int no = 0;

    public Order(int orderNo, int type, Crane crane, Location start, Location end, double startTime) {
        this.type = type;
        this.crane = crane.getId();
        this.start = new double[]{start.getX(), start.getY(), start.getZ()};
        this.end = new double[]{end.getX(), end.getY(), end.getZ()};
        this.orderNo = "Order_" + orderNo;
        this.coilNo = new StringBuilder("Coil2020_").append(++no).toString();
        this.startTime = startTime;
    }

    public static void setNo(int no) {
        Order.no = no;
    }


}
