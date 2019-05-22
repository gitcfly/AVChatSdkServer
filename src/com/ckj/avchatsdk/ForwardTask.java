package com.ckj.avchatsdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;

public class ForwardTask implements Runnable {

    DatagramSocket serverSocket;
    DatagramPacket recivedPacket;

    public ForwardTask(DatagramSocket serverSocket,DatagramPacket recivedPackt){
        this.serverSocket=serverSocket;
        this.recivedPacket=recivedPackt;
    }

    @Override
    public void run() {
        try{
            String message=new String(recivedPacket.getData(),0,recivedPacket.getLength());
            RequestDataPack request=JSON.parseObject(message, RequestDataPack.class);
            String code=request.type;
            if("connect".equals(code)) {
            	RemoteUser user=new RemoteUser(recivedPacket.getSocketAddress(),System.currentTimeMillis());
            	ChatSdkServer.userMap.put(request.sourceId,user);
            	System.out.println("recived heartBeat from "+request.sourceId);
            }else if("disconnect".equals(code)) {
            	ChatSdkServer.userMap.remove(request.sourceId);
            	System.out.println("disconnect :"+request.sourceId);
            }else {
                List<String> targetIds=request.targetIds;
                if(targetIds==null||targetIds.size()==0){
                    return;
                }
                for (int i=0;i<targetIds.size();i++){
                	RemoteUser user;
                    if((user=ChatSdkServer.userMap.get(targetIds.get(i)))!=null){
                        DatagramPacket forwardPacket=new DatagramPacket(recivedPacket.getData(),recivedPacket.getLength(),user.address);
                        serverSocket.send(forwardPacket);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
