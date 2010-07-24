package eu.tanov.sumc.crawler;

import java.util.List;

import org.apache.log4j.Logger;

import eu.tanov.sumc.crawler.model.Line;
import eu.tanov.sumc.crawler.model.SumcConfiguration;
import eu.tanov.sumc.crawler.model.VehicleType;


public class Main {
	private static final Logger log = Logger.getLogger(Main.class.getName());

	private static final long DEFAULT_TIMEOUT_SLEEP = 200;
//	private static final long DEFAULT_TIMEOUT_AFTER_ERROR = 10000;

	public Main() {
	}

	public static void main(String[] args) {
		final Provider provider = new Provider();
		provider.connect();

		final SumcConfiguration result = getConfiguration(provider);
		
		System.out.println(result.toString());
		
	}

	private static SumcConfiguration getConfiguration(Provider provider) {
		final SumcConfiguration result = new SumcConfiguration();

		final List<String> vehicleTypes = provider.getVehicleTypes();
		for (final String vehicleTypeName : vehicleTypes) {
			result.getVehicleTypes().add(getVehicleType(provider, vehicleTypeName));
		}

		return result;
	}

	private static VehicleType getVehicleType(Provider provider, String vehicleTypeName) {
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

	private static Line getLine(Provider provider, String vehicleTypeName, String lineName) {
		final Line result = new Line();
		result.setName(lineName);
		result.getBusStopsDirection1().addAll(provider.getBusStops(vehicleTypeName, lineName, true));
		result.getBusStopsDirection2().addAll(provider.getBusStops(vehicleTypeName, lineName, false));
		
		return result;
	}


}
