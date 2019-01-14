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
import org.apache.commons.text.similarity.*;


/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class DataUtils {
//	LevenshteinDistance ld = null;
    //String discarded_chars = "| ,-(){}[]*&@\\\t/.:;!?_\"'";
    public static String NCIT_GRAPH = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
	OWLSPARQLUtils owlSPARQLUtils = null;
	String serviceUrl = null;
	String named_graph = null;
	String ncit_version = null;
	HashMap nameVersion2NamedGraphMap = null;
	MetadataUtils mdu = null;
	HashMap uriBaseHashMap = null;
	HashMap nameGraph2PredicateHashMap = null;
	Vector supportedNamedGraphs = null;

	PorterStemmer theStemmer = null;
	//HashSet filler_set = null;
	HashMap key2ConceptMap = null;
	HashMap code2LabelMap = null;

	HashSet keyword_set = null;
	//MappingHelper mh = null;

	String discarded_chars = gov.nih.nci.evs.mapping.common.Constants.DISCARDED_CHARS;//"| ,-(){}[]*&@\\\t/.:;!?_\"'";

	HTTPUtils httpUtils = null;
	LevenshteinDistance ld = new LevenshteinDistance(null);

	//private static String TERMINOLOGY_FILE = "terminology.txt";
	private static String VARIANT_FILE = "variant.txt";
	//AmericanBritishSpellings
	private static String DISCARDED_PHRASE_FILE = "discarded_phrases.txt";

	//private static String SYNONYM_SER_FILE = "terminology.ser";
	private Vector term_vec = null;
	private Vector synonyms = new Vector();
	private Vector discarded_synonyms = null;
	private HashMap variantHashMap = new HashMap();
	private Vector variants = new Vector();
	private HashSet filler_set = null;
	private Vector discarded_phrases = null;
	private String data_directory = null;
	HashMap kwd_count_hashmap = null;
	String codingSchemeName = null;
	HashMap keywordFreqHashMap = null;
	private int REWARD_PARAM = 10;

	public static String[] fillers = new String[] {
			"a",    "about",        "again",        "all",  "almost",
			"also", "although",     "always",       "among",        "an",
			"and",  "another",      "any",  "are",  "as",
			"at",   "be",   "because",      "been", "before",
			"being",        "between",      "both", "but",  "by",
			"can",  "could",        "did",  "do",   "does",
			"done", "due",  "during",       "each", "either",
			"enough",       "especially",   "etc",  "external",     "for",
			"found",        "from", "further",      "had",  "has",
			"have", "having",       "here", "how",  "however",
			"i",    "if",   "in",   "internal",     "into",
			"is",   "it",   "its",  "itself",       "just",
			"made", "mainly",       "make", "may",  "might",
			"most", "mostly",       "must", "nearly",       "neither",
			"nor",  "obtained",     "of",   "often",        "on",
			"or",   "other",        "our",  "overall",      "part",
			"parts",        "perhaps",      "pmid", "quite",        "rather",
			"really",       "regarding",    "secondary",    "seem", "seen",
			"several",      "should",       "show", "showed",       "shown",
			"shows",        "significantly",        "since",        "site", "sites",
			"so",   "some", "specification",        "specified",    "such",
			"than", "that", "the",  "their",        "theirs",
			"them", "then", "there",        "therefore",    "these",
			"they", "this", "those",        "through",      "thus",
			"to",   "unspecified",  "upon", "use",  "used",
			"using",        "various",      "very", "was",  "we",
			"were", "what", "when", "which",        "while",
			"with", "within",       "without",      "would"};


	public static String[] discarded_phrase_values = new String[] {
			"ill-defined",
			"not elsewhere classified",
			"not otherwise classified",
			"not specified",
			"Other specified",
			"Other types of",
			"primary site unknown, so stated",
			"Unknown and unspecified causes of"};


	public DataUtils(String serviceUrl) {
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl);
		filler_set = toHashSet(fillers);//loadFillers(gov.nih.nci.evs.mapping.common.Constants.FILLER_FILE);
		discarded_phrases = toVector(discarded_phrase_values);
		ld = new LevenshteinDistance(null);
	}


	public DataUtils(String serviceUrl, String data_directory) {
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl);
		filler_set = loadFillers(data_directory + File.separator + gov.nih.nci.evs.mapping.common.Constants.FILLER_FILE);

		Vector v = Utils.readFile(data_directory + File.separator + gov.nih.nci.evs.mapping.common.Constants.DISCARDED_PHRASE_FILE);
		discarded_phrases = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			line.toLowerCase();
			line = line.trim();
			discarded_phrases.add(line);
		}
		discarded_phrases = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(discarded_phrases);
		ld = new LevenshteinDistance(null);
	}


	public DataUtils(String serviceUrl, String data_directory, String codingSchemeName) {
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl);
		if (data_directory == null) {
			data_directory = MappingUtils.getCurrentWorkingDirectory();
		}

		filler_set = loadFillers(data_directory + File.separator + gov.nih.nci.evs.mapping.common.Constants.FILLER_FILE);

		Vector v = Utils.readFile(data_directory + File.separator + gov.nih.nci.evs.mapping.common.Constants.DISCARDED_PHRASE_FILE);
		discarded_phrases = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			line.toLowerCase();
			line = line.trim();
			discarded_phrases.add(line);
		}
		discarded_phrases = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(discarded_phrases);
		ld = new LevenshteinDistance(null);
		String term_file = data_directory + File.separator + codingSchemeName + ".txt";
		Vector term_vec = Utils.readFile(term_file);
		System.out.println("term_vec: " + term_vec.size());
		kwd_count_hashmap = create_kwd_count_hashmap(term_vec);
	}

	public HashMap create_kwd_count_hashmap(Vector term_vec) {
		long ms = System.currentTimeMillis();
		keywordFreqHashMap = new HashMap();
		boolean removeFillers = true;
		HashMap kwd_count_hashmap = new HashMap();
		for (int k=0; k<term_vec.size(); k++) {
			String line = (String) term_vec.elementAt(k);
			Vector u = StringUtils.parseData(line, '|');
			String code = (String) u.elementAt(0);
			String pt = (String) u.elementAt(1);

			Vector tokens = tokenize(pt, removeFillers);
			for (int i=0; i<tokens.size(); i++) {
				String token = (String) tokens.elementAt(i);
				Integer int_obj = new Integer(1);
				if (kwd_count_hashmap.containsKey(token)) {
					int_obj = (Integer) kwd_count_hashmap.get(token);
					int int_val = int_obj.intValue();
					int_obj = new Integer(int_val+1);
				}
				kwd_count_hashmap.put(token, int_obj);
				gov.nih.nci.evs.mapping.bean.Keyword keyword = new gov.nih.nci.evs.mapping.bean.Keyword(int_obj.intValue(), token);
				keywordFreqHashMap.put(token, keyword);
			}
			String syn = (String) u.elementAt(2);
			tokens = tokenize(syn, removeFillers);
			for (int i=0; i<tokens.size(); i++) {
				String token = (String) tokens.elementAt(i);
				Integer int_obj = new Integer(1);
				if (kwd_count_hashmap.containsKey(token)) {
					int_obj = (Integer) kwd_count_hashmap.get(token);
					int int_val = int_obj.intValue();
					int_obj = new Integer(int_val+1);
				}
				kwd_count_hashmap.put(token, int_obj);
				gov.nih.nci.evs.mapping.bean.Keyword keyword = new gov.nih.nci.evs.mapping.bean.Keyword(int_obj.intValue(), token);
				keywordFreqHashMap.put(token, keyword);
			}

		}
		System.out.println("\nTotal create_kwd_count_hashmap run time (ms): " + (System.currentTimeMillis() - ms));
		return kwd_count_hashmap;
	}


	public HashSet toHashSet(String[] a) {
		HashSet hset = new HashSet();
		for (int i=0; i<a.length; i++) {
			String t = a[i];
			hset.add(t);
		}
		return hset;
	}

	public Vector toVector(String[] a) {
		Vector w = new Vector();
		for (int i=0; i<a.length; i++) {
			String t = a[i];
			w.add(t);
		}
		return w;
	}



    public HashSet loadFillers(String filename) {
		Vector v = Utils.readFile(filename);
		HashSet hset = new HashSet();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			line = line.substring(2, line.length());
			Vector u = StringUtils.parseData(line, ',');
			for (int j=0; j<u.size(); j++) {
				String word = (String) u.elementAt(j);
				word = word.trim();
				word = word.toLowerCase();
				hset.add(word);
			}
		}
		return hset;
	}

	public boolean isNCIt(String namedGraph) {
		if (namedGraph.indexOf("Thesaurus") != -1 || namedGraph.indexOf("NCIt") != -1) return true;
		return false;
	}


    public Vector loadTerminology(String namedGraph) {
		long ms = System.currentTimeMillis();
		Vector term_vec = null;
        File f = new File(gov.nih.nci.evs.mapping.common.Constants.TERMINOLOGY_FILE);
		if(f.exists() && !f.isDirectory()) {
		    term_vec = Utils.readFile(gov.nih.nci.evs.mapping.common.Constants.TERMINOLOGY_FILE);
		    //key2ConceptMap = createKey2ConceptMap(term_vec);
		} else {
			if (isNCIt(namedGraph)) {
				term_vec = get_terms(namedGraph, null);
			} else {
				term_vec = get_names(namedGraph);
			}
			Utils.saveToFile(gov.nih.nci.evs.mapping.common.Constants.TERMINOLOGY_FILE, term_vec);
		}
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms));
		return term_vec;
	}

	public static Vector formatOutput(Vector v) {
		return ParserUtils.formatOutput(v);
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

    public String getQuery(String query_file) {
		try {
			String query = httpUtils.loadQuery(query_file, false);
			return query;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

    public String getJSONResponseString(String query) {
        try {
			query = httpUtils.encode(query);
            String json = httpUtils.executeQuery(query);
			return json;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
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

    public void runQuery(String query_file) {
		long ms = System.currentTimeMillis();
		String query = getQuery(query_file);
		System.out.println(query);
		Vector w = new Vector();
		w.add(query);
		int n = query_file.lastIndexOf(".");
		String method_name = "construct_" + query_file.substring(0, n);
		String params = "String matchText";
		Vector w0 = Utils.create_construct_statement(method_name, params, query_file);
		w.addAll(w0);
		String json = getJSONResponseString(query);
		w.add(json);
        Vector v = execute(query_file);
        v = formatOutput(v);

        Utils.saveToFile("output_" + query_file, v);
        w.addAll(v);
        Utils.saveToFile("results_" + query_file, w);
        System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
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


	public Vector get_terms(String namedGraph, String code) {
	    String query = construct_get_terms(namedGraph, code);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    return v;
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

    public Vector replace_null_values(Vector v) {
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String code = (String) u.elementAt(0);
			String pt = (String) u.elementAt(1);
			String syn = (String) u.elementAt(2);
			if (syn.compareTo("null") == 0) {
				w.add(code + "|" + pt + "|" + pt);
			} else {
				w.add(line);
			}
		}
		return w;
	}

	public boolean has_intersection(Vector v1, Vector v2) {
        Vector v3 = (Vector) v1.clone();
        for (int i=0; i<v2.size(); i++) {
			String wd = (String) v2.elementAt(i);
			if (!v3.contains(wd)) {
				v3.add(wd);
			}
		}
		if (v3.size() == v1.size() + v2.size()) {
			return false;
		}
		return true;
	}

//http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.rdf


	public String construct_get_roles_by_code(String namedGraph, String code) {
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
		buf.append("SELECT ?y_label ?z_label ?z_code").append("\n");
		buf.append("{").append("\n");
		buf.append("graph <" + namedGraph + ">").append("\n");
		buf.append("{").append("\n");
		buf.append("?x a owl:Class .").append("\n");
		buf.append("?x :NHC0 \"" + code + "\"^^<http://www.w3.org/2001/XMLSchema#string> .").append("\n");
		buf.append("?x ?y ?z .").append("\n");
		buf.append("?z a owl:Class .").append("\n");
		buf.append("?z rdfs:label ?z_label .").append("\n");
		buf.append("?z :NHC0 ?z_code .").append("\n");
		buf.append("?y rdfs:label ?y_label .").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}

	public Vector get_roles_by_code(String namedGraph, String code) {
	    String query = construct_get_roles_by_code(namedGraph, code);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    return v;
	}


	public String construct_get_properties_by_code(String namedGraph, String code) {
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
		buf.append("SELECT ?y_label ?z").append("\n");
		buf.append("{").append("\n");
		buf.append("graph <" + namedGraph + ">").append("\n");
		buf.append("{").append("\n");
		buf.append("?x a owl:Class .").append("\n");
		buf.append("?x :NHC0 \"" + code + "\"^^<http://www.w3.org/2001/XMLSchema#string> .").append("\n");
		buf.append("?x ?y ?z .").append("\n");
		buf.append("?y rdfs:label ?y_label .").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}

	public Vector get_properties_by_code(String namedGraph, String code) {
	    String query = construct_get_properties_by_code(namedGraph, code);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) return null;
	    v = new ParserUtils().getResponseValues(v);
	    v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    return v;
	}

/*
	public Vector review_synonyms(HashMap key2ConceptMap) {
		boolean output_rules = false;
		return review_synonyms(key2ConceptMap, output_rules);
	}

	public Vector review_synonyms(HashMap key2ConceptMap, boolean output_rules) {
		Vector w = new Vector();
		int knt = 0;
		for (int i=0; i<synonyms.size(); i++) {
			String line = (String) synonyms.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String word1 = (String) u.elementAt(0);
			String word2 = (String) u.elementAt(1);
			if (key2ConceptMap.containsKey(word1) || key2ConceptMap.containsKey(word2)) {
				Vector w1 = (Vector) key2ConceptMap.get(word1);
				Vector w2 = (Vector) key2ConceptMap.get(word2);

				if (w1 != null && w2 != null && w1.size() > 0 && w2.size() > 0) {
					if (!has_intersection(w1, w2)) {
						knt++;
						//System.out.println("\n(" + knt + ") " + line);

						Vector rules = this.mh.get_mapping_rule(word1, word2);
						if (output_rules) {
							w.addAll(rules);
						} else {
							w.add(line);
						}

						//StringUtils.dumpVector("\tBusiness Rules", rules);
						//System.out.println("\n");

						//StringUtils.dumpVector("\t" + word1, w1);
						//StringUtils.dumpVector("\n\t" + word2, w2);
						//System.out.println("\n\tWARNING: difference concepts.");
					}
				}
			}
		}
		return w;
	}
*/


    public static String toTabDelimited(Vector words) {
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<words.size(); k++) {
			String word = (String) words.elementAt(k);
			buf.append(word).append("|");
		}
		String t = buf.toString();
		if (t.endsWith("|")) {
			t = t.substring(0, t.length()-1);
		}
		return t;
	}

	public boolean isDiscardedChar(char c) {
		if (discarded_chars.indexOf(c) != -1) {
			return true;
		}
		return false;
	}

	public String removeLeadingDiscardedChar(String s) {
		if (s.length() > 0) {
			char c = s.charAt(0);
			if (isDiscardedChar(c)) {
				return s.substring(1, s.length());
			}
		}
		return s;
	}

	public String removeTrailingDiscardedChar(String s) {
		if (s.length() > 0) {
			char c = s.charAt(s.length()-1);
			if (isDiscardedChar(c)) {
				return s.substring(0, s.length()-1);
			}
		}
		return s;
	}

	public boolean isFiller(String word) {
		return filler_set.contains(word);
	}


	public Vector tokenize(String t) {
		return tokenize(t, true);
	}

	public Vector tokenize(String t, boolean discardFiller) {
		if (t == null) return null;
		t = t.toLowerCase();
		//t = t.replaceAll("[^a-zA-Z0-9]", " ");

		StringTokenizer st = new StringTokenizer(t);
		Vector words = new Vector();
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 token = removeTrailingDiscardedChar(token);
			 token = removeLeadingDiscardedChar(token);
             if (discardFiller) {
				 if (!isFiller(token)) {
					 if (!words.contains(token)) {
						 words.add(token);
					 }
				 }
			 } else {
				 if (!words.contains(token)) {
					 words.add(token);
				 }
			 }
		}
		words = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(words);
		return words;
	}


	public String substitute(String t) {
		t = t.toLowerCase();
		for (int i=0; i<discarded_phrases.size(); i++) {
			String target = (String) discarded_phrases.elementAt(i);
			target = target.toLowerCase();
			t = t.replaceAll(target, "");
		}
		return t.trim();
	}

	public String construct_partial_matches(String namedGraph, String matchText) {
		String line = substitute(matchText);
		line = line.trim();
		Vector u = tokenize(line);

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
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_code ?x_label ").append("\n");
		buf.append("{").append("\n");
		buf.append("graph <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl> {").append("\n");
		buf.append("{").append("\n");
		buf.append("?x a owl:Class .").append("\n");
		buf.append("?x :NHC0 ?x_code .").append("\n");
		buf.append("?x rdfs:label ?x_label .").append("\n");
		buf.append("FILTER (").append("\n");
		for (int i=0; i<u.size(); i++) {
			String token = (String) u.elementAt(i);
			if (i < u.size()-1) {
		    	buf.append("contains(lcase(str(?x_label)), '" + token + "') && ").append("\n");
			} else {
				buf.append("contains(lcase(str(?x_label)), '" + token + "') ").append("\n");
			}
		}
		buf.append(")").append("\n");
		buf.append("}").append("\n");

		buf.append("UNION").append("\n");
		buf.append("{").append("\n");
		buf.append("?x a owl:Class .").append("\n");
		buf.append("?x :NHC0 ?x_code .").append("\n");
		buf.append("?x rdfs:label ?x_label .").append("\n");
		buf.append("?z_axiom a owl:Axiom  .").append("\n");
		buf.append("?z_axiom owl:annotatedSource ?x .").append("\n");
		buf.append("?z_axiom owl:annotatedProperty ?p .").append("\n");
		buf.append("?p rdfs:label  \"FULL_SYN\"^^xsd:string .").append("\n");
		buf.append("?z_axiom owl:annotatedTarget ?term_name .").append("\n");
		buf.append("?z_axiom ?y ?z .").append("\n");
		buf.append("FILTER (").append("\n");
		for (int i=0; i<u.size(); i++) {
			String token = (String) u.elementAt(i);
			if (i < u.size()-1) {
		    	buf.append("contains(lcase(str(?term_name)), '" + token + "') && ").append("\n");
			} else {
				buf.append("contains(lcase(str(?term_name)), '" + token + "') ").append("\n");
			}
		}
		buf.append(")").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		buf.append("}").append("\n");
		return buf.toString();
	}


	public String construct_partial_matches_meta_source(String namedGraph, String matchText) {
		String line = substitute(matchText);
		line = line.trim();
		Vector u = tokenize(line);
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#>").append("\n");
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_code ?x_label").append("\n");
		buf.append("{");
		buf.append("GRAPH <" + namedGraph + "> {").append("\n");
		buf.append("		{");
		buf.append("			?x ?p ?y .").append("\n");
		buf.append("			?x skos:prefLabel ?x_label .").append("\n");
		buf.append("			?x skos:notation ?x_code .").append("\n");
		buf.append("			FILTER (").append("\n");
		for (int i=0; i<u.size(); i++) {
			String token = (String) u.elementAt(i);
			if (i < u.size()-1) {
		    	buf.append("contains(lcase(str(?x_label)), '" + token + "') && ").append("\n");
			} else {
				buf.append("contains(lcase(str(?x_label)), '" + token + "') ").append("\n");
			}
		}
		buf.append("			)").append("\n");
		buf.append("		}").append("\n");
		buf.append("		UNION").append("\n");
		buf.append("		{");
		buf.append("			?x ?p ?y .").append("\n");
		buf.append("			?x skos:altLabel ?x_label .").append("\n");
		buf.append("			?x skos:notation ?x_code .").append("\n");

		buf.append("			FILTER (").append("\n");
		for (int i=0; i<u.size(); i++) {
			String token = (String) u.elementAt(i);
			if (i < u.size()-1) {
		    	buf.append("contains(lcase(str(?x_label)), '" + token + "') && ").append("\n");
			} else {
				buf.append("contains(lcase(str(?x_label)), '" + token + "') ").append("\n");
			}
		}
		buf.append("			)").append("\n");

		buf.append("		}").append("\n");
		buf.append("	}").append("\n");
		buf.append("}").append("\n");
		buf.append("");
		return buf.toString();
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////
	public Vector get_kwd_frequencies(String term) {
		Vector w = new Vector();
		Vector tokens = tokenize(term, true);
		for (int i=0; i<tokens.size(); i++) {
			String token = (String) tokens.elementAt(i);
			Integer int_obj = (Integer) kwd_count_hashmap.get(token);
			int freq = 0;
			if (int_obj != null) {
				freq = int_obj.intValue();
			}
			w.add(new Keyword(freq, token));
		}
		w = new gov.nih.nci.evs.mapping.util.SortUtils().quickSort(w);
		return w;
	}

    public String getMostSignificantKeywords(Vector kwd_vec, int n) {
        int knt = 0;
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<kwd_vec.size(); i++) {
			Keyword kwd = (Keyword) kwd_vec.elementAt(i);
			buf.append(kwd.getWord()).append(" ");
			knt++;
			if (knt == n) break;
		}
		return buf.toString().trim();
	}

    public String getLeastSignificantKeywords(Vector kwd_vec, int n) {
        int knt = 0;
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<kwd_vec.size(); i++) {
			int j = kwd_vec.size() - i-1;
			Keyword kwd = (Keyword) kwd_vec.elementAt(j);
			buf.append(kwd.getWord()).append(" ");
			knt++;
			if (knt == n) break;
		}
		return buf.toString().trim();
	}

    public Vector searchBestMatchedTerms(String namedGraph, String term) {
		Vector w = new Vector();
		int reward = REWARD_PARAM;
        //Vector<gov.nih.nci.evs.mapping.bean.MappingEntry> v = mu.mapTo("NA", term);
		Vector kwd_vec = get_kwd_frequencies(term);
		int n = 4;
		Vector partial_matches = null;
		String search_string = getMostSignificantKeywords(kwd_vec, n);
		while ((partial_matches == null || partial_matches.size() == 0) && n > 1) {
	        //System.out.println(	"\n*** search_string: " + search_string);
			partial_matches = get_partial_matches(namedGraph, search_string);
			if (partial_matches == null || partial_matches.size() == 0) {
				//System.out.println(	"*** No match found for: " + search_string);
			} else {
				//StringUtils.dumpVector("partial_matches", partial_matches);
				w.addAll(partial_matches);
			}
			n = n-1;
			search_string = getMostSignificantKeywords(kwd_vec, n);
		}
		//if (w != null || w.size() > 0) {
		//	return sortMatchedTerms(w, term, reward);
		//}

		Vector w2 = searchSecondaryMatchedTerms(namedGraph, term);
        if (w2 != null && w2.size() > 0) {
			w.addAll(w2);
		}
		if (w != null || w.size() > 0) {
			return sortMatchedTerms(w, term, reward);
		}

		for (int k=0; k<=3; k++) {
			if (kwd_vec.size() > 3) {
				Keyword second_kwd = (Keyword) kwd_vec.elementAt(k);
				search_string = second_kwd.getWord();
				System.out.println(	"*** search_string: " + search_string);
				partial_matches = get_partial_matches(namedGraph, search_string);
				if (partial_matches == null || partial_matches.size() == 0) {
					System.out.println(	"\n*** No match found for: " + search_string);
				} else {
					//StringUtils.dumpVector("partial_matches", partial_matches);
					w.addAll(partial_matches);
				}
			}
	    }
	    Vector v = sortMatchedTerms(w, term, reward);
	    return v;
	}

    public Vector searchSecondaryMatchedTerms(String namedGraph, String term) {
		Vector w = new Vector();
        //Vector<gov.nih.nci.evs.mapping.bean.MappingEntry> v = mu.mapTo("NA", term);
		Vector kwd_vec = get_kwd_frequencies(term);
		int n = 3;
		String search_string = getLeastSignificantKeywords(kwd_vec, n);
		Vector partial_matches = get_partial_matches(namedGraph, search_string);
		return partial_matches;
	}


    public int getSimilarityScore(String searchString, String matchedTerm) {
		return (ld.apply(searchString.toLowerCase(), matchedTerm.toLowerCase())).intValue();
	}


	public Vector sortMatchedTerms(Vector v, String searchString, int reward) {
		Vector tokens = tokenize(searchString, true);
		Vector w = new Vector();
		if (v == null ) return w;
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String matchedTerm = (String) u.elementAt(1);
			Vector mt_tokens = tokenize(matchedTerm, true);
			int base_score = 0;
			for (int j=0; j<mt_tokens.size(); j++) {
				String wd = (String) mt_tokens.elementAt(j);
				if (tokens.contains(wd)) {
					base_score = base_score + reward;
				}
			}
			int score = getSimilarityScore(searchString, matchedTerm) - reward;
			if (score < 0) score = 0;
			w.add("" + score + "|" + line);
		}
		return sortMatchedTerms(w);
	}


	public static Vector sortMatchedTerms(Vector v) {
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			DelimitedString ds = new DelimitedString(line, '|', StringUtils.parseData(line, '|'), line.length());
			ds.setSortIndex(0);
			w.add(ds);
		}
		v = new Vector();
		w = new gov.nih.nci.evs.mapping.util.SortUtils().quickSort(w);
		for (int i=0; i<w.size(); i++) {
			DelimitedString ds = (DelimitedString) w.elementAt(i);
			//v.add(ds.getLine());
			System.out.println(ds.getLine());
			String t = ds.getLine();
			int n = t.indexOf("|");
			v.add(t.substring(n+1, t.length()));
		}
		return v;
	}

	public Vector get_partial_matches(String namedGraph, String matchText) {
		String query = null;
		if (isNCIt(namedGraph)) {
	        query = construct_partial_matches(namedGraph, matchText);
		} else {
			query = construct_partial_matches_meta_source(namedGraph, matchText);
		}
		//System.out.println(query);
	    Vector v = executeQuery(query);
	    if (v == null) return null;
	    if (v.size() == 0) {
			return v;
		}
	    v = new ParserUtils().getResponseValues(v);
	    return new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
	    //return sortMatchedTerms(v, matchText, REWARD_PARAM);
	}

    public static void main(String[] args) {
        String serviceUrl = args[0];
        String named_graph = args[1];

        //String query_file = args[2];

        System.out.println(serviceUrl);
        System.out.println(named_graph);

        /*
        System.out.println(query_file);
        String code = "C32608";

        named_graph = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.rdf";
        Vector w = new DataUtils(serviceUrl).get_roles_by_code(named_graph, code);
        StringUtils.dumpVector("roles_" + code, w);

        w = new DataUtils(serviceUrl).get_properties_by_code(named_graph, code);
        StringUtils.dumpVector("properties_" + code, w);


		//new DataUtils(serviceUrl).runQuery(query_file);
		*/

		String matchText = "megakaryoblastic leukemia";
		matchText = "relapse myeloma multiple";
		matchText = "Combined immunodeficiency, unspecified";
		matchText = "Malignant neoplasm: Optic nerve";
		matchText = "Malignant neoplasm of piriform sinus";

System.out.println("Case 1");
	    Vector w = new DataUtils(serviceUrl).get_partial_matches(named_graph, matchText);
        StringUtils.dumpVector(matchText, w);

System.out.println("Case 2");
	    w = new DataUtils(serviceUrl, null, "NCI_Thesaurus").searchBestMatchedTerms(named_graph, matchText);
        StringUtils.dumpVector(matchText, w);
	}
}
