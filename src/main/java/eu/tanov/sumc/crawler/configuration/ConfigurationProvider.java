package eu.tanov.sumc.crawler.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.util.SelectWebElementHelper;
import eu.tanov.sumc.crawler.util.WaitHelper;
import eu.tanov.sumc.crawler.util.WaitHelper.Condition;
import eu.tanov.sumc.crawler.util.WebElementHelper;

public class ConfigurationProvider {
	private static final Logger log = Logger.getLogger(ConfigurationProvider.class.getName());
	
	private static final String NAME_COMBO_VEHICLE_TYPES = "ctl00$ContentPlaceHolder1$ddlTransportType";
	private static final String NAME_COMBO_LINES = "ctl00$ContentPlaceHolder1$ddlLines";
	private static final String NAME_RADIO_DIRECTION = "ctl00$ContentPlaceHolder1$rblRoute";
	private static final String NAME_COMBO_BUS_STOPS = "ctl00$ContentPlaceHolder1$ddlStops";
	//XXX use By.name() instead:
	private static final String FORMAT_XPATH_BY_NAME = "//*[@name='%s']";
//	private static final String FORMAT_XPATH_BY_FOR = "//*[@for='%s']";
//	private static final String FORMAT_XPATH_BY_ID = "//*[@id='%s']";

	/**
//	private static final String URL_MAIN = "http://pt.sumc.bg/Web/SelectByLine.aspx";
	 * or (forwards to same addres): 
	 */
	private static final String URL_MAIN = "http://gps.skgt-bg.com/";
	private static final int DEFAULT_TIMEOUT = 10000;
	private static final char BUS_STOP_CODE_PREFIX = '(';
	private static final char BUS_STOP_CODE_SUFFIX = ')';
	private static final String BUS_STOP_CODE_SUFFIX_REGEX = Pattern.quote(String.valueOf(BUS_STOP_CODE_SUFFIX));

	//use ChromeDriver(true) while developing in order to see what happens
//	private final WebDriver webDriver = new ChromeDriver();
	private final WebDriver webDriver = new HtmlUnitDriver(true);
	
	private List<String> getList(String name, boolean skipFirst) {
		final WebElement combo = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, name)));
		if (combo == null) {
			throw new NullPointerException("Could not find listbox with name: "+name);
		}
		
		final List<WebElement> options = SelectWebElementHelper.getOptions(combo);
		
		//-1 because of first/empty record
		final List<String> result = new ArrayList<String>(options.size());
		
		boolean emptyRemoved = !skipFirst;
		for (WebElement option : options) {
			if (!emptyRemoved) {
				emptyRemoved = true;
				continue;
			}
			
			result.add(WebElementHelper.getText(option));
		}
		return result;
	}
	
	public void connect() {
		webDriver.get(URL_MAIN);
	}
	public List<String> getVehicleTypes() {
		return getList(NAME_COMBO_VEHICLE_TYPES, true);
	}

	public List<String> getLines(String vehicleType) {
		setVehicleType(vehicleType);
		return getList(NAME_COMBO_LINES, true);
	}

	private void waitForAnswer(final List<WebElement> oldElements, final String elementName) {
		WaitHelper.waitForCondition(new Condition() {
			public boolean completed() {
				try {
					//check if there is new element
					final List<WebElement> matched = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, elementName)));
					
					if (matched.size()>oldElements.size()) {
						return true;
					}
					if (matched.size() == 0) {
						return false;
					}
					return !oldElements.get(0).equals(matched.get(0));
				} catch (Exception e) {
//					log.info("exception for "+elementName, e);
					return false;
				}
			}
		}, DEFAULT_TIMEOUT);
	}

	private void setVehicleType(String vehicleType) {
		final WebElement vehicleTypes = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_VEHICLE_TYPES)));
		//is need to change?
		final WebElement selectedVehicleType = SelectWebElementHelper.getSelectedOption(vehicleTypes);
		if (selectedVehicleType!=null && vehicleType.equals(WebElementHelper.getText(selectedVehicleType))) {
			//no need to change
			return;
		}
		
		final List<WebElement> lines = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_LINES)));
		WebElementHelper.setText(vehicleTypes , vehicleType);
		//wait new results
		waitForAnswer(lines, NAME_COMBO_LINES);
	}

	private void setLine(String vehicleType, final String line) {
		setVehicleType(vehicleType);
		
		final WebElement lines = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_LINES)));
		
		//is need to change?
		final WebElement selectedLine = SelectWebElementHelper.getSelectedOption(lines);
		if (selectedLine!=null && line.equals(WebElementHelper.getText(selectedLine))) {
			//no need to change
			return;
		}		
		
		final List<WebElement> directions = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_RADIO_DIRECTION)));
		WebElementHelper.setText(lines , line);
		//wait new results
		waitForAnswer(directions, NAME_RADIO_DIRECTION);
	}

	private void setDirection(String vehicleType, String line, boolean firstDirection) {
		setLine(vehicleType, line);
		final List<WebElement> radioButtons = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_RADIO_DIRECTION)));
		
		
		
		if (radioButtons.size()!=2) {
			throw new IllegalStateException("Expected 2 directions, not: "+WebElementHelper.webElementsToString(radioButtons));
		}
		
		if (!radioButtons.get(firstDirection?0:1).isSelected()) {
			final List<WebElement> busStops = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_BUS_STOPS)));
			//TODO add this to WebElementHelper - set value of radio button group (by index)
			radioButtons.get(firstDirection?0:1).click();
			//setSelected() does not work - site expects click...
			
			//wait new results
			waitForAnswer(busStops, NAME_COMBO_BUS_STOPS);
		}
	}

	public List<BusStop> getBusStops(String vehicleType, String line, boolean firstDirection) {
		setDirection(vehicleType, line, firstDirection);
		log.debug("vehicleType: " +vehicleType + ", line: " + line + ", firstDirection: "+firstDirection);

		return parseBusStopsNames(getList(NAME_COMBO_BUS_STOPS, true));
	}
	
	private List<BusStop> parseBusStopsNames(List<String> list) {
		final List<BusStop> result = new ArrayList<BusStop>(list.size());
		for (String busStopName : list) {
			result.add(createBusStopName(busStopName));
		}
		return result;
	}

	private BusStop createBusStopName(String busStopName) {
		final BusStop result = new BusStop();
		final int busStopPrefixIndex = busStopName.indexOf(BUS_STOP_CODE_PREFIX);
		final int busStopSuffixIndex = busStopName.indexOf(BUS_STOP_CODE_SUFFIX);
		
		if (busStopPrefixIndex != 0 || busStopSuffixIndex == -1) {
			throw new IllegalArgumentException("can't parse name: "+busStopName);
		}
		String[] split = busStopName.split(BUS_STOP_CODE_SUFFIX_REGEX, 2);
		result.setName(split[1].trim());

		result.setCode(split[0].substring(1));

		return result;
	}

	public void close() {
		webDriver.quit();
	}
}
