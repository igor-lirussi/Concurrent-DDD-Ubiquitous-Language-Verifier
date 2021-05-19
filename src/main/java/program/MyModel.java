package main.java.program;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.HashMap;


//MONITOR, ogni metodo pubblico synchronized, no campi pubblici, 
//il codice usa oggetti solo confinati nel monitor
public class MyModel {

	private List<ModelObserver> observers;
	private int fileProcessedState;
	private int wordsProcessed;
	private HashMap<String, Integer> occurrences;
	private List<Entry<String, Integer>> orederedList;
	
	public MyModel(){
		observers = new ArrayList<ModelObserver>();
		fileProcessedState = 0;
		wordsProcessed=0;
		occurrences = new HashMap<String, Integer>();
	}
	
	public synchronized void update(){
		fileProcessedState++;
		orderMap();
		notifyObservers();
	}
	
	public synchronized int getState(){
		return fileProcessedState;
	}
	
	
	public synchronized void addWord(String word){
		wordsProcessed++;
		if (!occurrences.containsKey(word)) {
			occurrences.put(word, 1);
		}else {
			Integer wordPassedOccurences = occurrences.get(word);
			wordPassedOccurences = wordPassedOccurences + 1;
			occurrences.put(word, wordPassedOccurences);
		}
	}
	
	public synchronized int getWordsProcessed(){
		return wordsProcessed;
	}
	

	public synchronized List<Entry<String, Integer>> getOrderedOccurrences(){
		return orederedList;
	}
	
	
	public synchronized void addObserver(ModelObserver obs){
		observers.add(obs);
	}
	
	//private methods
	private void notifyObservers(){
		for (ModelObserver obs: observers){
			obs.modelUpdated(this);
		}
	}
	
	private void orderMap(){ 
		orederedList = new ArrayList<>(occurrences.entrySet());
		orederedList.sort(Entry.comparingByValue());
		//orederedList.forEach(System.out::println);
		
		/*
		FrequencyComparator comp = new FrequencyComparator(occurrences);
        TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(comp);
        sortedMap.putAll(occurrences);
        System.out.println(sortedMap.toString());
        */
	}
	
	
	final class FrequencyComparator implements Comparator<String> {
        HashMap<String,Integer> refMap;
        public FrequencyComparator(HashMap<String,Integer> passed) {
            this.refMap = passed;
        }
        @Override
        public int compare(String k1, String k2) {
            Integer val1 = refMap.get(k1);
            Integer val2 = refMap.get(k2);
            int num = val1.compareTo(val2)  ;
            return  num;
        }
    }
    
}
