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

public class Mapping
{

// Variable declaration
	private int totalCount;
	private int matchCount;
	private List<MappingEntry> entries;

// Default constructor
	public Mapping() {
	}

// Constructor
	public Mapping(
		int totalCount,
		int matchCount,
		List<MappingEntry> entries) {

		this.totalCount = totalCount;
		this.matchCount = matchCount;
		this.entries = entries;
	}

// Set methods
	public void setTotalCount(int totalCount) { 
		this.totalCount = totalCount;
	}

	public void setMatchCount(int matchCount) { 
		this.matchCount = matchCount;
	}

	public void setEntries(List<MappingEntry> entries) { 
		this.entries = entries;
	}


// Get methods
	public int getTotalCount() { 
		return this.totalCount;
	}

	public int getMatchCount() { 
		return this.matchCount;
	}

	public List<MappingEntry> getEntries() { 
		return this.entries;
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
