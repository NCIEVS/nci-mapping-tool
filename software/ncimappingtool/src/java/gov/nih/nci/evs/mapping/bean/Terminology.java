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

public class Terminology
{

// Variable declaration
	private String namedGraph;
	private String codingSchemeName;
	private String codingSchemeVersion;
	private String filename;
	private Vector data;
	private HashSet keywordSet;

// Default constructor
	public Terminology() {
	}

// Constructor
	public Terminology(
		String namedGraph,
		String codingSchemeName,
		String codingSchemeVersion,
		String filename,
		Vector data,
		HashSet keywordSet) {

		this.namedGraph = namedGraph;
		this.codingSchemeName = codingSchemeName;
		this.codingSchemeVersion = codingSchemeVersion;
		this.filename = filename;
		this.data = data;
		this.keywordSet = keywordSet;
	}

// Set methods
	public void setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
	}

	public void setCodingSchemeName(String codingSchemeName) {
		this.codingSchemeName = codingSchemeName;
	}

	public void setCodingSchemeVersion(String codingSchemeVersion) {
		this.codingSchemeVersion = codingSchemeVersion;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setData(Vector data) {
		this.data = data;
	}

	public void setKeywordSet(HashSet keywordSet) {
		this.keywordSet = keywordSet;
	}


// Get methods
	public String getNamedGraph() {
		return this.namedGraph;
	}

	public String getCodingSchemeName() {
		return this.codingSchemeName;
	}

	public String getCodingSchemeVersion() {
		return this.codingSchemeVersion;
	}

	public String getFilename() {
		return this.filename;
	}

	public Vector getData() {
		return this.data;
	}

	public HashSet getKeywordSet() {
		return this.keywordSet;
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
