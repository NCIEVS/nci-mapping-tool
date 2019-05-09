package util;

//import gov.nih.nci.evs.restapi.util.*;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.*;
import java.util.*;

public class Mapping2TTLRunner {

	 public static void saveToFile(String outputfile, Vector v) {
		if (outputfile.indexOf(" ") != -1) {
			outputfile = replaceFilename(outputfile);
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			if (v != null && v.size() > 0) {
				for (int i=0; i<v.size(); i++) {
					String t = (String) v.elementAt(i);
					pw.println(t);
				}
		    }
		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	 }

	public static void saveToFile(PrintWriter pw, Vector v) {
		if (v != null && v.size() > 0) {
			for (int i=0; i<v.size(); i++) {
				String t = (String) v.elementAt(i);
				pw.println(t);
			}
		}
	}

	public static String replaceFilename(String filename) {
	    return filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	public static Vector readFile(String filename)
	{
		Vector v = new Vector();
		try {
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
						  new FileInputStream(filename), "UTF8"));
			String str;
			while ((str = in.readLine()) != null) {
				v.add(str);
			}
            in.close();
		} catch (Exception ex) {
            ex.printStackTrace();
		}
		return v;
	}

    public static void run(String ttlfile, String IRIref) {
		int n = ttlfile.lastIndexOf(".");
		String mappingName = ttlfile.substring(0, n);
		String name = mappingName.toLowerCase();

		Vector w = readFile(ttlfile);
		String firstline = (String) w.elementAt(0);
		String creator = "NCI EVS";
		Vector v = new Vector();
		String base = "<" + IRIref + "mapping/" + name + ">";
        v.add("@prefix mapping: " + base + " .");
		v.add("@prefix dc: <http://purl.org/dc/elements/1.1/> .");
		v.add("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
		v.add("@prefix skos: <http://www.w3.org/2004/02/skos/core#> .");
		v.add("\n");

		n = name.lastIndexOf("_");
		String title = mappingName.substring(0, n);
		String version = name.substring(n+1, name.length());
        v.add(base);
        v.add("\t" + "dc:title \"" + title + "\" ;");
        v.add("\t" + "owl:versionInfo \"" + version + "\" ;");
        v.add("\t" + "dc:creator \"" + creator + "\" .");
        v.add("\n");
		v.add("mapping:sourceCode skos:definition \"Source Code\" .");
        v.add("mapping:sourceName skos:definition \"Source Name\" .");
        v.add("mapping:sourceCodingScheme skos:definition \"Source Coding Scheme\" .");
        v.add("mapping:sourceCodingSchemeVersion skos:definition \"Source Coding Scheme Version\" .");
        v.add("mapping:sourceCodingSchemeNamespace skos:definition \"Source Coding Scheme Namespace\" .");
        v.add("mapping:associationName skos:definition \"Association Name\" .");
        v.add("mapping:rel skos:definition \"Relationship Attribute\" .");
        v.add("mapping:mapRank skos:definition \"Map Rank\" .");
		v.add("mapping:targetCode skos:definition \"Target Code\" .");
        v.add("mapping:targetName skos:definition \"Target Name\" .");
        v.add("mapping:targetCodingScheme skos:definition \"Target Coding Scheme\" .");
        v.add("mapping:targetCodingSchemeVersion skos:definition \"Target Coding Scheme Version\" .");
        v.add("mapping:targetCodingSchemeNamespace skos:definition \"Target Coding Scheme Namespace\" .");
        for (int j=1; j<w.size(); j++) {
			String line = (String) w.elementAt(j);
			v.add(line);
		}
		saveToFile(ttlfile, v);
	}

    public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String inputfile = args[0];
		System.out.println("Creating document ... " + inputfile);
		String ns = "mapping";
		String namespaceURI = "http://gov.nih.nci.evs/";
		int n = inputfile.lastIndexOf(".");
		String outputfile = inputfile.substring(0, n) + ".ttl";
		Mapping2TTL.run(inputfile, ns, namespaceURI);
		run(outputfile, namespaceURI);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("Execution time: " + time);
    }
}
