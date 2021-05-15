import java.io.File;
import java.util.concurrent.Callable;

//callable
public class TaskFindPdfs implements Callable<String[]>  {

	private String dirName;

	public TaskFindPdfs(String dirName) {
		this.dirName = dirName;
	}

	public String[] call() { //call 
		log("Executing task "+ this.getClass().getName());
		
		File directoryPath = new File(dirName);
	    String pdfs[] = directoryPath.list();

		log("Computed result "+ this.getClass().getName());
		return pdfs; //return
	}

	private void log(String msg) {
		System.out.println(msg);
	}
}
