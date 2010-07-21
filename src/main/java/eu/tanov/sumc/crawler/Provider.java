package eu.tanov.sumc.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import eu.tanov.sumc.crawler.data.BusStop;
import eu.tanov.sumc.crawler.util.WaitHelper;
import eu.tanov.sumc.crawler.util.WaitHelper.Condition;
import eu.tanov.sumc.crawler.util.WebElementHelper;

public class Provider {
	private static final Logger log = Logger.getLogger(Provider.class.getName());
	
	private static final String NAME_COMBO_VEHICLE_TYPES = "ctl00$ContentPlaceHolder1$ddlTransportType";
	private static final String NAME_COMBO_LINES = "ctl00$ContentPlaceHolder1$ddlLines";
	private static final String NAME_RADIO_DIRECTION = "ctl00$ContentPlaceHolder1$rblRoute";
	private static final String NAME_COMBO_BUS_STOPS = "ctl00$ContentPlaceHolder1$ddlStops";
//	private static final String NAME_IMAGE_MAP = "ctl00$ContentPlaceHolder1$imgMap";

	private static final String FORMAT_XPATH_BY_NAME = "//*[@name='%s']";
//	private static final String FORMAT_XPATH_BY_FOR = "//*[@for='%s']";
//	private static final String FORMAT_XPATH_BY_ID = "//*[@id='%s']";

	/**
//	private static final String URL_MAIN = "http://pt.sumc.bg/Web/SelectByLine.aspx";
	 * or (forwards to same addres): 
	 */
	private static final String URL_MAIN = "http://gps.skgt-bg.com/";
	private static final int DEFAULT_TIMEOUT = 10000;
//	private static final String ATTRIBUTE_IMAGE_SRC = "src";
//	private static final char SEPARATOR_URL_PARAMETER = '=';
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
		
		final List<WebElement> options = WebElementHelper.getSelectOptions(combo);
		
		//-1 because of first/empty record
		final List<String> result = new ArrayList<String>(options.size());
		
		boolean emptyRemoved = !skipFirst;
		for (WebElement option : options) {
			if (!emptyRemoved) {
				emptyRemoved = true;
				continue;
			}
			
			result.add(WebElementHelper.getTextValue(option));
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

	private void waitForAnswer(final String isChangedConditionName) {
		WaitHelper.waitForCondition(new Condition() {
			public boolean completed() {
				try {
					final List<WebElement> condition = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, isChangedConditionName)));
					
					if (condition.size() == 0) {
						//not found - wait...
						return false;
					}
					if (condition.size()>1) {
						//radio buttons... found
						return true;
					}

					//one element - it is select
					
					return WebElementHelper.getSelectOptions(condition.get(0)).size()>1;
				} catch (StaleElementReferenceException e) {
					//content is just refreshing by JS
					return false;
				}
			}
		}, DEFAULT_TIMEOUT);
	}

	private void setVehicleType(String vehicleType) {
		final WebElement vehicleTypes = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_VEHICLE_TYPES)));
		//first hide previous value
		WebElementHelper.getSelectOptions(vehicleTypes).get(0).setSelected();
		//and wait for refresh
		WaitHelper.waitForCondition(new Condition() {
			public boolean completed() {
				try {
					final WebElement comboLines = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_LINES)));
					//one element - treat as select and check his options count
					return WebElementHelper.getSelectOptions(comboLines).size() < 2;
				} catch (StaleElementReferenceException e) {
					//content is just refreshing by JS
					return false;
				}
			}
		}, DEFAULT_TIMEOUT);

		final WebElement refreshedVehicleTypes = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_VEHICLE_TYPES)));

		WebElementHelper.setValue(refreshedVehicleTypes , vehicleType);
		//wait new results
		waitForAnswer(NAME_COMBO_LINES);
	}

	private void setLine(String vehicleType, final String line) {
		setVehicleType(vehicleType);
		
		final WebElement lines = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_LINES)));
		WebElementHelper.setValue(lines , line);
		
		//wait new results
		waitForAnswer(NAME_RADIO_DIRECTION);
	}

	private void setDirection(String vehicleType, String line, boolean firstDirection) {
		setLine(vehicleType, line);

		final List<WebElement> radioButtons = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_RADIO_DIRECTION)));
		if (radioButtons.size()!=2) {
			throw new IllegalStateException("Expected 2 directions, not: "+WebElementHelper.webElementsToString(radioButtons));
		}
		
		//TODO add this to WebElementHelper - set value of radio button group (by index)
		radioButtons.get(firstDirection?0:1).click();
		//setSelected() does not work - site expects click...
//		radioButtons.get(firstDirection?0:1).setSelected();//XXX or .click();

		//wait new results
		waitForAnswer(NAME_COMBO_BUS_STOPS);
	}

	public List<BusStop> getBusStops(String vehicleType, String line, boolean firstDirection) {
		setDirection(vehicleType, line, firstDirection);
		log.debug("vehicleType: " +vehicleType + ", line: " + line + ", firstDirection: "+firstDirection);
		WaitHelper.waitForCondition(new Condition() {
			public boolean completed() {
				try {
					final WebElement stops = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_BUS_STOPS)));
					final List<WebElement> selectOptions = WebElementHelper.getSelectOptions(stops);
					return selectOptions.size()>1;
				} catch (StaleElementReferenceException e) {
					//content is just refreshing by JS
					return false;
				}
			}
		}, DEFAULT_TIMEOUT);
		
		return parseBusStopsNames(getList(NAME_COMBO_BUS_STOPS, true));
	}
	
	private List<BusStop> parseBusStopsNames(List<String> list) {
		final List<BusStop> result = new ArrayList<BusStop>(list.size());
		for (String busStopName : list) {
			result.add(createBusStopName(busStopName));
		}
		return result;
	}

//	private void setBusStop(String vehicleType, String line, boolean firstDirection, final String busStop) {
//		setDirection(vehicleType, line, firstDirection);
//
//		final WebElement busStops = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_BUS_STOPS)));
//		WebElementHelper.setValue(busStops, busStop);
//
//		//wait new results
//		waitForAnswer(NAME_IMAGE_MAP);
//	}

//	public BusStop getBusStop(String vehicleType, String line, boolean firstDirection, String busStopName) {
//		log.info("getting "+vehicleType+", "+line+", "+firstDirection+", "+busStopName);
//
//		setBusStop(vehicleType, line, firstDirection, busStopName);
//
//		final WebElement image = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_IMAGE_MAP)));
//		final String src = image.getAttribute(ATTRIBUTE_IMAGE_SRC);
//		if (src == null || src.lastIndexOf(SEPARATOR_URL_PARAMETER) == -1) {
//			throw new IllegalStateException("bad src for: "+vehicleType+", "+line+", "+firstDirection+", "+busStopName+": "+src);
//		}
//		//skip SEPARATOR_URL_PARAMETER char
//		final String bgMapsId = src.substring(src.lastIndexOf(SEPARATOR_URL_PARAMETER)+1);
//		final BusStop result = new BusStop();
//		result.setBgmapsId(bgMapsId);
//		parseBusName(result, busStopName);
//		return result;
//	}

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

//		log.trace("adding : " +result);

		return result;
	}

	public void close() {
		webDriver.quit();
	}
}
