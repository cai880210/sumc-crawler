package eu.tanov.sumc.crawler.model;

import java.text.DecimalFormatSymbols;

public class BusStop {
	private static final String FORMAT_BUS_STOP_BGMAPS = "\n\t\t\t\t<busStop code=\"%s\" label=\"%s\" bgmapsLink=\"http://bgmaps.com/chooseobject.aspx?tplname=skgt&amp;key=%s\" />";
	private static final String FORMAT_BUS_STOP_COORDINATES = "\n\t\t\t\t<busStop code=\"%s\" label=\"%s\" lat=\"%s\" lon=\"%s\" />";
	
	private static final char DECIMAL_POINT = '.';

	private String code;
	private String label;
	private Double lat;
	private Double lon;
	
	@Override
	public String toString() {
		if (lat == null || lon == null) {
			return String.format(FORMAT_BUS_STOP_BGMAPS, code, label, code);
		} else {
			return String.format(FORMAT_BUS_STOP_COORDINATES, code, label,
					doubleToString(lat), doubleToString(lon));
		}
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	private static String doubleToString(double d) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(DECIMAL_POINT);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.######", symbols);
		return df.format(d);
	}

}
