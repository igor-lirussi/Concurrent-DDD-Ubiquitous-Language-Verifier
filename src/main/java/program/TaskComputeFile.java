package main.java.program;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


//nei task non si possono usare meccanismi di livello di thread,
//es: si possono usare monitor ma non i meccanismi di wait (essere sbloccato da azione di un altro task), o una barriera (bloccherebbe non solo il task ma anche l'executor) 
//(gli altri task non verrebbero presi in consegna)
//blocca i thread sotto, non solo il task, quindi nessun'altro viene eseguito
public class TaskComputeFile implements Runnable {

	private MyModel model;
	private Flag continueFlag;
	
	private String fileName;
	private List<String> ignoredWords;
	
	public TaskComputeFile(MyModel model, Flag contFlag, String fileName, String[] ignoredWords){
		//entrambi gli elementi passati a cui i thread faranno accesso sono monitor, ma non ci sono deadlock perchè:
		this.model = model; //monitor semplice, solo per mutua esclusione
		this.continueFlag = contFlag; //i task bloccano pure i thread nella wait, ma la notify non viene fatta da un'altro task, bensì dal flusso di controllo del controller
		
		this.fileName = fileName;
		this.ignoredWords = Arrays.asList(ignoredWords);
	}
	
	@Override
	public void run() {
		log(">>> Executing task "+this.getClass().getName()+ " on file "+fileName);
		
		try {
			List<String> lines = Collections.emptyList();
		    try {
		      lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.ISO_8859_1);
		    }
		    catch (IOException e) {
		      e.printStackTrace();
		    }
		    for (String line:lines) {
				if(continueFlag.isOn()) { //MONITOR, se non è ON, aspetta qui
			        String[] words = line.trim().toLowerCase().split("\\W+");
			        //System.out.println(Arrays.toString(words));
			        for(String word : words) {
			        	if(!ignoredWords.contains(word) && !word.isEmpty()) {
			        		model.addWord(word);
			        	}
			        } 
				}
		    }
	        
	        model.update(); //aumenta documenti completati, triggera il riordino e la view -> alla fine di un doc perchè il riordino è intensive e la view ha senso aggiornare ad ogni file
		    
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		
		log("Computed result "+ this.getClass().getName());
	}
	
	
	
	

	private void log(String msg) {
		System.out.println(msg);
	}

}
