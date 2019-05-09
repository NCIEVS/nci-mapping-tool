package util;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.*;
import java.util.*;

public class Mapping2TTL {

    private static Model model = new LinkedHashModel();

    public static void run(String inputfile, String ns, String namespaceURI) {
		int n = inputfile.lastIndexOf(".");
		String outputfile = inputfile.substring(0, n) + ".ttl";
        long startTime = System.currentTimeMillis();
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(outputfile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Creating document ... " + inputfile);
        model = createDocuments(inputfile);
        System.out.println("model.size(): " + model.size());
        RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
        try {
            writer.startRDF();
            writer.handleNamespace(ns, namespaceURI);
            model.forEach(writer::handleStatement);
            writer.endRDF();
        }
        catch (RDFHandlerException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        System.out.println("Execution time: " + time);
	}


    public static Vector parseData(String line, char delimiter) {
		if(line == null) return null;
		Vector w = new Vector();
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<line.length(); i++) {
			char c = line.charAt(i);
			if (c == delimiter) {
				w.add(buf.toString());
				buf = new StringBuffer();
			} else {
				buf.append(c);
			}
		}
		w.add(buf.toString());
		return w;
	}

    private static String[] split(String line) {
		Vector v = parseData(line, '|');
		String[] a = new String[v.size()];
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			a[i] = t;
		}
		return a;
	}

    public static org.openrdf.model.Model createDocuments(String filename) {
        org.openrdf.model.Model model = null;
        String line;
        String delimiter = "|";
        int numberOfDocuments = 0;
        int numberOfInvalidDocuments = 0;
        model = new LinkedHashModel();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

            // The first line is always the column names/predicates/attributes.
            line = bufferedReader.readLine();
            //String[] attributes = line.split(delimiter);
            String[] attributes = split(line);

            while ((line = bufferedReader.readLine()) != null) {

                //String[] csvinput = line.split(delimiter);
                String[] csvinput = split(line);
                // Skipping resources that is uncomplete.

                if (csvinput.length < attributes.length) {
                    numberOfInvalidDocuments++;
                }

                // Only handling resources who are complete.
                else if (csvinput.length == attributes.length) {
                    Document document = new Document(attributes, csvinput);
                    document.setNamespace("http://gov.nih.nci.evs/");
                    numberOfDocuments++;
                    model.addAll(document.getModel());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Number of documents created: " + numberOfDocuments);
            System.out.println("Number of documents skipped: " + numberOfInvalidDocuments);
        }
        return model;
    }

    public static void main(String[] args) {
		String inputfile = args[0];
		String ns = "mapping";
		String namespaceURI = "http://gov.nih.nci.evs/";
		run(inputfile, ns, namespaceURI);
	}
}
