package com.sun.controller;

import com.sun.init.Data;
import com.sun.pojo.Population;
import com.sun.pojo.TaskCreate;
import com.sun.schedule.ACO;
import com.sun.schedule.GeneticAlgorithm;
import com.sun.schedule.SimulatedAnnealing;
import com.sun.solution.Collision;
import com.sun.solution.Solution;
import com.sun.solution.Track;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import static com.sun.init.Data.updateCraneList;
import static com.sun.util.Util.*;

@RequestMapping
@RestController
@CrossOrigin
public class QueryController {
    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, Object> data) throws IOException, CloneNotSupportedException {
        //for (String s : data.keySet()) {
        //    System.out.println(s + ":" + data.get(s));
        //}
        List<Integer> crane1Type = (List<Integer>) data.get("crane1");
        List<Integer> crane2Type = (List<Integer>) data.get("crane2");
        List<Integer> crane3Type = (List<Integer>) data.get("crane3");
        int algorithm = (int) data.get("algorithm");
        List<TaskCreate> tasks = new ArrayList<>();
        Map<String, Object> task1 = (Map<String, Object>) data.get("task1");
        TaskCreate taskCreate1 = new TaskCreate((int) task1.get("num"), 0, (List<Integer>) task1.get("beginType"), (List<Integer>) task1.get("endType"));
        Map<String, Object> task2 = (Map<String, Object>) data.get("task2");
        TaskCreate taskCreate2 = new TaskCreate((int) task2.get("num"), 1, (List<Integer>) task2.get("beginType"), (List<Integer>) task2.get("endType"));
        Map<String, Object> task3 = (Map<String, Object>) data.get("task3");
        TaskCreate taskCreate3 = new TaskCreate((int) task3.get("num"), 2, (List<Integer>) task3.get("beginType"), (List<Integer>) task3.get("endType"));
        tasks.add(taskCreate1);
        tasks.add(taskCreate2);
        tasks.add(taskCreate3);

        Data.initData(crane1Type, crane2Type, crane3Type, tasks);
        Solution best = null;
        if (algorithm == 0) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            sa.initSolution();
            best = sa.anneal(null, 10000, 0.1, 0.1, 200);
        } else if (algorithm == 1) {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            Population population = new Population(20);
            best = ga.run(population);
        } else if (algorithm == 2) {
            ACO aco = new ACO();
            aco.init();
            best = aco.solve();
        }
        assert best != null;
        Track track = new Collision().getTrack(best.getPriority());

        writeCsvForOrder();
        writeCsvForPath(track.getTimeList(), track.getPath(), track.getTaskNo());

        //System.out.println(Data.SafeDistance);

        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("time", best.getConsumeTime());
        return map;
    }

    @GetMapping("/craneInfo")
    public Map<String, Object> getCraneInfo() {
        return getCraneConfigInfo();
    }

    @PostMapping("/craneInfo")
    public Map<String, Object> updateCraneInfo(@RequestBody Map<String, Object> data) {
        //for (String s : data.keySet()) {
        //    System.out.println(s + ":" + data.get(s));
        //}

        int distance = Integer.parseInt(data.get("distance").toString());

        List<double[]> crane1Info = new ArrayList<>();
        List<double[]> crane2Info = new ArrayList<>();
        List<double[]> crane3Info = new ArrayList<>();


        List<Map<String, Object>> craneInfoList = (ArrayList) data.get("craneList");
        for (Map<String, Object> craneInfo : craneInfoList) {
            double[] location = Arrays.stream(((String) craneInfo.get("begin")).split(",")).mapToDouble(Double::parseDouble).toArray();
            double[] velocity = new double[]{Double.parseDouble(craneInfo.get("v1").toString()), Double.parseDouble(craneInfo.get("v2").toString()), Double.parseDouble(craneInfo.get("v3").toString())};
            if (craneInfo.get("craneName").equals("crane1")) {
                crane1Info.add(location);
                crane1Info.add(velocity);
            } else if (craneInfo.get("craneName").equals("crane2")) {
                crane2Info.add(location);
                crane2Info.add(velocity);
            } else {
                crane3Info.add(location);
                crane3Info.add(velocity);
            }
        }
        updateCraneList(distance, crane1Info, crane2Info, crane3Info);


        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        return map;
    }

}
