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

public class MappingEntry
{

// Variable declaration
	private String sourceCode;
	private String sourceTerm;
	private String targetCode;
	private String targetLabel;

// Default constructor
	public MappingEntry() {
	}

// Constructor
	public MappingEntry(
		String sourceCode,
		String sourceTerm,
		String targetCode,
		String targetLabel) {

		this.sourceCode = sourceCode;
		this.sourceTerm = sourceTerm;
		this.targetCode = targetCode;
		this.targetLabel = targetLabel;
	}

// Set methods
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public void setTargetLabel(String targetLabel) {
		this.targetLabel = targetLabel;
	}


// Get methods
	public String getSourceCode() {
		return this.sourceCode;
	}

	public String getSourceTerm() {
		return this.sourceTerm;
	}

	public String getTargetCode() {
		return this.targetCode;
	}

	public String getTargetLabel() {
		return this.targetLabel;
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

	public String toDelimited() {
		return this.sourceCode + "|" + this.sourceTerm + "|" + this.targetCode + "|" + this.targetLabel;
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
