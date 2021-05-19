package main.java.program;

import java.io.File;
import java.util.concurrent.Callable;

//callable
public class TaskFindFiles implements Callable<String[]>  {

	private String dirName;

	public TaskFindFiles(String dirName) {
		this.dirName = dirName;
	}

	public String[] call() { //call 
		log("Executing task "+ this.getClass().getName());
		
		File directoryPath = new File(dirName);
	    String files[] = directoryPath.list();

		log("Computed result "+ this.getClass().getName());
		return files; //return
	}

	private void log(String msg) {
		System.out.println(msg);
	}
}
