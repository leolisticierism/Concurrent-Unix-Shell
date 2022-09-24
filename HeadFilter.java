public class HeadFilter extends ConcurrentFilter {
	
	String command;

	
	public HeadFilter(String command) {
		super();
		this.command = command;
	}

	/**
	 * override the run method to make the list filter able to run concurrently
	 */
	@Override
	public void run() {
		try {
			int size = 0;
			String line = input.readAndWait();
			while(line != null && size < 10) {
				output.writeAndWait(line);
				size ++;
				line = input.readAndWait();
			}
			output.writePoisonPill();
		} catch (InterruptedException e) {
		}
	}
	
	
	@Override
	public void process() {
		if(input == null) {
			System.out.print(Message.REQUIRES_INPUT.with_parameter(command.trim()));	// if no input, then raise error
		} else {
			int size = 0;
			if (input.size() < 10) {		// if input size is less than 10, write all the lines
				size = input.size();
			} else {
				size = 10;					// else, write the first lines
			}
			for (int i = 0; i < size; i++) {
				String line = input.read();
				String processedLine = processLine(line);
				if (processedLine != null) {
					output.write(processedLine);
				}
			}
		}
	}
	@Override
	/**
	 * This method simply takes the line, and return the line
	 */
	protected String processLine(String line) {
		return line;
	}
}
