package com.sun.pojo;

import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Order {
    private String orderNo;
    private int type;
    private String crane;
    private String start;
    private String end;
    private String coilNo;
    private double startTime;
    private static int no=0;

    public Order(int orderNo,int type, Crane crane, Location start, Location end,double startTime) {
        this.type = type;
        this.crane = crane.getId();
        this.start = start.getLocationName();
        this.end = end.getLocationName();
        this.orderNo = "Order_"+orderNo;
        this.coilNo = new StringBuilder("Coil2020_").append(++no).toString();
        this.startTime = startTime;
    }

}
