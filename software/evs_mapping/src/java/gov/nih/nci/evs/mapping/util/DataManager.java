package gov.nih.nci.evs.mapping.util;

import gov.nih.nci.evs.mapping.bean.*;
import gov.nih.nci.evs.mapping.common.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.common.*;

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.codec.binary.Base64;
import org.json.*;

import java.util.StringTokenizer;
import java.util.regex.*;
import org.apache.commons.codec.binary.Base64;
import org.json.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.gson.*;


/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class DataManager {

	PorterStemmer theStemmer = null;
	HashSet filler_set = null;
	HashMap key2ConceptMap = null;
	HashMap code2LabelMap = null;
	HashMap historyMap = null;

	HashSet keyword_set = null;

	private Vector term_vec = null;
	private Vector synonyms = new Vector();
	private HashMap variantHashMap = new HashMap();
	private Vector variants = new Vector();

	private Vector discarded_phrases = null;
	//private HashMap americanBritishSpellingHashMap = null;

	private String data_directory = null;
	private String serviceUrl = null;
	//private DataUtils dataUtils = null;
	private Vector terminologies = new Vector();

	private HashMap namedGraph2TerminologyHashmap = null;
	private HashMap codingSchemeName2TerminologyHashmap = null;

	public static String NCI_THESAURUS = "NCI_Thesaurus";
	public static String NCI_Thesaurus_RDF_Graph = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.rdf";
	public static String NCI_Thesaurus_OWL_Graph = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";

	HTTPUtils httpUtils = null;




	public DataManager() {
		data_directory = System.getProperty("user.dir");
	}

	public HashMap getCodingSchemeName2TerminologyHashmap() {
		return this.codingSchemeName2TerminologyHashmap;
	}

	public DataManager(String serviceUrl, String data_directory) {
        this.data_directory = data_directory;
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl);
		this.terminologies = new Vector();
		this.namedGraph2TerminologyHashmap = new HashMap();
		this.codingSchemeName2TerminologyHashmap = new HashMap();
		System.out.println("getTerminologyMetadata ...");
		Vector cs_data = getTerminologyMetadata(serviceUrl);
        System.out.println("Number of terminologies: " + cs_data.size());
		for (int i=0; i<cs_data.size(); i++) {
			String line = (String) cs_data.elementAt(i);
			System.out.println(line);
			Vector u = StringUtils.parseData(line, '|');
			String codingSchemeName = (String) u.elementAt(0);
			String codingSchemeVersion = (String) u.elementAt(1);
			String namedGraph = (String) u.elementAt(2);
			String filename = codingSchemeName + ".txt";
		//if (namedGraph.compareTo(NCI_Thesaurus_RDF_Graph) != 0 && namedGraph.compareTo(NCI_Thesaurus_OWL_Graph) != 0) {
			String filePathString = data_directory + File.separator + filename;
			File f = new File(filePathString);
			Vector data = null;
			HashSet keywordSet = null;
			if(f.exists() && !f.isDirectory()) {
				System.out.println("File " + filePathString + " exists.");
				data = Utils.readFile(filePathString);
				keywordSet = new MappingUtils().create_keyword_set(data);
			} else {
				System.out.println("File " + filePathString + " does not exist.");
				if (codingSchemeName.compareTo(NCI_THESAURUS) == 0) {
					data = get_terms(namedGraph, null);
				} else if (namedGraph.compareTo("http://cbiit.nci.nih.gov/caDSR") == 0) {
					data = get_names_cadsr(namedGraph);
				} else {
					data = get_names(namedGraph);
				}
				Utils.saveToFile(filePathString, data);
				keywordSet = new MappingUtils().create_keyword_set(data);
			}
			Terminology terminology = new Terminology(namedGraph, codingSchemeName, codingSchemeVersion, filename, data, keywordSet);
			terminologies.add(terminology);
			namedGraph2TerminologyHashmap.put(namedGraph, terminology);
			codingSchemeName2TerminologyHashmap.put(codingSchemeName, terminology);
		    //}
		}
	}

	public String construct_get_names_cadsr(String namedGraph) {
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#>").append("\n");
		buf.append("").append("\n");
		buf.append("SELECT DISTINCT ?x_code ?x_label ?x_syn").append("\n");
		buf.append("{").append("\n");
		buf.append("GRAPH <" + namedGraph + "> ").append("\n");
		buf.append("{").append("\n");
		buf.append("?x ?p ?y .").append("\n");
		buf.append("?x http://www.w3.org/2000/01/rdf-schema#label ?x_label .").append("\n");
		buf.append("?x http://cbiit.nci.nih.gov/caDSR#publicId ?x_code .").append("\n");
		buf.append("OPTIONAL {").append("\n");
		//buf.append("?x skos:altLabel ?x_syn .").append("\n");
		buf.append("?x http://www.w3.org/2004/02/skos/core#altLabel ?x_syn .").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}


	public Vector getTerminologies() {
		return terminologies;
	}

    public Vector executeQuery(String query) {
        Vector v = null;
        try {
			query = httpUtils.encode(query);
            String json = httpUtils.executeQuery(query);
			v = new JSONUtils().parseJSON(json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}

	public String construct_name_query(String namedGraph) {
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#>").append("\n");
		buf.append("").append("\n");
		buf.append("SELECT DISTINCT ?x_label ?x_code ?x_syn").append("\n");
		buf.append("{").append("\n");
		buf.append("GRAPH <" + namedGraph + "> ").append("\n");
		buf.append("{").append("\n");
		buf.append("?x ?p ?y .").append("\n");
		buf.append("?x skos:prefLabel ?x_label .").append("\n");
		buf.append("?x skos:notation ?x_code .").append("\n");
		buf.append("OPTIONAL {").append("\n");
		buf.append("?x skos:altLabel ?x_syn .").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}

	public Vector name_query(String namedGraph) {
	    String query = construct_name_query(namedGraph);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    return v;
	}

    public String getPrefixes() {
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX :<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("\n");
		buf.append("PREFIX base:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl>").append("\n");
		buf.append("PREFIX Thesaurus:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("\n");
		buf.append("PREFIX xml:<http://www.w3.org/XML/1998/namespace>").append("\n");
		buf.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append("\n");
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>").append("\n");
		buf.append("PREFIX protege:<http://protege.stanford.edu/plugins/owl/protege#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX ncicp:<http://ncicb.nci.nih.gov/xml/owl/EVS/ComplexProperties.xsd#>").append("\n");
		buf.append("PREFIX dc:<http://purl.org/dc/elements/1.1/>").append("\n");
		return buf.toString();
	}
/*
	public String construct_get_terms(String named_graph) {
		return construct_get_terms(named_graph, null);
	}

	public String construct_get_terms(String named_graph, String code) {
		String named_graph_id = ":NHC0";
		String prefixes = getPrefixes();
		StringBuffer buf = new StringBuffer();
		buf.append(prefixes);
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_code ?x_label ?term_name").append("\n");
		buf.append("{").append("\n");
		buf.append("    graph <" + named_graph + "> {").append("\n");
		buf.append("            ?x a owl:Class .").append("\n");
		if (code != null) {
			buf.append("            ?x " + named_graph_id + " \"" + code + "\"^^xsd:string .").append("\n");
		}
		buf.append("            ?x " + named_graph_id + " ?x_code .").append("\n");
		buf.append("            ?x rdfs:label ?x_label .").append("\n");
		buf.append("            ?z_axiom a owl:Axiom  .").append("\n");
		buf.append("            ?z_axiom owl:annotatedSource ?x .").append("\n");
		buf.append("            ?z_axiom owl:annotatedProperty ?p .").append("\n");
		buf.append("            ?p rdfs:label " + " \"FULL_SYN\"^^xsd:string .").append("\n");
		buf.append("            ?z_axiom owl:annotatedTarget ?term_name .").append("\n");
		buf.append("            ?z_axiom ?y ?z ").append("\n");
		buf.append("    }").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}
	*/

	public String construct_get_labels(String named_graph, String code) {
		String named_graph_id = ":NHC0";
		String prefixes = getPrefixes();
		StringBuffer buf = new StringBuffer();
		buf.append(prefixes);
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_code ?x_label").append("\n");
		buf.append("{").append("\n");
		buf.append("    graph <" + named_graph + "> {").append("\n");
		buf.append("            ?x a owl:Class .").append("\n");
		if (code != null) {
			buf.append("            ?x " + named_graph_id + " \"" + code + "\"^^xsd:string .").append("\n");
		}
		buf.append("            ?x " + named_graph_id + " ?x_code .").append("\n");
		buf.append("            ?x rdfs:label ?x_label .").append("\n");
		buf.append("    }").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}


	public String construct_get_synonyms(String named_graph, String code) {
		String named_graph_id = ":NHC0";
		String prefixes = getPrefixes();
		StringBuffer buf = new StringBuffer();
		buf.append(prefixes);
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_code ?term_name").append("\n");
		buf.append("{").append("\n");
		buf.append("    graph <" + named_graph + "> {").append("\n");
		buf.append("            ?x a owl:Class .").append("\n");
		if (code != null) {
			buf.append("            ?x " + named_graph_id + " \"" + code + "\"^^xsd:string .").append("\n");
		}
		buf.append("            ?x " + named_graph_id + " ?x_code .").append("\n");
		//buf.append("            ?x rdfs:label ?x_label .").append("\n");
		buf.append("            ?z_axiom a owl:Axiom  .").append("\n");
		buf.append("            ?z_axiom owl:annotatedSource ?x .").append("\n");
		buf.append("            ?z_axiom owl:annotatedProperty ?p .").append("\n");
		buf.append("            ?p rdfs:label " + " \"FULL_SYN\"^^xsd:string .").append("\n");
		buf.append("            ?z_axiom owl:annotatedTarget ?term_name .").append("\n");
		buf.append("            ?z_axiom ?y ?z ").append("\n");
		buf.append("    }").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}

	public Vector get_terms(String namedGraph, String code) {
	    //String query = construct_get_terms(namedGraph, code);
	    String query = construct_get_synonyms(namedGraph, code);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    //v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    Vector w = new Vector();
	    HashSet hset = new HashSet();
		HashMap code2LabelHashMap = new HashMap();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String s1 = (String) u.elementAt(0);
			String s2 = (String) u.elementAt(1);
			code2LabelHashMap.put(s1, s2);
			w.add(s1 + "|" + s2 + "|" + s2);
			hset.add(s1 + "|" + s2 + "|" + s2);
		}
	    query = construct_get_labels(namedGraph, code);
	    Vector v2 = executeQuery(query);
	    if (v2 == null) return w;
	    if (v2.size() == 0) return w;
	    v2 = new ParserUtils().getResponseValues(v2);
		for (int i=0; i<v2.size(); i++) {
			String line = (String) v2.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String s1 = (String) u.elementAt(0);
			String s2 = (String) u.elementAt(1);
			String label = (String) code2LabelHashMap.get(s1);
			String t = s1 + "|" + label + "|" + s2;
			if (!hset.contains(t)) {
				hset.add(t);
				w.add(s1 + "|" + label + "|" + s2);
			}
		}
	    return new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
	}


	public String construct_get_names(String namedGraph) {
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#>").append("\n");
		buf.append("").append("\n");
		buf.append("SELECT DISTINCT ?x_code ?x_label ?x_syn").append("\n");
		buf.append("{").append("\n");
		buf.append("GRAPH <" + namedGraph + "> ").append("\n");
		buf.append("{").append("\n");
		buf.append("?x ?p ?y .").append("\n");
		buf.append("?x skos:prefLabel ?x_label .").append("\n");
		buf.append("?x skos:notation ?x_code .").append("\n");
		buf.append("OPTIONAL {").append("\n");
		buf.append("?x skos:altLabel ?x_syn .").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}

	public Vector get_names(String namedGraph) {
	    String query = construct_get_names(namedGraph);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    v = replace_null_values(v);
	    return v;
	}

	public Vector get_names_cadsr(String namedGraph) {
	    String query = construct_get_names_cadsr(namedGraph);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    v = replace_null_values(v);
	    return v;
	}

    public Vector replace_null_values(Vector v) {
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String code = (String) u.elementAt(0);
			String pt = (String) u.elementAt(1);
			if (u.size() > 2) {
				String syn = (String) u.elementAt(2);
				if (syn.compareTo("null") == 0) {
					w.add(code + "|" + pt + "|" + pt);
				} else {
					w.add(line);
				}
			} else {
				w.add(code + "|" + pt + "|" + pt);
			}
		}
		return w;
	}

	public HashMap getNamedGraph2TerminologyHashmap() {
		return this.namedGraph2TerminologyHashmap;
	}

    public Terminology getTerminologyByNamedGraph(String namedGraph) {
		return (Terminology) namedGraph2TerminologyHashmap.get(namedGraph);
	}

    public Terminology getTerminologyByCodingSchemeName(String codingSchemeName) {
		return (Terminology) codingSchemeName2TerminologyHashmap.get(codingSchemeName);
	}

	public Terminology populateTerminologyData(Terminology terminology) {
		String codingSchemeName = (String) terminology.getCodingSchemeName();
		String codingSchemeVersion = (String) terminology.getCodingSchemeVersion();
		String namedGraph = (String) terminology.getNamedGraph();
		String filename = (String) terminology.getFilename();
		Vector data = (Vector) terminology.getData();
		if (data == null) {
			System.out.println("Generating " + filename + ". please wait...");
			if (codingSchemeName.compareTo("NCI_Thesaurus") == 0) {
				data = get_terms(namedGraph, null);
			} else {
				data = get_names(namedGraph);
			}
			Utils.saveToFile(data_directory + File.separator + filename, data);
		} else {
			data = Utils.readFile(data_directory + File.separator + filename);
		}
		HashSet keywordSet = new MappingUtils().create_keyword_set(data);
		terminology = new Terminology(namedGraph, codingSchemeName, codingSchemeVersion, filename, data, keywordSet);
		return terminology;
	}

    public Vector getTerminologyMetadata(String serviceUrl) {
		Vector cs_data = new Vector();
		MetadataUtils mdu = new MetadataUtils(serviceUrl);
		HashMap nameVersion2NamedGraphMap = mdu.getNameVersion2NamedGraphMap();
		Iterator it = nameVersion2NamedGraphMap.keySet().iterator();
		while (it.hasNext()) {
			String nameVersion = (String) it.next();
			Vector u = StringUtils.parseData(nameVersion);
			String codingSchemeName = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			Vector named_graphs = (Vector) nameVersion2NamedGraphMap.get(nameVersion);
			for (int i=0; i<named_graphs.size(); i++) {
				String named_graph = (String) named_graphs.elementAt(i);
				cs_data.add(codingSchemeName + "|" + version + "|" + named_graph);
			}
		}
		return cs_data;
	}


	public void initialize(String data_directory) {
		/*
		long ms0 = System.currentTimeMillis();
		theStemmer = new PorterStemmer();
		historyMap = load_history(data_directory + File.separator + Constants.HISTORY_FILE);
		this.discarded_phrases = readFile(data_directory + File.separator + Constants.DISCARDED_PHRASE_FILE);
		this.synonyms = readFile(data_directory + File.separator + Constants.SYNONYM_FILE);
		filler_set = loadFillers(data_directory + File.separator + Constants.FILLER_FILE);
		keyword_set = new HashSet();
		variants = readFile(data_directory + File.separator + Constants.VARIANT_FILE);
		variantHashMap = createVariantHashMap();
		term_vec = readFile(data_directory + File.separator + Constants.TERMINOLOGY_FILE);
		key2ConceptMap = createKey2ConceptMap(term_vec);
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms0));
		*/
	}

    public Vector execute(String query_file) {
		try {
			String query = httpUtils.loadQuery(query_file, false);
			return executeQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

    public static void test(String serviceUrl, String data_directory) {
		long ms = System.currentTimeMillis();
        System.out.println("serviceUrl: " + serviceUrl);
        System.out.println("data_directory: " + data_directory);
        DataManager dm = new DataManager(serviceUrl, data_directory);

        String codingSchemeName = "NCI_Thesaurus";
        Terminology terminology = dm.getTerminologyByCodingSchemeName(codingSchemeName);
        System.out.println(terminology.getCodingSchemeName());
        System.out.println("\t" + terminology.getCodingSchemeVersion());
        System.out.println("\t" + terminology.getNamedGraph());
        System.out.println("\t" + terminology.getFilename());
        System.out.println("\t" + terminology.getData().size());
        System.out.println("\t" + terminology.getKeywordSet().size());

        codingSchemeName = "MEDDRA";
        codingSchemeName = "STY";
        codingSchemeName = "ICD10";
        codingSchemeName = "ICDO";
        codingSchemeName = "RXNORM";
        codingSchemeName = "SNOMEDCT";
        codingSchemeName = "CTEP-SDC";

        terminology = dm.getTerminologyByCodingSchemeName(codingSchemeName);
        if (terminology == null) {
			System.out.println("CodingSchemeName " + codingSchemeName + " not found.");
		} else {
			terminology = dm.populateTerminologyData(terminology);
			System.out.println(terminology.getCodingSchemeName());
			System.out.println("\t" + terminology.getCodingSchemeVersion());
			System.out.println("\t" + terminology.getNamedGraph());
			System.out.println("\t" + terminology.getFilename());
			System.out.println("\t" + terminology.getData().size());
			System.out.println("\t" + terminology.getKeywordSet().size());
    	}

        System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
	}


    public void createTerminologyDataAsNeeded(String ng) {
        Terminology terminology = (Terminology) getTerminologyByNamedGraph(ng);
        if (terminology == null) return;
        if (terminology.getData() == null) {
            String codingSchemeName = terminology.getCodingSchemeName();
			String filePathString = data_directory + File.separator + codingSchemeName + ".txt";
			File f = new File(filePathString);
			Vector term_vec = null;
			if(f.exists() && !f.isDirectory()) {
				//term_vec = readFile(filePathString);
			} else {
				if (codingSchemeName.compareTo("NCI_Thesaurus") == 0) {
					term_vec = get_terms(ng, null);
				} else {
					term_vec = get_names(ng);
				}
				Utils.saveToFile(filePathString, term_vec);
			}
		}
    }

    public void setNamedGraph2TerminologyHashmap(String ng, Terminology terminology) {
        this.namedGraph2TerminologyHashmap.put(ng, terminology);
	}


    public void createTerminologyData(String serviceUrl, String data_directory) {
		Vector data = new Vector();
		Vector terminologies = getTerminologies();
		for (int i=0; i<terminologies.size(); i++) {
			Terminology terminology = (Terminology) terminologies.elementAt(i);
			String codingSchemeName = terminology.getCodingSchemeName();
			String namedGraph = terminology.getNamedGraph();
			if (codingSchemeName.compareTo(NCI_THESAURUS) == 0) {
				data = get_terms(namedGraph, null);
			} else {
				data = get_names(namedGraph);
			}
			Utils.saveToFile(data_directory + File.separator + codingSchemeName + ".txt", data);
		}
    }


    public static void main(String[] args) {
		long ms = System.currentTimeMillis();
        String serviceUrl = args[0];
        String data_directory = args[1];
        System.out.println("serviceUrl: " + serviceUrl);
        System.out.println("data_directory: " + data_directory);

        String queryfile = args[2];
        Vector v = new DataManager().execute(queryfile);
        StringUtils.dumpVector(queryfile, v);

        System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
	}



}


/*
        (1) ICDO|2018AA|http://purl.bioontology.org/ontology/ICDO
        (2) ICD10|2018AA|http://purl.bioontology.org/ontology/ICD10
        (3) SNOMEDCT|2018AA|http://purl.bioontology.org/ontology/SNOMEDCT
        (4) NCI_Thesaurus|18.11b|http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl
        (5) NCI_Thesaurus|18.11b|http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.rdf
        (6) caDSR|1.0|http://cbiit.nci.nih.gov/caDSR
        (7) STY|1.0|http://purl.bioontology.org/ontology/STY
        (8) RXNORM|2018AA|http://purl.bioontology.org/ontology/RXNORM
        (9) CTEP-SDC|2018AA|http://purl.bioontology.org/ontology/CTEP-SDC
        (10) MEDDRA|2018AA|http://purl.bioontology.org/ontology/MEDDRA
*/
