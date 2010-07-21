package eu.tanov.sumc.crawler;

import java.util.List;


public class Main {

	public Main() {
	}

	public static void main(String[] args) {
		final Provider provider = new Provider();

		final List<String> vehicleTypes = provider.getVehicleTypes();
		List<String> stops = provider.getStops("Автобусен", "2", false);
//		provider.close();
		
		System.out.println(stops);
		
	}


}
