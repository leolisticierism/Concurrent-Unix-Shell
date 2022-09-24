import java.util.ArrayList;
import java.util.List;
public class ConcurrentCommandBuilder {
	public static List<ConcurrentFilter> createFiltersFromCommand(String command) {
		String cur = command.trim();
		List<ConcurrentFilter> filterList= new ArrayList<ConcurrentFilter>();
		while(cur.contains("|")) {			//use a while loop in case the command has multiple subcommands
			String subCommand = cur.substring(0, cur.indexOf("|")).trim();			//find the command separated by |
			if(constructFilterFromSubCommand(subCommand) != null) {
				filterList.add(constructFilterFromSubCommand(subCommand));
				if (filterList.size() == 1) {
					if (constructFilterFromSubCommand(subCommand) instanceof HeadFilter ||
							constructFilterFromSubCommand(subCommand) instanceof GrepFilter ||
							constructFilterFromSubCommand(subCommand) instanceof WordCountFilter ||
							constructFilterFromSubCommand(subCommand) instanceof UniqFilter ||
							constructFilterFromSubCommand(subCommand) instanceof CaesarCipherFilter) {
						System.out.print(Message.REQUIRES_INPUT.with_parameter(subCommand.trim()));		//Those commands require inputs. If the size of the arraylist is 1, it means no input, and print out the error
					}
					if (constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {   //Cd command cannot pipe into another subcommand
						System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(subCommand.trim()));
					}
				}
				if (filterList.size() > 1) {
					if (constructFilterFromSubCommand(subCommand) instanceof WorkingDirectoryFilter ||
							constructFilterFromSubCommand(subCommand) instanceof ListFilter ||
							constructFilterFromSubCommand(subCommand) instanceof CatFilter ||
							constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_INPUT.with_parameter(subCommand.trim()));	//Those commands cannot have input. If the size of the arraylist is larger than 1, it means the command has input, and the error should be raised.
					}
					if (constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(subCommand.trim()));
					}
				}
			}
			cur = cur.substring(cur.indexOf("|")+1).trim();			//delete the first filter from the user input
		}
		if (cur.contains(">")) {
			if(!cur.startsWith(">")) {
				String subCommand = cur.substring(0, cur.indexOf(">")).trim();		//find the command separated by >
				if(constructFilterFromSubCommand(subCommand) != null) {
					filterList.add(constructFilterFromSubCommand(subCommand));
					if (filterList.size() == 1) {
						if (constructFilterFromSubCommand(subCommand) instanceof HeadFilter ||
								constructFilterFromSubCommand(subCommand) instanceof GrepFilter ||
								constructFilterFromSubCommand(subCommand) instanceof WordCountFilter ||
								constructFilterFromSubCommand(subCommand) instanceof UniqFilter ||
								constructFilterFromSubCommand(subCommand) instanceof CaesarCipherFilter) {
							System.out.print(Message.REQUIRES_INPUT.with_parameter(subCommand.trim()));   //Those commands require inputs. If the size of the arraylist is 1, it means no input, and print out the error
						}
						if (constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {
							System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(subCommand.trim()));	//Cd command cannot pipe into another subcommand
						}
					}
					if (filterList.size() > 1) {
						if (constructFilterFromSubCommand(subCommand) instanceof WorkingDirectoryFilter ||
								constructFilterFromSubCommand(subCommand) instanceof ListFilter ||
								constructFilterFromSubCommand(subCommand) instanceof CatFilter ||
								constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {
							System.out.print(Message.CANNOT_HAVE_INPUT.with_parameter(subCommand.trim()));  //Those commands cannot have input. If the size of the arraylist is larger than 1, it means the command has input, and the error should be raised.
						}
						if (constructFilterFromSubCommand(subCommand) instanceof ChangeDirectoryFilter) {
							System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(subCommand.trim()));
						}
					}
				}
			}
			cur = cur.substring(cur.indexOf(">")).trim();			// find the redirection command, and check errors
			if(constructFilterFromSubCommand(cur) != null) {
				filterList.add(constructFilterFromSubCommand(cur));
				if (filterList.size() == 1) {
					if (constructFilterFromSubCommand(cur) instanceof HeadFilter ||
							constructFilterFromSubCommand(cur) instanceof GrepFilter ||
							constructFilterFromSubCommand(cur) instanceof WordCountFilter ||
							constructFilterFromSubCommand(cur) instanceof UniqFilter ||
							constructFilterFromSubCommand(cur) instanceof CaesarCipherFilter ||
							constructFilterFromSubCommand(cur) instanceof RedirectFilter) {
						System.out.print(Message.REQUIRES_INPUT.with_parameter(cur.trim()));
					}
					if (constructFilterFromSubCommand(cur) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(cur.trim()));
					}
				}
				if (filterList.size() > 1) {
					if (constructFilterFromSubCommand(cur) instanceof WorkingDirectoryFilter ||
							constructFilterFromSubCommand(cur) instanceof ListFilter ||
							constructFilterFromSubCommand(cur) instanceof CatFilter ||
							constructFilterFromSubCommand(cur) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_INPUT.with_parameter(cur.trim()));
					}
					if (constructFilterFromSubCommand(cur) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(cur.trim()));
					}
				}
			}
		} else {
			if(constructFilterFromSubCommand(cur) != null) {		// if there is no redirection command, then add the last subcommand, and add a print filter
				filterList.add(constructFilterFromSubCommand(cur));
				filterList.add(new PrintFilter());
				if (filterList.size() == 2) {
					if (constructFilterFromSubCommand(cur) instanceof HeadFilter ||
							constructFilterFromSubCommand(cur) instanceof GrepFilter ||
							constructFilterFromSubCommand(cur) instanceof WordCountFilter ||
							constructFilterFromSubCommand(cur) instanceof UniqFilter ||
							constructFilterFromSubCommand(cur) instanceof CaesarCipherFilter) {
						System.out.print(Message.REQUIRES_INPUT.with_parameter(cur.trim()));
						filterList.remove(filterList.size()-1);
					}
				}
				if (filterList.size() > 2) {
					if (constructFilterFromSubCommand(cur) instanceof WorkingDirectoryFilter ||
							constructFilterFromSubCommand(cur) instanceof ListFilter ||
							constructFilterFromSubCommand(cur) instanceof CatFilter ||
							constructFilterFromSubCommand(cur) instanceof ChangeDirectoryFilter) {
						System.out.print(Message.CANNOT_HAVE_INPUT.with_parameter(cur.trim()));
						filterList.remove(filterList.size()-1);
					}
				}
			}
		}
		linkFilters(filterList);		// link elements in the array list
		if (filterList.size() > 0) {
			filterList.get(0).input = new ConcurrentPipe();	//set the input pipe of the first element as a new pipe
		}
		return filterList;
	}

	/**
	 * This method takes a subcommand, and returns a filter based on the command
	 * @param subCommand the subcommand from user input
	 * @return the corresponding filter of the subcommand, null if the subcommand cannot be categorized
	 */
	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand) {
		if (subCommand.trim().equals("pwd")) {		// a pwd command only contains pwd
			return new WorkingDirectoryFilter(subCommand);
		}
		else if (subCommand.trim().equals("ls")) {	// a ls command only contains ls
			return new ListFilter(subCommand);
		}
		else if (subCommand.trim().startsWith("cd")) {	//a cd command starts with cd
			return new ChangeDirectoryFilter(subCommand);
		} 
		else if (subCommand.trim().startsWith("cat")) {	//a cat command starts with cat
			return new CatFilter(subCommand);
		}
		else if (subCommand.trim().equals("head")) {	//a head command only contains head
			return new HeadFilter(subCommand);
		}
		else if (subCommand.trim().startsWith("grep")) { //a grep command starts with grep
			return new GrepFilter(subCommand);
		}
		else if (subCommand.trim().equals("uniq")) {	//a uniq command only contains uniq
			return new UniqFilter(subCommand);
		}
		else if (subCommand.trim().equals("wc")) {	//a wc command only contains headwc
			return new WordCountFilter(subCommand);
		}
		else if (subCommand.trim().startsWith("cc")) { //a cc command starts with cc
			return new CaesarCipherFilter(subCommand);
		}
		else if (subCommand.trim().startsWith(">")) {	// a redirection command starts with >
			return new RedirectFilter(subCommand);
		}
		else {
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(subCommand.trim())); // if the subcommand cannot be categorized, raise an error and return null
			return null;
		}
	}

	/**
	 * This method links the filters from the list
	 * @param filters a list of filters
	 * @return true when filters are linked
	 */
	private static boolean linkFilters(List<ConcurrentFilter> filters) {
		if (filters.size() > 0) {
			for(int i=0; i<filters.size()-1; i++) {
				if (filters.get(i) instanceof ChangeDirectoryFilter) {	// if it is a cd filter, then do not link, since cd filter does not take input or has output
					continue;
				} else {
					filters.get(i).setNextFilter(filters.get(i+1));	// link the adjacent filters 
				}
			}
		}
		return true;
	}
}
