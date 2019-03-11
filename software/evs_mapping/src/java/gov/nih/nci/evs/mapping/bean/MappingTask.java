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

public class MappingTask
{

// Variable declaration
	private String name;
	private String targetCodingScheme;
	private Mapping mapping;
	private String creationDate;

// Default constructor
	public MappingTask() {
	}

// Constructor
	public MappingTask(
		String name,
		String targetCodingScheme,
		Mapping mapping,
		String creationDate) {

		this.name = name;
		this.targetCodingScheme = targetCodingScheme;
		this.mapping = mapping;
		this.creationDate = creationDate;
	}

// Set methods
	public void setName(String name) { 
		this.name = name;
	}

	public void setTargetCodingScheme(String targetCodingScheme) { 
		this.targetCodingScheme = targetCodingScheme;
	}

	public void setMapping(Mapping mapping) { 
		this.mapping = mapping;
	}

	public void setCreationDate(String creationDate) { 
		this.creationDate = creationDate;
	}


// Get methods
	public String getName() { 
		return this.name;
	}

	public String getTargetCodingScheme() { 
		return this.targetCodingScheme;
	}

	public Mapping getMapping() { 
		return this.mapping;
	}

	public String getCreationDate() { 
		return this.creationDate;
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
