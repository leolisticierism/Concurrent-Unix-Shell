
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Liang Zhuang
 */

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 */
public class ConcurrentREPL {

	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {

		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		
		// create four array lists. 
		//The first one indicates all the background threads
		List<Thread> threads = new ArrayList<Thread>();
		//The second arraylist indicates all the commands corresponding to the thread arraylist (so maybe there are duplicates)
		List<String> commands = new ArrayList<String>();
		//The third arraylist indicates all the commands that still have threads alive, and there are no duplicated commands
		List<String> commands_no_duplicate = new ArrayList<String>();
		//The fourth arraylist indicates whether the command is killed or not. The index corresponds to the third arraylist
		List<Boolean> killed = new ArrayList<Boolean>();

		while (true) {
			System.out.print(Message.NEWCOMMAND);

			// read user command, if its just whitespace, skip to next command
			String cmd = consoleReader.nextLine();
			if (cmd.trim().isEmpty()) {
				continue;
			}

			// exit the REPL if user specifies it
			if (cmd.trim().equals("exit")) {
				break;
			}
			
			// kill a certain command if user types kill
			if (cmd.trim().startsWith("kill")) {
				// constructs command_no_duplicate array list in case kill is called before repl_jobs
				if(threads.size() > 0) {
					int i = 0;
					while (i<threads.size()) {
						if(threads.get(i).isAlive() == true) {
							// add commands that contain threads that are alive and avoid duplicated commands
							if(commands_no_duplicate.size() == 0) {
								commands_no_duplicate.add(commands.get(i));
								//the one just added is not killed
								killed.add(false);
							} else if(!commands_no_duplicate.contains(commands.get(i))) {
								commands_no_duplicate.add(commands.get(i));
								//the one just added is not killed
								killed.add(false);
							}
							i++;
						} else {
							// if a thread is not alive, remove it and remove the corresponding command
							threads.remove(i);
							commands.remove(i);
						}
					}
					int j = 0;
					while (i<commands_no_duplicate.size()) {
						// if the command added to command_no_duplicate has finished executing all the threads, and if the command has not been killed, 
						if(!commands.contains(commands_no_duplicate.get(i)) && killed.get(i) != true) {
							commands_no_duplicate.remove(j);
						} else {
							j++;
						}
					}
				}
				// this is where the real kill part starts ***
				int index = Integer.parseInt(cmd.trim().substring(5));
				// decrease the index by 1 because java arraylist's start index is 0
				index --;
				if(index < commands_no_duplicate.size()) {
					// interrupt all threads that are from the killed command
					for(int i = 0; i < commands.size(); i++) {
						if(commands.get(i).equals(commands_no_duplicate.get(index))) {
							threads.get(i).interrupt();
						}
						// change the corresponding index in killed arraylist to indicate that a command has been killed so the ordering won't get wrong
						killed.set(index, true);
					}
				}
			}
			// constructs command_no_duplicate array list, which is an array list indicating the commands that are currently running
			else if (cmd.trim().equals("repl_jobs")) {
				int num = 1;
				if(threads.size() > 0) {
					int i = 0;
					while (i<threads.size()) {
						if(threads.get(i).isAlive() == true) {
							// add commands that contain threads that are alive and avoid duplicated commands
							if(commands_no_duplicate.size() == 0) {
								commands_no_duplicate.add(commands.get(i));
								//the one just added is not killed
								killed.add(false);
							} else if(!commands_no_duplicate.contains(commands.get(i))) {
								commands_no_duplicate.add(commands.get(i));
								//the one just added is not killed
								killed.add(false);
							}
							i++;
						} else {
							// if a thread is not alive, remove it and remove the corresponding command
							threads.remove(i);
							commands.remove(i);
						}
					}
					int j = 0;
					while (i<commands_no_duplicate.size()) {
						// if the command added to command_no_duplicate has finished executing all the threads, and if the command has not been killed, 
						if(!commands.contains(commands_no_duplicate.get(i)) && killed.get(i) != true) {
							commands_no_duplicate.remove(j);
						} else {
							j++;
						}
					}
				}
				// print out the commands with the corresponding order in the array list
				if (commands_no_duplicate.size() > 0) {
					for (int i=0; i<commands_no_duplicate.size(); i++) {
						if(killed.get(i) == true) {
							num ++;
						} else {
							System.out.println("\t" + num + ". " + commands_no_duplicate.get(i));
							num ++;
						}
					}
				}
			} else {
			try {
				// parse command into sub commands, then into Filters, add final PrintFilter if
				// necessary, and link them together - this can throw IAE so surround in
				// try-catch so appropriate Message is printed (will be the message of the IAE)
				if(cmd.endsWith("&")) {
					String newcmd = cmd.substring(0, cmd.length()-1);
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(newcmd);
					// call process on each of the filters to have them execute
					for (int i = 0; i < filters.size(); i++) {
						//create new thread, start the thread, add command and thread to the corresponding array list
						Thread thread = new Thread(filters.get(i));
						thread.start();
						commands.add(cmd);
						threads.add(thread);
					}
				} else {
					// parse command into sub commands, then into Filters, add final PrintFilter if
					// necessary, and link them together - this can throw IAE so surround in
					// try-catch so appropriate Message is printed (will be the message of the IAE)
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
					for (int i = 0; i < filters.size(); i++) {
						Thread thread = new Thread(filters.get(i));
						thread.start();
						//for foreground commands, join the last filter because the commands should be executed sequentially
						if (i == filters.size()-1) {
							try {
								 thread.join();
							} catch (InterruptedException e) {
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.print(e.getMessage());
			}
			}
		}
		System.out.print(Message.GOODBYE);
		consoleReader.close();

	}

}
