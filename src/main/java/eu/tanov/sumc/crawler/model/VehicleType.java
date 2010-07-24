package eu.tanov.sumc.crawler.model;

import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class VehicleType {
	private final List<Line> lines = new LinkedList<Line>();
	private String name;
	
	
	@Override
	public String toString() {
		return "<vehicleType name=\""+name+"\">"+CollectionsHelper.toStringNoSpaces(lines)+"</vehicleType>";
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Line> getLines() {
		return lines;
	}

}
