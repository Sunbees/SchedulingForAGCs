package com.sun.controller;

import com.sun.algorithm.ACO;
import com.sun.algorithm.GeneticAlgorithm;
import com.sun.algorithm.SimulatedAnnealing;
import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.data.Data;
import com.sun.pojo.*;
import com.sun.util.Util;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class TestController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/query")
    public String query(Model model) throws IOException {
        List<String> list = new ArrayList<>();
        boolean isInitial = Data.stocks.size() == 0;
        String path = "./static/data/StoreLocation.csv";
        ArrayList<String[]> stockList = new ArrayList<>();
        Util.readCsv(path, stockList);
        stockList.forEach(e -> {
            list.add(e[0]);
            if (isInitial) {
                Stock stock = new Stock(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                Data.stocks.add(stock);
            }
        });
        //System.out.println(list);
        model.addAttribute("options", list);
        return "queryNew";
    }

    @PostMapping("/query")
    public String query(@RequestParam("algorithmType") Integer algorithmType, Tasks4Create tasks, Cranes cranes, Model model) throws IOException, CloneNotSupportedException {
        Data.initDataForDraw(tasks, cranes);

        Solution best = null;
        if (algorithmType == 0) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            sa.initSolution();
            best = sa.anneal();
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
        model.addAttribute("craneNum", Data.craneList.size());
        //new Collision().calRunningTime(best.getPriority(),true);
        Track track = new Collision().getTrack(best.getPriority());
        //track.getPath().keySet().forEach(System.out::println);
        //track.getPath().values().forEach(System.out::println);
        Util.writeCsvForPath(track.getTimeList(), track.getPath(), track.getTaskNo());
        Util.writeCsvForOrder();
        return "/2/path";
    }

    @PostMapping("/query_old")
    public String test(Cranes cranes, Tasks tasks, @RequestParam("algorithmType") Integer algorithmType, Model model) throws IOException, CloneNotSupportedException {
        //System.out.println(cranes);
        //System.out.println(tasks);
        Data.initData(cranes, tasks);
        Solution best = null;
        if (algorithmType == 0) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            sa.initSolution();
            best = sa.anneal();
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
        //new Collision().calRunningTime(best.getPriority(),true);
        Track track = new Collision().getTrack(best.getPriority());
        track.getPath().keySet().forEach(System.out::println);
        track.getPath().values().forEach(System.out::println);
        model.addAttribute("time", track.getTimeList());
        model.addAttribute("path", track.getPath());
        model.addAttribute("taskNo", track.getTaskNo());
        Util.writeCsvForPath(track.getTimeList(), track.getPath(), track.getTaskNo());
        //System.out.println(track);
        //for (List<Integer> value : track.getTaskNo().values()) {
        //    System.out.println(value.size());
        //}
        //for (List<Location> value : track.getPath().values()) {
        //    System.out.println(value.size());
        //}
        return "show";
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
        }
        else {
            return "error";
        }
        return "ok";
    }

    @GetMapping("/order")
    public String path() throws IOException {
        return "showOrder";
    }

}
