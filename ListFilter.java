import java.io.File;

public class ListFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	String command;
	String [] filelist;
	
	public ListFilter(String command) {
		super();
		File directory = new File(CurrentWorkingDirectory.get());
		filelist = directory.list();	//list the files and store it in the filelist field
		this.command = command;
	}
	
	@Override
	public void run() {
		File filelist = new File(CurrentWorkingDirectory.get());
		File[] files = filelist.listFiles();
		try {
			for(int i=0; i<files.length; i++) {
				this.output.writeAndWait(files[i].getName());
			}
			this.output.writePoisonPill();
		}catch (InterruptedException e) {
		}
	}
	
	@Override
	protected String processLine(String line) {
		return null;
	}

	/**
	 * Overrides {@link SequentialFilter#process()} to add the files located in
	 * {@link SequentialREPL#currentWorkingDirectory} to the output queue.
	 */
	@Override
	public void process() {
		if(this.output == null) {
			this.output = new ConcurrentPipe();
		}
		if(filelist.length > 0) {
			for(int i=0; i < filelist.length; i++) {	//use a for loop to write the file/directory names
				String processed = processLine(filelist[i]);
				this.output.write(processed);
			}
		}
	}

}
