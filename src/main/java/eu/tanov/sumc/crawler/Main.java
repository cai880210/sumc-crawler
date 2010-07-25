package eu.tanov.sumc.crawler;

import eu.tanov.sumc.crawler.configuration.ConfigurationCrawler;
import eu.tanov.sumc.crawler.coordinates.CoordinatesCrawler;


public class Main {
	private static final String ARGUMENT_ACTION_CONFIGURATION = "configuration";
	private static final String ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE = "-output";

	private static final String ARGUMENT_ACTION_COORDINATES = "coordinates";
	private static final String ARGUMENT_PARAMETER_COORDINATES_OUTPUT_FILE = "-output";
	private static final String ARGUMENT_PARAMETER_COORDINATES_CONFIGURATION = "-configuration";
	private static final String ARGUMENT_PARAMETER_COORDINATES_OLD_COORDINATES = "-old";
	private static final String ARGUMENT_PARAMETER_COORDINATES_LOG = "-log";
	
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
			action = createConfigurationAction(args);
		} else if (ARGUMENT_ACTION_COORDINATES.equals(args[0])) {
			action = createCoordinatesAction(args);
		} else {
			//unknown action
			showHelp();
			return;
		}
		
		if (action != null) {
			action.run();
		}
	}

	private static Runnable createConfigurationAction(String[] args) {
		if (args.length != 3) {
			showHelp();
			return null;
		}
		
		final String outputFilename =
			getArgument(args, ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE);
		
		if (outputFilename == null) {
			showHelp();
			return null;
		}
		return new ConfigurationCrawler(outputFilename);
	}


	private static Runnable createCoordinatesAction(String[] args) {
		if (args.length != 9) {
			showHelp();
			return null;
		}
		
		final String outputFilename =
			getArgument(args, ARGUMENT_PARAMETER_COORDINATES_OUTPUT_FILE);

		final String configurationFilename =
			getArgument(args, ARGUMENT_PARAMETER_COORDINATES_CONFIGURATION);
		
		final String oldCoordinatesFilename =
			getArgument(args, ARGUMENT_PARAMETER_COORDINATES_OLD_COORDINATES);
		
		final String logFilename =
			getArgument(args, ARGUMENT_PARAMETER_COORDINATES_LOG);
		
		
		if (outputFilename == null || configurationFilename == null ||
				oldCoordinatesFilename == null || logFilename == null) {
			showHelp();
			return null;
		}
		return new CoordinatesCrawler(outputFilename, configurationFilename,
				oldCoordinatesFilename, logFilename);
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
				ARGUMENT_PARAMETER_CONFIGURATION_OUTPUT_FILE + " <filename>"
		);
		System.out.println("To get coordinates use arguments: "+
				ARGUMENT_ACTION_COORDINATES+" "+
				ARGUMENT_PARAMETER_COORDINATES_OUTPUT_FILE + " <filename> " + 
				ARGUMENT_PARAMETER_COORDINATES_CONFIGURATION + " <filename> " + 
				ARGUMENT_PARAMETER_COORDINATES_OLD_COORDINATES + " <filename> " + 
				ARGUMENT_PARAMETER_COORDINATES_LOG + " <filename> " 
		);
	}
	


}
