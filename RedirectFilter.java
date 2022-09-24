import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class RedirectFilter extends ConcurrentFilter {

	/**
	 * destination of redirection
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * writer for writing - set in process(), leave as null till then
	 */
	private PrintWriter printWriter;

	/**
	 * Constructs a RedirectFilter given a >.
	 * 
	 * @param cmd cmd is guaranteed to either be ">" or ">" followed by a space.
	 * @throws IllegalArgumentException if a file parameter was not provided
	 */
	public RedirectFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just ">" =>
		// > needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		dest = CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + relativeDest;

		File destFile = new File(dest);
		if (destFile.isFile()) {
			destFile.delete();
		}
	}
	
	/**
	 * overrides the run method and close the printWriter in the try catch
	 */
	@Override
	public void run() {
		try {
			try {
				printWriter = new PrintWriter(new FileWriter(new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + command.trim().substring(2))));
				String line = input.readAndWait();
				while(line != null) {
					processLine(line);
					line = input.readAndWait();
				}
				printWriter.close();
			} catch (IOException e) {
			}
		} catch (InterruptedException e) {
			printWriter.close();
		}
	}
	
	/**
	 * Overrides SequentialFilter.process to close write stream
	 */
	@Override
	public void process() {
		if(command.trim().equals(">")) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command.trim()));	// if no parameter, raise error
		} else if (command.trim().charAt(1) != ' '){
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		// if incorrect format, raise error
		} else {
			String parameter = command.trim().substring(2);
			try {
				PrintStream result = new PrintStream(new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + parameter)); // create a printstream and type the output to the file
				if(this.output == null) {
					this.output = new ConcurrentPipe();
				}
				while (!input.isEmpty()) {
					String line = input.read();
					String processed = processLine(line);
					result.println(processed);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

		@Override
	protected String processLine(String line) {
		printWriter.println(line);
		return null;
	}

	@Override
	public void setNextFilter(Filter nextFilter) {
		throw new IllegalArgumentException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}

}
