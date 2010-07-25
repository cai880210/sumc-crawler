package eu.tanov.sumc.crawler.coordinates;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import eu.tanov.sumc.crawler.configuration.ConfigurationParser;
import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.model.Line;
import eu.tanov.sumc.crawler.model.SumcConfiguration;
import eu.tanov.sumc.crawler.model.VehicleType;

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
		//TODO iterate list
		
		//TODO get list with new
		//TODO get list with removed
		
		if (!checkFiles()) {
			return;
		}
		final Map<String, BusStop> codeToBusStop = createCodeToBusStopMap();
		if (codeToBusStop == null) {
			return;
		}
		final SumcConfiguration configuration;
		try {
			configuration = new ConfigurationParser(configurationFilename).parse();
		} catch (Exception e) {
			log.error("Exception while parsing configuration: "+configurationFilename, e);
			return;
		}
		
		final List<BusStop> usedBusStops = getUsedBusStops(configuration, codeToBusStop);
		
		//TODO save:
//		private static final String OUTPUT_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
//
//		private static final String OUTPUT_ROOT_BEGIN = "<stations>";
//		private static final String OUTPUT_ROOT_END = "</stations>";

		
	}

	private List<BusStop> getUsedBusStops(SumcConfiguration configuration, Map<String, BusStop> codeToBusStop) {
		final Set<BusStop> result = new TreeSet<BusStop>(new Comparator<BusStop>() {
			public int compare(BusStop arg0, BusStop arg1) {
				//XXX convert to integer:
				return arg0.getCode().compareTo(arg1.getCode());
			}
		});

		for (VehicleType vehicleType : configuration.getVehicleTypes()) {
			for (Line line : vehicleType.getLines()) {
				@SuppressWarnings("unchecked")
				final List<List<BusStop>> directions = Arrays.asList(line.getDirection1(), line.getDirection2());
				for (List<BusStop> direction : directions) {
					for (BusStop withoutCoordinates : direction) {
						final BusStop withCoordinates = codeToBusStop.get(withoutCoordinates.getCode());
						
						if (withCoordinates != null) {
							result.add(withCoordinates);
						} else {
							result.add(withoutCoordinates);
						}
						
					}
				}
			}
		}
		return new ArrayList<BusStop>(result);
	}

	/**
	 * @return null if error else Map BusStopCode > BusStop 
	 */
	private Map<String, BusStop> createCodeToBusStopMap() {
//parse oldCoordinates
		final List<BusStop> oldCoordinates;
		try {
			oldCoordinates = new CoordinatesParser(oldCoordinatesFilename).parse();
		} catch (Exception e) {
			log.error("Exception while parsing coordinates: "+oldCoordinatesFilename, e);
			return null;
		}

//create map
		final Map<String, BusStop> result = new HashMap<String, BusStop>(oldCoordinates.size());
		for (BusStop busStop : oldCoordinates) {
			result.put(busStop.getCode(), busStop);
		}
		return result;
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
