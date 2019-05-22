package com.ckj.avchatsdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SdkTcpServer extends Thread{
	
	ServerSocket serSocket;
	
	SdkTcpServer(int port){
		try {
			serSocket=new ServerSocket(port);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				Socket socket=serSocket.accept();
				System.out.println("请求在线用户");
				BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				String line=null;
                StringBuilder builder=new StringBuilder();
                while ((line=reader.readLine())!=null){
                    builder.append(line);
                }
                JSONObject request=JSON.parseObject(builder.toString());
                String code= request.getString("code");
                if("getUserList".equals(code)) {
    				BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
    				List<String> userList=new ArrayList();
    				for(String userId:ChatSdkServer.userMap.keySet()) {
    					userList.add(userId);
    				}
    				JSONObject result=new JSONObject();
    				result.put("userList", userList);
    				writer.write(result.toJSONString());
    				System.out.println(result.toJSONString());
    				writer.flush();
    				reader.close();
    				writer.close();
    				socket.close();
                }
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
	}

}
