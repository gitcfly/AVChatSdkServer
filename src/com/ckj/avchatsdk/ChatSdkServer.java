package com.ckj.avchatsdk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatSdkServer extends Thread{
    public static HashMap<String,RemoteUser> userMap=new HashMap<>();
    public static ExecutorService handlerPool= Executors.newFixedThreadPool(10);
    int bufLength;
    int port;
    DatagramSocket serverSockt;
    HeartCheckServer checkClientServer;
    public ChatSdkServer(int port,int bufLength){
        try{
            this.port=port;
            this.bufLength=bufLength;
            serverSockt=new DatagramSocket(port);
            checkClientServer=new HeartCheckServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	checkClientServer.start();
        while (true){
            try{    
                byte[] buffer=new byte[bufLength];
                DatagramPacket recivedPacket=new DatagramPacket(buffer,bufLength);
                serverSockt.receive(recivedPacket);
                HandleRequestTask task=new HandleRequestTask(serverSockt,recivedPacket);
                handlerPool.execute(task);
            }catch (SocketException e){
                serverSockt.close();
                e.printStackTrace();
                break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
