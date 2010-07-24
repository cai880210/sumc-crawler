package eu.tanov.sumc.crawler.model;

public class BusStop {
	private static final String FORMAT_BUS_STOP = "\n\t\t\t\t<busStop code=\"%s\" name=\"%s\" bgmapsLink=\"http://bgmaps.com/chooseobject.aspx?tplname=skgt&amp;key=%s\" />";
	private String code;
	private String name;
	
	@Override
	public String toString() {
		return String.format(FORMAT_BUS_STOP, code, name, code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
