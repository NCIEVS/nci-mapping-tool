package gov.nih.nci.evs.mapping.util;
import gov.nih.nci.evs.mapping.bean.*;
import gov.nih.nci.evs.mapping.common.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;

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
import com.google.gson.*;


/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class MappingUtils {

	PorterStemmer theStemmer = null;
	HashSet filler_set = null;
	HashMap key2ConceptMap = null;
	HashMap code2LabelMap = null;
	HashMap historyMap = null;

	HashSet keyword_set = null;
	//MappingHelper mh = null;

	private Vector term_vec = null;
	private Vector synonyms = new Vector();
	private HashMap variantHashMap = new HashMap();
	private Vector variants = new Vector();

	private Vector discarded_phrases = null;
	private HashMap americanBritishSpellingHashMap = null;

	private String data_directory = null;
	private Terminology terminology = null;
	private String codingSchemeName = null;

	public String vbtfile_nm = null;
	public String source = null;

	public String textfile = null;//output_file_prefix + "_" + date_str + ".txt";
	public String jsonfile = null;//output_file_prefix + "_" + date_str + ".json";
	public String excelfile = null;//output_file_prefix + "_" + date_str + ".xlsx";
	public String htmlfile = null;//output_file_prefix + "_" + date_str + ".html";
	public String htmltblfile = null;//output_file_prefix + "_table_" + date_str + ".html";
	public String output_file_prefix = null;


    public static String getHyperlink(String named_graph, String name, String code) {
		StringBuffer buf = new StringBuffer();
		buf.append("<a href=\"/sparql/ConceptReport.jsp?ng=" + named_graph + "&code=" + code + "\">").append("\n");
		buf.append(name).append("\n");
		buf.append("</a>").append("\n");
		return buf.toString();
    }


	public MappingUtils() {
        try {
			data_directory = System.getProperty("user.dir");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public MappingUtils(String data_directory) {
		this.data_directory = data_directory;
        initialize(data_directory);
	}

	public MappingUtils(String data_directory, Terminology terminology) {
		this.data_directory = data_directory;
		this.terminology = terminology;
		this.codingSchemeName = terminology.getCodingSchemeName();
        initialize(data_directory, terminology);
	}

	public MappingUtils(String data_directory, String codingSchemeName) {
		this.data_directory = data_directory;
		this.codingSchemeName = codingSchemeName;
        initialize(data_directory, codingSchemeName);
	}

	public void initialize(String data_directory) {
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
	}

	public void initialize(String data_directory, String codingSchemeName) {
		long ms0 = System.currentTimeMillis();
		theStemmer = new PorterStemmer();
		historyMap = load_history(data_directory + File.separator + Constants.HISTORY_FILE);
		this.discarded_phrases = readFile(data_directory + File.separator + Constants.DISCARDED_PHRASE_FILE);
		this.synonyms = readFile(data_directory + File.separator + Constants.SYNONYM_FILE);
		filler_set = loadFillers(data_directory + File.separator + Constants.FILLER_FILE);
		keyword_set = new HashSet();
		variants = readFile(data_directory + File.separator + Constants.VARIANT_FILE);
		variantHashMap = createVariantHashMap();

		String filePathString = data_directory + File.separator + codingSchemeName + ".txt";
		term_vec = readFile(filePathString);
		key2ConceptMap = createKey2ConceptMap(term_vec);
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms0));
	}

	public void initialize(String data_directory, Terminology terminology) {
		long ms0 = System.currentTimeMillis();
		theStemmer = new PorterStemmer();
		historyMap = load_history(data_directory + File.separator + Constants.HISTORY_FILE);
		this.discarded_phrases = readFile(data_directory + File.separator + Constants.DISCARDED_PHRASE_FILE);
		this.synonyms = readFile(data_directory + File.separator + Constants.SYNONYM_FILE);
		filler_set = loadFillers(data_directory + File.separator + Constants.FILLER_FILE);
		keyword_set = new HashSet();
		variants = readFile(data_directory + File.separator + Constants.VARIANT_FILE);
		variantHashMap = createVariantHashMap();
		term_vec = readFile(data_directory + File.separator + terminology.getFilename());

		key2ConceptMap = createKey2ConceptMap(term_vec);
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms0));
	}

	public boolean isFiller(String word) {
		return filler_set.contains(word);
	}

	public boolean isKeyword(String word) {
		return keyword_set.contains(word);
	}

	public HashMap getVariantHashMap() {
		return this.variantHashMap;
	}

    public HashMap getCode2LabelMap() {
		return code2LabelMap;
	}

	public boolean isDiscardedChar(char c) {
		if (Constants.DISCARDED_CHARS.indexOf(c) != -1) {
			return true;
		}
		return false;
	}

	 public void saveToFile(String outputfile, String t) {
		 Vector v = new Vector();
		 v.add(t);
		 saveToFile(outputfile, v);
	 }

	 public void saveToFile(String outputfile, Vector v) {
		if (outputfile == null) return;
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			if (pw != null) {
				if (v != null && v.size() > 0) {
					for (int i=0; i<v.size(); i++) {
						String t = (String) v.elementAt(i);
						pw.println(t);
					}
				}
			}
		} catch (Exception ex) {

		} finally {
			try {
				if (pw != null) pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	 }

	public Vector readFile(String filename)
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

    public Vector parseData(String line, char delimiter) {
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

	public String removeLeadingDiscardedChar(String s) {
		if (s.length() > 0) {
			char c = s.charAt(0);
			if (isDiscardedChar(c)) {
				return s.substring(1, s.length());
			}
		}
		return s;
	}


	public String removeLeadingBackSlashSChar(String s) {
		if (s.length() > 1) {
			if (s.startsWith("\\\\s")) {
				return s.substring(2, s.length());
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

	public String stem(String s) {
		theStemmer.reset();
		char[] c = s.toCharArray();
		for (char element : c) {
			theStemmer.add(element);
		}
		theStemmer.stem();
		return theStemmer.toString();
	}

    public HashSet loadFillers(String filename) {
		Vector v = readFile(filename);
		HashSet hset = new HashSet();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			line = line.substring(2, line.length());
			Vector u = parseData(line, ',');
			for (int j=0; j<u.size(); j++) {
				String word = (String) u.elementAt(j);
				word = word.trim();
				word = word.toLowerCase();
				hset.add(word);
			}
		}
		return hset;
	}


	public Vector get_term_vec() {
		return this.term_vec;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    public String vbt2Key(String t) {
		return vbt2Key(t, true);
	}

/*
	public Vector tokenize(String t) {
		if (t == null) return null;
		t = t.toLowerCase();
		//t = t.replaceAll("[^a-zA-Z0-9]", " ");
		t = t.replaceAll("[^éa-zA-Z0-9]", " ");
		StringTokenizer st = new StringTokenizer(t);
		Vector words = new Vector();
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 if (variantHashMap.containsKey(token)) {
				 String variant = (String) variantHashMap.get(token);
				 token = variant;
			 }
			 token = removeTrailingDiscardedChar(token);
			 token = removeLeadingDiscardedChar(token);
			 if (!isFiller(token)) {
				 if (!words.contains(token)) {
					 words.add(token);
				 }
			 }
		}
		words = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(words);
		return words;
	}
*/

    public static String toDelimited(Vector words) {
		return toDelimited(words, "|");
	}

    public static String toDelimited(Vector words, String tab) {
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<words.size(); k++) {
			String word = (String) words.elementAt(k);
			buf.append(word);
			if (k<words.size()-1) {
				buf = buf.append(tab);
			}
		}
		return buf.toString();
	}


	public String words2Key(Vector words, boolean stemming) {
		if (words == null) return null;
		words = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(words);
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<words.size(); i++) {
			 String token = (String) words.elementAt(i);
			 if (variantHashMap.containsKey(token)) {
				 String s = (String) variantHashMap.get(token);
				 buf.append(s).append("|");
			 } else {
				 token = removeTrailingDiscardedChar(token);
				 String stem = token;
				 if (stemming) {
					 stem = stem(token);
				 }
				 buf.append(stem).append("|");
			 }
		}
		String s = buf.toString();
		if (s.endsWith("|")) {
			s = s.substring(0, s.length()-1);
		}
		return s;
	}

    public String vbt2Key(String t, boolean stemming) {
		if (t == null) return null;
		Vector words = tokenize_str(t);
		String s = words2Key(words, stemming);
		return s;
    }


    public String toKey(String t) {
        if (t == null) return null;
		Vector words = tokenize_str(t);
		String s = words2Key(words, true);
		return s;
    }

    public String getLabel(String code) {
		if (!code2LabelMap.containsKey(code)) return null;
		return (String) code2LabelMap.get(code);
	}

    public HashMap createCode2LabelMap(Vector term_vec) {
		HashMap code2LabelMap = new HashMap();
		HashMap hmap = new HashMap();
		for (int i=0; i<term_vec.size(); i++) {
			String line = (String) term_vec.elementAt(i);
			Vector u = parseData(line, '|');
			String code = (String) u.elementAt(0);
			String pt = (String) u.elementAt(1);
			String syn = (String) u.elementAt(2);
			if (!code2LabelMap.containsKey(code)) {
				code2LabelMap.put(code, pt);
			}
    	}
		return code2LabelMap;
	}

	public String removeSpecifiedCharacters(String str, String charsToRemove) {
		if (str == null || str.length() == 0) return str;
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			String s = "" + str.charAt(i);
			if (charsToRemove.indexOf(s) == -1) {
				buf.append(s);
			} else {
				buf.append(" ");
			}
		}
		String t = buf.toString();
		return t.trim();
	}

	public String discardedLeadingChars(String str, String charsToDiscard) {
		if (str == null || str.length() == 0) return str;
		String s = "" + str.charAt(0);
		if (charsToDiscard.indexOf(s) != -1) {
			str = str.substring(1, str.length());
		}
		return str;
	}

    public Vector tokenize_term(String t) {
		Vector w = new Vector();
		String charsToDiscard = "'//\"#%{}";
		t = t.replaceAll("[-+.^:,]"," ");
		t = removeSpecifiedCharacters(t, "()_{}%\"#[]*");
		t = t.toLowerCase();
		if (t.endsWith(",")) {
			t = t.substring(0, t.length()-1);
		}

		StringTokenizer st = new StringTokenizer(t);
		Vector words = new Vector();
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 token = removeTrailingDiscardedChar(token);
			 token = discardedLeadingChars(token, charsToDiscard);
			 token = token.trim();
			 if (token.length() > 0) {
				 w.add(token);
			 }
		}
		return w;
	}


    public HashSet create_keyword_set(Vector term_vec) {
        if (term_vec == null) return null;
        String charsToDiscard = "'//";
        HashSet keyword_set = new HashSet();
        //for (int i=0; i<1000; i++) {
		for (int i=0; i<term_vec.size(); i++) {
			String line = (String) term_vec.elementAt(i);
			Vector u = parseData(line, '|');
			for (int k=1; k<=2; k++) {
				String t = (String) u.elementAt(k);
				/*
				t = t.replaceAll("[-+.^:,]","");
				t = removeSpecifiedCharacters(t, "()_{}%\"#[]*", charsToDiscard);
				t = t.toLowerCase();
				if (t.endsWith(",")) {
					t = t.substring(0, t.length()-1);
				}

				StringTokenizer st = new StringTokenizer(t);
				Vector words = new Vector();
				while (st.hasMoreTokens()) {
					 String token = st.nextToken();
					 token = removeTrailingDiscardedChar(token);
					 token = discardedLeadingChars(token, charsToDiscard);
					 token = token.trim();
					 if (token.length() > 0) {
					 	 keyword_set.add(token);
					 }
				}
				*/



				Vector w = tokenize_term(t);
				for (int j=0; j<w.size(); j++) {
					String word = (String) w.elementAt(j);
					keyword_set.add(word);
				}
			 }
		 }
		 return keyword_set;
	 }

    public HashMap createKey2ConceptMap(Vector term_vec) {
		code2LabelMap = createCode2LabelMap(term_vec);
		keyword_set = create_keyword_set(term_vec);

		HashMap hmap = new HashMap();
		for (int i=0; i<term_vec.size(); i++) {
			String line = (String) term_vec.elementAt(i);
			Vector u = parseData(line, '|');

			String code = (String) u.elementAt(0);
			String pt = (String) u.elementAt(1);
			String syn = (String) u.elementAt(2);

			pt = pt.toLowerCase();
			if (pt.endsWith(",")) {
				pt = pt.substring(0, pt.length()-1);
			}

			syn = syn.toLowerCase();
			if (syn.endsWith(",")) {
				syn = syn.substring(0, syn.length()-1);
			}

			String key = toKey(pt);
			//System.out.println(pt + " --> " + key);

			Vector w = new Vector();
			if (hmap.containsKey(key)) {
				w = (Vector) hmap.get(key);
			}
			if (!w.contains(code)) {
				w.add(code);
			}
			hmap.put(key, w);

			key = toKey(syn);
			//System.out.println(syn + " --> " + key);


			w = new Vector();
			if (hmap.containsKey(key)) {
				w = (Vector) hmap.get(key);
			}
			if (!w.contains(code)) {
				w.add(code);
			}
			hmap.put(key, w);
		}
		return hmap;
	}

	public Vector mapTo(String vbt) {
		if (vbt == null) return null;
		Vector w = null;
		String key = vbt2Key(vbt);
		if (key2ConceptMap.containsKey(key)) {
			w = new Vector();
			Vector v = (Vector) key2ConceptMap.get(key);
			for (int i=0; i<v.size(); i++) {
				String code = (String) v.elementAt(i);
				String label = getLabel(code);
				w.add(label + "|" + code);
			}
		}
		return w;
	}

/*
	public void analyzeVerbatim(PrintWriter pw, String t) {
        if (t == null) return;
        t = t.toLowerCase();
        t = t.replaceAll("_", " ");
		StringTokenizer st = new StringTokenizer(t);
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 token = removeTrailingDiscardedChar(token);

			 if (keyword_set.contains(token)) {
				if (isFiller(token)) {
				 	pw.println("\t" + token + " (filler)");
				} else {
					pw.println("\t" + token);
				}
			 } else {
				pw.println("\t" + token + " (?)");
			 }
		}
	}

	public void analyzeVerbatim(String t) {
        if (t == null) return;
        t = t.toLowerCase();
        t = t.replaceAll("_", " ");
		StringTokenizer st = new StringTokenizer(t);
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 //if (token.endsWith(",")) {
				 token = removeTrailingDiscardedChar(token);
				 //token = token.substring(0, token.length()-1);
			 //}

			 if (keyword_set.contains(token)) {
				if (isFiller(token)) {
				 	System.out.println("\t" + token + " (filler)");
				} else {
					System.out.println("\t" + token);
				}
			 } else {
				System.out.println("\t" + token + " (?)");
			 }
		}
	}
*/
    public List<MappingEntry> loadMappingEntries(String filename) {
        List<MappingEntry> entries =  new ArrayList<>();
        Vector w = readFile(filename);
        for (int i=0; i<w.size(); i++) {
			String line = (String) w.elementAt(i);
			Vector u = parseData(line, '|');
			String sourceCode = (String) u.elementAt(0);
			String sourceTerm = (String) u.elementAt(1);
			String targetCode = (String) u.elementAt(2);
			String targetLabel = (String) u.elementAt(3);
			MappingEntry entry = new MappingEntry(sourceCode, sourceTerm, targetCode, targetLabel);
			entries.add(entry);

		}
		return entries;
	}

	public HashMap createAmericanBritishSpellingHashmap(String americanBritishSpellingsTxt) {
		HashMap hmap = new HashMap();
		Vector w = readFile(americanBritishSpellingsTxt);
		for (int i=0; i<w.size(); i++) {
			String line = (String) w.elementAt(i);
			if (line.indexOf(", ") != -1) {
				line = line.replaceAll(", ", "|");
			}
			Vector u = parseData(line, '|');
			for (int j=0; j<u.size(); j++) {
				String str = (String) u.elementAt(j);
				hmap.put(stem(str), u);
			}
		}
		return hmap;
	}

	public Vector get_matches(String matchText) {
		String line = matchText;
		line = line.trim();
        Vector w = mapTo(line);
	    return w;
	}

	public HashMap load_history(String filename) {
		HashMap hmap = new HashMap();
		Vector v = readFile(filename);
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
            Vector u = parseData(line, '|');
            String condition = (String) u.elementAt(0);
            condition = condition.toLowerCase();
            String mapTo = (String) u.elementAt(1);
            mapTo = mapTo.toLowerCase();
            hmap.put(condition, mapTo);
		}
		return hmap;
	}

	public String search_history(String term) {
		term = term.toLowerCase();
		Vector words = tokenize_str(term);
		Iterator it = historyMap.keySet().iterator();
		while (it.hasNext()) {
			boolean bool = true;
			String condition = (String) it.next();
				Vector tokens = tokenize_str(condition, true);
				for (int i=0; i<tokens.size(); i++) {
					String token = (String) tokens.elementAt(i);
					if (!words.contains(token)) {
						bool = false;
						break;
					}
				}
				if (bool) {
					return (String) historyMap.get(condition);
				}
		}
		return null;
	}


	public Vector tokenize_str(String t) {
		boolean removeFillers = true;
		return tokenize_str(t, removeFillers);
	}

	public Vector tokenize_str(String t, boolean removeFillers) {
		if (t == null) return null;
		if (t.length() == 0) return new Vector();
		Vector w = tokenize_term(t);
		Vector v = new Vector();
		for (int i=0; i<w.size(); i++) {
			String word = (String) w.elementAt(i);
			if (!isFiller(word)) {
				v.add(word);
			}
		}
		v = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(v);
		return v;
	}

