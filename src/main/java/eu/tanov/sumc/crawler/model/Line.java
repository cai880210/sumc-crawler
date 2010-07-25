package eu.tanov.sumc.crawler.model;

import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class Line {
	private final List<BusStop> busStopsDirection1 = new LinkedList<BusStop>();
	private final List<BusStop> busStopsDirection2 = new LinkedList<BusStop>();
	
	private String label;
	
	@Override
	public String toString() {
		return "\n\t\t<line label=\""+label+"\">"+
			"\n\t\t\t<direction1>"+CollectionsHelper.toStringNoSpaces(busStopsDirection1)+
			"\n\t\t\t</direction1>"+
			"\n\t\t\t<direction2>"+CollectionsHelper.toStringNoSpaces(busStopsDirection2)+
			"\n\t\t\t</direction2>"+
		"\n\t\t</line>";
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public List<BusStop> getBusStopsDirection1() {
		return busStopsDirection1;
	}

	public List<BusStop> getBusStopsDirection2() {
		return busStopsDirection2;
	}
}
