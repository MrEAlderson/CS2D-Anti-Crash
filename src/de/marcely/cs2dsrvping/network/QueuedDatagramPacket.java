package de.marcely.cs2dsrvping.network;

import java.net.InetAddress;

public class QueuedDatagramPacket {
	
	public final byte[] buffer;
	public final InetAddress address;
	public final int port;
	
	public QueuedDatagramPacket(byte[] buffer, InetAddress inetAddress, int port){
		this.buffer = buffer;
		this.address = inetAddress;
		this.port = port;
	}
}
