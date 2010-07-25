package eu.tanov.sumc.crawler.coordinates;

public class CoordinatesCrawler implements Runnable {

	private final String outputFilename;
	private final String configurationFilename;
	private final String oldCoordinatesFilename;
	private final String logFilename;

	public CoordinatesCrawler(String outputFilename,
			String configurationFilename, String oldCoordinatesFilename,
			String logFilename) {
		this.outputFilename = outputFilename;
		this.configurationFilename = configurationFilename;
		this.oldCoordinatesFilename = oldCoordinatesFilename;
		this.logFilename = logFilename;
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

}
