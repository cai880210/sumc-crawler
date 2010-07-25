package eu.tanov.sumc.crawler.configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;

import eu.tanov.sumc.crawler.model.Line;
import eu.tanov.sumc.crawler.model.SumcConfiguration;
import eu.tanov.sumc.crawler.model.VehicleType;

public class ConfigurationCrawler implements Runnable {
	private static final Logger log = Logger.getLogger(ConfigurationCrawler.class.getName());

	private final String outputFilename;
	private final ConfigurationProvider provider;

	private static final long DEFAULT_TIMEOUT_SLEEP = 200;
//	private static final long DEFAULT_TIMEOUT_AFTER_ERROR = 10000;

	public ConfigurationCrawler(String outputFilename) {
		this.outputFilename = outputFilename;
		this.provider = new ConfigurationProvider();
	}
	
	public void run() {
		if (!checkFile()) {
			log.error("can't open file "+outputFilename);
			return;
		}
		provider.connect();
		
		final SumcConfiguration result = getConfiguration(provider);
		log.debug("configuration: "+result.toString());

		try {
			writeToFile(result);
			log.debug("saved to "+outputFilename);
		} catch (IOException e) {
			log.error("could not save configuration to "+outputFilename, e);
		}
	}

	/**
	 * save time if file could not be opened - do not crawl whole site
	 * XXX improve
	 * @return 
	 */
	private boolean checkFile() {
		try {
			writeToFile(null);
			return true;
		} catch (IOException e) {
			log.error("could not save configuration to "+outputFilename, e);
			return false;
		}
	}

	private void writeToFile(SumcConfiguration result) throws IOException {
		final FileWriter outFile = new FileWriter(outputFilename);
		final PrintWriter out = new PrintWriter(outFile);
		//allow result to be null
		out.println(String.valueOf(result));
		out.close();
		outFile.close();
	}

	private SumcConfiguration getConfiguration(ConfigurationProvider provider) {
		final SumcConfiguration result = new SumcConfiguration();

		final List<String> vehicleTypes = provider.getVehicleTypes();
		for (final String vehicleTypeName : vehicleTypes) {
			result.getVehicleTypes().add(getVehicleType(provider, vehicleTypeName));
		}

		return result;
	}

	private VehicleType getVehicleType(ConfigurationProvider provider, String vehicleTypeName) {
		final VehicleType result = new VehicleType();
		result.setName(vehicleTypeName);
		final List<String> lines = provider.getLines(vehicleTypeName);
		for (final String lineName : lines) {
//			boolean success = false;
//			while(!success) {
//				try {
					result.getLines().add(getLine(provider, vehicleTypeName, lineName));
//					success = true;
//				} catch (Throwable e) {
//					log.info("error, retring", e);
//					//try again
//					try {
//						Thread.sleep(DEFAULT_TIMEOUT_AFTER_ERROR);
//					} catch (InterruptedException e1) {
//						log.warn("while sleeping", e);
//					}
//				}
//				
//			}
			//keep server load
			try {
				Thread.sleep(DEFAULT_TIMEOUT_SLEEP);
			} catch (InterruptedException e) {
				log.warn("while sleeping", e);
			}
		}

		return result;
	}

	private Line getLine(ConfigurationProvider provider, String vehicleTypeName, String lineName) {
		final Line result = new Line();
		result.setName(lineName);
		result.getBusStopsDirection1().addAll(provider.getBusStops(vehicleTypeName, lineName, true));
		result.getBusStopsDirection2().addAll(provider.getBusStops(vehicleTypeName, lineName, false));
		
		return result;
	}


}