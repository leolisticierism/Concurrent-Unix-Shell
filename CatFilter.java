import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CatFilter extends ConcurrentFilter {
	String command;
	boolean existence = true;
	boolean includeredirect = false;
	
	public CatFilter(String command) {
	this.command = command;
	}
	
	/**
	 * This method processes a Cat command
	 */
	public void process() {
		if(command.trim().equals("cat")) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command.trim()));	// raise error if no paramater
		} else if (command.trim().contains(">")){
			includeredirect = true;
			System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(command.trim().substring(command.indexOf(">")).trim())); // raise error if > is included
		} else if (command.trim().charAt(3) != ' '){
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		// raise error if incorrect format
		} else {
			String parameter = command.trim().substring(4);
			if (!new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + parameter).exists()) {	// check if the file can be found
				System.out.print(Message.FILE_NOT_FOUND.with_parameter(command.trim()));
				existence = false;
				output = null;
			} else {
				try {
					Scanner filereader = new Scanner(new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + parameter));
					if(this.output == null) {
						this.output = new ConcurrentPipe();
					}
					while (filereader.hasNextLine()) {									//write out the lines as output
						String processed = processLine(filereader.nextLine());
						this.output.write(processed);
					}
				} catch (FileNotFoundException e) {
				e.printStackTrace();
				}
			}
		}
	}
	
	protected String processLine(String line) {
		return line;
	}
	
	public void run() {
		Scanner filereader = null;
		try {
			try {
				filereader = new Scanner(new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + command.trim().substring(4)));
				while (filereader.hasNextLine()) {
					this.output.write(filereader.nextLine());
				}
				filereader.close();
				this.output.writePoisonPill();
			} catch (FileNotFoundException e) {
			}
		} catch (InterruptedException e) {
			filereader.close();
		}
	}
}
