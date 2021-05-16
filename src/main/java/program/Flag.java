package main.java.program;
//Usato per play e pausa, i thread fanno il check del monitor, se è ok, continunano, altrimenti aspettano
//il controller setta off la variabile per mettere in wait tutti, setta on e segnala tutti quelli in wait che continuino
//MONITOR, ogni metodo pubblico synchronized, no campi pubblici, 
//il codice usa oggetti solo confinati nel monitor
public class Flag {

	private boolean contrinueFlag;
	
	public Flag() {
		contrinueFlag = false;
	}
	
	public synchronized void setOff() {
		contrinueFlag = false;
		System.out.println("[CONTINUE FLAG] set to off");
	}
	
	public synchronized void setOn() {
		contrinueFlag = true;
		//SBLOCCA TUTTI IN WAIT
		notifyAll();
		System.out.println("[CONTINUE FLAG] set to on, NOTIFIED ALL");
	}
	
	public synchronized boolean isOn() {
		while (!contrinueFlag) { //while come doppio if siccome java ha variante della "signal and continue"
			try {
				System.out.println("[CONTINUE FLAG] is off, WAITING");
				//ASPETTA
				wait();
			} catch (InterruptedException e) {}
		}
		return contrinueFlag;
	}
}
