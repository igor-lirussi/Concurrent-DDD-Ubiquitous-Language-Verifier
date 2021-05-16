import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

class MyFirstJUnitJupiterTests {

	//creating EXECUTOR
	int poolSize = Runtime.getRuntime().availableProcessors() + 1; //num thread = num processori+1 //si potrebbero usare anche 2 thread, 1 per task
	ExecutorService	executor = Executors.newFixedThreadPool(poolSize);
	
    @Test
    void baseTest() {
        assertEquals(2, 1+1);
    }
    
    @Test
    void ignoredWordsTest() {
    	//create file and write
		String fileName = "testWordsToIgnore.txt";
    	try {
	        File myObj = new File(fileName);
	        if (!myObj.createNewFile()) {
	       	  System.out.println("File already exists, deleting "+ myObj.getName());
	    	  myObj.delete();
	        }
	        System.out.println("File created: " + myObj.getName());
	        FileWriter myWriter = new FileWriter(fileName);
	        myWriter.write("these \n words \n are \n ignored");
	        myWriter.close();
	        System.out.println("File written: " + myObj.getName());
	        myObj.delete();

	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
    	//Extracting list words to ignore, WITH A TASK passed to the executor
    	Future<String[]> futIgnoredWords = null;
    	try {
    		futIgnoredWords = executor.submit( new TaskListIgnoredWords(fileName) ); //faccio submit, mi restituisce una future
    	} catch (Exception e) { 
    		e.printStackTrace();
    	}
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
        String[] ignoredWordsWritten =  {"these", "words", "are", "ignored"};
        assertEquals(ignoredWords,ignoredWordsWritten);
    }

}