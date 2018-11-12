package com.hks.springzookeeper.distributeLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: hekuangsheng
 * @Date: 2018/11/12
 */
public class RecipesLock {

    static String lock_path = "/curator_recipes_lock_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) {
        try{
            client.start();
            final InterProcessMutex lock = new InterProcessMutex(client,lock_path);
            final CountDownLatch down = new CountDownLatch(1);
            for(int i=0;i<10;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            down.await();
                            lock.acquire();
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        System.out.print("生成的订单号是："+orderNo);
                        try{
                            lock.release();
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
            down.countDown();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
