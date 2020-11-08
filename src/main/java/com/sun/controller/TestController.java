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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (Data.stocks.size() == 0) {
            String path = "./static/data/StoreLocation.csv";
            ArrayList<String[]> stockList = new ArrayList<>();
            Util.readCsv(path, stockList);
            stockList.forEach(e -> {
                Stock stock = new Stock(e[0], e[1], Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[4]), Double.parseDouble(e[5]));
                Data.stocks.add(stock);
                list.add(e[0]);
            });
        }
        //System.out.println(list);
        model.addAttribute("options", list);
        return "queryNew";
    }

    @PostMapping("/query")
    public String query(@RequestParam("algorithmType") Integer algorithmType, Tasks4Create tasks, Cranes cranes, Model model) throws IOException, CloneNotSupportedException {

        //System.out.println(algorithmType);
        //for (Task4Create task : tasks.getTasks()) {
        //    System.out.println(task.getType());
        //    System.out.println(task.getNum());
        //    System.out.println(task.getBegin());
        //    System.out.println(task.getEnd());
        //}
        //for (Crane2 crane : cranes.getCranes()) {
        //    System.out.println(crane);
        //}

        Data.initDataForDraw(tasks,cranes);

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
        model.addAttribute("craneNum",Data.craneList.size());
        //new Collision().calRunningTime(best.getPriority(),true);
        Track track = new Collision().getTrack(best.getPriority());
        //track.getPath().keySet().forEach(System.out::println);
        //track.getPath().values().forEach(System.out::println);
        Util.writeCsvForPath(track.getTimeList(), track.getPath(), track.getTaskNo());

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


    @GetMapping("/path")
    public String path() throws IOException {
        //String file = "./static/data/StoreLocation.csv";
        //String[] names = new String[]{"AREA_Z12_E1",
        //        "AREA_Z12_E2",
        //        "AREA_Z12_D1",
        //        "AREA_Z12_D2",
        //        "AREA_Z12_C1",
        //        "AREA_Z12_C2",
        //        "AREA_Z12_B1",
        //        "AREA_Z12_B2",
        //        "AREA_Z12_A1"};
        //String[] types = new String[]{
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard",
        //        "yard"
        //};
        //String[] x = new String[]{
        //        "214.399",
        //        "214.399",
        //        "165.000",
        //        "165.000",
        //        "120.800",
        //        "120.800",
        //        "79.899",
        //        "79.899",
        //        "50.000"
        //};
        //String[] y = new String[]{
        //        "18.622",
        //        "35.800",
        //        "18.622",
        //        "32.900",
        //        "18.622",
        //        "32.900",
        //        "18.622",
        //        "32.900",
        //        "20.800"
        //};
        //String[] widths = new String[]{
        //        "48.601",
        //        "48.601",
        //        "37.501",
        //        "37.501",
        //        "46.199",
        //        "46.199",
        //        "30.601",
        //        "30.601",
        //        "27.199"
        //};
        //String[] heights = new String[]{
        //        "16.622",
        //        "17.178",
        //        "14.222",
        //        "14.278",
        //        "14.222",
        //        "14.278",
        //        "16.622",
        //        "14.278",
        //        "4.3"
        //};
        //Util.writeCsvForLocation(file, names, types, x, y, widths, heights);
        //ArrayList<String[]> list = new ArrayList<>();
        //Util.readCsv(file, list);
        //list.forEach(num -> {
        //    for (String s : num) {
        //        System.out.print(s + " ");
        //    }
        //    System.out.println();
        //});


        return "/2/path";
    }

}
