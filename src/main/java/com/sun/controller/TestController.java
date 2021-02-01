package com.sun.controller;

import com.sun.algorithm.ACO;
import com.sun.algorithm.GeneticAlgorithm;
import com.sun.algorithm.SimulatedAnnealing;
import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Crane;
import com.sun.data.Data;
import com.sun.pojo.*;
import com.sun.util.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class TestController {
    @RequestMapping("/hello")
    public String hello(Model model) throws IOException {
        return "hello";
    }

    @GetMapping("/query")
    public String query(Model model) throws IOException {
        List<String> list = new ArrayList<>();
        boolean isInitial = Data.stockAreas.size() == 0;
        String path = "./static/data/StoreLocation.csv";
        ArrayList<String[]> stockList = new ArrayList<>();
        Util.readCsv(path, stockList);
        stockList.forEach(e -> {
            list.add(e[0]);
            if (isInitial) {
                StockArea stockArea = new StockArea(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                Data.stockAreas.add(stockArea);
            }
        });
        List<String> locations = new ArrayList<>();
        locations.add(arrayToStr(Data.location_1));
        locations.add(arrayToStr(Data.location_2));
        locations.add(arrayToStr(Data.location_3));
        List<double[]> velocities = new ArrayList<>();
        velocities.add(Data.velocity_1);
        velocities.add(Data.velocity_2);
        velocities.add(Data.velocity_3);
        //System.out.println(list);
        model.addAttribute("options", list);
        model.addAttribute("safeDis", Data.SafeDistance);
        model.addAttribute("locations", locations);
        model.addAttribute("velocities", velocities);
        return "queryProduct";
    }

    private String arrayToStr(double[] arr) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i < arr.length - 1) {
                builder.append(", ");
            } else {
                builder.append(")");
            }
        }
        return builder.toString();
    }

    @GetMapping("/change")
    public String query() {
        return "change";
    }

    @PostMapping("/change")
    @ResponseBody
    public String query(@RequestBody craneLV cranelv) {
        //System.out.println(cranelv.distance);
        if (cranelv.distance > 1e-6) {
            Data.SafeDistance = cranelv.distance;
        }
        for (int i = 0; i < 3; i++) {
            String location = cranelv.location.get(i);
            String velocity = cranelv.velocity.get(i);
            double[] dLocation = i == 0 ? Data.location_1 : i == 1 ? Data.location_2 : Data.location_3;
            double[] dVelocity = i == 0 ? Data.velocity_1 : i == 1 ? Data.velocity_2 : Data.velocity_3;

            if (location != null && location.trim().length() > 0) {
                double[] l = Arrays.asList(location.split(",")).stream().mapToDouble(Double::parseDouble).toArray();
                if (l.length == 3) {
                    dLocation[0] = l[0];
                    dLocation[1] = l[1];
                    dLocation[2] = l[2];
                }
            }
            if (velocity != null && velocity.trim().length() > 0) {
                double[] v = Arrays.asList(velocity.split(",")).stream().mapToDouble(Double::parseDouble).toArray();
                if (v.length == 3) {
                    dVelocity[0] = v[0];
                    dVelocity[1] = v[1];
                    dVelocity[2] = v[2];
                }
            }
        }
        return "hello";
    }

    @PostMapping("/query")
    public String query(@RequestParam("algorithmType") Integer algorithmType, Tasks4Create tasks, Cranes cranes, Model model) throws IOException, CloneNotSupportedException {
        Data.initDataForDraw(tasks, cranes);

        Solution best = null;
        if (algorithmType == 0) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            sa.initSolution();
            best = sa.anneal(null);
            model.addAttribute("algorithm", "模拟退火");
        } else if (algorithmType == 1) {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            Population population = new Population(20);
            best = ga.run(population);
            model.addAttribute("algorithm", "遗传算法");
        } else if (algorithmType == 2) {
            ACO aco = new ACO();
            aco.init();
            best = aco.solve();
            model.addAttribute("algorithm", "蚁群算法");
        }
        System.out.println(best);
        model.addAttribute("property", best.getPriority());
        model.addAttribute("consumeTime", best.getConsumeTime());
        StringBuilder sb = new StringBuilder();
        for (Crane crane : Data.craneList) {
            if (crane.getId().endsWith("1")) {
                sb.append("1");
            } else if (crane.getId().endsWith("2")) {
                sb.append("2");
            } else {
                sb.append("3");
            }
        }
        model.addAttribute("craneNum", sb.toString());
        //new Collision().calRunningTime(best.getPriority(),true);
        Track track = new Collision().getTrack(best.getPriority());
        //track.getPath().keySet().forEach(System.out::println);
        //track.getPath().values().forEach(System.out::println);
        Util.writeCsvForPath(track.getTimeList(), track.getPath(), track.getTaskNo());
        Util.writeCsvForOrder();
        return "/2/path";
    }
    @GetMapping("/set/{what}/{id}/{a}/{b}/{c}")
    @ResponseBody
    public String set(@PathVariable String what, @PathVariable int id, @PathVariable double a, @PathVariable double b, @PathVariable double c) {
        if (what.startsWith("v")) {
            double[] velocity = id == 1 ? Data.velocity_1 : id == 2 ? Data.velocity_2 : Data.velocity_3;
            velocity[0] = a;
            velocity[1] = b;
            velocity[2] = c;
        } else if (what.startsWith("l")) {
            double[] location = id == 1 ? Data.location_1 : id == 2 ? Data.location_2 : Data.location_3;
            location[0] = a;
            location[1] = b;
            location[2] = c;
        } else {
            return "error";
        }
        return "ok";
    }

    @GetMapping("/order")
    public String path() throws IOException {
        return "showOrder";
    }

}
