package eu.tanov.sumc.crawler.coordinates;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.util.BgmapsHelper;
import eu.tanov.sumc.crawler.util.WaitHelper;
import eu.tanov.sumc.crawler.util.WebElementHelper;
import eu.tanov.sumc.crawler.util.WaitHelper.Condition;

public class CoordinatesProvider {
	private static final String CITY_SEPARATOR = ", ";
	private static final String NAME_SEARCH = "q";
	private static final String ID_FLAG_LOCATION_SELECTED = "FLAG_LOCATION_SELECTED";
	private static final String ID_COORDINATES = "latlon";
	

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
		
		log.info("Place cross at the bus stop and click on button \"This is bus stop\"");
		WaitHelper.waitForCondition(new Condition() {
			
			public boolean completed() {
				final List<WebElement> matched = location.findElements(By.id(ID_FLAG_LOCATION_SELECTED));
				
				return !matched.isEmpty();
			}
		}, -1);
		
		final WebElement coordinatesHolder = location.findElement(By.id(ID_COORDINATES));
		final String coordinates = WebElementHelper.getText(coordinatesHolder);

		setCoordinates(busStop, coordinates);
	}

	private void setCoordinates(BusStop busStop, String coordinates) {
		final String[] split = coordinates.split(", ");
		if (split.length!=2) {
			throw new IllegalStateException("Could not parse coordinates: "+coordinates);
		}
		busStop.setLat(Double.valueOf(split[0]));
		busStop.setLon(Double.valueOf(split[1]));
	}

	private void prepareBgmaps(BusStop busStop) {
		bgmaps.get(BgmapsHelper.getBgmapsLink(busStop.getCode()));
		
	}

	private void prepareLocation(BusStop busStop) {
		location.get(URL_LOCATION);
		
		addReadyButton(busStop);
		addBgmapsImage(busStop);
		
		//focus on query
		final WebElement search = location.findElement(By.name(NAME_SEARCH));
		search.sendKeys(Keys.HOME + CITY_SEPARATOR + Keys.HOME);
		
		WebElementHelper.focus(search);
	}

	private void addReadyButton(BusStop busStop) {
		final JavascriptExecutor javascriptExecutor = WebElementHelper.toJavascriptExecutor(location);
		javascriptExecutor.executeScript("var button = document.createElement('input');" +
				"button.type='button';" +
				"button.setAttribute('id', 'BUTTON_LOCATION_SELECTED');" +
				"button.value = 'This is bus stop \"'+arguments[0]+'\"';" +
				"button.setAttribute('onClick'," +
					"'var flag = document.createElement(\"div\");" +
					"flag.id=\""+ID_FLAG_LOCATION_SELECTED+"\";" +
					"document.getElementById(\""+ID_COORDINATES+"\").parentElement.appendChild(flag);" +
					"document.getElementById(\"BUTTON_LOCATION_SELECTED\").value=\"Please wait...\";" +
					"');" +
				"document.getElementById('"+ID_COORDINATES+"').parentElement.appendChild(button);"
					,
				busStop.getLabel()
		);
	}

	private void addBgmapsImage(BusStop busStop) {
		
		final JavascriptExecutor javascriptExecutor = WebElementHelper.toJavascriptExecutor(location);

		javascriptExecutor.executeScript("var img = document.createElement('img');" +
				"img.src = arguments[0];" +
				"document.getElementById('"+ID_COORDINATES+"').parentElement.appendChild(img);",
				BgmapsHelper.getBgmapsImage(busStop.getCode())
		);
		
	}

	public void close() {
		location.quit();
		bgmaps.quit();
	}

}
