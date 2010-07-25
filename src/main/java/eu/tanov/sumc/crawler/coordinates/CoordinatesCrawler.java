package eu.tanov.sumc.crawler.coordinates;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import eu.tanov.sumc.crawler.configuration.ConfigurationCrawler;
import eu.tanov.sumc.crawler.model.SumcConfiguration;

public class CoordinatesCrawler implements Runnable {
	private static final Logger log = Logger.getLogger(CoordinatesCrawler.class.getName());

	private final String outputFilename;
	private final String configurationFilename;
	private final String oldCoordinatesFilename;
	private final String logFilename;

	public CoordinatesCrawler(String outputFilename,
			String configurationFilename, String oldCoordinatesFilename,
			String logFilename) {
		this.outputFilename = outputFilename;
		this.configurationFilename = configurationFilename;
		this.oldCoordinatesFilename = oldCoordinatesFilename;
		this.logFilename = logFilename;
	}

	public void run() {
		// TODO Auto-generated method stub
//		check output file
//		chech log file
		//get old coordinates
		//fill map

		//get configuration
		//for each in configuration .. get coordinates from old coordinates
		//if not - add to list
		
		//iterate list
		
		//get list with new
		//get list with removed
		
		if (!checkFiles()) {
			return;
		}
		
	}

	/**
	 * save time if file could not be opened - do not crawl whole site
	 */
	private boolean checkFiles() {
		return checkFile(outputFilename) && checkFile(logFilename);
	}

	/**
	 * XXX improve
	 */
	private boolean checkFile(String filename) {
		try {
			writeToFile(filename, "check write permissions");
			return true;
		} catch (IOException e) {
			log.error("could not write to "+filename, e);
			return false;
		}
	}

	private void writeToFile(String filename, String content) throws IOException {
		final FileWriter outFile = new FileWriter(filename);
		final PrintWriter out = new PrintWriter(outFile);
		out.println(content);
		out.close();
		outFile.close();
	}
}
