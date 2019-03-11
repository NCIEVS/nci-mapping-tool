package gov.nih.nci.evs.mapping.bean;

import java.io.*;
import java.util.*;
import java.net.*;

import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.XStream;

public class ConceptProperties
{

// Variable declaration
	private String label;
	private String code;
	private List<ConceptProperty> properties;

// Default constructor
	public ConceptProperties() {
	}

// Constructor
	public ConceptProperties(
		String label,
		String code,
		List<ConceptProperty> properties) {

		this.label = label;
		this.code = code;
		this.properties = properties;
	}

// Set methods
	public void setLabel(String label) {
		this.label = label;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setProperties(List<ConceptProperty> properties) {
		this.properties = properties;
	}


// Get methods
	public String getLabel() {
		return this.label;
	}

	public String getCode() {
		return this.code;
	}

	public List<ConceptProperty> getProperties() {
		return this.properties;
	}

	public String toXML() {
		XStream xstream_xml = new XStream(new DomDriver());
		String xml = xstream_xml.toXML(this);
		xml = escapeDoubleQuotes(xml);
		StringBuffer buf = new StringBuffer();
		String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		buf.append(XML_DECLARATION).append("\n").append(xml);
		xml = buf.toString();
		return xml;
	}

	public String toJson() {
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
	}

	public String escapeDoubleQuotes(String inputStr) {
		char doubleQ = '"';
		StringBuffer buf = new StringBuffer();
		for (int i=0;  i<inputStr.length(); i++) {
			char c = inputStr.charAt(i);
			if (c == doubleQ) {
				buf.append(doubleQ).append(doubleQ);
			}
			buf.append(c);
		}
		return buf.toString();
	}
}
