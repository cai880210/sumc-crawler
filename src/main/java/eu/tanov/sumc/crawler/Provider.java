package eu.tanov.sumc.crawler;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import eu.tanov.sumc.crawler.util.WebElementHelper;

public class Provider {
	private static final String NAME_COMBO_VEHICLE_TYPES = "ctl00$ContentPlaceHolder1$ddlTransportType";
	private static final String NAME_COMBO_LINES = "ctl00$ContentPlaceHolder1$ddlLines";
	private static final String NAME_RADIO_DIRECTION = "ctl00$ContentPlaceHolder1$rblRoute";
	private static final String NAME_COMBO_STOPS = "ctl00$ContentPlaceHolder1$ddlStops";

	private static final String FORMAT_XPATH_BY_NAME = "//*[@name='%s']";
//	private static final String FORMAT_XPATH_BY_FOR = "//*[@for='%s']";
//	private static final String FORMAT_XPATH_BY_ID = "//*[@id='%s']";

	/**
//	private static final String URL_MAIN = "http://pt.sumc.bg/Web/SelectByLine.aspx";
	 * or (forwards to same addres): 
	 */
	private static final String URL_MAIN = "http://gps.skgt-bg.com/";

	//use ChromeDriver(true) while developing in order to see what happens
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
	
	public List<String> getVehicleTypes() {
		webDriver.get(URL_MAIN);
		return getList(NAME_COMBO_VEHICLE_TYPES, true);
	}

	public List<String> getLines(String vehicleType) {
		setVehicleType(vehicleType);
		return getList(NAME_COMBO_LINES, true);
	}

	private void setVehicleType(String vehicleType) {
		final WebElement vehicleTypes = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_VEHICLE_TYPES)));
		WebElementHelper.setValue(vehicleTypes , vehicleType);
	}

	private void setLine(String vehicleType, String line) {
		setVehicleType(vehicleType);
		
		//FIXME use waitUntil():
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final WebElement lines = webDriver.findElement(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_COMBO_LINES)));
		WebElementHelper.setValue(lines , line);
	}

	private void setDirection(String vehicleType, String line, boolean firstDirection) {
		setLine(vehicleType, line);
		//FIXME use waitUntil():
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final List<WebElement> radioButtons = webDriver.findElements(By.xpath(String.format(FORMAT_XPATH_BY_NAME, NAME_RADIO_DIRECTION)));
		if (radioButtons.size()!=2) {
			throw new IllegalStateException("Expected 2 directions, not: "+WebElementHelper.webElementsToString(radioButtons));
		}
		
		//TODO add this to WebElementHelper - set value of radio button group (by index)
		radioButtons.get(firstDirection?0:1).click();
		//setSelected() does not work - site expects click...
//		radioButtons.get(firstDirection?0:1).setSelected();//XXX or .click();
	}

	public List<String> getStops(String vehicleType, String line, boolean firstDirection) {
		setDirection(vehicleType, line, firstDirection);
		//FIXME use waitUntil():
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getList(NAME_COMBO_STOPS, true);
	}
	
	public void close() {
		webDriver.quit();
	}
}
