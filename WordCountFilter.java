public class WordCountFilter extends ConcurrentFilter {
	String command;
	int numlines = 0;
	int numwords = 0;
	int numchars = 0;
	
	public WordCountFilter(String command) {
		this.command = command;
	}
	
	/**
	 * overrides run method and enable the WordCountFilter to run concurrently as a thread
	 */
	@Override
	public void run() {
		try {
			String line = input.readAndWait();
			while(line != null) {
				processLine(line);
				line = input.readAndWait();
			}
			output.writeAndWait(numlines + " " + numwords + " " + numchars);
			output.writePoisonPill();
		} catch (InterruptedException e) {
		}
	}
	/**
	 * Overrides {@link SequentialFilter#process()} by computing the word count,
	 * line count, and character count then adding the string with line count + " "
	 * + word count + " " + character count to the output queue
	 */
	@Override
	public void process() {
		if(input != null) {
			if(output == null) {
				output = new ConcurrentPipe();
			}
			while(!input.isEmpty()) {
				String line = input.read();
				processLine(line);
			}
		output.write(numlines + " " + numwords + " " + numchars); // write a one line output based on the counts
		}
	}

	/**
	 * Overrides SequentialFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		numlines ++;		// increase the number of lines by 1
		numchars += line.length();	//increase the number of characters by the number of character in the current line
		String[] words = line.split(" ");	//split the words
		numwords += words.length;	//increase the number of words
		return null;
	}

}
