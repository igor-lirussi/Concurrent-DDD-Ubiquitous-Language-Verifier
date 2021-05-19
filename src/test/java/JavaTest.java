package test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import main.java.program.Flag;
import main.java.program.MyModel;
import main.java.program.TaskFindFiles;
import main.java.program.TaskListIgnoredWords;


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
		String fileName = "testWordsToIgnore.txt";
        String ignoredWordsString = "these \nwords\n are \n ignored";
        String[] ignoredWordsArray =  {"these", "words", "are", "ignored"};

    	//create file and write
        File myObj = new File(fileName);
    	try {
	        if (!myObj.createNewFile()) {
	       	  System.out.println("File already exists, deleting "+ myObj.getName());
	    	  myObj.delete();
	    	  myObj.createNewFile();
	        }
	        System.out.println("File created: " + myObj.getName());
	        FileWriter myWriter = new FileWriter(fileName);
	        myWriter.write(ignoredWordsString);
	        myWriter.close();
	        System.out.println("File written: " + myObj.getName());
	        
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
    	
    	//Extracting list words to ignore, WITH A TASK passed to the executor
    	Future<String[]> futIgnoredWords = null;
    	TaskListIgnoredWords taskListWords = new TaskListIgnoredWords("./"+fileName);
    	try {
    		futIgnoredWords = executor.submit( taskListWords ); //faccio submit, mi restituisce una future
    	} catch (Exception e) { 
    		e.printStackTrace();
    	}
    	//richiesta risultato parole da ignorare
    	String[] ignoredWords = {""};
    	try {
    		ignoredWords = futIgnoredWords.get(); //su future mi blocco sulla get fino a che non è pronta
    		myObj.delete();
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
    	//ciclic assert for all array
        for(int i=0; i<ignoredWords.length; i++) {
            assertEquals(ignoredWords[i],ignoredWordsArray[i]);
        }
    }
    
    
    @Test
    void findFilesTest() {

    	String filesDir = "./src/test/java";
    	//Extracting list file names from directory, WITH A TASK passed to the executor
    	Future<String[]> futPdfList = null;
    	try {
    		futPdfList = executor.submit( new TaskFindFiles(filesDir) ); //faccio submit, mi restituisce una future
    	} catch (Exception e) { 
    		e.printStackTrace();
    	}
    	//richiesta risultato lista pdf
        String pdfs[] = {""};
    	try {
    		pdfs = futPdfList.get(); //su future mi blocco sulla get fino a che non è pronta
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
    	List<String> pdfsList = new ArrayList<>(Arrays.asList(pdfs));
    	//assert that in the current folder,  this class is contained in the list of files discovered
    	assertTrue(pdfsList.contains("JavaTest.java"));
    }
    
    
    @Test
    void testFlag() {
    	Flag flag = new Flag();
    	flag.setOn();
    	assertTrue(flag.isOn());
    	flag.setOff(); 
    	//assertFalse(flag.isOn()); //the check here will be in wait till another thread sets on the flag
    	flag.setOn();
    	assertTrue(flag.isOn());
    }
    

    @Test
    void testModel() {
    	MyModel model = new MyModel();
    	assertEquals(0, model.getState());
    	assertEquals(0, model.getWordsProcessed());
    	//add Word
    	model.addWord("test");
    	assertEquals(0, model.getState());
    	assertEquals(1, model.getWordsProcessed());
    	//add Word
    	model.addWord("test");
    	assertEquals(0, model.getState());
    	assertEquals(2, model.getWordsProcessed());
    	//update
    	model.update();
    	assertEquals(1, model.getState());
    	assertEquals(2, model.getWordsProcessed());
    	//update
    	model.update();
    	assertEquals(2, model.getState());
    	assertEquals(2, model.getWordsProcessed());

    	//check map
    	assertEquals("test", model.getOrderedOccurrences().get(0).getKey());
    	assertEquals(2, model.getOrderedOccurrences().get(0).getValue());
    }
    
    
    
    
    
}