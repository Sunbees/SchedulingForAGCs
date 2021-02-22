package com.sun.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.sun.util.Util.*;

@RequestMapping
@RestController
@CrossOrigin
public class ShowController {
    @GetMapping("/store")
    public Map<String, Object> getStore() {
        return readStoreLocation();
    }

    @GetMapping("/order")
    public Map<String, Object> getOrder() {
        return readOrderInfo();
    }

    @GetMapping("/path")
    public Map<String, Object> getPath() {
        return readPath();
    }

    @GetMapping("/stockNo")
    public Map<String, Object> getStockNo() {
        return readStockForNo();
    }

}
