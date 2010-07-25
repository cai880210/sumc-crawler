package eu.tanov.sumc.crawler.coordinates;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.util.BaseSaxParser;

public class CoordinatesParser extends BaseSaxParser<List<BusStop>> {

	private static final String ELEMENT_NAME_BUS_STOP = "busStop";
	private static final String ATTRIBUTE_NAME_CODE = "code";
	private static final String ATTRIBUTE_NAME_LABEL = "label";
	private static final String ATTRIBUTE_NAME_LON = "lon";
	private static final String ATTRIBUTE_NAME_LAT = "lat";

	public CoordinatesParser(String filename) {
		super(filename);
	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if (ELEMENT_NAME_BUS_STOP.equals(name)) {
			final String code = atts.getValue(ATTRIBUTE_NAME_CODE);
			final String lat = atts.getValue(ATTRIBUTE_NAME_LAT);
			final String lon = atts.getValue(ATTRIBUTE_NAME_LON);
			final String label = atts.getValue(ATTRIBUTE_NAME_LABEL);

			final BusStop busStop = new BusStop();
			busStop.setCode(code);
			busStop.setLabel(label);
			busStop.setLat(Double.valueOf(lat));
			busStop.setLon(Double.valueOf(lon));
			
			result.add(busStop);
		}
	}

	@Override
	protected void initResult() {
		result = new LinkedList<BusStop>();
	}
}
