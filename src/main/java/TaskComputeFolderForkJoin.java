import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;




//il task  ìprende in input la cartella, crea dei task per ogni pdf, si forka per ogni sottocartella
public class TaskComputeFolderForkJoin extends RecursiveAction {  //invece extends RecursiveTask<> è per quei task che sono come la callable, resituiscono future, questo è come i Runnable

	private MyModel model;
	private Flag continueFlag;
	
	private String dirName;
	private String[] ignoredWordsArray;
	
	ExecutorService executor;
	
	public TaskComputeFolderForkJoin(MyModel model, Flag contFlag, String dirName, String[] ignoredWords, ExecutorService executor){
		
		this.model = model;
		this.continueFlag = contFlag;
		
		this.dirName = dirName;

		this.ignoredWordsArray = ignoredWords;
		this.executor= executor; 
	}
	
	@Override
	protected void compute() {
		log("Executing task "+ this.getClass().getName());
		
		//lista di forks (di altri task uguali a questo) per sottocartelle
        List<RecursiveAction> forks = new LinkedList<RecursiveAction>();
        
       
		
        File directoryPath = new File(dirName);
        log("folder "+ dirName);
	    File folder[] = directoryPath.listFiles();
        for (File file : folder) {
        	if (file.isDirectory()) {//se directory
        		log(file.getPath()+ " is: dir");
	            TaskComputeFolderForkJoin task = new TaskComputeFolderForkJoin(model, continueFlag, file.getPath(), ignoredWordsArray, executor);
	            forks.add(task);
	            task.fork();//lui stesso fa la fork,creando sottotask, submission implicita dell'task creato al suo executor
        	} else {
        		log(file.getPath()+ " is: pdf");
        		//process pdf con task submission
        		executor.execute( new TaskComputeFile(model, continueFlag, file.getPath(), ignoredWordsArray));
        	}
        }
        try {
        	executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS  );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log("Computed result "+ this.getClass().getName());
	}
	


	private void log(String msg) {
		System.out.println(msg);
	}



}
