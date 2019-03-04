package test;

import gov.nih.nci.evs.mapping.util.*;
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


public class TestMappingUtils {

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
	private HashMap americanBritishSpellingHashMap = null;

	private String data_directory = null;
	private Terminology terminology = null;
	private String codingSchemeName = null;

	public String vbtfile_nm = null;
	public String source = null;

	public TestMappingUtils(String data_directory, String codingSchemeName) {
		this.data_directory = data_directory;
		this.codingSchemeName =codingSchemeName;
		initialize();
	}

	public void initialize() {
		long ms0 = System.currentTimeMillis();
		theStemmer = new PorterStemmer();
		this.discarded_phrases = readFile(data_directory + File.separator + Constants.DISCARDED_PHRASE_FILE);
		this.synonyms = readFile(data_directory + File.separator + Constants.SYNONYM_FILE);
		filler_set = loadFillers(data_directory + File.separator + Constants.FILLER_FILE);
		System.out.println("fillers: " + filler_set.size());
		keyword_set = new HashSet();
		variants = readFile(data_directory + File.separator + Constants.VARIANT_FILE);
		System.out.println("variants: " + variants.size());
		variantHashMap = createVariantHashMap();
		System.out.println("variantHashMap.keySet(): " + variantHashMap.keySet().size());
		term_vec = readFile(data_directory + File.separator + codingSchemeName + ".txt");
		System.out.println("terms: " + term_vec.size());
		key2ConceptMap = createKey2ConceptMap(term_vec);
		System.out.println("key2ConceptMap.keySet(): " + key2ConceptMap.keySet().size());
		System.out.println("\nTotal initialization run time (ms): " + (System.currentTimeMillis() - ms0));
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

	public Vector get_term_vec() {
		return term_vec;
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

	public boolean isFiller(String word) {
		return filler_set.contains(word);
	}

	public boolean isKeyword(String word) {
		return keyword_set.contains(word);
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
		t = t.trim();
		return t;
	}

	public String discardedLeadingChars(String str, String charsToDiscard) {
		if (str == null || str.length() == 0) return str;
		String s = "" + str.charAt(0);
		if (charsToDiscard.indexOf(s) != -1) {
			str = str.substring(1, str.length());
		}
		return str;
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

    public static String DISCARDED_CHARS = "| ,-(){}[]*&@\\\t/.:;!?_\"'";
	public boolean isDiscardedChar(char c) {
		if (DISCARDED_CHARS.indexOf(c) != -1) {
			return true;
		}
		return false;
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

	public void dumpVector(String label, Vector v) {
		System.out.println(label);
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			System.out.println("\t" + t);
		}
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

	public Vector getMatches(String vbt) {
		String vbt_key = vbt2Key(vbt, true);
		System.out.println(vbt + " --> " + vbt_key);
		Vector w = null;
		if (!key2ConceptMap.containsKey(vbt_key)) {
			System.out.println("No match.");
		} else {
			w = (Vector) key2ConceptMap.get(vbt_key);
			//dumpVector(vbt_key, w);
		}
		return w;
	}

    public void dumpKey2ConceptMap() {
		Iterator it = key2ConceptMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println("\n" + key);
			Vector v = (Vector) key2ConceptMap.get(key);
			for (int i=0; i<v.size(); i++) {
				String code = (String) v.elementAt(i);
				String label = (String) code2LabelMap.get(code);
				System.out.println("\t" + label + " (" + code + ")");
			}
		}
	}


    public static void main(String[] args) {
		String data_directory = args[0];
		TestMappingUtils test = new TestMappingUtils(data_directory, "NCI_Thesaurus");

		test.dumpKey2ConceptMap();
		System.out.println("==================================================================================");

		String test_str = "Eosinophil Lysophospholipas";
		String vbt_key = test.vbt2Key(test_str, true);
		//System.out.println(test_str + " --> " + vbt_key);
		test_str = "CDISC ADAS-Cog - Naming Objects and Fingers 12";
		vbt_key = test.toKey(test_str);
		System.out.println(test_str + " --> " + vbt_key);

		Vector w = test.getMatches(test_str);
		if (w != null) {
			test.dumpVector(test_str, w);
		}

		test_str = "CDISC ADAS-Cog - Naming Object and Finger 12";
		vbt_key = test.vbt2Key(test_str, true);
		System.out.println(test_str + " --> " + vbt_key);
		w = test.getMatches(test_str);
		if (w != null) {
			test.dumpVector(test_str, w);
		}

		test_str = "Anti-CD37 Antibody-Drug Conjug IMGN529";
		vbt_key = test.vbt2Key(test_str, true);
		System.out.println(test_str + " --> " + vbt_key);
		w = test.getMatches(test_str);
		if (w != null) {
			test.dumpVector(test_str, w);
		}

		test_str = "Eosinophil Lysophospholipase";
		vbt_key = test.vbt2Key(test_str, true);
		System.out.println(test_str + " --> " + vbt_key);
		w = test.getMatches(test_str);
		if (w != null) {
			test.dumpVector(test_str, w);
		}
	}
}
