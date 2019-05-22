package com.ckj.avchatsdk;

public class App {
	
	public static void main(String[] args) {
		SdkTcpServer tcpServer=new SdkTcpServer(8080);
		tcpServer.start();
		ChatSdkServer server=new ChatSdkServer(8888,65535);
		server.start();
		System.out.print("·şÎñÆ÷Æô¶¯");
	}

}
