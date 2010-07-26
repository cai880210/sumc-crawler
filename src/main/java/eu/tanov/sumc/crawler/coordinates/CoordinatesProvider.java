package eu.tanov.sumc.crawler.coordinates;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import eu.tanov.sumc.crawler.model.BusStop;

public class CoordinatesProvider {
	private static final Logger log = Logger.getLogger(CoordinatesProvider.class.getName());

	private static final String URL_LOCATION = "http://www.getlatlon.com/?Sofia";

	private final WebDriver location = new ChromeDriver();
	private final WebDriver bgmaps = new ChromeDriver();

	/**
	 * get coordinates and set to busStop
	 */
	public void fetchCoordinates(BusStop busStop) {
		prepareLocation(busStop);
		prepareBgmaps(busStop);
	}

	private void prepareBgmaps(BusStop busStop) {
		bgmaps.get(busStop.getBgmapsLink());
		
	}

	private void prepareLocation(BusStop busStop) {
		location.get(URL_LOCATION);
		
	}

}
