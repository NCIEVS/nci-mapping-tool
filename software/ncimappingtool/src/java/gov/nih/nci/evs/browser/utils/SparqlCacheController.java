package gov.nih.nci.evs.browser.utils;

//import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.restapi.meta.util.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;

import java.util.*;
import net.sf.ehcache.*;
import org.json.*;
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
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class SparqlCacheController {
    private static Logger _logger = Logger.getLogger(SparqlCacheController.class);
    public static final String ONTOLOGY_ADMINISTRATORS = "ontology_administrators";
    public static final String ONTOLOGY_FILE = "ontology_file";
    public static final String ONTOLOGY_FILE_ID = "ontology_file_id";
    public static final String ONTOLOGY_DISPLAY_NAME = "ontology_display_name";
    public static final String ONTOLOGY_NODE = "ontology_node";
    public static final String ONTOLOGY_NODE_ID = "ontology_node_id";
    public static final String ONTOLOGY_NODE_SCHEME = "ontology_node_scheme";
    public static final String ONTOLOGY_SOURCE = "ontology_source";
    public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
    public static final String ONTOLOGY_NODE_PARENT_ASSOC = "ontology_node_parent_assoc";
    public static final String ONTOLOGY_NODE_CHILD_COUNT = "ontology_node_child_count";
    public static final String ONTOLOGY_NODE_DEFINITION = "ontology_node_definition";
    public static final String CHILDREN_NODES = "children_nodes";

    public static final String ONTOLOGY_NODE_NS = "ontology_node_ns";

    private static SparqlCacheController _instance = null;
    private static CacheManager _cacheManager = null;
    private static Cache _cache = null;

    private SPARQLUtils utils = null;

    static {
		String cacheName = "treeCache";
		_cacheManager = getCacheManager();
        if (!_cacheManager.cacheExists(cacheName)) {
            _cacheManager.addCache(cacheName);
			_logger.debug("cache added");
        }
        _cache = _cacheManager.getCache(cacheName);
    }


    public SparqlCacheController(String cacheName) {
        if (!_cacheManager.cacheExists(cacheName)) {
            _cacheManager.addCache(cacheName);
        }

        _logger.debug("cache added");
        _cache = _cacheManager.getCache(cacheName);
    }


    public static SparqlCacheController getInstance() {
        synchronized (SparqlCacheController.class) {
            if (_instance == null) {
                _instance = new SparqlCacheController("treeCache");
            }
        }
        return _instance;
    }

    private static CacheManager getCacheManager() {
        if (_cacheManager != null)
            return _cacheManager;
        try {
            String ehcache_xml_pathname = NCImtBrowserProperties.get_EHCACHE_XML_PATHNAME();
            _cacheManager = new CacheManager(ehcache_xml_pathname);
            return _cacheManager;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String[] getCacheNames() {
        return getCacheManager().getCacheNames();
    }

    public void clear() {
        _cache.removeAll();
    }

    public boolean containsKey(Object key) {
        return _cache.isKeyInCache(key);
    }

    public boolean containsValue(Object value) {
        return _cache.isValueInCache(value);
    }

    public boolean isEmpty() {
        return _cache.getSize() > 0;
    }

    public JSONArray getSubconcepts(String scheme, String version, String code) {
        return getSubconcepts(scheme, version, code, null, true);
    }

    public JSONArray getSubconcepts(String scheme, String version, String code, String ns) {
        return getSubconcepts(scheme, version, code, ns, true);
    }


    public String getRemainingSubconceptJSONString(String codingScheme, String version, String parent_code, String parent_ns, String focus_code) {
        TreeItem root = new TreeItem("Root", "<Root>");

		long ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);
		//json = "{\"nodes\":" + json + "}";
		return json;
	}


    public JSONArray getRootConcepts(String scheme, String version) {
        return getRootConcepts(scheme, version, true);
    }

    public JSONArray getRootConcepts(String scheme, String version, boolean fromCache) {
		JSONArray nodeArray = null;
        return nodeArray;
    }


    private int findFocusNodePosition(String node_id, List<gov.nih.nci.evs.restapi.bean.TreeItem> children) {
        for (int i = 0; i < children.size(); i++) {
            gov.nih.nci.evs.restapi.bean.TreeItem childItem = (gov.nih.nci.evs.restapi.bean.TreeItem) children.get(i);
            if (node_id.compareTo(childItem._code) == 0)
                return i;
        }
        return -1;
    }

    private JSONArray toJSONArray(gov.nih.nci.evs.restapi.bean.TreeItem ti) {
        JSONArray nodesArray = new JSONArray();
        for (String association : ti._assocToChildMap.keySet()) {
            List<gov.nih.nci.evs.restapi.bean.TreeItem> children = ti._assocToChildMap.get(association);
			for (int i = 0; i < children.size(); i++) {
				gov.nih.nci.evs.restapi.bean.TreeItem childItem = (gov.nih.nci.evs.restapi.bean.TreeItem) children.get(i);
				int knt = 0;
				if (childItem._expandable) {
					knt = 1;
				}
				JSONObject nodeObject = new JSONObject();
				try {
					nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
					//nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
					nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
					nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
					nodeObject.put(CHILDREN_NODES, toJSONArray(childItem));
					nodesArray.put(nodeObject);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return nodesArray;
	}

/*

    public String treeItem2Json(gov.nih.nci.evs.restapi.bean.TreeItem ti) {
		StringBuffer buf = new StringBuffer();
		for (String association : ti._assocToChildMap.keySet()) {
			List<gov.nih.nci.evs.restapi.bean.TreeItem> children = ti._assocToChildMap.get(association);
			new SortUtils().quickSort(children);
			for (int i=0; i<children.size(); i++) {
				gov.nih.nci.evs.restapi.bean.TreeItem childItem = (gov.nih.nci.evs.restapi.bean.TreeItem) children.get(i);
				String s = getTreeItemInJson(childItem);
				buf.append(s);
			}
		}
		buf.append("]");
		String t = buf.toString();
		t = replace(t, "}[{", "},{");
		return t;
	}

    public String getTreeItemInJson(gov.nih.nci.evs.restapi.bean.TreeItem ti) {
        StringBuffer buf = new StringBuffer();
	    String _code = ti._code;
	    String _text = ti._text;
	    String _ns = ti._ns;
		if (hasChildren(ti)) {
            buf.append("[{\"children_nodes\":");
	        for (String association : ti._assocToChildMap.keySet()) {
		        List<gov.nih.nci.evs.restapi.bean.TreeItem> children = ti._assocToChildMap.get(association);
		        new SortUtils().quickSort(children);
		        for (int i=0; i<children.size(); i++) {
		            gov.nih.nci.evs.restapi.bean.TreeItem childItem = (gov.nih.nci.evs.restapi.bean.TreeItem) children.get(i);
		            String s = getTreeItemInJson(childItem);
		            buf.append(s);
	            }
			}
			buf.append("],");
	    }
	    else {
        	buf.append("[{\"children_nodes\":[],");
	    }
        if (_text.compareTo("...") == 0) {
			buf.append("\"page\":1,");
		}
        if (ti._expandable) {
            buf.append("\"ontology_node_child_count\":1,");
        } else {
            buf.append("\"ontology_node_child_count\":0,");
        }
        buf.append("\"ontology_node_id\":\"" + _code + "\",");
        if (_text.compareTo("...") != 0) {
        	buf.append("\"ontology_node_ns\":\"" + _ns + "\",");
	    }
        buf.append("\"ontology_node_name\":\"" + _text + "\"}");
        String retstr = buf.toString();
        return retstr;
    }
*/
    public String getRootJSONString(String named_graph, String version) {
		if (named_graph == null) {
			named_graph = NCImtProperties.get_default_named_graph();
		}

		String codingScheme = named_graph;
		String ns = codingScheme;
		String key = codingScheme + "$" + version + "$root";
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		gov.nih.nci.evs.restapi.bean.TreeItem root = new gov.nih.nci.evs.restapi.bean.TreeItem("Root", "<Root>");
        Vector label_code_vec = new Vector();
        Vector w = new Vector();
        if (NCImtProperties.isNCIt(named_graph)) {
			codingScheme = NCImtProperties.get_TERMINOLOGY();
			version = null;
			String serviceUrl = NCImtProperties.get_service_url();
			OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(serviceUrl, null, null);
			label_code_vec = owlSPARQLUtils.get_roots(named_graph);
	    } else {
            if (NCImtProperties.isMetaThesaurusSource(named_graph)) {
                TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(NCImtProperties.get_service_url());
                label_code_vec = ttlQueryUtils.root_query(named_graph);
			}
		}

label_code_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(label_code_vec);
for (int j=0; j<label_code_vec.size(); j++) {
	String label_code = (String) label_code_vec.elementAt(j);
	Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(label_code);
	String c_label = (String) u.elementAt(0);
	String c_code = (String) u.elementAt(1);
	ResolvedConceptReference rcr = new ResolvedConceptReference(codingScheme, version, c_code, ns, c_label);
	w.add(rcr);
}

		w = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
        for (int i=0; i<w.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
			String cs_name = rcr.getCodingScheme();
			String cs_version = rcr.getVersion();
			String cs_ns = rcr.getNamespace();
			String code = rcr.getCode();
			String name = rcr.getName();
			boolean is_expandable = true;
			gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name);
			child._expandable = is_expandable;
			root.addChild("has_child", child);
		}

            System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		String json = null;
		try {
		    json = gov.nih.nci.evs.restapi.util.JSON2TreeItem.treeItem2Json(root);
            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}

    public JSONArray getSubconcepts(String scheme, String version, String code, String ns, boolean fromCache) {
		JSONArray nodeArray = null;
        return nodeArray;
    }

/*
    public Vector getSubclassesByCode(String parent_code) {
		Vector u = new Vector();
		String target = "|" + parent_code + "|";
		Vector w = NCImtProperties.get_PARENT_CHILDREN();
		for (int i=0; i<w.size(); i++) {
			String t = (String) w.elementAt(i);
			if (t.indexOf(target) != -1) {
				Vector v = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
				String child_name = (String) v.elementAt(2);
				String child_code = (String) v.elementAt(3);
				u.add(child_name + "|" + child_code);
			} else {

			}
		}
		return u;
	}
*/

    public Vector getSubclassesByCode(String named_graph, String code) {
		Vector v = null;
		try {
			v = new OWLSPARQLUtils(NCImtProperties.get_service_url()).getSubclassesByCode(named_graph, code);
			if (v == null) {
				return null;
			}
			v = new ParserUtils().getResponseValues(v);
			v = new SortUtils().quickSort(v);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return v;
	}


    public String getSubconceptJSONString(String named_graph, String focus_code) {
		String key = "subclasses_of_$" + focus_code;
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		gov.nih.nci.evs.restapi.bean.TreeItem root = new gov.nih.nci.evs.restapi.bean.TreeItem("Root", "<Root>");
		root._expandable = false;
		Vector w = null;
		if (named_graph == null) {
			named_graph = NCImtProperties.get_default_named_graph();
		}

        if (NCImtProperties.isNCIt(named_graph)) {
			try {
				w = getSubclassesByCode(named_graph, focus_code);
				if (w != null) {
					for (int i=0; i<w.size(); i++) {
						String t = (String) w.elementAt(i);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						String cs_name = NCImtProperties.get_TERMINOLOGY();
						String cs_version = null;
						String cs_ns = cs_name;
						String name = (String) u.elementAt(0);
						String code = (String) u.elementAt(1);
						gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name);
						boolean isLeaf = NCImtProperties.isLeaf(named_graph, code);
						child._expandable = !isLeaf;
						root.addChild("has_child", child);
						root._expandable = true;
					}
			    }
			} catch (Exception ex) {
				return null;
			}
		} else {
			try {
				TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(NCImtProperties.get_service_url());
				w = ttlQueryUtils.subclass_query(named_graph, focus_code);
				if (w != null) {
					for (int i=0; i<w.size(); i++) {
						String t = (String) w.elementAt(i);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						String cs_name = named_graph;
						String cs_version = null;
						String cs_ns = cs_name;
						String name = (String) u.elementAt(2);
						String code = (String) u.elementAt(3);
						//gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name, cs_ns, null);
						gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name);

						Vector w2 = ttlQueryUtils.subclass_query(named_graph, code);
						boolean isLeaf = false;
						if (w2 != null && w2.size() > 0) {
							 isLeaf = true;
						}
						child._expandable = !isLeaf;
						root.addChild("has_child", child);
						root._expandable = true;
					}
			    }
			} catch (Exception ex) {
				return null;
			}
		}

		ms = System.currentTimeMillis();
		String json = gov.nih.nci.evs.restapi.util.JSON2TreeItem.treeItem2Json(root);
            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}


	public String getViewInHierarchyJSONString(String named_graph, String focus_code) {
		String json = null;

		String key = "vih_of_$" + focus_code;
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}
		gov.nih.nci.evs.restapi.bean.TreeItem root = null;
		if (named_graph == null) {
			named_graph = NCImtProperties.get_default_named_graph();
		}

System.out.println("getViewInHierarchyJSONString named_graph: " + named_graph);
System.out.println("getViewInHierarchyJSONString focus_code: " + focus_code);


		long ms = System.currentTimeMillis();
		if (NCImtProperties.isNCIt(named_graph)) {

System.out.println("getViewInHierarchyJSONString isNCIt: ");
			gov.nih.nci.evs.restapi.util.VIHUtils vih_util = new gov.nih.nci.evs.restapi.util.VIHUtils(NCImtProperties.get_PARENT_CHILDREN());
			try {
				root = vih_util.buildViewInHierarchyTree(focus_code);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			//System.out.println("getViewInHierarchyJSONString is NOT NCIt: ");
			gov.nih.nci.evs.restapi.meta.util.VIHUtils vihUtils = NCImtProperties.get_ttl_vihUtils();//new gov.nih.nci.evs.restapi.meta.util.VIHUtils(NCImtProperties.get_SPARQL_SERVICE());
			root = vihUtils.buildViewInHierarchyTree(named_graph, focus_code);
		}

            System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		json = gov.nih.nci.evs.restapi.util.JSON2TreeItem.treeItem2Json(root);
            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;

	}

/*
    public String getPartonomyRootJSONString(String codingScheme, String version) {
		String key = codingScheme + "$" + version + "$partonomy_root";
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		TreeItem root = new TreeItem("Root", "<Root>");

        String roots = NCImtProperties.get_PARTONOMY_ROOT_STRING();
        Vector v = gov.nih.nci.evs.restapi.util.StringUtils.parseData(roots);

        Vector w = new Vector();
        codingScheme = NCImtProperties.get_TERMINOLOGY();
        String ns = codingScheme;
        version = null;
        SPARQLTDBUtils TDBUtils = new SPARQLTDBUtils(NCImtProperties.get_SPARQL_SERVICE());
		if (NCImtProperties.useTDB()) {
			try {
				for (int i=0; i<v.size(); i++) {
					String code = (String) v.elementAt(i);
					String name = TDBUtils.getEntityDescriptionByCode(code);
					ResolvedConceptReference rcr = new ResolvedConceptReference(codingScheme, version, code, ns, name);
					w.add(rcr);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			SPARQLUtils utils = new SPARQLUtils(NCImtProperties.get_DB_NAME(),
												NCImtProperties.get_CLASSNAME(),
												NCImtProperties.get_DB_URL(),
												NCImtProperties.get_DB_USER(),
												NCImtProperties.get_DB_PASSWORD());
			for (int i=0; i<v.size(); i++) {
				String code = (String) v.elementAt(i);
				String name = utils.getEntityDescriptionByCode(code);
				ResolvedConceptReference rcr = new ResolvedConceptReference(codingScheme, version, code, ns, name);
				w.add(rcr);
			}
		}

		w = SortUtils.quickSort(w);
        for (int i=0; i<w.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
			String cs_name = rcr.getCodingScheme();
			String cs_version = rcr.getVersion();
			String cs_ns = rcr.getNamespace();
			String code = rcr.getCode();
			String name = rcr.getName();


			gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name, cs_ns, null);
			child._expandable = true;
			root.addChild("has_child", child);
			root._expandable = true;
		}

            System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);

            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		//String json = "{\"root_nodes\":" + json + "}";

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json;
	}
*/

    public String getValueSetRootJSONString(String codingScheme, String version) {
		String key = codingScheme + "$" + version + "$valueset_root";
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		gov.nih.nci.evs.restapi.bean.TreeItem root = new gov.nih.nci.evs.restapi.bean.TreeItem("Root", "<Root>");
        Vector w = new Vector();
        codingScheme = NCImtProperties.get_TERMINOLOGY();
        String ns = codingScheme;
        version = null;
		try {
			Vector v = NCImtProperties.vs_hh.getRoots();
			for (int i=0; i<v.size(); i++) {
				String code = (String) v.elementAt(i);
				String name = NCImtProperties.vs_hh.getLabel(code);
				ResolvedConceptReference rcr = new ResolvedConceptReference(codingScheme, version, code, ns, name);
				w.add(rcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		w = SortUtils.quickSort(w);
        for (int i=0; i<w.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
			String cs_name = rcr.getCodingScheme();
			String cs_version = rcr.getVersion();
			String cs_ns = rcr.getNamespace();
			String code = rcr.getCode();
			String name = rcr.getName();
			gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name);
			child._expandable = true;
			root.addChild("has_child", child);
			root._expandable = true;
		}

        System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		String json = gov.nih.nci.evs.restapi.util.JSON2TreeItem.treeItem2Json(root);

            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json;
	}
/*


    public String getPartonomySubconceptJSONString(String focus_code) {
		String key = "partonomy_subclasses_of_$" + focus_code;
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}
		long ms = System.currentTimeMillis();
		gov.nih.nci.evs.restapi.bean.TreeItem root = new gov.nih.nci.evs.restapi.bean.TreeItem("Root", "<Root>");
		root._expandable = false;
		Vector w = null;
		String role_name = NCImtProperties.get_PARTONOMY_ROLE_NAME();
		if (NCImtProperties.useTDB()) {
			//SPARQLTDBUtils TDBUtils = NCImtProperties.get_sPARQLTDBUtils();
			SPARQLTDBUtils TDBUtils = new SPARQLTDBUtils(NCImtProperties.get_SPARQL_SERVICE());

			try {

System.out.println("Calling SparqlCacheController getTransitiveClosure ..." + focus_code);

				w = TDBUtils.getTransitiveClosure(role_name, focus_code, false);

System.out.println("Calling SparqlCacheController getTransitiveClosure .w.size().." + w.size());


				if (w != null) {
					System.out.println("partonomy number of child nodes: " + w.size());
					for (int i=0; i<w.size(); i++) {
						String t = (String) w.elementAt(i);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						String cs_name = NCImtProperties.get_TERMINOLOGY();
						String cs_version = null;
						String cs_ns = cs_name;
						String name = (String) u.elementAt(0);
						String code = (String) u.elementAt(1);
						gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name, cs_ns, null);

						boolean isLeaf = false;
						try {
							isLeaf = NCImtProperties.isPartonomyLeaf(code);
						} catch (Exception ex) {
							System.out.println("WARNING: NCImtProperties.isPartonomyLeaf throws exception.");
						}
						child._expandable = !isLeaf;
						root.addChild("has_child", child);
						root._expandable = true;
					}
			    }
			} catch (Exception ex) {
				//ex.printStackTrace();
				return null;
			}
		} else {

			SPARQLUtils utils = new SPARQLUtils(NCImtProperties.get_DB_NAME(),
												NCImtProperties.get_CLASSNAME(),
												NCImtProperties.get_DB_URL(),
												NCImtProperties.get_DB_USER(),
												NCImtProperties.get_DB_PASSWORD());
			w = utils.getTransitiveClosure(role_name, focus_code, false);
			for (int i=0; i<w.size(); i++) {
				String t = (String) w.elementAt(i);
				Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
				String cs_name = NCImtProperties.get_TERMINOLOGY();
				String cs_version = null;
				String cs_ns = cs_name;
				String code = (String) u.elementAt(1);
				String name = (String) u.elementAt(0);
				// to be modified
				boolean is_expandable = true;
				gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name, cs_ns, null);
				boolean isLeaf = NCImtProperties.isPartonomyLeaf(code);
				child._expandable = !isLeaf;
				root.addChild("has_child", child);
			}

		}
		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);
            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}
*/

    public String getValueSetSubconceptJSONString(String focus_code) {
		String key = "partonomy_subclasses_of_$" + focus_code;
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}
		long ms = System.currentTimeMillis();
		gov.nih.nci.evs.restapi.bean.TreeItem root = new gov.nih.nci.evs.restapi.bean.TreeItem("Root", "<Root>");
		root._expandable = false;

		Vector leaf_nodes = NCImtProperties.vs_hh.getLeaves();
		Vector w = null;

			try {
                w = NCImtProperties.vs_hh.getSubclassCodes(focus_code);
				if (w != null) {
					for (int i=0; i<w.size(); i++) {
						String code = (String) w.elementAt(i);
						String cs_name = NCImtProperties.get_TERMINOLOGY();
						String cs_version = null;
						String cs_ns = cs_name;
						String name = NCImtProperties.vs_hh.getLabel(code);
						gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name);

						boolean isLeaf = leaf_nodes.contains(code);
						child._expandable = !isLeaf;
						root.addChild("has_child", child);
						root._expandable = true;
					}
			    }
			} catch (Exception ex) {
				//ex.printStackTrace();
				return null;
			}

		ms = System.currentTimeMillis();
		String json = gov.nih.nci.evs.restapi.util.JSON2TreeItem.treeItem2Json(root);
            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}

/*
    public String getValueSetRootJSONString(String codingScheme, String version, String focus_code) {
		String key = codingScheme + "$" + version + "$" + focus_code + "$valueset_root";
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		TreeItem root = new TreeItem("Root", "<Root>");

        Vector w = new Vector();
        codingScheme = NCImtProperties.get_TERMINOLOGY();
        String ns = codingScheme;
        version = null;
        Vector leaves = NCImtProperties.vs_hh.getLeaves();
		try {
			String name = NCImtProperties.vs_hh.getLabel(focus_code);
			ResolvedConceptReference rcr = new ResolvedConceptReference(codingScheme, version, focus_code, ns, name);
			w.add(rcr);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		w = SortUtils.quickSort(w);
        for (int i=0; i<w.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
			String cs_name = rcr.getCodingScheme();
			String cs_version = rcr.getVersion();
			String cs_ns = rcr.getNamespace();
			String code = rcr.getCode();
			String name = rcr.getName();
			gov.nih.nci.evs.restapi.bean.TreeItem child = new gov.nih.nci.evs.restapi.bean.TreeItem(code, name, cs_ns, null);
			child._expandable = true;
			if (leaves.contains(code)) {
				child._expandable = false;
			}

			root.addChild("has_child", child);
			root._expandable = true;
		}

        System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);

            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json;
	}
	*/
}

