package com.ckj.avchatsdk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class HeartCheckServer extends Thread{

    boolean running=true;


    @Override
    public void run() {
    	running=true;
        while (running){
            try{
            	Map<String, RemoteUser> userMap=ChatSdkServer.userMap;
                for(Map.Entry<String, RemoteUser> kv:userMap.entrySet()) {
                	long now=System.currentTimeMillis();
                	if((now-kv.getValue().lastTime)>5000) {
                		userMap.remove(kv.getKey());
                		System.out.println(kv.getKey()+" disconnect by Server");
                	}
                }
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void stopCheck() {
    	running=false;
    }

}
