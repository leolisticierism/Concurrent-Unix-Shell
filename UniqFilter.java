public class UniqFilter extends ConcurrentFilter {
	String command;
	String prevline;
	
	public UniqFilter(String command) {
	this.command = command;
	}
	
	@Override
	public void run() {
		try {
			String prevLine = "";
			String line = input.readAndWait();
			while(line != null) {
				if(!line.equals(prevLine)) {
					output.writeAndWait(line);
				}
				prevLine = line;
				line = input.readAndWait();
			}
			output.writePoisonPill();
		} catch (InterruptedException e) {
		}
	}
	
	public void process() {
		prevline = "";
		while(!input.isEmpty()) {
			String line = input.read();
			String processed = processLine(line);
			if(processed != null) {
				output.write(processed);
			}
			prevline = line;	// store the previous line in the prevline field
		}
	}

	@Override
	/**
	 * this method processes a single line. If the current line is not the same as the previous line, return line. Otherwise, return null
	 */
	protected String processLine(String line) {
		if (!line.equals(prevline)) {
			return line;
		} else {
			return null;
		}
	}

}
