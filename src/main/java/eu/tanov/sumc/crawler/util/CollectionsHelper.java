package eu.tanov.sumc.crawler.util;


public class CollectionsHelper {
	//helper, without instance
	private CollectionsHelper() {}

	
	public static String toStringNoSpaces(Iterable<? extends Object> list) {
		final StringBuilder result = new StringBuilder();
		for (Object iterable_element : list) {
			result.append(String.valueOf(iterable_element));
		}
		return result.toString();
	}
}
