package eu.tanov.sumc.crawler.model;

import java.text.DecimalFormatSymbols;

import eu.tanov.sumc.crawler.util.BgmapsHelper;

public class BusStop {
	private static final String FORMAT_BUS_STOP_BGMAPS = "\n\t\t\t\t<busStop code=\"%s\" label=\"%s\" bgmapsLink=\"%s\" />";
	private static final String FORMAT_BUS_STOP_COORDINATES = "\n\t\t\t\t<busStop code=\"%s\" label=\"%s\" lat=\"%s\" lon=\"%s\" />";

	private static final String XML_SPECIAL_CHAR = "&";
	private static final String XML_SPECIAL_CHAR_REPLACEMENT = "&amp;";
	
	private static final char DECIMAL_POINT = '.';


	private int code;
	private String label;
	private Double lat;
	private Double lon;
	
	@Override
	public String toString() {
		if (lat == null || lon == null) {
			return String.format(FORMAT_BUS_STOP_BGMAPS, code, label, BgmapsHelper.getBgmapsLink(code).replace(XML_SPECIAL_CHAR, XML_SPECIAL_CHAR_REPLACEMENT));
		} else {
			return String.format(FORMAT_BUS_STOP_COORDINATES, code, label,
					doubleToString(lat), doubleToString(lon));
		}
		
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Double getLat() {
		return lat;
	}
	
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	public Double getLon() {
		return lon;
	}
	
	public void setLon(Double lon) {
		this.lon = lon;
	}
	
	private static String doubleToString(double d) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(DECIMAL_POINT);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.######", symbols);
		return df.format(d);
	}
	
	@Override
	public int hashCode() {
		return code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BusStop) {
			final BusStop otherBusStop = (BusStop) obj;
			return otherBusStop.getCode()== this.getCode();
		}
		return false;
	}

}
