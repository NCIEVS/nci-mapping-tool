package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

public class MappingToolUtils{
    private LexBIGService lbSvc = null;

    public MappingToolUtils(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
    }

    public Vector<String> getSupportedSearchTechniqueNames()
	{
		Vector<String> v = new Vector<String>();
		try {
			ModuleDescriptionList list = lbSvc.getMatchAlgorithms();
			if (list == null)
			{
				System.out.println("WARNING: ModuleDescriptionList.getMatchAlgorithms returns null.");
			    return v;
			}
			ModuleDescription[] eda = list.getModuleDescription();
			if (eda == null)
			{
				System.out.println("WARNING: ModuleDescriptionList.getModuleDescription returns null.");
			    return v;
			}

			for (int i=0; i<eda.length; i++)
			{
				//ExtensionDescription ed = (ExtensionDescription) eda[i];
				ModuleDescription ed = (ModuleDescription) eda[i];

				//v.add(ed.getExtensionName());
				v.add(ed.getName());


				System.out.println(ed.getName() + ": " + ed.getDescription());
			}
	    } catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return v;
    }
}