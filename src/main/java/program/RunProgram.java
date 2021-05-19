package main.java.program;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



public class RunProgram {

	  private static String filesDir;
	  private static String fileIgnoredWords;
	  private static int topWords;
	
  static public void main(String[] args){
	
	  
	//Arguments
	filesDir = "./src";
	topWords = 30; 
	fileIgnoredWords = "./wordsToIgnore.txt";
	try {
		filesDir = args[0];
		topWords = Integer.parseInt(args[1]);
		fileIgnoredWords = args[2];
	} catch (Exception e) {
		usage();
	}
	
	//creating EXECUTOR
	int poolSize = Runtime.getRuntime().availableProcessors() + 1; //num thread = num processori+1 //si potrebbero usare anche 2 thread, 1 per task
	ExecutorService	executor = Executors.newFixedThreadPool(poolSize);
		
	
	//Extracting list words to ignore, WITH A TASK passed to the executor
	Future<String[]> futIgnoredWords = null;
	try {
		futIgnoredWords = executor.submit( new TaskListIgnoredWords(fileIgnoredWords) ); //faccio submit, mi restituisce una future
	} catch (Exception e) { 
		e.printStackTrace();
	}
	    
    //Extracting list file names from directory, WITH A TASK passed to the executor
	Future<String[]> futFilesList = null;
	try {
		futFilesList = executor.submit( new TaskFindFiles(filesDir) ); //faccio submit, mi restituisce una future
	} catch (Exception e) { 
		e.printStackTrace();
	}
	
	//closing executor when terminated all tasks submitted
	executor.shutdown();
	
	
	
	//richiesta risultato parole da ignorare
	String[] ignoredWords = {""};
	try {
		ignoredWords = futIgnoredWords.get(); //su future mi blocco sulla get fino a che non è pronta
	} catch (Exception ex){
		ex.printStackTrace();
	}
	//stampa
	System.out.println("List of Ignored Words:");
    for(int i=0; i<ignoredWords.length; i++) {
        System.out.println(ignoredWords[i]);
     }
    System.out.println();
	
	//richiesta risultato lista file
    String filesList[] = {""};
	try {
		filesList = futFilesList.get(); //su future mi blocco sulla get fino a che non è pronta
	} catch (Exception ex){
		ex.printStackTrace();
	}
    //stampa
    System.out.println("List of files in the specified directory:");
    for(int i=0; i<filesList.length; i++) {
       System.out.println(filesList[i]);
    }
    System.out.println();
	
    //PROGRAM STRUCTURE
	//model con la struttura dati protetta
	MyModel model = new MyModel();
	MyController controller = new MyController(model, ignoredWords, filesList, filesDir);
    MyView view = new MyView(controller, topWords, filesList);
    model.addObserver(view);    
    view.display();
    
  }
  
  
  

/**
 * This will print the usage for this program.
 */
private static void usage() {
    System.err.println("Error with arguments, using default ones");
    System.err.println("Usage: java " + RunProgram.class.getName() +
    		" <files directory (default "+ filesDir +")>" +
    		" <number top words (default "+topWords+")>" +
    		" <file words to ignore (default "+fileIgnoredWords+")>");
}
  
}
