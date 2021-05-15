import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class MyView extends JFrame implements ActionListener, ModelObserver {

	private static final long serialVersionUID = 1L;
	private MyController controller;
	private int topWords;
	private String[] pdfs;
	private JButton pause;
	private JButton play;
	private JTextField state;
	private JProgressBar progressBar;
	private JTextField words;
	private JTextArea rank;
	
	public MyView(MyController controller, int topWords, String[] pdfs) {
		super("PDF Tool");
		
		this.controller = controller;
		this.topWords=topWords;
		this.pdfs=pdfs;
		
		setSize(300,400);	
		setResizable(false);
		
		//buttons
		JButton button1 = new JButton("Event #1");
		button1.addActionListener(this);

		JButton newWorker = new JButton("new");
		newWorker.addActionListener(this);

		pause = new JButton("pause");
		pause.addActionListener(this);
		
		play = new JButton("play");
		play.addActionListener(this);
		
		//box
		Box bButtons = new Box(BoxLayout.X_AXIS);
		bButtons.add(play);
		bButtons.add(pause);
		//b1.add(newWorker);
		//b1.add(button1);
		

		//state
		state = new JTextField();
		state.setText("Pdf completed: 0");
		progressBar = new JProgressBar(0, this.pdfs.length);
		progressBar.setStringPainted(true);
	
		//box
		Box bState = new Box(BoxLayout.Y_AXIS);
		bState.add(state);
		bState.add(progressBar);
		
		//words
		words = new JTextField(20);
		words.setText("Words processed: 0");
		
		//box
		Box bWords = new Box(BoxLayout.Y_AXIS);
		bWords.add(words);
		
		//rank
		rank = new JTextArea(12,1);
		rank.setText("Top words occurrences are listed here");
		rank.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(rank);
		
		//box
		Box bRank = new Box(BoxLayout.X_AXIS);
		bRank.add(scrollPane);
		
		
		//box container
		Box outerBox = new Box(BoxLayout.Y_AXIS);
		outerBox.add(Box.createVerticalStrut(10));
		outerBox.add(bButtons);
		outerBox.add(Box.createVerticalStrut(10));
		outerBox.add(bState);
		outerBox.add(Box.createVerticalStrut(10));
		outerBox.add(bWords);
		outerBox.add(Box.createVerticalStrut(10));
		outerBox.add(bRank);
		
		JPanel panel = new JPanel();
		panel.add(outerBox);	
				
		Container cp = getContentPane();
		cp.add(panel);
	    		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(-1);
			}
		});
	}
	
	public void actionPerformed(ActionEvent ev) {
		try {
			new Thread(() -> { //il thread della gui non processa eventi del controller per non diventare unresponsive
				controller.processEvent(ev.getActionCommand());
			}).start();
		} catch (Exception ex) {
		}
	}

	@Override
	public void modelUpdated(MyModel model) {
		try {
			SwingUtilities.invokeLater(() -> { //il thread del model non esegue operazioni gui per non creare race-conditions, non aspetta neanche per evitare deadlocks se la gui aspetta di aggiornare un elemento di cui il model ha il lock 
				
				int completed = model.getState();
				state.setText("Pdf completed: " + completed);
				System.out.println("Pdf completed: " + completed);
				progressBar.setValue(completed);
				if(completed==this.pdfs.length) {
					actionPerformed(new ActionEvent(new Object(), 0, "end"));
				}
				
				int wordsNum= model.getWordsProcessed();
				words.setText("Words processed: "+ wordsNum);
				System.out.println("Words processed: "+ wordsNum);
				
				List<Entry<String, Integer>> occurrences = model.getOrderedOccurrences();
				String rankString="Most Common: \n";
				for(int i=occurrences.size()-1; i>occurrences.size()-topWords-1;i-- ) {
					rankString= rankString+occurrences.get(i).toString()+"\n";
				}
				rank.setText(rankString);
				System.out.println(rankString);
			});
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> { //il flusso di controllo esterno non accede a elementi della gui, come spiegato sopra
        	this.setVisible(true);
        });
    }
}
