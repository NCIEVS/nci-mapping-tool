/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.restapi.util.*;

import gov.nih.nci.evs.mapping.util.*;
import gov.nih.nci.evs.mapping.bean.*;

import gov.nih.nci.evs.browser.utils.*;


import java.util.*;
import java.net.URI;
import java.io.*;

import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.*;
import javax.faces.event.*;
import javax.faces.model.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import javax.faces.event.ValueChangeEvent;
import java.util.*;

import gov.nih.nci.evs.mapping.util.*;
import gov.nih.nci.evs.mapping.bean.*;
import gov.nih.nci.evs.mapping.common.*;

import javax.servlet.ServletOutputStream;

/**
 *
 */

public class MappingSessionBean {
    private static Logger _logger = Logger.getLogger(MappingSessionBean.class);
    private static List _rela_list = null;
    private static List _association_name_list = null;
    private static List _property_name_list = null;
    private static List _property_type_list = null;
    private static List _source_list = null;

    private static Vector _value_set_uri_vec = null;
    private static Vector _coding_scheme_vec = null;
    private static Vector _concept_domain_vec = null;

    private String _name = null;

    private boolean _isNotEmpty = false;

    private String _status = "1";

	private static String NULL_STRING = "NULL";

	private static int NULL_STRING_HASH_CODE = NULL_STRING.hashCode();

	private static String NCI_THESASURUS = "NCI_Thesaurus";

	public static String NCI_Thesaurus_OWL_Graph = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";


	//private HashMap<String, ComponentObject> _restrictions = null;

    private static String _mode_of_operation = null;

    private String[] _selectedProperties = null;


    public void MappingSessionBean() {

	}

    public String selectTerminologyAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String prev_ng = (String) request.getSession().getAttribute("prev_ng");
        String ng = (String) request.getParameter("ng");
        if (prev_ng == null || ng.compareTo(prev_ng) != 0) {
			Terminology terminology = gov.nih.nci.evs.browser.utils.DataUtils.getTerminologyByNamedGraph(ng);
			String codingSchemeName = terminology.getCodingSchemeName();
			Vector parent_child_vec = NCImtProperties.get_parent_child_vec(codingSchemeName, ng);
			gov.nih.nci.evs.restapi.util.HierarchyHelper hh = new gov.nih.nci.evs.restapi.util.HierarchyHelper(parent_child_vec);
			request.getSession().setAttribute("hh", hh);
		}

