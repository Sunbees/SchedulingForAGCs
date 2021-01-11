package com.sun;

public class TestThread {

    public static void main(String[] args) {
        MyThread mt = new MyThread("新线程————看大片");
        //开启新线程
        mt.start();
        //在主方法中执行for循环
        for (int i = 0; i < 5; i++) {
            System.out.println("main线程————撸代码，没意思。。" + i);
        }
    }

    //继承Thread类
    public static class MyThread extends Thread {
        //定义指定线程名称的构造方法
        public MyThread(String name) {
            //调用父类的String参数的构造方法，指定线程的名称(原理：利用继承特点，将线程名称传递)
            super(name);
        }

        //重写run方法，定义线程要执行的代码
        @Override
        public void run() {
            for (int j = 0; j < 5; j++) {
                //getName()方法 来自父亲(就是Thread类中，获取当前线程名称方法)
                System.out.println(getName() + " ：好刺激哟，不行了，快、快。。" + j);
            }
        }
    }
}