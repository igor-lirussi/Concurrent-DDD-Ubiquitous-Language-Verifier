package main.java.program;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

//callable
public class TaskListIgnoredWords implements Callable<String[]>  {

	private String fileIgnoredWords;

	public TaskListIgnoredWords(String fileIgnoredWords) {
		this.fileIgnoredWords = fileIgnoredWords;
	}

	public String[] call() { //call 
		log("Executing task "+this.getClass().getName());
		
		String[] arr = {""};
	    List<String> itemsSchool = new ArrayList<String>();

	    FileInputStream fstream_school;
		try {
			fstream_school = new FileInputStream(fileIgnoredWords);
		
		    DataInputStream data_input = new DataInputStream(fstream_school); 
		    BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input)); 
		    String str_line; 
	
		    while ((str_line = buffer.readLine()) != null) {
		        if ((str_line.length()!=0))  
		        { 
		            itemsSchool.add(str_line.trim().toLowerCase().replaceAll("\\s", ""));
		        } 
		    }
		    buffer.close();
		    
		    arr = (String[])itemsSchool.toArray(new String[itemsSchool.size()]);
	    
	    
		} catch (FileNotFoundException e) {
			System.err.println("File not ignore not found, no words are going to be ignored");
	        e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log("Computed result "+ this.getClass().getName());
		return arr; //return
	}

	private void log(String msg) {
		System.out.println(msg);
	}
}