        //System.out.println(ng);
        request.getSession().setAttribute("ng", ng);
        request.getSession().setAttribute("prev_ng", ng);
        return "data";
	}

	public Vector tab2BarDelimited(Vector v) {
		if (v == null) return null;
		if (v.size() == 0) return v;
		//String v0 = (String) v.elementAt(0);
		//if (v0.indexOf("\t") == -1) return v;
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			if(line.length() > 0) {
				line = line.replaceAll("\r", "");
				line = line.replaceAll("\t", "|");
				w.add(line);
			}
		}
		return w;
	}

    public String resetAction() {
		//System.out.println("resetAction");
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        String data = (String) request.getParameter("data");
        request.getSession().setAttribute("data", "");
        request.getSession().removeAttribute("mapping_name");
        return "reset";
	}

    public String mappingAction() {
		//System.out.println("mappingAction");
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        HttpServletResponse response =
            (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();

        request.getSession().removeAttribute("msg");
        String data = (String) request.getParameter("data");
        if (data == null || data.length() == 0) {
			String msg = "No mapping data is entered.";
			request.getSession().setAttribute("msg", msg);
			return "warning";
		}

        Vector vbt_vec = new Vector();
        Vector vbt_vec_tmp = gov.nih.nci.evs.restapi.util.StringUtils.parseData(data, '\n');
        for (int i=0; i<vbt_vec_tmp.size(); i++) {
			String t = (String) vbt_vec_tmp.elementAt(i);
			t = t.trim();
			if (t.length() > 0) {
				vbt_vec.add(t);
			}
		}

        Vector w = tab2BarDelimited(vbt_vec);
        request.getSession().setAttribute("vbt_vec", w);

		String prev_ng = (String) request.getSession().getAttribute("prev_ng");
        String ng = (String) request.getParameter("ng");

        if (ng == null) {
			ng = (String) request.getSession().getAttribute("ng");
		}
		if (ng == null) {
			ng = DataManager.NCI_Thesaurus_OWL_Graph;
		}

		Terminology terminology = gov.nih.nci.evs.browser.utils.DataUtils.getTerminologyByNamedGraph(ng);
		String data_directory = NCImtProperties._data_directory;

		HashMap mappingUtilsHashMap = (HashMap) request.getSession().getAttribute("mappingUtilsHashMap");
		if (mappingUtilsHashMap == null) {
			mappingUtilsHashMap = new HashMap();
		}
		gov.nih.nci.evs.mapping.util.MappingUtils mappingUtils = null;
		if (mappingUtilsHashMap.containsKey(ng)) {
			mappingUtils = (gov.nih.nci.evs.mapping.util.MappingUtils) mappingUtilsHashMap.get(ng);
		} else {
			mappingUtils = new gov.nih.nci.evs.mapping.util.MappingUtils(data_directory, terminology);
			mappingUtilsHashMap.put(ng, mappingUtils);
		}
		request.getSession().setAttribute("mappingUtilsHashMap", mappingUtilsHashMap);
		request.getSession().setAttribute("codingSchemeName", terminology.getCodingSchemeName());

        request.getSession().setAttribute("prev_ng", ng);
        request.getSession().setAttribute("ng", ng);

		Mapping mapping = mappingUtils.run(vbt_vec);
        request.getSession().setAttribute("mapping", mapping);

        System.out.println("Redirect to mapping_results...");
        return "mapping_results";
	}

    public String encode_term(String s) {
		if (s == null || s.length() == 0) return s;
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			String t = "" + c;
			//if (t.compareTo(",") == 0 || t.compareTo("\"") == 0) {
			if (t.compareTo("\"") == 0) {
				buf.append(t).append(t);
		    } else {
				buf.append(t);
			}
		}
		return buf.toString();
	}

    public String exportToExcelAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        HttpServletResponse response =
            (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();

        request.getSession().removeAttribute("msg");
	    Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();

        StringBuffer sb = new StringBuffer();
        try {
   			sb.append("\"Source Code\",");
   			sb.append("\"Source Name\",");
   			sb.append("\"Target Code\",");
   			sb.append("\"Target Name\"");
   			sb.append("\r\n");
			for (int i=0; i<entries.size(); i++) {
				gov.nih.nci.evs.mapping.bean.MappingEntry entry
				   = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);

				   String source_code = entry.getSourceCode();
				   String source_term = entry.getSourceTerm();
				   source_term = encode_term(source_term);
				   String target_code = entry.getTargetCode();
				   String target_label = entry.getTargetLabel();
				   target_label = encode_term(target_label);

				   sb.append("\"" + source_code + "\",");
				   sb.append("\"" + source_term + "\",");
				   sb.append("\"" + target_code + "\",");
				   sb.append("\"" + target_label + "\"");
				   if (i<entries.size()-1)
				   {
					    sb.append("\r\n");
				   }
			}
   		} catch (Exception ex)	{
   			sb.append("WARNING: Export to CVS action failed.");
   			ex.printStackTrace();
   		}

   		//String filename = mapping_schema + "_" + mapping_version;

   		String filename = (String) request.getSession().getAttribute("mapping_name");
        if (filename == null) {
   			filename = filename + "_" + gov.nih.nci.evs.restapi.util.StringUtils.getToday() + ".csv";
		} else {
			int n = filename.lastIndexOf(".");
			filename = filename.substring(0, n) + "_" + gov.nih.nci.evs.restapi.util.StringUtils.getToday() + ".csv";
		}
        System.out.println("Export to " + filename);
   		response.setContentType("text/csv");
   		response.setHeader("Content-Disposition", "attachment; filename="
   				+ filename);

   		response.setContentLength(sb.length());

   		try {
   			ServletOutputStream ouputStream = response.getOutputStream();
   			ouputStream.write(sb.toString().getBytes("UTF-8"), 0, sb.length());
   			ouputStream.flush();
   			ouputStream.close();
   		} catch (Exception ex) {
   			ex.printStackTrace();
   			sb.append("WARNING: Export to CVS action failed.");
   		}
   		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}


    public String exportNoMatchToExcelAction() {

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        HttpServletResponse response =
            (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();

        request.getSession().removeAttribute("msg");

	    Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();

           StringBuffer sb = new StringBuffer();
           try {
   			sb.append("\"Source Code\",");
   			sb.append("\"Source Name\",");
   			sb.append("\"Target Code\",");
   			sb.append("\"Target Name,");
   			sb.append("\r\n");

		for (int i=0; i<entries.size(); i++) {
			gov.nih.nci.evs.mapping.bean.MappingEntry entry
			   = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
			   if (entry.getTargetCode().compareTo("") == 0) {
					sb.append("\"" + entry.getSourceCode() + "\",");
					sb.append("\"" + entry.getSourceTerm() + "\",");
					sb.append("\"" + entry.getTargetCode() + "\",");
					sb.append("\"" + entry.getTargetLabel() + "\"");
					if (i<entries.size()-1)
					{
						sb.append("\r\n");
					}
				}
   			}
   		} catch (Exception ex)	{
   			sb.append("WARNING: Export to CVS action failed.");
   			ex.printStackTrace();
   		}

   		//String filename = mapping_schema + "_" + mapping_version;
   		String filename = "mapping_nomatch";
  		filename = filename + "_" + gov.nih.nci.evs.restapi.util.StringUtils.getToday() + ".csv";

   		response.setContentType("text/csv");
   		response.setHeader("Content-Disposition", "attachment; filename="
   				+ filename);

   		response.setContentLength(sb.length());

   		try {
   			ServletOutputStream ouputStream = response.getOutputStream();
   			ouputStream.write(sb.toString().getBytes("UTF-8"), 0, sb.length());
   			ouputStream.flush();
   			ouputStream.close();
   		} catch (Exception ex) {
   			ex.printStackTrace();
   			sb.append("WARNING: Export to CVS action failed.");
   		}
   		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}


	public String manualMappingAction() {
		return "manual_mapping";
	}

    public String searchAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("msg");
        request.getSession().removeAttribute("partial_matches");
        request.getSession().removeAttribute("searchstring");

        String term = (String) request.getParameter("term");
        if (term == null) {
			term = (String) request.getSession().getAttribute("term");
		}

        String searchstring = (String) request.getParameter("searchstring");

        System.out.println(term);
        System.out.println("searchstring: " + searchstring);

        searchstring = searchstring.trim();
        if (searchstring.length() == 0) {
			String msg = "WARNING: No search string is entered.";
			return "warning";
		}
		request.getSession().setAttribute("searchstring", searchstring);

        String named_graph = (String) request.getParameter("named_graph");
        if (named_graph == null) {
			named_graph = (String) request.getParameter("ng");
		}
		if (named_graph == null) {
			named_graph = gov.nih.nci.evs.browser.common.Constants.NCIT_NG;
		}

		System.out.println("ng: " + named_graph);
		request.getSession().setAttribute("named_graph", named_graph);
		request.getSession().setAttribute("ng", named_graph);

        gov.nih.nci.evs.mapping.util.DataUtils dataUtils = (gov.nih.nci.evs.mapping.util.DataUtils) request.getSession().getAttribute("dataUtils");
        if (dataUtils == null) {
			String serviceUrl = NCImtProperties._service_url;
			String data_directory = NCImtProperties._data_directory;
			dataUtils = new gov.nih.nci.evs.mapping.util.DataUtils(serviceUrl, data_directory);
			request.getSession().setAttribute("dataUtils", dataUtils);
		}

		Vector v = dataUtils.get_partial_matches(named_graph, searchstring);
		if (v == null || v.size() == 0) {
			request.getSession().setAttribute("msg", "No match.");
		} else {
			request.getSession().setAttribute("partial_matches", v);
		}

		return "search";
	}

    public HashMap createCode2LabelHashMap(Vector v) {
		HashMap code2LabelHashMap = new HashMap();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
			String code = (String) u.elementAt(0);
			String label = (String) u.elementAt(1);
			code2LabelHashMap.put(code, label);
		}
		return code2LabelHashMap;
	}

    public String saveAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("msg");
        String term = (String) request.getParameter("term");
        if (term == null) {
			term = (String) request.getSession().getAttribute("term");
		}
		term = term.replaceAll("%22", "\"");
        String[] codes = (String[]) request.getParameterValues("concepts");
        Vector code_vec = new Vector();
        if (codes != null && codes.length > 0) {
			for (int i=0; i<codes.length; i++) {
				String code = codes[i];
				System.out.println(code);
				code_vec.add(code);
			}
		} else {
			String msg = "WARNING: No item is selected.";
			return "warning";
		}
		Vector v = (Vector) request.getSession().getAttribute("partial_matches");
		HashMap Code2LabelHashMap = createCode2LabelHashMap(v);
	    Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");


		System.out.println("mapping total count: " + mapping.getTotalCount());
		System.out.println("mapping match count: " + mapping.getMatchCount());

	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> new_entries = new ArrayList<gov.nih.nci.evs.mapping.bean.MappingEntry>();
	    int knt = 0;

		for (int i=0; i<entries.size(); i++) {
			gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
			int k = i+1;
			if (entry.getSourceTerm().compareTo(term) == 0) {
				for (int j=0; j<code_vec.size(); j++) {
					String code = (String) code_vec.elementAt(j);
					System.out.println("code: " + code);
					String sourceCode = entry.getSourceCode();
					String sourceTerm = term;
					String targetCode = code;
					String targetLabel = (String) Code2LabelHashMap.get(code);
					gov.nih.nci.evs.mapping.bean.MappingEntry new_entry = new gov.nih.nci.evs.mapping.bean.MappingEntry(
						sourceCode, sourceTerm, targetCode, targetLabel);
					new_entries.add(new_entry);
					knt++;
				}
			} else {
				new_entries.add(entry);
			}
		}
		Mapping new_mapping = new Mapping(mapping.getTotalCount(), mapping.getMatchCount() + knt, new_entries);
		//saved_data.add(new_mapping.toJson());
		System.out.println("new_mapping total count: " + new_mapping.getTotalCount());
		System.out.println("new_mapping match count: " + new_mapping.getMatchCount());

        request.getSession().setAttribute("mapping", new_mapping);
        request.getSession().removeAttribute("partial_matches");
        request.getSession().removeAttribute("msg");
        request.getSession().removeAttribute("searchstring");
        //Utils.saveToFile(NCImtBrowserProperties._data_directory + File.separator + "debug_11302018.txt", saved_data);

		return "mapping_results";
	}


	public boolean hasMultiple(List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries, String source_term) {
		int knt = 0;
		for (int i=0; i<entries.size(); i++) {
			gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
			Integer row_id = new Integer(i);
			String rowid = row_id.toString();
			String sourceCode = entry.getSourceCode();
			String sourceTerm = entry.getSourceTerm();
			if (sourceTerm.compareTo(source_term) == 0) {
				knt++;
			}
		}
		if (knt > 1) return true;
		return false;
    }

    public String deleteAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("msg");

        String[] rowids = (String[]) request.getParameterValues("rowids");
		if (rowids == null || rowids.length == 0) {
			String msg = "WARNING: No item is selected.";
			request.getSession().setAttribute("msg", msg);
			return "warning";
		}

        Vector rowid_vec = new Vector();
        if (rowids != null && rowids.length > 0) {
			for (int i=0; i<rowids.length; i++) {
				String rowid = rowids[i];
				rowid_vec.add(rowid);
			}
		}

	    Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> new_entries = new ArrayList<gov.nih.nci.evs.mapping.bean.MappingEntry>();
	    int knt = 0;
		for (int i=0; i<entries.size(); i++) {
			gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
			Integer row_id = new Integer(i);
			String rowid = row_id.toString();
			String sourceCode = entry.getSourceCode();
			String sourceTerm = entry.getSourceTerm();
			if (rowid_vec.contains(rowid)) {
				if (!hasMultiple(entries, sourceTerm)) {
					gov.nih.nci.evs.mapping.bean.MappingEntry new_entry = new gov.nih.nci.evs.mapping.bean.MappingEntry(
						sourceCode, sourceTerm, "", "");
				    new_entries.add(new_entry);
				}
			} else {
				new_entries.add(entry);
			}
		}
		Mapping new_mapping = new Mapping(mapping.getTotalCount(), mapping.getMatchCount() + knt, new_entries);
        request.getSession().setAttribute("mapping", new_mapping);
        request.getSession().removeAttribute("rowids");
        request.getSession().removeAttribute("msg");
		return "mapping_results";
	}

    public String backAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("msg");
        /*
	    Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
	    List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();
        request.getSession().setAttribute("mapping", mapping);
        */
		return "mapping_results";
	}

    public String uploadMappingAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().setAttribute("action", "upload_mapping");
        /*
		//request.getSession().setAttribute("type", type);
        Vector cs_data = new Vector();
		DataManager dm = (DataManager) request.getSession().getAttribute("dm");
		if (dm == null) {
			String serviceUrl = NCImtBrowserProperties._service_url;
			String data_directory = NCImtBrowserProperties._data_directory;
			dm = new DataManager(serviceUrl, data_directory);
			Vector terminologies = dm.getTerminologies();
			for (int i=0; i<terminologies.size(); i++) {
				Terminology terminology = (Terminology) terminologies.elementAt(i);
				cs_data.add(terminology.getCodingSchemeName() + "|" + terminology.getCodingSchemeVersion() + "|" + terminology.getNamedGraph());
			}
			cs_data = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(cs_data);
			request.getSession().setAttribute("cs_data", cs_data);
			request.getSession().setAttribute("dm", dm);
		}
        //request.getSession().setAttribute("type", type);
        */
		return "upload";
	}

    public String uploadDataAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        request.getSession().setAttribute("action", "upload_data");
        return "upload";
	}

