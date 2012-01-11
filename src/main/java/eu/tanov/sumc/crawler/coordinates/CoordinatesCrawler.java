package eu.tanov.sumc.crawler.coordinates;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class CoordinatesCrawler implements Runnable {
	private static final Logger log = Logger.getLogger(CoordinatesCrawler.class.getName());

	private static final String CHARSET = "UTF-8";

	private static final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	private static final String COORDINATES_ROOT_BEGIN = "<busStops>";
	private static final String COORDINATES_ROOT_END = "</busStops>";

	private static final String FORMAT_XML_DATE = "\n\t<!-- %s -->\n\t<date>%s</date>\n";

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
		if (!checkFiles()) {
			return;
		}
		final Map<Integer, BusStop> codeToBusStop = createCodeToBusStopMap();
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
		
		final Set<BusStop> usedBusStops = getUsedBusStops(configuration, codeToBusStop);
		
		final Collection<BusStop> addedBusStops = getAddedBusStops(codeToBusStop.values(), usedBusStops);
		final Collection<BusStop> removedBusStops = getRemovedBusStops(codeToBusStop.values(), usedBusStops);

		fetchNewCoordinates(usedBusStops, addedBusStops);

		writeResult(configuration, usedBusStops);
		writeLog(configuration, addedBusStops, removedBusStops);
	}

	private void fetchNewCoordinates(Set<BusStop> usedBusStops, Collection<BusStop> addedBusStops) {
		final CoordinatesProvider coordinatesProvider = new CoordinatesProvider();
		
		int i = 0;
		for (BusStop busStop : addedBusStops) {
			i++;
			//usedBusStops can be not used (because busStop.hashCode() and busStop.equals() only use .code, but for safety)  
			if (!usedBusStops.remove(busStop)) {
				throw new IllegalStateException("Removing unknown bus stop: "+busStop);
			}
			log.info(String.format("Bus stop %s of %s", i, addedBusStops.size()));
			coordinatesProvider.fetchCoordinates(busStop);
			usedBusStops.add(busStop);
		}
		
		coordinatesProvider.close();
	}

	private Collection<BusStop> getAddedBusStops(Collection<BusStop> oldBusStops, Collection<BusStop> newBusStops) {
		final List<BusStop> result = new ArrayList<BusStop>(newBusStops);
		result.removeAll(oldBusStops);
		return result;
	}
	
	private Collection<BusStop> getRemovedBusStops(Collection<BusStop> oldBusStops, Collection<BusStop> newBusStops) {
		final List<BusStop> result = new ArrayList<BusStop>(oldBusStops);
		result.removeAll(newBusStops);
		return result;
	}
	
	private void writeLog(SumcConfiguration configuration, Collection<BusStop> addedBusStops, Collection<BusStop> removedBusStops) {
		final String result = "\n\nFrom "+new SimpleDateFormat().format(configuration.getDateCreated()) +
			"\nAdded: "+CollectionsHelper.toStringNoSpaces(addedBusStops)+
			"\nRemoved: "+CollectionsHelper.toStringNoSpaces(removedBusStops);
		try {
			writeToFile(logFilename, result);
		} catch (IOException e) {
			log.error("could not write log to "+logFilename+", result: "+result, e);
			return;
		}
	}

	private void writeResult(SumcConfiguration configuration, Set<BusStop> usedBusStops) {
		final String result = HEADER_XML+"\n"+
			COORDINATES_ROOT_BEGIN +
				xmlDate(configuration)+
				CollectionsHelper.toStringNoSpaces(usedBusStops)+"\n"+
			COORDINATES_ROOT_END;
		try {
			writeToFile(outputFilename, result);
		} catch (IOException e) {
			log.error("could not write to "+outputFilename+", result: "+result, e);
			return;
		}
	}

	private String xmlDate(SumcConfiguration configuration) {
		final String timeAsString = new SimpleDateFormat().format(configuration.getDateCreated());
		return String.format(FORMAT_XML_DATE, timeAsString, configuration.getDateCreated().getTime());
	}

	/**
	 * @return sorted set
	 */
	private Set<BusStop> getUsedBusStops(SumcConfiguration configuration, Map<Integer, BusStop> codeToBusStop) {
		final Set<BusStop> result = new TreeSet<BusStop>(new Comparator<BusStop>() {
			public int compare(BusStop arg0, BusStop arg1) {
				return arg0.getCode() - arg1.getCode();
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
		return result;
	}

	/**
	 * @return null if error else Map BusStopCode > BusStop 
	 */
	private Map<Integer, BusStop> createCodeToBusStopMap() {
//parse oldCoordinates
		final List<BusStop> oldCoordinates;
		try {
			oldCoordinates = new CoordinatesParser(oldCoordinatesFilename).parse();
		} catch (Exception e) {
			log.error("Exception while parsing coordinates: "+oldCoordinatesFilename, e);
			return null;
		}

//create map
		final Map<Integer, BusStop> result = new HashMap<Integer, BusStop>(oldCoordinates.size());
		for (BusStop busStop : oldCoordinates) {
			result.put(busStop.getCode(), busStop);
		}
		return result;
	}

	/**
	 * save time if file could not be opened - do not crawl whole site
	 */
	private boolean checkFiles() {
		return checkFile(outputFilename);
		//do not check log && checkFile(logFilename);
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
		final PrintWriter out = new PrintWriter(filename, CHARSET);
		out.println(content);
		out.close();
	}
}
