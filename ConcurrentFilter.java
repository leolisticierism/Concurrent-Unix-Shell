/**
 * Liang Zhuang
 */

/**
 * An abstract class that extends the Filter and implements the basic
 * functionality of all filters. Each filter should extend this class and
 * implement functionality that is specific for this filter.
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The input pipe for this filter
	 */
	protected ConcurrentPipe input;
	/**
	 * The output pipe for this filter
	 */
	protected ConcurrentPipe output;

	/**
	 * Override the run method and call process
	 */
	@Override
	public void run() {
		process();
	}
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}

	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter) {
			ConcurrentFilter concurrentNext = (ConcurrentFilter) nextFilter;
			this.next = concurrentNext;
			concurrentNext.prev = this;
			if (this.output == null) {
				this.output = new ConcurrentPipe();
			}
			concurrentNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}

	/**
	 * Processes the input pipe and passes the result to the output pipe
	 */
	public void process() {
		try {
			String line = input.readAndWait();
			while (line != null) {
				String processedLine = processLine(line);
				if (processedLine != null) {
					output.writeAndWait(processedLine);
				}
				line = input.readAndWait();
			}
			output.writePoisonPill();
		} catch (InterruptedException e) {
		}
	}
		protected abstract String processLine(String line);

}
