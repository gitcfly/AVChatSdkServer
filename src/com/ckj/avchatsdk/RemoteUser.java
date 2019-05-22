package com.ckj.avchatsdk;

import java.net.InetAddress;
import java.net.SocketAddress;

public class RemoteUser {
	
	public SocketAddress address;
	public int port;
	public long lastTime;
	
	public RemoteUser(SocketAddress address,long lastTime) {
		this.address = address;
		this.lastTime = lastTime;
	}
}
