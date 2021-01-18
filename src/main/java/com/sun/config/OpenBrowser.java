package com.sun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OpenBrowser implements CommandLineRunner {
    @Value("${server.port}")
    private String port;

    @Override
    public void run(String... args) throws Exception {
        //System.out.println("应用已经准备就绪 ... 启动浏览器并自动加载指定的页面 ... ");
        try {
            //System.out.println(port);
            //Runtime.getRuntime().exec("cmd /c start msedge.exe --incognito http://localhost:" + port + "/query");//指定自己项目的路径
            Runtime.getRuntime().exec("cmd /c start http://localhost:" + port + "/query");//指定自己项目的路径
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
