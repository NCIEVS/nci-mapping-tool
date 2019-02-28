/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.properties;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.common.*;

import gov.nih.nci.evs.restapi.meta.util.*;
import gov.nih.nci.evs.restapi.meta.bean.*;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class NCImtBrowserProperties {
    private static Logger _logger =
        Logger.getLogger(NCImtBrowserProperties.class);
    public static String SPARQL_SERVICE = "SPARQL_SERVICE";
    private static List _displayItemList;
    private static List _metadataElementList;
    private static List _defSourceMappingList;
    private static HashMap _defSourceMappingHashMap;
    private static List _securityTokenList;
    private static HashMap _securityTokenHashMap;
    private static HashMap _configurableItemMap;

    // KLO
    private static final String DEBUG_ON = "DEBUG_ON";
    public static final String EVS_SERVICE_URL = "EVS_SERVICE_URL";
    public static final String LG_CONFIG_FILE = "LG_CONFIG_FILE";
    public static final String MAXIMUM_RETURN = "MAXIMUM_RETURN";
    public static final String EHCACHE_XML_PATHNAME = "EHCACHE_XML_PATHNAME";
    public static final String SORT_BY_SCORE = "SORT_BY_SCORE";
    public static final String MAIL_SMTP_SERVER = "MAIL_SMTP_SERVER";
    public static final String NCICB_CONTACT_URL = "NCICB_CONTACT_URL";
    public static final String MAXIMUM_TREE_LEVEL = "MAXIMUM_TREE_LEVEL";
    public static final String TERMINOLOGY_SUBSET_DOWNLOAD_URL =
        "TERMINOLOGY_SUBSET_DOWNLOAD_URL";
    public static final String NCIT_BUILD_INFO = "NCIT_BUILD_INFO";
    public static final String NCIT_APP_VERSION = "APPLICATION_VERSION";
    public static final String ANTHILL_BUILD_TAG_BUILT =
        "ANTHILL_BUILD_TAG_BUILT";
    public static final String NCIM_URL = "NCIM_URL";
    public static final String NCIT_URL = "NCIT_URL";
    public static final String TERM_SUGGESTION_APPLICATION_URL =
        "TERM_SUGGESTION_APPLICATION_URL";
    public static final String LICENSE_PAGE_OPTION = "LICENSE_PAGE_OPTION";

    public static final String PAGINATION_TIME_OUT = "PAGINATION_TIME_OUT";
    public static final String MINIMUM_SEARCH_STRING_LENGTH =
        "MINIMUM_SEARCH_STRING_LENGTH";
    public static final String SLIDING_WINDOW_HALF_WIDTH =
        "SLIDING_WINDOW_HALF_WIDTH";
    public static final String STANDARD_FTP_REPORT_URL =
        "STANDARD_FTP_REPORT_URL";
    public static final String STANDARD_FTP_REPORT_INFO =
        "STANDARD_FTP_REPORT_INFO";
    public static final int STANDARD_FTP_REPORT_INFO_MAX = 20;

    public static final String MAPPING_DIR = "MAPPING_DIR";
    public static final String MODE_OF_OPERATION = "MODE_OF_OPERATION";

    public static final String BATCH_MODE_OF_OPERATION = "batch";
    public static final String INTERACTIVE_MODE_OF_OPERATION = "interactive";

    private static NCImtBrowserProperties _browserProperties = null;
    private static Properties _properties = new Properties();

    private static boolean _debugOn = false;
    private static int _maxToReturn = 1000;
    private static int _maxTreeLevel = 1000;
    //private static String _service_url = null;
    private static String _lg_config_file = null;
    private static String _mapping_dir = null;
    private static String _mode_of_operation = null;

    private static String _sort_by_score = null;
    private static String _mail_smtp_server = null;
    private static String _ncicb_contact_url = null;
    private static String _terminology_subset_download_url = null;
    private static String _term_suggestion_application_url = null;

    private static String _license_page_option = null;
    private static String _ncim_url = null;
    private static String _ncit_url = null;
    private static int _pagination_time_out = 4;
    private static int _minimum_search_string_length = 1;

    private static int _sliding_window_half_width = 5;
    private static String _standard_ftp_report_url = "";
    private static Vector<StandardFtpReportInfo> _standard_ftp_report_info_list =
        new Vector<StandardFtpReportInfo>();

    public static Vector _PARENT_CHILDREN = null;
    public static HashMap code2NameMap = null;

    public static String _PARTONOMY_ROLE_NAME = "Anatomic_Structure_Is_Physical_Part_Of";
    public static Vector _PARTONOMY_DATA = null;
    public static String _PARTONOMY_ROOT_STRING = null;

    private static HashSet _partonomy_root_set = null;
    private static HashSet _partonomy_leaf_set = null;

    private static int _totalNumberOfTriples = -1;

    private static gov.nih.nci.evs.restapi.util.HierarchyHelper hh = null;
    public static gov.nih.nci.evs.restapi.util.HierarchyHelper vs_hh = null;

    public static String ncit_version = null;

    public static HashMap nameVersion2NamedGraphMap = null;
    public static HashMap basePrefixUIDHashMap = null;
    public static Vector cs_data = null;
    public static String default_named_graph = null;
    public static String default_flat_ncit_named_graph = null;
    public static gov.nih.nci.evs.restapi.bean.TreeItem VALUE_SET_TREE = null;

    public static TTLQueryUtilsRunner runner = null;//new TTLQueryUtilsRunner(serviceUrl);
    //public static TTLQueryUtils ttlQueryUtils = null;//new TTLQueryUtilsRunner(serviceUrl);

    public static HashMap namedGraph2SchemeMap = null;
    public static gov.nih.nci.evs.restapi.meta.util.VIHUtils ttl_vihUtils = null;//new gov.nih.nci.evs.restapi.meta.util.VIHUtils(SparqlProperties.get_SPARQL_SERVICE());

    //public static NCImtBrowserProperties _browserProperties = null;

    public static String _data_directory = null;
    public static String _sparql_service_url = null;
    public static String _service_url = null;

    public static String DATA_DIRECTORY = "DATA_DIRECTORY";

    public static String NCIT_NG = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";

    //public static String EHCACHE_XML_PATHNAME = "EHCACHE_XML_PATHNAME";
    public static String _ehcache_xml_pathname = null;


    /**
     * Private constructor for singleton pattern.
     */
    private NCImtBrowserProperties() {
    }



    static {
		try {
			_browserProperties = new NCImtBrowserProperties();
			//gov.nih.nci.evs.browser.NCImBrowserProperties=c:/apps/evs/ncim/conf/NCImBrowserProperties.xml
			System.out.println("NCImtBrowserProperties loadProperties ...");
			loadProperties();

			_data_directory =
				_browserProperties
					.getProperty(_browserProperties.DATA_DIRECTORY);

			if (_data_directory == null || _data_directory.length() == 0 || _data_directory.compareTo("null") == 0) {
				_data_directory = get_application_configuration_directory();
			}
			System.out.println(_data_directory);

			_debugOn = Boolean.parseBoolean(getProperty(DEBUG_ON));

			String max_str =
				_browserProperties
					.getProperty(_browserProperties.MAXIMUM_RETURN);
			_maxToReturn = Integer.parseInt(max_str);

			String max_tree_level_str =
				_browserProperties
					.getProperty(_browserProperties.MAXIMUM_TREE_LEVEL);
			_maxTreeLevel = Integer.parseInt(max_tree_level_str);

			_service_url =
				_browserProperties
					.getProperty(_browserProperties.EVS_SERVICE_URL);
			// _logger.info("EVS_SERVICE_URL: " + service_url);
			System.out.println("_service_url: " + _service_url);


			_lg_config_file =
				_browserProperties
					.getProperty(_browserProperties.LG_CONFIG_FILE);
			// _logger.info("LG_CONFIG_FILE: " + lg_config_file);

			_sort_by_score =
				_browserProperties
					.getProperty(_browserProperties.SORT_BY_SCORE);
			_ncicb_contact_url =
				_browserProperties
					.getProperty(_browserProperties.NCICB_CONTACT_URL);
			_mail_smtp_server =
				_browserProperties
					.getProperty(_browserProperties.MAIL_SMTP_SERVER);
			_terminology_subset_download_url =
				_browserProperties
					.getProperty(_browserProperties.TERMINOLOGY_SUBSET_DOWNLOAD_URL);
			_term_suggestion_application_url =
				_browserProperties
					.getProperty(_browserProperties.TERM_SUGGESTION_APPLICATION_URL);
			_license_page_option =
				_browserProperties
					.getProperty(_browserProperties.LICENSE_PAGE_OPTION);
			_ncim_url =
				_browserProperties
					.getProperty(_browserProperties.NCIM_URL);
			_ncit_url =
				_browserProperties
					.getProperty(_browserProperties.NCIT_URL);

			_mapping_dir =
				_browserProperties
					.getProperty(_browserProperties.MAPPING_DIR);

			_mode_of_operation =
				_browserProperties
					.getProperty(_browserProperties.MODE_OF_OPERATION);

			_sparql_service_url =
			_browserProperties
				.getProperty(_browserProperties.SPARQL_SERVICE);

System.out.println("_sparql_service_url: " + _sparql_service_url);


			runner = new TTLQueryUtilsRunner(_sparql_service_url);

			String pagination_time_out_str =
				_browserProperties
					.getProperty(_browserProperties.PAGINATION_TIME_OUT);
			if (pagination_time_out_str != null) {
				_pagination_time_out =
					Integer.parseInt(pagination_time_out_str);
			}

			String minimum_search_string_length_str =
				_browserProperties
					.getProperty(_browserProperties.MINIMUM_SEARCH_STRING_LENGTH);
			if (minimum_search_string_length_str != null) {
				int min_search_string_length =
					Integer.parseInt(minimum_search_string_length_str);
				if (min_search_string_length > 1) {
					_minimum_search_string_length =
						min_search_string_length;
				}
			}
			String sliding_window_half_width_str =
				_browserProperties
					.getProperty(_browserProperties.SLIDING_WINDOW_HALF_WIDTH);
			if (sliding_window_half_width_str != null) {
				int sliding_window_halfwidth =
					Integer.parseInt(sliding_window_half_width_str);
				if (sliding_window_halfwidth > 1) {
					_sliding_window_half_width =
						sliding_window_halfwidth;
				}
			}
			_standard_ftp_report_url = getProperty(STANDARD_FTP_REPORT_URL);
			_standard_ftp_report_info_list = StandardFtpReportInfo.parse(
				STANDARD_FTP_REPORT_INFO, STANDARD_FTP_REPORT_INFO_MAX);

			_ehcache_xml_pathname =
				_browserProperties
					.getProperty(_browserProperties.EHCACHE_XML_PATHNAME);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static boolean get_debugOn() {
		return _debugOn;
	}
    /**
     * Gets the single instance of NCImtBrowserProperties.
     *
     * @return single instance of NCImtBrowserProperties
     *
     * @throws Exception the exception
     */
    public static NCImtBrowserProperties getInstance() {//throws Exception {
		/*
        if (_browserProperties == null) {
            synchronized (NCImtBrowserProperties.class) {

                if (_browserProperties == null) {
                    _browserProperties = new NCImtBrowserProperties();
                    loadProperties();

                    _debugOn = Boolean.parseBoolean(getProperty(DEBUG_ON));

                    String max_str =
                        _browserProperties
                            .getProperty(_browserProperties.MAXIMUM_RETURN);
                    _maxToReturn = Integer.parseInt(max_str);

                    String max_tree_level_str =
                        _browserProperties
                            .getProperty(_browserProperties.MAXIMUM_TREE_LEVEL);
                    _maxTreeLevel = Integer.parseInt(max_tree_level_str);

                    _service_url =
                        _browserProperties
                            .getProperty(_browserProperties.EVS_SERVICE_URL);
                    // _logger.info("EVS_SERVICE_URL: " + service_url);

                    _lg_config_file =
                        _browserProperties
                            .getProperty(_browserProperties.LG_CONFIG_FILE);
                    // _logger.info("LG_CONFIG_FILE: " + lg_config_file);

                    _sort_by_score =
                        _browserProperties
                            .getProperty(_browserProperties.SORT_BY_SCORE);
                    _ncicb_contact_url =
                        _browserProperties
                            .getProperty(_browserProperties.NCICB_CONTACT_URL);
                    _mail_smtp_server =
                        _browserProperties
                            .getProperty(_browserProperties.MAIL_SMTP_SERVER);
                    _terminology_subset_download_url =
                        _browserProperties
                            .getProperty(_browserProperties.TERMINOLOGY_SUBSET_DOWNLOAD_URL);
                    _term_suggestion_application_url =
                        _browserProperties
                            .getProperty(_browserProperties.TERM_SUGGESTION_APPLICATION_URL);
                    _license_page_option =
                        _browserProperties
                            .getProperty(_browserProperties.LICENSE_PAGE_OPTION);
                    _ncim_url =
                        _browserProperties
                            .getProperty(_browserProperties.NCIM_URL);
                    _ncit_url =
                        _browserProperties
                            .getProperty(_browserProperties.NCIT_URL);

                    _mapping_dir =
                        _browserProperties
                            .getProperty(_browserProperties.MAPPING_DIR);

                    _mode_of_operation =
                        _browserProperties
                            .getProperty(_browserProperties.MODE_OF_OPERATION);

                    String pagination_time_out_str =
                        _browserProperties
                            .getProperty(_browserProperties.PAGINATION_TIME_OUT);
                    if (pagination_time_out_str != null) {
                        _pagination_time_out =
                            Integer.parseInt(pagination_time_out_str);
                    }

                    String minimum_search_string_length_str =
                        _browserProperties
                            .getProperty(_browserProperties.MINIMUM_SEARCH_STRING_LENGTH);
                    if (minimum_search_string_length_str != null) {
                        int min_search_string_length =
                            Integer.parseInt(minimum_search_string_length_str);
                        if (min_search_string_length > 1) {
                            _minimum_search_string_length =
                                min_search_string_length;
                        }
                    }
                    String sliding_window_half_width_str =
                        _browserProperties
                            .getProperty(_browserProperties.SLIDING_WINDOW_HALF_WIDTH);
                    if (sliding_window_half_width_str != null) {
                        int sliding_window_halfwidth =
                            Integer.parseInt(sliding_window_half_width_str);
                        if (sliding_window_halfwidth > 1) {
                            _sliding_window_half_width =
                                sliding_window_halfwidth;
                        }
                    }
                    _standard_ftp_report_url = getProperty(STANDARD_FTP_REPORT_URL);
                    _standard_ftp_report_info_list = StandardFtpReportInfo.parse(
                        STANDARD_FTP_REPORT_INFO, STANDARD_FTP_REPORT_INFO_MAX);
                }
            }
        }
        */

        return _browserProperties;
    }

    // public String getProperty(String key) throws Exception{
    public static String getProperty(String key) throws Exception {
        // return properties.getProperty(key);
        String ret_str = (String) _configurableItemMap.get(key);
        if (ret_str == null)
            return null;
        if (ret_str.compareToIgnoreCase("null") == 0)
            return null;
        return ret_str;
    }

    public static List getDisplayItemList() {
        return _displayItemList;
    }

    public static List getMetadataElementList() {
        return _metadataElementList;
    }

    public static List getDefSourceMappingList() {
        return _defSourceMappingList;
    }

    public static HashMap getDefSourceMappingHashMap() {
        return _defSourceMappingHashMap;
    }

    public static List getSecurityTokenList() {
        return _securityTokenList;
    }

    public static HashMap getSecurityTokenHashMap() {
        return _securityTokenHashMap;
    }

    public static String get_application_configuration_directory() {
        String propertyFile =
            System.getProperty("gov.nih.nci.evs.browser.NCIMappingToolProperties");
        return new File(propertyFile).getParent();
	}

    private static void loadProperties() throws Exception {
        String propertyFile =
            //System.getProperty("NCImtProperties");
            System.getProperty("gov.nih.nci.evs.browser.NCIMappingToolProperties");
        _logger.info("NCImtBrowserProperties File Location= " + propertyFile);

		System.out.println("propertyFile: " + propertyFile);
		System.out.println("application_configuration_directory: " + get_application_configuration_directory());

        PropertyFileParser parser = new PropertyFileParser(propertyFile);
        parser.run();

        _displayItemList = parser.getDisplayItemList();
        _metadataElementList = parser.getMetadataElementList();
        _defSourceMappingList = parser.getDefSourceMappingList();
        _defSourceMappingHashMap = parser.getDefSourceMappingHashMap();
        _securityTokenList = parser.getSecurityTokenList();
        _securityTokenHashMap = parser.getSecurityTokenHashMap();

        _configurableItemMap = parser.getConfigurableItemMap();

    }

    public static String getLicensePageOption() {
        return _license_page_option;
    }

    public static String getNCIM_URL() {
        return _ncim_url;
    }

    public static String getNCIT_URL() {
        return _ncit_url;
    }

    public static String getMappingDir() {
        return _mapping_dir;
    }

    public static String getModeOfOperation() {
        return _mode_of_operation;
    }

    public static void setModeOfOperation(String mode) {
        _mode_of_operation = mode;
    }

    public static int getPaginationTimeOut() {
        return _pagination_time_out;
    }

    public static int getMinimumSearchStringLength() {
        return _minimum_search_string_length;
    }

    public static int getSlidingWindowHalfWidth() {
        return _sliding_window_half_width;
    }

    public static String getStandardFtpReportUrl() {
        return _standard_ftp_report_url;
    }

    public static Vector<StandardFtpReportInfo> getStandardFtpReportInfoList() {
        return _standard_ftp_report_info_list;
    }

    public static String get_default_named_graph() {
		return NCIT_NG;
	}

    public static TTLQueryUtilsRunner getTTLQueryUtilsRunner() {
		return runner;
	}


    public static String getPrefixes() {
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX :<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("|");
		buf.append("PREFIX base:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl>").append("|");
		buf.append("PREFIX Thesaurus:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("|");
		buf.append("PREFIX xml:<http://www.w3.org/XML/1998/namespace>").append("|");
		buf.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append("|");
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("|");
		buf.append("PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>").append("|");
		buf.append("PREFIX protege:<http://protege.stanford.edu/plugins/owl/protege#>").append("|");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("|");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("|");
		buf.append("PREFIX ncicp:<http://ncicb.nci.nih.gov/xml/owl/EVS/ComplexProperties.xsd#>").append("|");
		buf.append("PREFIX dc:<http://purl.org/dc/elements/1.1/>");
		buf.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#>").append("\n");
		buf.append("PREFIX cadsr:<http://cbiit.nci.nih.gov/caDSR#>").append("\n");
		buf.append("PREFIX ncit:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("\n");
		return buf.toString();
	}

    public static boolean isNCIt(String named_graph) {
		if (named_graph == null) return true;
		if (named_graph.indexOf("NCIt") != -1 || named_graph.indexOf("Thesaurus") != -1) return true;
		return false;
	}

	public static String get_SPARQL_SERVICE() {
			return _service_url;
	}

	public static String get_TERMINOLOGY() {
		return "NCI Thesaurus";
	}

	public static String get_EHCACHE_XML_PATHNAME() {
	    return _ehcache_xml_pathname;
	}


}
