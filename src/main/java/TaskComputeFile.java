import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


//nei task non si possono usare meccanismi di livello di thread,
//es: si possono usare monitor ma non i meccanismi di wait (essere sbloccato da azione di un altro task), o una barriera (bloccherebbe non solo il task ma anche l'executor) 
//(gli altri task non verrebbero presi in consegna)
//blocca i thread sotto, non solo il task, quindi nessun'altro viene eseguito
public class TaskComputeFile implements Runnable {

	private MyModel model;
	private Flag continueFlag;
	
	private String pdfName;
	private List<String> ignoredWords;
	
	public TaskComputeFile(MyModel model, Flag contFlag, String pdfName, String[] ignoredWords){
		//entrambi gli elementi passati a cui i thread faranno accesso sono monitor, ma non ci sono deadlock perchè:
		this.model = model; //monitor semplice, solo per mutua esclusione
		this.continueFlag = contFlag; //i task bloccano pure i thread nella wait, ma la notify non viene fatta da un'altro task, bensì dal flusso di controllo del controller
		
		this.pdfName = pdfName;
		this.ignoredWords = Arrays.asList(ignoredWords);
	}
	
	@Override
	public void run() {
		log("Executing task "+this.getClass().getName()+ " on "+pdfName);
		
		try {
			
			// open file
	        String text= "";
	        //for piece of file
	        int fileParts[] = {0};
	        for (int part:fileParts) {
					if(continueFlag.isOn()) { //MONITOR, se non è ON, aspetta qui
				        String[] words = text.split("\\W+");
				        for(String word : words) {
				        	if(!ignoredWords.contains(word)) {
				        		model.addWord(word.toLowerCase());
				        	}
				        } 
					}
	        }
			
	        
	        model.update(); //aumenta documenti completati, triggera il riordino e la view -> alla fine di un doc perchè il riordino è intensive e la view ha senso aggiornare ad ogni pdf
	        
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		
		log("Computed result "+ this.getClass().getName());
	}
	
	
	
	

	private void log(String msg) {
		System.out.println(msg);
	}

}
