package eu.tanov.sumc.crawler.model;

import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class VehicleType {
	private final List<Line> lines = new LinkedList<Line>();
	private String label;
	
	
	@Override
	public String toString() {
		return "\n\t<vehicleType label=\""+label+"\">"+CollectionsHelper.toStringNoSpaces(lines)+
				"\n\t</vehicleType>";
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public List<Line> getLines() {
		return lines;
	}

}
