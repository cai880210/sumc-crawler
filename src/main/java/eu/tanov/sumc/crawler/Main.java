package eu.tanov.sumc.crawler;

import eu.tanov.sumc.crawler.configuration.ConfigurationCrawler;


public class Main {
	private static final String ARGUMENT_ACTION_CONFIGURATION = "configuration";
	//TODO: coordinates:
//	private static final String ARGUMENT_ACTION_COORDINATES = "coordinates";
	private static final String ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE = "-output";

	public Main() {
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			//no arguments
			showHelp();
			return;
		}
		final Runnable action;
		if (ARGUMENT_ACTION_CONFIGURATION.equals(args[0])) {
			if (args.length != 3) {
				showHelp();
				return;
			}
			
			final String outputFilename =
				getArgument(args, ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE);
			
			if (outputFilename == null) {
				showHelp();
				return;
			}
			action = new ConfigurationCrawler(outputFilename);
//TODO coordinates:			
//		} else if (ARGUMENT_ACTION_COORDINATES.equals(args[0])) {
//			
		} else {
			//unknown action
			showHelp();
			return;
		}
		action.run();
	}

	/**
	 * @return null if not found
	 */
	private static String getArgument(String [] args, String argumentName) {
		boolean found = false; 
		for (String argument : args) {
			if (found) {
				return argument;
			}
			if (argumentName.equals(argument)) {
				found = true;
			}
		}
		
		return null;
	}

	private static void showHelp() {
		System.out.println("Usage:");
		System.out.println("To get configuration use arguments: "+
				ARGUMENT_ACTION_CONFIGURATION+" "+
				ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE + " <filename>");
		//TODO coordinates:
//		System.out.println("To get coordinates run "+Arrays.toString(args)+" -output filename");
	}
	


}
