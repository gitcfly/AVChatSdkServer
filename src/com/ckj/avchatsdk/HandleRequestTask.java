package com.ckj.avchatsdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.util.IOUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HandleRequestTask implements Runnable {

    DatagramSocket serverSocket;
    DatagramPacket recivedPacket;

    public HandleRequestTask(DatagramSocket serverSocket,DatagramPacket recivedPackt){
        this.serverSocket=serverSocket;
        this.recivedPacket=recivedPackt;
    }

    @Override
    public void run() {
        try{
            long begin=System.currentTimeMillis();
        	RequestDataPack request=JSON.parseObject(recivedPacket.getData(), 0, recivedPacket.getLength(), IOUtils.UTF8, RequestDataPack.class);
        	long mid=System.currentTimeMillis();
        	CodeType code=request.type;
            switch(code) {
            case online:
            	RemoteUser user=new RemoteUser(recivedPacket.getSocketAddress(),System.currentTimeMillis());
            	ChatSdkServer.userMap.put(request.sourceId,user);
            	break;
            case forwardOffline:
            	ChatSdkServer.userMap.remove(request.sourceId);
            	farwardData(serverSocket,request);
            	System.out.println(request.sourceId+":"+code);
            	break;
            case returnUsers:
            	List<String> userList=new ArrayList();
				for(String userId:ChatSdkServer.userMap.keySet()) {
					userList.add(userId);
				}
				request.setExtra(userList);
				RemoteUser sourceUser=ChatSdkServer.userMap.get(request.getSourceId());
				byte[] returnPack=JSON.toJSONBytes(request);
				DatagramPacket returnPacket=new DatagramPacket(returnPack,returnPack.length,sourceUser.address);
		        serverSocket.send(returnPacket);
		        System.out.println(request.sourceId+":"+code+" result:"+JSON.toJSONString(userList));
            	break;
            default:
            	farwardData(serverSocket,request);
            }
            long end=System.currentTimeMillis();
            System.out.println("alltime:"+(end-begin)+",parse time:"+(mid-begin));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void farwardData(DatagramSocket serverSocket,RequestDataPack request) throws Exception {
    	List<String> targetIds=request.targetIds;
        if(targetIds==null||targetIds.size()==0){
            return;
        }
        for (int i=0;i<targetIds.size();i++){
        	RemoteUser user1;
            if((user1=ChatSdkServer.userMap.get(targetIds.get(i)))!=null){
                DatagramPacket forwardPacket=new DatagramPacket(recivedPacket.getData(),recivedPacket.getLength(),user1.address);
                serverSocket.send(forwardPacket);
            }
        }
    }
}
