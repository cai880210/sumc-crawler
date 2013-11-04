package eu.tanov.sumc.crawler.configuration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.tanov.sumc.crawler.model.Line;
import eu.tanov.sumc.crawler.model.SumcConfiguration;
import eu.tanov.sumc.crawler.model.VehicleType;

public class ConfigurationCrawler implements Runnable {
	private static final Logger log = Logger.getLogger(ConfigurationCrawler.class.getName());

	private static final String CHARSET = "UTF-8";
	private static final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

	private final String outputFilename;
	private final ConfigurationProvider provider;

	private static final long DEFAULT_TIMEOUT_SLEEP = 200;
	private static final long DEFAULT_TIMEOUT_AFTER_ERROR = 5000;


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
		final PrintWriter out = new PrintWriter(outputFilename, CHARSET);
		out.println(HEADER_XML);
		//allow result to be null
		out.println(String.valueOf(result));
		out.close();
	}

	private SumcConfiguration getConfiguration(ConfigurationProvider provider) {
		final SumcConfiguration result = new SumcConfiguration();

		final List<String> vehicleTypes = provider.getVehicleTypes();
		for (final String vehicleTypeLabel : vehicleTypes) {

			boolean success = false;
			while(!success) {
				try {
					result.getVehicleTypes().add(getVehicleType(provider, vehicleTypeLabel));
					success = true;
				} catch (Exception e) {
					log.info("error, retring", e);
					//try again
					try {
						Thread.sleep(DEFAULT_TIMEOUT_AFTER_ERROR);
					} catch (InterruptedException e1) {
						log.warn("while sleeping", e);
					}
				}
			}			
			
		}

		result.setDateCreated(new Date());
		return result;
	}

	private VehicleType getVehicleType(ConfigurationProvider provider, String vehicleTypeLabel) {
		final VehicleType result = new VehicleType();
		result.setLabel(vehicleTypeLabel);
		final List<String> lines = provider.getLines(vehicleTypeLabel);
		for (final String lineLabel : lines) {
			boolean success = false;
			while(!success) {
				try {
					result.getLines().add(getLine(provider, vehicleTypeLabel, lineLabel));
					success = true;
				} catch (Exception e) {
					log.info("error, retring", e);
					//try again
					try {
						Thread.sleep(DEFAULT_TIMEOUT_AFTER_ERROR);
					} catch (InterruptedException e1) {
						log.warn("while sleeping", e);
					}
				}
				
			}
			//keep server load
			try {
				Thread.sleep(DEFAULT_TIMEOUT_SLEEP);
			} catch (InterruptedException e) {
				log.warn("while sleeping", e);
			}
		}

		return result;
	}

	private Line getLine(ConfigurationProvider provider, String vehicleTypeLabel, String lineLabel) {
		final Line result = new Line();
		result.setLabel(lineLabel);
		result.getDirection1().addAll(provider.getBusStops(vehicleTypeLabel, lineLabel, true));
		result.getDirection2().addAll(provider.getBusStops(vehicleTypeLabel, lineLabel, false));
		//TODO keep server load
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				log.warn("while sleeping", e);
			}
		return result;
	}


}
