import java.io.File;

public class ChangeDirectoryFilter extends ConcurrentFilter {

	private String command;

	public ChangeDirectoryFilter(String cmd) {
		super();
		command = cmd;

	}
	@Override
	protected String processLine(String line) {
		return null;
	}

	@Override
	public void process() {
		if(command.trim().equals("cd")) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command.trim()));	//raise error if no parameter
		} else if (command.trim().charAt(2) != ' '){
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		//raise error if the format is incorrect
		} else {
			String oldDirectory = CurrentWorkingDirectory.get();	//get the old directory
			String parameter = command.trim().substring(3);		//get the directory to go to
			if(parameter.equals("..")) {
				CurrentWorkingDirectory.setTo(CurrentWorkingDirectory.get().substring(0, CurrentWorkingDirectory.get().lastIndexOf(CurrentWorkingDirectory.FILE_SEPARATOR)));	//find the last directory if .. is the command
			} else if (!parameter.equals(".")) {
				while(parameter.contains(CurrentWorkingDirectory.FILE_SEPARATOR)) {
					CurrentWorkingDirectory.setTo(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + parameter.substring(0,parameter.indexOf(CurrentWorkingDirectory.FILE_SEPARATOR)));
					parameter = parameter.substring(parameter.indexOf(CurrentWorkingDirectory.FILE_SEPARATOR) + 1);
				}
				CurrentWorkingDirectory.setTo(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + parameter);	//update the new directory based on user input
			}
			if (command.charAt(2) != ' ') {
				System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		//raise error if the format is incorrect
			}
			else if (!new File(CurrentWorkingDirectory.get()).isDirectory()) {
				System.out.print(Message.DIRECTORY_NOT_FOUND.with_parameter(command.trim()));	//if the directory is not found, raise error
				CurrentWorkingDirectory.setTo(oldDirectory);
			}
		this.output = null;
		}

	}
}
