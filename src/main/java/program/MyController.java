package main.java.program;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class MyController {
	
	private MyModel model;
	private Flag continueFlag;
	private String[] ignoredWords;
	private String[] filesList;
	
	long t0;
	
	ExecutorService	executor;
	
	public MyController(MyModel model, String[] ignoredWords, String[] filesList, String filesDir){
		this.model = model;
		this.ignoredWords= ignoredWords;
		this.filesList = filesList;
		
		this.continueFlag = new Flag();
		
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("N cores: "+cores);
		
		//CREAZIONE EXECUTOR 
		ExecutorService executor = Executors.newFixedThreadPool(cores+1);
		/*
		//passing TASKs
		for(String file:this.filesList) {
			executor.execute( new TaskComputeFile(this.model, this.continueFlag, filesDir+"/"+file, this.ignoredWords));
		}
		*/
		//for "fork-join tasks" (passo solo la folder principale, si forca per ogni sottofolder" 
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		forkJoinPool.execute( new TaskComputeFolderForkJoin(this.model, this.continueFlag, filesDir, this.ignoredWords, executor) );
		//invoke aspetterebbe la terminazione, ma in questo caso non vogliamo essere bloccanti e usiamo execute
		
	}
	
	public void processEvent(String event) {
		try {
			if (event.equals("play")) {
				continueFlag.setOn();
				//start timer
				t0 = System.currentTimeMillis();
			} else if (event.equals("pause")) {
				continueFlag.setOff();
			} else if (event.equals("end")) {
				//stop timer
			    long tElapsed = System.currentTimeMillis()-t0;
				System.out.println("Time elapsed: "+ tElapsed);
			}  else { 
			    System.out.println("[Controller] Unknown event  "+event+" ...");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	

}
