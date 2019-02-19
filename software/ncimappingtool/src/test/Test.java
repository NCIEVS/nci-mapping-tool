import gov.nih.nci.evs.mapping.util.*;
import gov.nih.nci.evs.mapping.bean.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.common.*;

import java.io.*;
import java.net.URI;

import java.util.Map;
import java.util.Map.Entry;

import java.text.*;
import java.util.*;
import java.sql.*;
import javax.faces.model.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;

import gov.nih.nci.evs.browser.properties.*;
import static gov.nih.nci.evs.browser.common.Constants.*;

import gov.nih.nci.evs.browser.common.Constants;
import gov.nih.nci.evs.browser.bean.MappingData;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

//import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.AssociationPredicate;


import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;


import org.apache.log4j.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.lexevs.property.PropertyExtension;


public class Test {

    public Test() {

    }


    public static Vector getTerminologyMetadata(String serviceUrl) {
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



    public static void main(String[] args) {
        String serviceUrl = args[0];
        String data_directory = args[1];

        System.out.println(serviceUrl);
        System.out.println(data_directory);
        //DataManager dm = new DataManager(serviceUrl, data_directory);

        Vector v = getTerminologyMetadata(serviceUrl);
        for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			System.out.println(s);

		}
    }

}


