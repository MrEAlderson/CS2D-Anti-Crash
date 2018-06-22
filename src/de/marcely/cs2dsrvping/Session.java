package de.marcely.cs2dsrvping;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import de.marcely.cs2dsrvping.network.QueuedDatagramPacket;
import de.marcely.cs2dsrvping.network.UDPSocket;

public abstract class Session {
	
	private final long timeout;
	
	private Timer timer;
	private long start;
	
	public Session(long timeout){
		this.timeout = timeout;
		
		this.timer = new Timer();
		
		this.start = System.currentTimeMillis();
		this.timer.schedule(new TimerTask(){
			public void run(){
				Session.this.timer = null;
				
				System.out.println("Timeout. :(");
				FileLogger.write("Timeout!");
				onResult(false);
			}
		}, timeout > 0 ? timeout : 0);
	}
	
	public void stop(){
		if(this.timer != null){
			this.timer.cancel();
			this.timer = null;
		}
	}
	
	public void ping(UDPSocket socket, InetAddress address, int port){
		socket.sendPacket(new byte[]{ 0x01, 0x00, (byte) 0xFA, (byte) 0xAF, 0x35, 0x17, 00 },  address, port);
		System.out.println("Ping! (Timeout in: " + (Math.round(timeout/1000)) + " sec.)");
	}
	
	public abstract void onResult(boolean isOk);
	
	public void onReceive(QueuedDatagramPacket packet){
		if(timer == null) return;
		
		timer.cancel();
		timer = null;
		
		System.out.println("Pong! (" + (System.currentTimeMillis()-start) + "ms)");
		
		onResult(true);
	}
}
