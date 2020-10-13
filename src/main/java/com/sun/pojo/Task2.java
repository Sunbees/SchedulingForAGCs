package com.sun.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task2 {
    private double startTime;
    private String startLocation;
    private String endLocation;
    private int type;
}