/*
	public String backAction() {
		return "back";
	}
*/
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


	public static gov.nih.nci.evs.mapping.bean.Mapping createMapping(String mapping_data) {
		if (mapping_data == null) return null;
		System.out.println("mapping_data.length(): " + mapping_data.length());
		Vector v = parseData(mapping_data, '\n');

		System.out.println("v.size(): " + v.size());

		List entries = new ArrayList();
		//int vbt_knt = 0;
		int match_knt = 0;
		HashSet hset = new HashSet();

		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			if (line.endsWith("\n")) {
				line = line.substring(0, line.length()-1);
			}
			//NA|Other and unspecified infectious diseases|C26726|Infectious Disorder
			Vector u0 = parseData(line, '|');
			if (u0.size() == 4) {
				String sourceCode = (String) u0.elementAt(0);
				String sourceTerm = (String) u0.elementAt(1);
				if (!hset.contains(sourceTerm)) {
					hset.add(sourceTerm);
				}
				String targetCode = (String) u0.elementAt(2);
				if (targetCode.length() > 0) {
					match_knt++;
				}
				String targetLabel = (String) u0.elementAt(3);
				MappingEntry me = new MappingEntry(sourceCode, sourceTerm, targetCode, targetLabel);
				entries.add(me);
			}
		}
		gov.nih.nci.evs.mapping.bean.Mapping mapping = new gov.nih.nci.evs.mapping.bean.Mapping(hset.size(), match_knt, entries);
		return mapping;
	}

}

