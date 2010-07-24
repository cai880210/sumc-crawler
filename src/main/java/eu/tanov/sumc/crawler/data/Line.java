package eu.tanov.sumc.crawler.data;

import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class Line {
	private final List<BusStop> busStopsDirection1 = new LinkedList<BusStop>();
	private final List<BusStop> busStopsDirection2 = new LinkedList<BusStop>();
	
	private String name;
	
	@Override
	public String toString() {
		return "<line name=\""+name+"\">"+
			"<direction1>"+CollectionsHelper.toStringNoSpaces(busStopsDirection1)+ "</direction1>"+
			"<direction2>"+CollectionsHelper.toStringNoSpaces(busStopsDirection2)+ "</direction2>"+
		"</line>";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BusStop> getBusStopsDirection1() {
		return busStopsDirection1;
	}

	public List<BusStop> getBusStopsDirection2() {
		return busStopsDirection2;
	}
}
