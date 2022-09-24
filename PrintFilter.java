public class PrintFilter extends ConcurrentFilter {
	/**
	 * overrides the run method to have the thread starts in the correct way. Avoid calling input.isEmpty()
	 */
	@Override
	public void run() {
		try {
			if(input != null) {
			String line = input.readAndWait();
			while(line != null) {
				processLine(line);
				line = input.readAndWait();
			}
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void process() {
		if(input != null) {
			while (!input.isEmpty()) {
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
	 * this method process a certain line and print the line
	 */
	protected String processLine(String line) {
		if(line != null) {
			System.out.println(line);
		}
		return null;
	}

}
