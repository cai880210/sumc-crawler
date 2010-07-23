package eu.tanov.sumc.crawler.util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SelectWebElementHelper {

	private static final String TAG_NAME_OPTION = "option";

	//utility class
	private SelectWebElementHelper() {}
	
	public static WebElement getSelected(WebElement select) {
		return null;
	}
	
	
	/**
	 * @param element
	 * @return selected option in select
	 */
	public static WebElement getSelectedOption(final WebElement select) {
		final List<WebElement> options = getOptions(select);
		for (WebElement option : options) {
			if (option.isSelected()) {
				return option;
			}
		}
		return null;
	}

	public static void setText(WebElement select, String text) {
		final List<WebElement> availableOptions = getOptions(select); 

		int index = WebElementHelper.indexOf(availableOptions, text);
		if (index == WebElementHelper.NOT_FOUND) {
			throw new IllegalArgumentException(text+" not found in "+WebElementHelper.webElementsToString(availableOptions));
		}
		availableOptions.get(index).setSelected();
	}
	//TODO setValue()
	public static List<WebElement> getOptions(WebElement select) {
		return select.findElements(By.tagName(TAG_NAME_OPTION)); 
	}

}
