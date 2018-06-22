package de.marcely.cs2dsrvping;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import de.marcely.cs2dsrvping.network.QueuedDatagramPacket;
import de.marcely.cs2dsrvping.network.UDPSocket;
import lombok.Getter;

public class ServerHandler {
	
	private final UDPSocket socket;
	@Getter private final InetAddress address;
	@Getter private final int port;
	private final long period, timeout, appRestartDelay;
	
	private Timer timer;
	private Session session;
	private long lastTimeout;
	
	public ServerHandler(UDPSocket socket, InetAddress address, int port, long period, long timeout, long appRestartDelay){
		this.socket = socket;
		this.address = address;
		this.port = port;
		this.period = period;
		this.timeout = timeout;
		this.appRestartDelay = appRestartDelay;
	}
	
	public boolean isRunning(){
		return this.timer != null;
	}
	
	public boolean run(){
		if(isRunning()) return false;
		
		this.timer = new Timer();
		this.lastTimeout = System.currentTimeMillis();
		
		this.timer.schedule(new TimerTask(){
			public void run(){
				if(session != null)
					session.stop();
				
				session = new Session(timeout-(System.currentTimeMillis()-lastTimeout)){
					public void onResult(boolean isOk){
						lastTimeout = System.currentTimeMillis()+period;
						
						if(!isOk){
							cancel();
							Util.restart(appRestartDelay);
						}
					}
				};
				
				session.ping(socket, address, port);
			}
		}, 0, period);
		
		return true;
	}
	
	public boolean cancel(){
		if(!isRunning()) return false;
		
		this.timer.cancel();
		this.timer = null;
		
		if(session != null){
			session.stop();
			session = null;
		}
		
		return true;
	}
	
	public void receivedPacket(QueuedDatagramPacket packet){
		if(session != null)
			session.onReceive(packet);
	}
}
