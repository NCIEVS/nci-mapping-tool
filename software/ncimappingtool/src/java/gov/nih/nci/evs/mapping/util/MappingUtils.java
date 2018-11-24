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

	public MappingUtils() {
        initialize();
        //filler_set = this.mh.get_filler_set();
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

	public void initialize() {
		long ms0 = System.currentTimeMillis();
		theStemmer = new PorterStemmer();
		historyMap = load_history(Constants.HISTORY_FILE);
		this.discarded_phrases = readFile(Constants.DISCARDED_PHRASE_FILE);
		this.synonyms = readFile(Constants.SYNONYM_FILE);
		filler_set = loadFillers(Constants.FILLER_FILE);
		keyword_set = new HashSet();
		//americanBritishSpellingHashMap = createAmericanBritishSpellingHashmap(Constants.AMERICAN_BRITISH_SPELLING_FILE);
		variants = readFile(Constants.VARIANT_FILE);
		variantHashMap = createVariantHashMap();
		term_vec = readFile(Constants.TERMINOLOGY_FILE);
		key2ConceptMap = createKey2ConceptMap(term_vec);
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms0));
	}

	public Vector get_term_vec() {
		return this.term_vec;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    public String vbt2Key(String t) {
		return vbt2Key(t, true);
	}

	public Vector tokenize(String t) {
		if (t == null) return null;
		t = t.toLowerCase();
		t = t.replaceAll("[^a-zA-Z0-9]", " ");
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
		words = new SortUtils().quickSort(words);
		return words;
	}

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


	public String words2Key(Vector words, boolean stemming) {
		if (words == null) return null;
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
		Vector words = tokenize(t);
		String s = words2Key(words, stemming);
		return s;
    }


    public String toKey(String t) {
        if (t == null) return null;
		Vector words = tokenize(t);
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
			code2LabelMap.put(code, pt);
		}
		return code2LabelMap;
	}


    public HashSet create_keyword_set(Vector term_vec) {
        if (term_vec == null) return null;
        HashSet keyword_set = new HashSet();
		for (int i=0; i<term_vec.size(); i++) {
			String line = (String) term_vec.elementAt(i);
			Vector u = parseData(line, '|');
			for (int k=1; k<=2; k++) {
				String t = (String) u.elementAt(k);

				t = t.toLowerCase();
				if (t.endsWith(",")) {
					t = t.substring(0, t.length()-1);
				}

				t = t.replaceAll("_", " ");
				StringTokenizer st = new StringTokenizer(t);
				Vector words = new Vector();
				while (st.hasMoreTokens()) {
					 String token = st.nextToken();
					 token = removeTrailingDiscardedChar(token);
					 keyword_set.add(token);
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
		t = t.toLowerCase();
		t = t.replaceAll("[^a-zA-Z0-9]", " ");

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
		words = new SortUtils().quickSort(words);
		return words;
	}


	public Vector tokenize(String t, boolean removeFillers) {
		if (t == null) return null;
		t = t.toLowerCase();
		t = t.replaceAll("[^a-zA-Z0-9]", " ");

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
		words = new SortUtils().quickSort(words);
		return words;
	}

	public gov.nih.nci.evs.mapping.bean.Mapping run(String vbtfile) {
		Vector v = readFile(vbtfile);
		List entries = new ArrayList();
		int match_knt = 0;
		String outputfile = "mapping_" + vbtfile;
        long ms = System.currentTimeMillis();
		PrintWriter pw = null;
		int lcv = 0;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			for (int i=0; i<v.size(); i++) {
				String line = (String) v.elementAt(i);
     			String sourceCode = "NA";
				String sourceTerm = line;
				Vector u0 = parseData(line, '\t');
				if (u0.size() == 2) {
				    sourceCode = (String) u0.elementAt(0);
				    sourceTerm = (String) u0.elementAt(1);
				}

				lcv++;
				String line2 = sourceTerm;
				line2 = line2.trim();
				Vector w = mapTo(line2);

				if (w == null || w.size() == 0) {
					w = mapTo(search_history(sourceTerm));
				}

				if (w == null || w.size() == 0) {
					String term = substitute(sourceTerm);
					w = mapTo(term);
				}

				if (w == null || w.size() == 0) {
					int n = sourceTerm.indexOf(",");
					if (n != -1) {
						String term = sourceTerm.substring(0, n);
						w = mapTo(term);
					}
				}

				if (w == null || w.size() == 0) {
					String term = removeOpenBrackets(sourceTerm);
					if (term.compareToIgnoreCase(sourceTerm) != 0) w = mapTo(term);
				}

				if (w == null || w.size() == 0) {
					String term = removeCloseBrackets(sourceTerm);
					if (term.compareToIgnoreCase(sourceTerm) != 0) w = mapTo(term);
				}

				if (w == null || w.size() == 0) {
					String term = trim_unspecified_clause(sourceTerm);
					if (term.compareToIgnoreCase(sourceTerm) != 0) w = mapTo(term);
				}


				if (w != null && w.size()>0) {

					match_knt++;
					for (int j=0; j<w.size(); j++) {
						String t = (String) w.elementAt(j);
						Vector u = parseData(t, '|');
						String code = (String) u.elementAt(1);
						String name = (String) u.elementAt(0);
						gov.nih.nci.evs.mapping.bean.MappingEntry m = new gov.nih.nci.evs.mapping.bean.MappingEntry(sourceCode, sourceTerm, code, name);
						entries.add(m);
						pw.println(sourceCode + "|" + sourceTerm + "|" + code+ "|" + name);
					}
				} else {
					pw.println(sourceCode + "|" + sourceTerm + "|No match|NA");
				}
				if (i/1000*1000 == i) {
					System.out.println("Processing " + i + " of " + v.size() + " -- " + line);
				}
			}
			System.out.println("Done. Number of matches: " + match_knt + " (out of " + v.size() + ")");

		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		gov.nih.nci.evs.mapping.bean.Mapping mapping = new gov.nih.nci.evs.mapping.bean.Mapping(v.size(), match_knt, entries);

		System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
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

	public void generateMapping(String vbtfile) {
        String outputfile = "mapping_" + vbtfile;
        int n = vbtfile.lastIndexOf(".");
        String jsonfile = vbtfile.substring(0, n) + ".json";
        String excelfile = vbtfile.substring(0, n) + ".xlsx";
        Mapping mapping = run(vbtfile);

        Vector w = new Vector();
        w.add(mapping.toJson());
        saveToFile(jsonfile, w);

        List<MappingEntry> entries = loadMappingEntries(outputfile);
        try{
			ExcelWriter.write(entries, excelfile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

    public static String toTabDelimited(Vector words, String tab) {
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<words.size(); k++) {
			String word = (String) words.elementAt(k);
			buf.append(word).append(tab);
		}
		String t = buf.toString();
		if (t.endsWith("|")) {
			t = t.substring(0, t.length()-1);
		}
		return t;
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
		return toTabDelimited(w, " ").trim();
	}


    public static void main(String[] args) {
		long ms = System.currentTimeMillis();
		String vbtfile = args[0];

		MappingUtils test = new MappingUtils();
		test.generateMapping(vbtfile);
	}

}
