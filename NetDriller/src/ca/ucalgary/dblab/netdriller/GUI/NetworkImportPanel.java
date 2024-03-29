package ca.ucalgary.dblab.netdriller.GUI;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ca.ucalgary.dblab.netdriller.data.Network;
import ca.ucalgary.dblab.netdriller.data.NetworkGraph;


public class NetworkImportPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean firstLoad = true;

	enum FileType{
		EXCEL, TEXT
	}

	int mode = 1;
	boolean headers = false;
	boolean isDirected = false;
	FileType file = FileType.TEXT;
	String path;
	
	CheckboxGroup networkTypeBox, fileTypeBox, directedBox;
	Checkbox headerCheck;
	NetworkPanel parentPanel;
	JFrame frame;
	
	NetworkImportPanel(NetworkPanel parent, JFrame frame, boolean flag)
	{
		this.parentPanel = parent;
		this.frame = frame;
		this.firstLoad = flag;
		this.setLayout(null);
		
		initNetworkLabel();
		initFileTypeLabel();
		initHeaderLabel();
		
		initButtons();
	}
	
	private void initNetworkLabel()
	{
		JLabel networkLabel = new JLabel();
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		TitledBorder titledBorder = new TitledBorder(blackline, "Network Type");
		networkLabel.setBorder(titledBorder);
		
		networkLabel.setBounds(10, 30, 150, 240);
		
		initTypeLabel(networkLabel);
		initDirectedLabel(networkLabel);
		this.add(networkLabel);
		
	}
	
	private void initTypeLabel(JLabel label)
	{
		JLabel modeLabel = new JLabel();
		
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		modeLabel.setBorder(raisedetched);
		networkTypeBox = new CheckboxGroup();
		
		Checkbox oneModeBox =new Checkbox("One Mode", true, networkTypeBox);
		oneModeBox.setBounds(15, 5, 100, 40);
		modeLabel.add(oneModeBox);
		
		Checkbox twoModeBox = new Checkbox("Two Mode", false, networkTypeBox);
		twoModeBox.setBounds(15, 35, 100, 40);
		modeLabel.add(twoModeBox);
		
		modeLabel.setBounds(15, 30, 120, 80);
		
		label.add(modeLabel);
	}
	private void initDirectedLabel(JLabel label)
	{

		JLabel directionLabel = new JLabel();
		
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		directionLabel.setBorder(raisedetched);
		directedBox = new CheckboxGroup();
		
		Checkbox oneModeBox =new Checkbox("Directed", true, directedBox);
		oneModeBox.setBounds(15, 5, 100, 40);
		directionLabel.add(oneModeBox);
		
		Checkbox twoModeBox = new Checkbox("Undirected", false, directedBox);
		twoModeBox.setBounds(15, 35, 100, 40);
		directionLabel.add(twoModeBox);
		
		directionLabel.setBounds(15, 130, 120, 80);
		label.add(directionLabel);
	}

	private void initFileTypeLabel()
	{

		JLabel fileTypeLabel = new JLabel();
		
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "File Type");
		fileTypeLabel.setBorder(titledBorder);
		fileTypeBox = new CheckboxGroup();
		
		Checkbox oneModeBox =new Checkbox("csv file", true, fileTypeBox);
		oneModeBox.setBounds(15, 15, 100, 40);
		fileTypeLabel.add(oneModeBox);
		
		Checkbox twoModeBox = new Checkbox("Text file", false, fileTypeBox);
		twoModeBox.setBounds(15, 45, 100, 40);
		fileTypeLabel.add(twoModeBox);
		
		fileTypeLabel.setBounds(180, 30, 120, 90);
		
		this.add(fileTypeLabel);
	}
	
	private void initHeaderLabel()
	{
		JLabel headerLabel = new JLabel();
		
		headerCheck = new Checkbox("The file contains headers.", false, null);
		headerCheck.setBounds(10, 10, 180, 50);
		headerLabel.setBounds(170, 130, 180, 40);
		headerLabel.add(headerCheck);
		
		this.add(headerLabel);
	}
	
	private void initButtons()
	{
		Button okBtn = new Button("Ok");
		Button cancelBtn = new Button("Cancel");
		
		okBtn.setBounds(190, 235, 60, 30);
		this.add(okBtn);
		
		cancelBtn.setBounds(260, 235, 60, 30);
		this.add(cancelBtn);
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkImportPanel.this.frame.dispose();		
			}
		});
		cancelBtn.requestFocusInWindow();
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkImportPanel.this.isDirected = NetworkImportPanel.this.directedBox.getSelectedCheckbox().getLabel() == "Directed" ? true : false;
				NetworkImportPanel.this.mode = NetworkImportPanel.this.networkTypeBox.getSelectedCheckbox().getLabel() == "One Mode" ? 1 : 2; 
				NetworkImportPanel.this.file = NetworkImportPanel.this.fileTypeBox.getSelectedCheckbox().getLabel()== "csv file" ? FileType.EXCEL : FileType.TEXT;
				NetworkImportPanel.this.headers = NetworkImportPanel.this.headerCheck.getState();
				NetworkImportPanel.this.frame.dispose();
				NetworkImportPanel.this.openFileChooser();
			}
		});
	}
	
	
	private void openFileChooser()
	{
		 FileDialog fd = new FileDialog(frame , "Open a Network File",FileDialog.LOAD);
		 fd.setFile(this.file == FileType.EXCEL? "*.csv" : "*.txt");
		 fd.setEnabled(true);
		 fd.setVisible(true);
		 fd.setAlwaysOnTop(true);
		 
		path = fd.getDirectory() + "/" + fd.getFile();
		if (fd.getDirectory() != null && fd.getFile() != null) {
			try {
				Network net = new Network(this.mode);
				int x = net.loadNetwork(isDirected, path, headers); 
				if(x == 3)
					throw new MyOwnException("A one-mode network has equal number of rows and columns");
				else if (x == 4)
					throw new MyOwnException("An undirected network should be symmetric");
				else if (x == 2)
					throw new MyOwnException("Invalid input");
				if(firstLoad)
					parentPanel.parentFrame.toggleNetworkMenus();
				parentPanel.netGraph = new NetworkGraph(net);
				parentPanel.loadGraph();
			} catch (MyOwnException e) {
				parentPanel.parentFrame.netPanel = null;
				parentPanel.parentFrame.reset();
				JOptionPane.showMessageDialog(frame,
					    e.message,
					    "Invalid input",
					    JOptionPane.ERROR_MESSAGE);
			}catch (Exception e) {
				parentPanel.parentFrame.netPanel = null;
				parentPanel.parentFrame.reset();
				JOptionPane.showMessageDialog(frame,
					    "The file has some problem!",
					    "Invalid input",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}

class MyOwnException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	MyOwnException(String m)
	{
		message = m;
	}
}
