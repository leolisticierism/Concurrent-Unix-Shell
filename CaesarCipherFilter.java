public class CaesarCipherFilter extends ConcurrentFilter {
	String command;
	int parameter;
	
	public CaesarCipherFilter(String command) {
		this.command = command;
	}
	
	/**
	 * This method processes a CaesarCipher command
	 */
	public void process() {
		if(command.trim().equals("cc")) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command.trim()));	// raise error if no parameter
		} else if (command.trim().charAt(2) != ' '){
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(command.trim()));		// raise error if the format is incorrect
		} else {
			this.parameter = Integer.parseInt(command.trim().substring(3));		//find the number to rotate
			while(!input.isEmpty()) {
				String line = input.read();
				String processed = processLine(line);
				output.write(processed);		//write output to the pipe
			}
		}
	}
	@Override
	/**
	 * This method process a single line in CaesarCipher Filter.
	 * @return the processed line
	 */
	protected String processLine(String line) {
		String newstring = "";
		for (int i = 0; i < line.length(); i++) {
			if(line.charAt(i) >= 65 && line.charAt(i) <= 90) {	// rotation for upper case letters
				parameter = parameter % 26;
				int newvalue = line.charAt(i) + parameter;
				if(newvalue > 90) {
					newvalue -=26;
				}
				newstring += (char) newvalue;
			} else if (line.charAt(i) >= 97 && line.charAt(i) <= 122) {	// rotation for lower case letters
				parameter = parameter % 26;
				int newvalue = line.charAt(i) + parameter;
				if(newvalue > 122) {
					newvalue -=26;
				}
				newstring += (char) newvalue;
			} else {
				newstring += line.charAt(i);	// no rotation for other characters
			}
		}
		return newstring;
	}
}
