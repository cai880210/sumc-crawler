package eu.tanov.sumc.crawler.configuration;

import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;

import eu.tanov.sumc.crawler.model.BusStop;
import eu.tanov.sumc.crawler.model.Line;
import eu.tanov.sumc.crawler.model.SumcConfiguration;
import eu.tanov.sumc.crawler.model.VehicleType;
import eu.tanov.sumc.crawler.util.BaseSaxParser;

public class ConfigurationParser extends BaseSaxParser<SumcConfiguration> {

	private static final String ELEMENT_NAME_SUMC_CONFIGURATION = "sumcConfiguration";
	private static final String ELEMENT_NAME_VEHICLE_TYPE = "vehicleType";
	private static final String ELEMENT_NAME_LINE = "line";
	private static final String ELEMENT_NAME_DIRECTION1 = "direction1";
	private static final String ELEMENT_NAME_DIRECTION2 = "direction2";
	private static final String ELEMENT_NAME_BUS_STOP = "busStop";

	private static final String ATTRIBUTE_NAME_SUMC_CONFIGURATION__CREATED = "created";
	private static final String ATTRIBUTE_NAME_VEHICLE_TYPE__LABEL = "label";
	private static final String ATTRIBUTE_NAME_LINE__LABEL = "label";
	private static final String ATTRIBUTE_NAME_BUS_STOP__CODE = "code";
	private static final String ATTRIBUTE_NAME_BUS_STOP__LABEL = "label";
//	private static final String ATTRIBUTE_NAME_BUS_STOP__BGMAPS_LINK = "bgmapsLink";

	
	private List<BusStop> currentDirection;
	private Line currentLine;
	private VehicleType currentVehicleType;

	public ConfigurationParser(String filename) {
		super(filename);
	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if (ELEMENT_NAME_SUMC_CONFIGURATION.equals(name)) {
			handleSumcConfiguration(atts);
		} else if (ELEMENT_NAME_VEHICLE_TYPE.equals(name)) {
			handleVehicleType(atts);
		} else if (ELEMENT_NAME_LINE.equals(name)) {
			handleLine(atts);
		} else if (ELEMENT_NAME_DIRECTION1.equals(name)) {
			handleDirection1(atts);
		} else if (ELEMENT_NAME_DIRECTION2.equals(name)) {
			handleDirection2(atts);
		} else if (ELEMENT_NAME_BUS_STOP.equals(name)) {
			handleBusStop(atts);
		} else {
			throw new IllegalStateException("Unknown element: "+name);
		}
	}

	private void handleSumcConfiguration(Attributes atts) {
		final String created = atts.getValue(ATTRIBUTE_NAME_SUMC_CONFIGURATION__CREATED);
		
		result.setDateCreated(new Date(Long.parseLong(created)));
	}

	private void handleLine(Attributes atts) {
		final String label = atts.getValue(ATTRIBUTE_NAME_LINE__LABEL);
		
		final Line line = new Line();
		line.setLabel(label);
		currentVehicleType.getLines().add(line);
		
		currentLine = line;
	}

	private void handleVehicleType(Attributes atts) {
		final String label = atts.getValue(ATTRIBUTE_NAME_VEHICLE_TYPE__LABEL);
		
		final VehicleType vehicleType = new VehicleType();
		vehicleType.setLabel(label);
		result.getVehicleTypes().add(vehicleType);
		
		currentVehicleType = vehicleType;
	}

	private void handleDirection1(Attributes atts) {
		currentDirection = currentLine.getDirection1();
	}

	private void handleDirection2(Attributes atts) {
		currentDirection = currentLine.getDirection2();
	}

	private void handleBusStop(Attributes atts) {
		final String code = atts.getValue(ATTRIBUTE_NAME_BUS_STOP__CODE);
		final String label = atts.getValue(ATTRIBUTE_NAME_BUS_STOP__LABEL);
//		not used:
//		final String bgmapsLink = atts.getValue(ATTRIBUTE_NAME_BUS_STOP__BGMAPS_LINK);

		final BusStop busStop = new BusStop();
		busStop.setCode(code);
		busStop.setLabel(label);
		//set bgmapsLink
		
		currentDirection.add(busStop);
	}

	@Override
	protected void initResult() {
		result = new SumcConfiguration();
	}
}
