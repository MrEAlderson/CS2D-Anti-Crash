package de.marcely.cs2dsrvping.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public abstract class UDPSocket {
	
	public final int bufferSize;
	
	private DatagramSocket socket;
	
	public UDPSocket(int bufferSize){
		this.bufferSize = bufferSize;
	}
	
	public abstract void onReceive(QueuedDatagramPacket packet);
	
	public boolean isRunning(){ return this.socket != null; }
	
	public boolean run() throws SocketException {
		if(isRunning()) return false;
		
		this.socket = new DatagramSocket();
		
		new Thread(){
			public void run(){
				while(isRunning()){
					final byte[] buffer = new byte[bufferSize];
					final DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
					
					try{
						socket.receive(packet);
					}catch(IOException e){
						final String msg = e.getMessage();
						
						if(msg == null || (!msg.equals("socket closed") && !msg.equals("Socket closed")))
							e.printStackTrace();
						else
							return;
					}
					
					onReceive(new QueuedDatagramPacket(Arrays.copyOf(buffer, packet.getLength()), packet.getAddress(), packet.getPort()));
				}
			}
		}.start();
		
		return true;
	}
	
	public boolean shutdown(){
		if(!isRunning()) return false;
		
		this.socket.close();
		this.socket = null;
		
		return true;
	}
	
	public boolean sendPacket(byte[] buffer, InetAddress address, int port){
		if(!isRunning()) return false;
		
		try{
			this.socket.send(new DatagramPacket(buffer, buffer.length, address, port));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return true;
	}
}