package com.sun.controller;

import com.sun.algorithm.ACO;
import com.sun.algorithm.GeneticAlgorithm;
import com.sun.algorithm.SimulatedAnnealing;
import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.data.Data;
import com.sun.pojo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String query() {
        return "query";
    }

    @PostMapping("/query")
    public String test(Cranes cranes, Tasks tasks, @RequestParam("algorithmType") Integer algorithmType, Model model) {
        //System.out.println(cranes);
        //System.out.println(tasks);
        Data.initData(cranes,tasks);
        Solution best = null;
        if (algorithmType == 0) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            sa.initSolution();
            best = sa.anneal();
            model.addAttribute("algorithm","模拟退火");
        } else if (algorithmType == 1) {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            Population population = new Population(20);
            best = ga.run(population);
            model.addAttribute("algorithm","遗传算法");
        } else if (algorithmType == 2) {
            ACO aco = new ACO();
            aco.init();
            best = aco.solve();
            model.addAttribute("algorithm","蚁群算法");
        }
        System.out.println(best);
        model.addAttribute("property",best.getPriority());
        model.addAttribute("consumeTime",best.getConsumeTime());
        //new Collision().calRunningTime(best.getPriority(),true);
        Track track = new Collision().getTrack(best.getPriority());
        model.addAttribute("time",track.getTimeList());
        model.addAttribute("path",track.getPath());
        model.addAttribute("taskNo",track.getTaskNo());
        //System.out.println(track);
        //for (List<Integer> value : track.getTaskNo().values()) {
        //    System.out.println(value.size());
        //}
        //for (List<Location> value : track.getPath().values()) {
        //    System.out.println(value.size());
        //}
        return "show";
    }
}
