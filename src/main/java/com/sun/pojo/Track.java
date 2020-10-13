package com.sun.pojo;

import com.sun.collision_dec.Location;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Track {
    private List<Double> timeList;
    private Map<String, List<Location>> path;
    private Map<String,List<Integer>> taskNo;
    public Track() {
        this.timeList = new ArrayList<>();
        this.path = new HashMap<>();
        this.taskNo = new HashMap<>();
    }

}
