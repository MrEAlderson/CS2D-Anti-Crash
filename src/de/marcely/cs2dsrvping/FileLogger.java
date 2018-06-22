package de.marcely.cs2dsrvping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileLogger {
	
	private static final File FILE = new File("latest.log");
	private static final int MAX = 512;
	
	private static void baseCheck(){
		if(!FILE.exists()){
			try{
				FILE.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private static int size(){
		try{
			int lines = 0;
			
			final BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(FILE)));
			
			while(stream.readLine() != null)
				lines++;
			
			stream.close();
			
			return lines;
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static List<String> read(){
		try{
			final List<String> list = new ArrayList<>(MAX);
			final BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(FILE)));
			final int size = size();
			
			for(int skip=0; skip<size-MAX; skip++)
				stream.readLine();
			
			String line = null;
			
			while((line = stream.readLine()) != null)
				list.add(line);
			
			stream.close();
			
			return list;
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public static void write(String message){
		baseCheck();
		
		try{
			final List<String> lines = read();
			final BufferedWriter stream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE)));
			
			if(lines.size() >= MAX)
				lines.remove(0);
			
			for(String line:lines){
				stream.write(line);
				stream.newLine();
			}
			
			stream.write(new SimpleDateFormat("[dd.MM.yyyy HH:mm:ss] ").format(new Date()) + message);
			
			stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
