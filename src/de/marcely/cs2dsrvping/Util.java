package de.marcely.cs2dsrvping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class Util {
	
	private static boolean isRestarting = false;
	
	public static void restart(long startTime){
		if(isRestarting) return;
		isRestarting = true;
		
		// shutdown everything
		Main.socket.shutdown();
		Main.handler.cancel();
		
		// stop this service
		System.out.println("-> Stopped this service");
		Main.shutdown();
		
		// magic
		try{
			//first kill server
			Process cProcess = Runtime.getRuntime().exec("screen -ls");
			BufferedReader pReader = new BufferedReader( new InputStreamReader(cProcess.getInputStream()));
			String line = null;
			
			while((line = pReader.readLine()) != null){
				if(line.startsWith("	")){
					while(line.startsWith("	")) line = line.substring(1);
					
					String[] part1 = line.split("\\.");
					String[] part2 = part1[1].split("	");
					
					final int id = Integer.parseInt(part1[0]);
					final String name = part2[0];
					
					if(name.contains("cs2d") && !name.contains("cs2d-checker")){
						Runtime.getRuntime().exec("kill " + id).waitFor();
						System.out.println("-> Killed process (" + id + "." + name + ")");
						FileLogger.write("Killed proccess (" + id + "." + name + ")");
					}
				}
			}
			
			// start server
			Runtime.getRuntime().exec("./start.sh", null, new File(new File(new File("").getAbsolutePath()).getParentFile(), "cs2d")).waitFor();
			System.out.println("-> Started process");
			FileLogger.write("Started process");
			
			// start this after time
			System.out.println("-> Ready, starting this service again after " + Math.round(startTime/1000L) + " sec. again");
			new Timer().schedule(new TimerTask(){
				public void run(){
					Main.run();
				}
			}, startTime);
		}catch(Exception e){
			e.printStackTrace();
			FileLogger.write("ERROR 1");
		}
		
		isRestarting = false;
	}
}
