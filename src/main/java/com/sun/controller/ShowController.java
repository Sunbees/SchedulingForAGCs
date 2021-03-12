package com.sun.controller;

import com.sun.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.sun.util.Util.*;

@RequestMapping
@RestController
@CrossOrigin
public class ShowController {
    Util util;

    @Autowired
    public void setUtil(Util util) {
        this.util = util;
    }

    @GetMapping("/store")
    public Map<String, Object> getStore() {
        return readStoreLocation();
    }

    @GetMapping("/order")
    public Map<String, Object> getOrder() {
        return util.readOrderInfo();
    }

    @GetMapping("/path")
    public Map<String, Object> getPath() {
        return util.readPath();
    }

    @GetMapping("/stockNo")
    public Map<String, Object> getStockNo() {
        return readStockForNo();
    }

}
