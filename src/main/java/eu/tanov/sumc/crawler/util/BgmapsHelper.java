package eu.tanov.sumc.crawler.util;


public class BgmapsHelper {
	private static final String LINK_BGMAPS_PREFIX = "http://bgmaps.com/chooseobject.aspx?tplname=skgt&key=";
	private static final String IMAGE_BGMAPS_PREFIX = "http://bgmaps.com/tplimage.ashx?tplname=skgt&key=";
	

	//helper, without instance
	private BgmapsHelper() {}

	private static String addLeadingZeros(int number, int minLength) {
		final String asString = String.valueOf(number);
		if (asString.length() >= minLength) {
			//nothing to add
			return asString;
		}
		
		final StringBuilder result = new StringBuilder(minLength);

		final int zeroesToAdd = minLength - asString.length();
		for (int i = 0; i < zeroesToAdd; i++) {
			result.append("0");
		}
		result.append(asString);
		return result.toString();
	}
	
	public static String getBgmapsLink(int busStopCode) {
		return LINK_BGMAPS_PREFIX + addLeadingZeros(busStopCode, 4);
	}	
	public static String getBgmapsImage(int busStopCode) {
		return IMAGE_BGMAPS_PREFIX + addLeadingZeros(busStopCode, 4);
	}	
}