/*
	public Vector tokenize_str(String t, boolean removeFillers) {
		if (t == null) return null;
		t = t.toLowerCase();
		//t = t.replaceAll("[^a-zA-Z0-9]", " ");
		t = t.replaceAll("[^éa-zA-Z0-9]", " ");

		StringTokenizer st = new StringTokenizer(t);
		Vector words = new Vector();
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 token = removeTrailingDiscardedChar(token);
			 token = removeLeadingDiscardedChar(token);
			 if (removeFillers) {
				 if (!isFiller(token)) {
					 if (!words.contains(token)) {
						 words.add(token);
					 }
				 }
			 }
		}
		words = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(words);
		return words;
	}
*/

/*
	public Vector tokenize(String t, boolean removeFillers) {
		if (t == null) return null;
		t = t.toLowerCase();
		//t = t.replaceAll("[^a-zA-Z0-9]", " ");
		t = t.replaceAll("[^éa-zA-Z0-9]", " ");

		StringTokenizer st = new StringTokenizer(t);
		Vector words = new Vector();
		while (st.hasMoreTokens()) {
			 String token = st.nextToken();
			 token = removeTrailingDiscardedChar(token);
			 token = removeLeadingDiscardedChar(token);
			 if (removeFillers) {
				 if (!isFiller(token)) {
					 if (!words.contains(token)) {
						 words.add(token);
					 }
				 }
			 }
		}
		words = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(words);
		return words;
	}
*/

	public Vector tokenize(String t, boolean removeFillers) {
		return tokenize_str(t, removeFillers);
	}

	public void saveMappingToFile(String outputfile, gov.nih.nci.evs.mapping.bean.Mapping mapping) {
        //long ms = System.currentTimeMillis();
		PrintWriter pw = null;
		int lcv = 0;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();
			for (int i=0; i<entries.size(); i++) {
				gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
				pw.println(entry.getSourceCode() + "|" + entry.getSourceTerm() + "|" + entry.getTargetCode() + "|" + entry.getTargetLabel());
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

	public gov.nih.nci.evs.mapping.bean.Mapping run(String vbtfile) {
		Vector v = readFile(vbtfile);
		return run(v);
	}


    public Vector<gov.nih.nci.evs.mapping.bean.MappingEntry> mapTo(String sourceCode, String sourceTerm) {
		Vector<gov.nih.nci.evs.mapping.bean.MappingEntry> v = new Vector();
		String line2 = sourceTerm;
		line2 = line2.trim();
		Vector w = null;
		String term = null;

		Vector w1 = tokenize(line2, true);
		if (w1.size() > 0) {
			term = line2;
			w = mapTo(term);
			if (w == null || w.size() == 0) {
				w = mapTo(search_history(sourceTerm));
			}

			if (w == null || w.size() == 0) {
				term = substitute(sourceTerm);
				w = mapTo(term);
			}

			if (w == null || w.size() == 0) {
				int n = sourceTerm.indexOf(",");
				if (n != -1) {
					term = sourceTerm.substring(0, n);
					w = mapTo(term);
				}
			}

			if (w == null || w.size() == 0) {
				term = removeOpenBrackets(sourceTerm);
				if (term.compareToIgnoreCase(sourceTerm) != 0) w = mapTo(term);
			}

			if (w == null || w.size() == 0) {
				term = removeCloseBrackets(sourceTerm);
				if (term.compareToIgnoreCase(sourceTerm) != 0) w = mapTo(term);
			}

			if (w == null || w.size() == 0) {
				term = trim_unspecified_clause(sourceTerm);
				Vector w0 = tokenize(term, true);
				if (w0.size() > 0) {
					if (term.compareToIgnoreCase(sourceTerm) != 0) {
						w = mapTo(term);
					}
				}
			}
		}

		if (w != null && w.size()>0) {
			for (int j=0; j<w.size(); j++) {
				String t = (String) w.elementAt(j);
				Vector u = parseData(t, '|');
				String name = (String) u.elementAt(0);
				String code = (String) u.elementAt(1);
				name = getLabel(code);
				gov.nih.nci.evs.mapping.bean.MappingEntry me = new gov.nih.nci.evs.mapping.bean.MappingEntry(sourceCode, sourceTerm, code, name);
				v.add(me);
			}
		} else {
			gov.nih.nci.evs.mapping.bean.MappingEntry me = new gov.nih.nci.evs.mapping.bean.MappingEntry(sourceCode, sourceTerm, "", "");
			v.add(me);
		}
		return v;
	}

	public gov.nih.nci.evs.mapping.bean.Mapping run(Vector v) {
		if (v == null) return null;
		if (v.size() == 0) {
			return new gov.nih.nci.evs.mapping.bean.Mapping(0, 0, new ArrayList());
		}
		String s1 = (String) v.elementAt(0);
		if (s1.indexOf("|") != -1) {
			v = bar2TabDelimited(v);
		}
		List entries = new ArrayList();
		int match_knt = 0;

		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			if (line.endsWith("\n")) {
				line = line.substring(0, line.length()-1);
			}
			String sourceCode = "NA";
			String sourceTerm = line;
			Vector u0 = parseData(line, '\t');
			if (u0.size() == 2) {
				sourceCode = (String) u0.elementAt(0);
				sourceTerm = (String) u0.elementAt(1);
			}
			if (sourceTerm.endsWith("\r")) {
				sourceTerm = sourceTerm.substring(0, sourceTerm.length()-1);
			}
			Vector<gov.nih.nci.evs.mapping.bean.MappingEntry> mapping_entry_vec = mapTo(sourceCode, sourceTerm);
			if (mapping_entry_vec.size() > 0) {
				for (int k=0; k<mapping_entry_vec.size(); k++) {
					gov.nih.nci.evs.mapping.bean.MappingEntry me = (gov.nih.nci.evs.mapping.bean.MappingEntry) mapping_entry_vec.elementAt(k);
					entries.add(me);
				}
				match_knt = match_knt + mapping_entry_vec.size();
		    } else {
				gov.nih.nci.evs.mapping.bean.MappingEntry m = new gov.nih.nci.evs.mapping.bean.MappingEntry(sourceCode, sourceTerm, "", "");
				entries.add(m);
			}
		}

		gov.nih.nci.evs.mapping.bean.Mapping mapping = new gov.nih.nci.evs.mapping.bean.Mapping(v.size(), match_knt, entries);
		return mapping;
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

    public String removeOpenBrackets(String str) {
		int m = str.indexOf("(");
		if (m == -1) return str;
		int n = str.indexOf(")");
		if (n == -1) return str;
		String s1 = str.substring(0, m);
		s1 = s1.trim();
		String s2 = str.substring(n+1, str.length());
		s2 = s2.trim();
		String t = s1 + " " + s2;
		return t;
	}

    public String removeCloseBrackets(String str) {
		int m = str.indexOf("[");
		if (m == -1) return str;
		int n = str.indexOf("]");
		if (n == -1) return str;
		String s1 = str.substring(0, m);
		s1 = s1.trim();
		String s2 = str.substring(n+1, str.length());
		s2 = s2.trim();
		String t = s1 + " " + s2;
		return t;
	}



	public HashMap createVariantHashMap() {
		HashMap variantHashMap = new HashMap();
		int knt = 0;
		for (int i=0; i<synonyms.size(); i++) {
			String line = (String) synonyms.elementAt(i);
			Vector u = parseData(line, '|');
			for (int j=0; j<u.size(); j++) {
				String key = (String) u.elementAt(j);
				variantHashMap.put(key, line);
			}
		}

		for (int i=0; i<variants.size(); i++) {
			String line = (String) variants.elementAt(i);
			Vector u = parseData(line, '|');
			for (int j=0; j<u.size(); j++) {
				String key = (String) u.elementAt(j);
				variantHashMap.put(key, line);
			}
		}
		return variantHashMap;
	}

	public String trim_unspecified_clause(String line) {
		line = substitute(line);
		line = line.toLowerCase();
		Vector w = new Vector();
		Vector u =StringUtils.parseData(line, ',');
		if (u.size() == 1) {
			if (line.startsWith("unspecified")) {
				int n = line.indexOf("unspecified");
				return line.substring(n+"unspecified".length(), line.length()).trim();
			}
		}
		for (int i=0; i<u.size(); i++) {
			String segment = (String) u.elementAt(i);
			segment = segment.trim();
			int n = segment.indexOf("unspecified");
			if (n != -1) {
				segment = segment.substring(0, n);
			}
			w.add(segment);
		}
		return toDelimited(w, " ").trim();
	}

	public static String getCurrentWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public static String getToday() {
		return StringUtils.getToday();
	}


    public boolean set_output_files(String vbtfile) {
		System.out.println("vbtfile: " + vbtfile);
		int n = vbtfile.lastIndexOf(".");
		if (n == -1) {
			System.out.println("ERROR: invalid verbatim data file name (.txt).");
			return false;
		}

		System.out.println("n: " + n);
		vbtfile_nm = vbtfile.substring(0, n);
		System.out.println("vbtfile_nm: " + vbtfile_nm);
		int m = vbtfile_nm.indexOf("_");
		System.out.println("m: " + m);
		String prefix = vbtfile_nm.substring(0, m);
		m = prefix.lastIndexOf("\\");
		if (m == -1) {
			m = prefix.lastIndexOf("/");
		}
		System.out.println("prefix: " + prefix);
		source = prefix.substring(m+1, prefix.length());
		System.out.println("source: " + source);

		System.out.println("codingSchemeName: " + codingSchemeName);
		output_file_prefix = prefix + "_to_" + codingSchemeName;
		System.out.println("output_file_prefix: " + output_file_prefix);
		String date_str = getToday();
		System.out.println("date_str: " + date_str);

		textfile = output_file_prefix + "_" + date_str + ".txt";
		jsonfile = output_file_prefix + "_" + date_str + ".json";
		excelfile = output_file_prefix + "_" + date_str + ".xlsx";
		htmlfile = output_file_prefix + "_" + date_str + ".html";
		htmltblfile = output_file_prefix + "_table_" + date_str + ".html";

		System.out.println("textfile: " + textfile);
		System.out.println("jsonfile: " + jsonfile);
		System.out.println("excelfile: " + excelfile);
		System.out.println("htmlfile: " + htmlfile);
		System.out.println("htmltblfile: " + htmltblfile);

		//htmltblfile: E:\EVSFocus\MAPPING\DEV\MappingTool\ICD10_to_NCI_Thesaurus_table_12-19-2018.html

		return true;
	}

	public void generateMapping(String vbtfile) {
		boolean bool_val = set_output_files(vbtfile);
		if (!bool_val) return;

        Mapping mapping = run(vbtfile);
        saveMappingToFile(textfile, mapping);
        Vector w = new Vector();
        w.add(mapping.toJson());
        saveToFile(jsonfile, w);

        List<MappingEntry> entries = mapping.getEntries();
        try{
			ExcelWriter.write(entries, excelfile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    public Vector bar2TabDelimited(Vector v) {
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(line, '|');
			String s1 = (String) u.elementAt(0);
			String s2 = (String) u.elementAt(1);
			w.add(s1 + "\t" + s2);
		}
		return w;
	}

    public static Mapping loadMapping(Vector v) {
	    Mapping mapping = null;
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = new ArrayList<gov.nih.nci.evs.mapping.bean.MappingEntry>();
	    int knt = 0;
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
			String sourceCode = (String) u.elementAt(0);
			String sourceTerm = (String) u.elementAt(1);
			String targetCode = (String) u.elementAt(2);
			String targetLabel = (String) u.elementAt(3);
			gov.nih.nci.evs.mapping.bean.MappingEntry entry = new gov.nih.nci.evs.mapping.bean.MappingEntry(
					sourceCode, sourceTerm, targetCode, targetLabel);
				entries.add(entry);
			if (targetCode.length()>0) knt++;
		}
		mapping = new Mapping(v.size(), knt, entries);
		return mapping;
	}

	public static String get_mapping_source(String mapping_file) {
		int n = mapping_file.indexOf("_");
		return mapping_file.substring(0, n);
	}

	public static String get_mapping_target(String mapping_file) {
		int n = mapping_file.indexOf("_");
		int m = mapping_file.lastIndexOf("_");
		return mapping_file.substring(n+"_to_".length(), m);
	}

	public Vector getKeywords(HashSet keywordSet) {
		Vector w = new Vector();
		Iterator it = keywordSet.iterator();
		while (it.hasNext()) {
			String word = (String) it.next();
			w.add(word);
		}
		w = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
		return w;
	}


    public static void main(String[] args) {
		MappingUtils mappingUtils = new MappingUtils();
		String ncit = "NCI_Thesaurus.txt";
		Vector term_vec = mappingUtils.readFile(ncit);
		HashSet keywordSet = mappingUtils.create_keyword_set(term_vec);
		Vector keywords = mappingUtils.getKeywords(keywordSet);
		mappingUtils.saveToFile("test_keywords.txt", keywords);

	}


/*
    public static void main(String[] args) {
		long ms = System.currentTimeMillis();
        String serviceUrl = args[0];
        String data_directory = args[1];
        String vbtfile = args[2];
        String codingSchemeName = args[3];
        System.out.println("serviceUrl: " + serviceUrl);
        System.out.println("data_directory: " + data_directory);
        System.out.println("vbtfile: " + vbtfile);
        System.out.println("codingSchemeName: " + codingSchemeName);

        MappingUtils test = new MappingUtils(data_directory, codingSchemeName);
		test.generateMapping(vbtfile);
	}
*/

/*
    public static void main(String[] args) {
		long ms = System.currentTimeMillis();

		//String data_dir = MappingUtils.getCurrentWorkingDirectory();
        String serviceUrl = args[0];
        String data_directory = args[1];
        String vbtfile = args[2];
        System.out.println("serviceUrl: " + serviceUrl);
        System.out.println("data_directory: " + data_directory);
        System.out.println("vbtfile: " + vbtfile);

        DataManager dm = new DataManager(serviceUrl, data_directory);
        String codingSchemeName = "MEDDRA";
        Terminology terminology = dm.getTerminologyByCodingSchemeName(codingSchemeName);
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

		MappingUtils test = new MappingUtils(data_directory, terminology);
		test.generateMapping(vbtfile);
	}
*/

}
