package gov.nih.nci.evs.browser.properties;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.mapping.util.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.common.*;

import gov.nih.nci.evs.restapi.meta.util.*;
import gov.nih.nci.evs.restapi.meta.bean.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author NGIS, Kim L. Ong
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class NCImtProperties {

    public static String _data_directory = null;
    public static String _service_url = null;
    public static TTLQueryUtilsRunner runner = null;

    public static gov.nih.nci.evs.restapi.meta.util.VIHUtils ttl_vihUtils = null;
    public static Vector PARENT_CHILDREN_VEC = null;
    public static String PARENT_CHILD_FILE = "parent_child.txt";

    public static gov.nih.nci.evs.restapi.util.HierarchyHelper vs_hh = null;
    public static Vector embedded_value_set_hierarchy_vec = null;
    public static TTLQueryUtils ttlQueryUtils = null;
    public static HashMap hierarchyDataHashMap = null;
    public static HashMap namedGraph2codingSchemeNameHashMap = null;
    public static HashMap concept_status_hmap = null;

    public static String CONCEPT_STATUS = "Concept_Status";

    private NCImtProperties() {

    }

    static {
		long ms = System.currentTimeMillis();
        _data_directory = NCImtBrowserProperties._data_directory;
        _service_url = NCImtBrowserProperties._sparql_service_url;
        namedGraph2codingSchemeNameHashMap = new HashMap();
		gov.nih.nci.evs.restapi.util.MetadataUtils mdu = new gov.nih.nci.evs.restapi.util.MetadataUtils(_service_url);
		HashMap nameVersion2NamedGraphMap = mdu.getNameVersion2NamedGraphMap();
		Iterator it = nameVersion2NamedGraphMap.keySet().iterator();
		while (it.hasNext()) {
			String nameVersion = (String) it.next();
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(nameVersion);
			String codingSchemeName = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			Vector named_graphs = (Vector) nameVersion2NamedGraphMap.get(nameVersion);
			for (int i=0; i<named_graphs.size(); i++) {
				String named_graph = (String) named_graphs.elementAt(i);
				namedGraph2codingSchemeNameHashMap.put(named_graph, codingSchemeName);
			}
		}

        //runner = NCImtBrowserProperties.runner;
        ttl_vihUtils = new gov.nih.nci.evs.restapi.meta.util.VIHUtils(_service_url);

        runner = new TTLQueryUtilsRunner(_service_url);
        ttlQueryUtils = new TTLQueryUtils(_service_url);

        String pathname = _data_directory + File.separator + "parent_child.txt";
        File file = new File(pathname);
        if (file.exists()) {
        	PARENT_CHILDREN_VEC = gov.nih.nci.evs.restapi.util.Utils.readFile(pathname);
		} else {
			PARENT_CHILDREN_VEC = generate_parent_child_vec();
            saveToFile("parent_child.txt", PARENT_CHILDREN_VEC);
		}

        pathname = _data_directory + File.separator + "embedded_value_set_hierarchy.txt";
        file = new File(pathname);
        if (file.exists()) {
        	embedded_value_set_hierarchy_vec = removeRootNodes(gov.nih.nci.evs.restapi.util.Utils.readFile(pathname));
		} else {
			gov.nih.nci.evs.restapi.util.ValueSetUtils vsu = new gov.nih.nci.evs.restapi.util.ValueSetUtils(_service_url, get_default_named_graph());
			vsu.set_data_directory(_data_directory);
			vsu.initialize();
            embedded_value_set_hierarchy_vec = removeRootNodes(gov.nih.nci.evs.restapi.util.Utils.readFile(pathname));
		}
		vs_hh = new gov.nih.nci.evs.restapi.util.HierarchyHelper(embedded_value_set_hierarchy_vec);
		if (vs_hh != null) {
			vs_hh.findRootAndLeafNodes();
		}
		concept_status_hmap = getConceptsWithAnnotationProperty(get_default_named_graph(), CONCEPT_STATUS);
		System.out.println("NCImtProperties total run time (ms): " + (System.currentTimeMillis() - ms));
    }

    public static String getCodingSchemeName(String ng) {
		return (String) namedGraph2codingSchemeNameHashMap.get(ng);
	}

    public static Vector removeRootNodes(Vector v) {
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			if (t.indexOf("<Root>") == -1) {
				w.add(t);
			}
		}
		return w;
	}

	 public static void saveToFile(String outputfile, Vector v) {
		outputfile = _data_directory + File.separator + outputfile;
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


	public static HashMap getConceptsWithAnnotationProperty(String named_graph, String propertyName) {
		OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(NCImtBrowserProperties._sparql_service_url);
		Vector w = owlSPARQLUtils.getConceptsWithAnnotationProperty(named_graph, propertyName);
		w = new ParserUtils().getResponseValues(w);
		HashMap hmap = new HashMap();
		for (int i=0; i<w.size(); i++) {
			String t = (String) w.elementAt(i);
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t, '|');
			String code = (String) u.elementAt(1);
			String value = (String) u.elementAt(3);
			hmap.put(code, value);
		}
		return hmap;
	}


	public static Vector generate_parent_child_vec() {
		OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(NCImtBrowserProperties._sparql_service_url);
		Vector parent_child_vec = owlSPARQLUtils.getHierarchicalRelationships(get_default_named_graph());
		parent_child_vec = new ParserUtils().getResponseValues(parent_child_vec);
		parent_child_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(parent_child_vec);
		return parent_child_vec;
	}

	public static boolean checkIfFileExists(String filename) {
		File file = null;
		if (_data_directory != null) {
			file = new File(_data_directory + File.separator + filename);
		} else {
			file = new File(filename);
		}
		return file.exists();
	}


    public static String getPrefixes() {
		return NCImtBrowserProperties.getPrefixes();
	}

	public static String get_default_named_graph() {
		return "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";//; //DataUtils.default_named_graph;
	}

	public static String get_TERMINOLOGY() {
		return "NCI_Thesaurus";
	}

	public static String get_service_url() {
		return _service_url;
	}

	public static boolean isNCIt(String ng) {
		if (ng.compareTo(get_default_named_graph()) == 0) return true;
		return false;
	}

    public static boolean isLeaf(String named_graph, String code) {
		Vector v = null;
		if (isNCIt(named_graph)) {
			return new OWLSPARQLUtils(_service_url).isLeaf(named_graph, code);
		} else {
			return new TTLQueryUtils(_service_url).isLeaf(named_graph, code);
		}
	}

	public static boolean isMetaThesaurusSource(String named_graph) {
		return !isNCIt(named_graph);
	}

	public static Vector get_PARENT_CHILDREN() {
        return PARENT_CHILDREN_VEC;
	}

	public static gov.nih.nci.evs.restapi.meta.util.VIHUtils get_ttl_vihUtils() {
		return ttl_vihUtils;
	}

    public static TTLQueryUtilsRunner getTTLQueryUtilsRunner() {
		return runner;
	}

    public static TTLQueryUtils getTTLQueryUtils() {
		return ttlQueryUtils;
	}

	public static Vector get_parent_child_vec(String codingSchemeName, String named_graph) {
		String filename = codingSchemeName + "_" + PARENT_CHILD_FILE;
		if (isNCIt(named_graph)) {
			filename = PARENT_CHILD_FILE;
		}
		Vector parent_child_vec = new Vector();
		File f = null;
		String filePathString = _data_directory + File.separator + filename;
		f = new File(filePathString);
		if (isNCIt(named_graph)) {
			if(f.exists() && !f.isDirectory()) {
				System.out.println(filename + " exists.");
				parent_child_vec = gov.nih.nci.evs.restapi.util.Utils.readFile(filePathString);
			} else {
				System.out.println(filename + " does not exists -- generating ...");
				OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(_service_url);
				parent_child_vec = owlSPARQLUtils.getHierarchicalRelationships(named_graph);
				parent_child_vec = new ParserUtils().getResponseValues(parent_child_vec);
				parent_child_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(parent_child_vec);
				gov.nih.nci.evs.restapi.util.Utils.saveToFile(filePathString, parent_child_vec);
			}
		} else {
			if(f.exists() && !f.isDirectory()) {
				parent_child_vec = gov.nih.nci.evs.restapi.util.Utils.readFile(filePathString);
			} else {
				TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(_service_url);
				parent_child_vec = ttlQueryUtils.hierarchy_data_query(named_graph);
				gov.nih.nci.evs.restapi.util.Utils.saveToFile(filePathString, parent_child_vec);
			}
		}
		return parent_child_vec;
	}

	public static String get_concept_status(String code) {
		String value = (String) concept_status_hmap.get(code);
		return value;
	}

}