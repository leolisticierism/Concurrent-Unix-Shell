public final class CurrentWorkingDirectory {

	/**
	 * The file separator used for generating paths.
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static String currentWorkingDirectory = System.getProperty("user.dir");

	private CurrentWorkingDirectory() {
	}

	/**
	 * Sets the shell's new current working directory.
	 * 
	 * @param newDirectory to set the shell's current file path to
	 */
	public static void setTo(String newDirectory) {
		currentWorkingDirectory = newDirectory;
	}

	/**
	 * {@return the current working directory}
	 */
	public static String get() {
		return currentWorkingDirectory;
	}

	/**
	 * Resets the current working directory to System.getProperty("user.dir")
	 */
	public static void reset() {
		currentWorkingDirectory = System.getProperty("user.dir");
	}
}
