package gov.nih.nci.evs.service;

import gov.nih.nci.evs.mapping.util.*;
import gov.nih.nci.evs.mapping.bean.*;
import gov.nih.nci.evs.mapping.common.*;
import gov.nih.nci.evs.restapi.util.*;

import java.awt.*;
import java.awt.event.* ;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.* ;
import java.util.*;
import javax.swing.* ;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import javax.swing.tree.*;


public class MappingPanel extends JPanel
                             implements ActionListener {
    DefaultHighlighter highlighter;
    DefaultHighlighter unhighlighter;
    DefaultHighlighter.DefaultHighlightPainter highlight_painter;
    DefaultHighlighter.DefaultHighlightPainter unhighlight_painter;
    SimpleAttributeSet sas;
    Color backgroundColor;
	static String title = "EVS Mapping Service";
	PrintWriter out;
    JFrame frame;
    JDialog dialog = null;
    JFileChooser fc = null;
    JPanel panel = null;
    JLabel dataDirectoryLabel = null;
    JTextField endpointField = null;
    JLabel endpointLabel = null;
    JTextField dataDirectory = null;
    //JTextField terminologyField = null;
    JComboBox terminologyList = null;
    JButton directoryButton = null;
    JLabel outputfileLabel = null;
    JTextField outputFile = null;
    JTextField verbatimField = null;
    JButton verbatimButton = null;
    JButton saveButton = null;
    JTextArea textarea = null;
    JPanel buttonPanel = null;
    JButton okButton = null;
    JButton closeButton = null;
    JButton outputButton = null;
    JButton updateButton = null;

    JButton terminologyButton = null;
    DataManager dm = null;

    String DEFAULT_ENDPOINT = "https://sparql-evs-dev.nci.nih.gov/sparql";
    String NCI_THESAURUS = "NCI_Thesaurus";
    Vector terminology_data = null;
    HashMap codingSchemeHashMap = null;
    Vector codingSchemeNames = null;
    String[] terminologies = null;

    public MappingPanel(String serviceUrl) {
		super(new BorderLayout());
		long ms = System.currentTimeMillis();
		set_DEFAULT_ENDPOINT(serviceUrl);
		fc = new JFileChooser();
        highlighter = new DefaultHighlighter();
        highlight_painter =
           new DefaultHighlighter.DefaultHighlightPainter(
                 new Color(198,198,250));
        sas = new SimpleAttributeSet();
		frame = new JFrame(title);
		frame.setSize(500,100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dialog = new JDialog(frame);
		dialog.setSize(750,300);

        GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0,5,0,0);

        JPanel panel = new JPanel(gridbag);

////////////////////////////////////////////////////////////////////////////////////////////
		c.gridx=0;
		c.gridy=0;
		JLabel endpointLabel = new JLabel("SPARQL Endpoint");
		gridbag.setConstraints(endpointLabel, c);
		panel.add(endpointLabel);

		c.gridx=1;
		c.gridy=0;
		endpointField = new JTextField(55);
		endpointField.setText(DEFAULT_ENDPOINT); //https://sparql-evs-dev.nci.nih.gov/sparql
		gridbag.setConstraints(endpointField, c);
		panel.add(endpointField);

		c.gridx=2;
		c.gridy=0;
		updateButton = new JButton("Update");
		updateButton.addActionListener(this);
		gridbag.setConstraints(updateButton, c);
		panel.add(updateButton);

		////////////////////////////////////////////////////////////////////////////////////////////
		c.gridx=0;
		c.gridy=1;
		JLabel dataDirectoryLabel = new JLabel("Data Directory");
		gridbag.setConstraints(dataDirectoryLabel, c);
		panel.add(dataDirectoryLabel);

		c.gridx=1;
		c.gridy=1;
		dataDirectory = new JTextField(55);
		String curr_dir = MappingUtils.getCurrentWorkingDirectory();
		dataDirectory.setText(curr_dir);
		gridbag.setConstraints(dataDirectory, c);
		panel.add(dataDirectory);
		c.gridx=2;
		c.gridy=1;
		directoryButton = new JButton("Select");
		directoryButton.addActionListener(this);
		gridbag.setConstraints(directoryButton, c);
		panel.add(directoryButton);

		updateTerminologies();
		////////////////////////////////////////////////////////////////////////////////////////////

		c.gridx=0;
		c.gridy=2;
		JLabel terminologyLabel = new JLabel("Terminology");
		gridbag.setConstraints(terminologyLabel, c);
		panel.add(terminologyLabel);

		c.gridx=1;
		c.gridy=2;
		terminologyList = new JComboBox(terminologies);

        Dimension preferredSize = terminologyList.getPreferredSize();
        preferredSize.width = 600;
        terminologyList.setPreferredSize(preferredSize);
		gridbag.setConstraints(terminologyList, c);
		terminologyList.addActionListener(this);
		terminologyList.setSelectedItem(NCI_THESAURUS);
		panel.add(terminologyList);

        ////////////////////////////////////////////////////////////////////////////////////////////

		c.gridx=0;
		c.gridy=3;
		JLabel verbatimLabel = new JLabel("Verbatim File (.txt)");
		gridbag.setConstraints(verbatimLabel, c);
		panel.add(verbatimLabel);
		c.gridx=1;
		c.gridy=3;
		verbatimField = new JTextField(55);
		verbatimField.setText("");
		gridbag.setConstraints(verbatimField, c);
		panel.add(verbatimField);
		c.gridx=2;
		c.gridy=3;
		verbatimButton = new JButton("Select");
		verbatimButton.addActionListener(this);
		gridbag.setConstraints(verbatimButton, c);
		panel.add(verbatimButton);
		////////////////////////////////////////////////////////////////////////////////////////////

		dialog.getContentPane().add(panel, BorderLayout.NORTH);

////////////////////////////////////////////////////////////////////////////////////////
        // Text Area
		textarea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textarea);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		scrollPane.setViewportView(textarea);
		dialog.getContentPane().add(scrollPane,BorderLayout.CENTER);
        // Button Panel
		JPanel buttonPanel = new JPanel();
		okButton = new JButton("Generate");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		closeButton = new JButton("Exit");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setLocation(100,20);
		dialog.setModal(true);
		dialog.setTitle(title);
		dialog.pack();
		dialog.show();
		frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		frame.setVisible(true);
		System.out.println("Total initialization run time (ms): " + (System.currentTimeMillis() - ms));
    }


    private void updateTerminologies() {
		terminology_data = new Vector();
		String endpoint = (String) endpointField.getText();
		String data_directory = (String) dataDirectory.getText();
		dm = new DataManager(endpoint, data_directory);
		Vector terminology_vec = dm.getTerminologies();
		for (int i=0; i<terminology_vec.size(); i++) {
			Terminology terminology = (Terminology) terminology_vec.elementAt(i);
			terminology_data.add(terminology.getCodingSchemeName() + "|" + terminology.getCodingSchemeVersion() + "|" + terminology.getNamedGraph());
		}
		try {
			codingSchemeHashMap = new HashMap();
			codingSchemeNames = new Vector();
			for (int i=0; i<terminology_data.size(); i++) {
				String line = (String) terminology_data.elementAt(i);
				Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
				String codingSchemeName = (String) u.elementAt(0);
				codingSchemeNames.add(codingSchemeName);
				codingSchemeHashMap.put(codingSchemeName, line);
			}
			codingSchemeNames = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(codingSchemeNames);
			terminologies = new String[codingSchemeNames.size()];
			for (int i=0; i<codingSchemeNames.size(); i++) {
				String codingSchemeName = (String) codingSchemeNames.elementAt(i);
				terminologies[i] = codingSchemeName;
			}
		} catch (Exception ex) {
			System.out.println("ERROR: Unable to retrieve mapping data from the server.");
		}
	}


    public void set_DEFAULT_ENDPOINT(String serviceUrl) {
		this.DEFAULT_ENDPOINT = serviceUrl;
	}


    public void highlightText(String target) {
        String theText = textarea.getText();
		if (theText.equals("")) return;
		int n = theText.indexOf(target);
		if (n == -1)
		{
		   System.out.println(target + " not found.");
		   return;
		}

        try{
		   highlighter.addHighlight(n, n+target.length(), highlight_painter);
        }
        catch(Exception e) {}
	}

    public void actionPerformed(ActionEvent event) {
       Object action = event.getSource();

       if (action == closeButton) {
			 frame.setVisible(false);
            System.exit(0);
       }

       if (action == updateButton) {
		   updateTerminologies();
	   }

	   if (action == directoryButton) {
		    System.out.println("directoryButton pressed");

			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Open Directory As...");

			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				System.out.println("file.getAbsolutePath() " + file.getAbsolutePath());

				if (file.exists()) {
 				     dataDirectory.setText(file.getAbsolutePath());
 				     return;
				} else {
					 JOptionPane.showMessageDialog(null,"Please specify a data directory.", "Warning",JOptionPane.WARNING_MESSAGE);
					 return;
				}
			}
       }

	   if (action == verbatimButton) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Open Verbatim File As...");
			int returnVal = chooser.showOpenDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				if (file.exists()) {
 				    verbatimField.setText(file.getAbsolutePath());
 				    return;
				} else {
					JOptionPane.showMessageDialog(null,"Please specify a verbatim file.", "Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
       }

	   if (action == outputButton) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Open Verbatim File As...");
			int returnVal = chooser.showSaveDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				if (file.exists()) {
 				    outputFile.setText(file.getAbsolutePath());
 				    return;
				} else {
					JOptionPane.showMessageDialog(null,"Please specify a verbatim file.", "Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
       }

	   if (action == okButton) {
			 String datadirectory = dataDirectory.getText();
			 Object terminologyItem = terminologyList.getSelectedItem();
			 String terminology = (String) terminologyItem;
			 String verbatim = verbatimField.getText();
			 //String outputfile = outputFile.getText();
     		 if (datadirectory.equals(""))
     		 {
				 JOptionPane.showMessageDialog(null,"Please specify a data directory.", "Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }

			 else if (terminology.equals(""))
			 {
				 JOptionPane.showMessageDialog(null,"Please specify an terminology file.","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }

			 else if (verbatim.equals(""))
			 {
				 JOptionPane.showMessageDialog(null,"Please specify a verbatim file.","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }

            textarea.setText("");
            okButton.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // generate the file
            datadirectory = (String) dataDirectory.getText();

            //new gov.nih.nci.evs.mapping.util.MappingUtils(datadirectory, terminology).generateMapping(vbtfile_nm + ".txt");
            MappingUtils mappingUtils = new gov.nih.nci.evs.mapping.util.MappingUtils(datadirectory, terminology);

            String vbtfile = (String) verbatimField.getText();
            mappingUtils.generateMapping(vbtfile);

            JQueryJSONViewer.generateHTML(mappingUtils.jsonfile);

			String textfile = mappingUtils.textfile;
			String jsonfile = mappingUtils.jsonfile;
			String excelfile = mappingUtils.excelfile;
			String htmlfile = mappingUtils.htmlfile;
			String htmltblfile = mappingUtils.htmltblfile;

            gov.nih.nci.evs.mapping.util.Text2HTMLConverter converter = new gov.nih.nci.evs.mapping.util.Text2HTMLConverter(mappingUtils.source, terminology);

            Terminology vocabulary = (Terminology) dm.getTerminologyByCodingSchemeName(terminology);
            String namedGraph = vocabulary.getNamedGraph();
            String host = "http://localhost:8080/";
            converter.assignMappingToolURL(gov.nih.nci.evs.mapping.common.Constants.LOCALHOST, namedGraph);
            converter.generate(textfile, htmltblfile);

            StringBuffer buf = new StringBuffer();
            //String text = (String) textarea.getText();
            buf.append(textfile + " generated." + "\n");
            buf.append(jsonfile + " generated." + "\n");
            buf.append(excelfile + " generated." + "\n");
            buf.append(htmlfile + " generated." + "\n");
            buf.append(htmltblfile + " generated." + "\n");
            textarea.setText(buf.toString());
            okButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
       }
       if (action == directoryButton) {
			System.out.println("Open button pressed");
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Open File As...");
			int returnVal = chooser.showOpenDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				String datadirectory = file.getAbsolutePath();
				dataDirectory.setText(datadirectory);
				int n = datadirectory.lastIndexOf(".");
				String outputfile = datadirectory.substring(0, n) + ".txt";
				outputFile.setText(outputfile);
			}
       }
       if (action == saveButton) {
			System.out.println("Save button pressed");
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Save File As...");
			int returnVal = chooser.showSaveDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				if (file.exists()) {
				    // If file already exists, ask before replacing it.
				    int action0 = JOptionPane.showConfirmDialog(this,
				                 "Replace existing file?");
					if (action0 != JOptionPane.YES_OPTION) return;
				}
				//saveToFile(file);
				outputFile.setText(file.getAbsolutePath());
			}
       }
       if (action == "dataDirectoryLabel") {
           //Enter your code here.
       }
       if (action == "dataDirectory") {
           //Enter your code here.
       }
       if (action == "outputfileLabel") {
           //Enter your code here.
       }
       if (action == "outputFile") {
           //Enter your code here.
       }
    }

    private static void createAndShowGUI(String serviceUrl) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComponent newContentPane = new MappingPanel(serviceUrl);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
		String serviceUrl = args[0];
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(serviceUrl);
            }
        });
    }
}

