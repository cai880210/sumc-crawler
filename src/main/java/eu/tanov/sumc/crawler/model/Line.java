package eu.tanov.sumc.crawler.model;

import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class Line {
	//TODO List with directions (not 1, 2, etc):
	private final List<BusStop> direction1 = new LinkedList<BusStop>();
	private final List<BusStop> direction2 = new LinkedList<BusStop>();
	
	private String label;
	
	@Override
	public String toString() {
		return "\n\t\t<line label=\""+label+"\">"+
			"\n\t\t\t<direction1>"+CollectionsHelper.toStringNoSpaces(direction1)+
			"\n\t\t\t</direction1>"+
			"\n\t\t\t<direction2>"+CollectionsHelper.toStringNoSpaces(direction2)+
			"\n\t\t\t</direction2>"+
		"\n\t\t</line>";
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public List<BusStop> getDirection1() {
		return direction1;
	}

	public List<BusStop> getDirection2() {
		return direction2;
	}
}
