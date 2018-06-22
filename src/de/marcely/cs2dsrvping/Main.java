package de.marcely.cs2dsrvping;

import java.net.InetAddress;

import de.marcely.cs2dsrvping.network.QueuedDatagramPacket;
import de.marcely.cs2dsrvping.network.UDPSocket;

public class Main {
	
	public static UDPSocket socket;
	public static ServerHandler handler;
	public static boolean isRunning = false;
	
	public static void main(String[] args){
		run();
	}
	
	public static void run(){
		if(isRunning) return;
		isRunning = true;
		
		FileLogger.write("Starting service...");
		
		try{
			socket = new UDPSocket(512){
				public void onReceive(QueuedDatagramPacket packet){
					if(handler.getAddress().getHostAddress().equals(packet.address.getHostAddress())
							&& handler.getPort() == packet.port)
						handler.receivedPacket(packet);
				}
			};
			handler = new ServerHandler(socket, InetAddress.getByName("<Censored :)>"), 36963, 30*1000L, 60*5*1000L, 60*2*1000L);
			
			if(socket.run() && handler.run())
				System.out.println("Running.");
			else{
				FileLogger.write("Failed to start!");
				System.out.println("Failed to start.");
				System.exit(0);
			}
		}catch(Exception e){
			e.printStackTrace();
			FileLogger.write("ERROR 2");
		}
	}
	
	public static void shutdown(){
		if(!isRunning) return;
		isRunning = false;
		
		FileLogger.write("Shutting down service...");
		
		socket.shutdown();
		handler.cancel();
	}
}
