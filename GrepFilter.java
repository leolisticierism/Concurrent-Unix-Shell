public class GrepFilter extends ConcurrentFilter {
	String command;
	String parameter;
	
	public GrepFilter(String command) {
	this.command = command;
	}
	
	/**
	 * This method processes a grep command
	 */
	public void process() {
		if(input == null) {
			output = null;
		} else {
			if(command.trim().equals("grep")) {
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command.trim()));	//raise error if missing parameter
			} else if (command.trim().charAt(4) != ' '){
				System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		//raise error if format is incorrect
			} else {
				parameter = command.trim().substring(5);
				while(!input.isEmpty()) {
					String line = input.read();			//read and process line by line
					String processed = processLine(line);
					if (processed != null){
						output.write(line);
					}
				}
			}
		}
	}

	@Override
	/**
	 * This method processes a single line. If the line contains the parameter, the line is returned. If not, return null
	 */
	protected String processLine(String line) {
		if(line.contains(parameter)) {
			return line;
		} else {
			return null;
		}
	}
}
