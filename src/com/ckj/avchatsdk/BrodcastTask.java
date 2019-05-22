package com.ckj.avchatsdk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class BrodcastTask implements Runnable{
	
	DatagramSocket serverSocket;
    DatagramPacket recivedPacket;

    public BrodcastTask(DatagramSocket serverSocket,DatagramPacket recivedPackt){
        this.serverSocket=serverSocket;
        this.recivedPacket=recivedPackt;
    }



	@Override
	public void run() {
		try{
			long start=System.currentTimeMillis();
            String message=new String(recivedPacket.getData(),0,recivedPacket.getLength());
            RequestDataPack request=JSON.parseObject(message, RequestDataPack.class);
            long parseEnd=System.currentTimeMillis();
			DatagramPacket forwardPacket=new DatagramPacket(recivedPacket.getData(),recivedPacket.getLength(), recivedPacket.getAddress(),9999);
            serverSocket.send(forwardPacket);
            long End=System.currentTimeMillis();
            System.out.println("allTime:"+(End-start)+" parseTime:"+(parseEnd-start));
            System.out.println("return send data, data length:"+forwardPacket.getLength());
        }catch (Exception e){
            e.printStackTrace();
        }
	}

}
