package eu.tanov.sumc.crawler.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.tanov.sumc.crawler.util.CollectionsHelper;

public class SumcConfiguration {
	private final List<VehicleType> vehicleTypes = new LinkedList<VehicleType>();
	private final Date dateCreated = new Date();

	@Override
	public String toString() {
		return "<sumcConfiguration created=\""+dateCreated.getTime()+"\">"+CollectionsHelper.toStringNoSpaces(vehicleTypes)+
				"\n</sumcConfiguration>";
	}

	public List<VehicleType> getVehicleTypes() {
		return vehicleTypes;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
}
