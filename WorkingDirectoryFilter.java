public class WorkingDirectoryFilter extends ConcurrentFilter {

	String command;

	public WorkingDirectoryFilter(String command) {
		this.command = command;
	}


	@Override
	public void run() {
		try {
			this.output.writeAndWait(CurrentWorkingDirectory.get());
			this.output.writePoisonPill();
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Overrides {@link SequentialFilter#process()} by adding
	 * {@link SequentialREPL#currentWorkingDirectory} to the output queue
	 */
	@Override
	public void process() {
		if(output == null) {
			output = new ConcurrentPipe();
		}
		String processed = processLine(command);
		this.output.write(processed);
	}
	
	@Override
	/**
	 * this method takes whatever line and returns the current working directory
	 */
	protected String processLine(String line) {
		return CurrentWorkingDirectory.get();
	}
}
