package eu.tanov.sumc.crawler.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class BaseSaxParser<T extends Object> extends DefaultHandler {
	private static final String ENCODING = "UTF-8";

	private final String filename;
	
	protected T result;
	
	public BaseSaxParser(String filename) {
		this.filename = filename;
		initResult();
	}
	
	/**
	 * Extending classes should create field result here 
	 */
	protected abstract void initResult();

	public T parse() throws SAXException, IOException {
		final XMLReader xr = XMLReaderFactory.createXMLReader();

		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		final InputStream openRawResource = new FileInputStream(filename);

		final InputSource inputSource = new InputSource(openRawResource);
		inputSource.setEncoding(ENCODING);
		
		xr.parse(inputSource);
		
		return result;
	}

	@Override
	public abstract void startElement(String uri, String name, String qName, Attributes atts);
}
