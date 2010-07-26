package eu.tanov.sumc.crawler.coordinates;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.util.BgmapsHelper;
import eu.tanov.sumc.crawler.util.WebElementHelper;

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
		
		//TODO wait for element "FLAG_LOCATION_SELECTED" to be created
		//TODO get content of elmement "latlon"
		//TODO parse it (split by ,)
		
		//TODO set to busStop
	}

	private void prepareBgmaps(BusStop busStop) {
		bgmaps.get(BgmapsHelper.getBgmapsLink(busStop.getCode()));
		
	}

	private void prepareLocation(BusStop busStop) {
		location.get(URL_LOCATION);
		
		addReadyButton(busStop);
		addBgmapsImage(busStop);
	}

	private void addReadyButton(BusStop busStop) {
		final JavascriptExecutor javascriptExecutor = WebElementHelper.toJavascriptExecutor(location);
		javascriptExecutor.executeScript("var button = document.createElement('input');" +
				"button.type='button';" +
				"button.value = 'This is bus station \"'+arguments[0]+'\"';" +
				"button.setAttribute('onClick'," +
					"'var flag = document.createElement(\"div\");" +
					"flag.id=\"FLAG_LOCATION_SELECTED\";" +
					"document.getElementById(\"latlon\").parentElement.appendChild(flag);" +
					"');" +
				"document.getElementById('latlon').parentElement.appendChild(button);"
					,
				busStop.getLabel()
		);
	}

	private void addBgmapsImage(BusStop busStop) {
		
		final JavascriptExecutor javascriptExecutor = WebElementHelper.toJavascriptExecutor(location);

		javascriptExecutor.executeScript("var img = document.createElement('img');" +
				"img.src = arguments[0];" +
				"document.getElementById('latlon').parentElement.appendChild(img);",
				BgmapsHelper.getBgmapsImage(busStop.getCode())
		);
		
	}

	public void close() {
		location.quit();
		bgmaps.quit();
	}

}
