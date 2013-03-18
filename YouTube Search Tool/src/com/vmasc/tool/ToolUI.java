/**
 * 
 * YouTube Search Tool
 * *******************************************
 * Author: Hamdi Kavak
 * Date: 03/17/2013
 * 
 * *******************************************
 *  Some part of the code in this project is adapted from: http://www.javacodegeeks.com 
 *  										 Article name: Query YouTube videos using YouTube Java API
 *  	
 */

package com.vmasc.tool;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.UIManager;

public class ToolUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtManualKeyword;
	private JList list;
	private JCheckBox chckbxIncludeThumbnails;
	private JCheckBox chckbCombineResults;

	private DefaultListModel listModel;
	private final JButton btnDeleteSelectedKeyword = new JButton("Delete Selected");
	private JProgressBar progressBar;
	private JLabel lblProgress;
	private JButton btnExportToFolder;
	
	private JTextField txtLengthFilterStartingFrom;
	private JTextField txtLengthFilterEnd;
	private JTextField txtRelatedKeywords;
	private JTextField txtKeywordFileLocation;
	private JTextField txtExportFolder;
	private JCheckBox chckbxOverwrite;
	private JCheckBox chckBoxHdOnly;
	private JComboBox cmbBoxVideoLength;
	private JLabel lblNumOfKeywordsVar;
	private JLabel lblTotalResultsVar;
	private JLabel lblSearchProgress;
	private JButton btnSearchVideos;
	private YouTubeSearchTool searchTool;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ToolUI frame = new ToolUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public ToolUI() {
		setResizable(false);
		setTitle("YouTube Video Search Tool - Hamdi KAVAK - VMASC");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 550);
		//setBounds(100, 100, 890, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		searchTool = new YouTubeSearchTool();
		
		listModel = new DefaultListModel();
		listModel.clear();
		
		btnSearchVideos = new JButton("Search Videos");
		btnSearchVideos.setForeground(new Color(128, 128, 128));
		btnSearchVideos.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnSearchVideos.setIcon(new ImageIcon(ToolUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		btnSearchVideos.setBounds(225, 474, 200, 40);
		contentPane.add(btnSearchVideos);
		
		btnSearchVideos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Thread thr = new Thread() {
					  public void run() {
						  performVideoSearch();
					  }
				};
				thr.start();
			}
		});
		
		
		btnExportToFolder = new JButton("Export");
		btnExportToFolder.setForeground(new Color(70, 130, 180));
		btnExportToFolder.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnExportToFolder.setIcon(new ImageIcon(ToolUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/maximize.gif")));
		btnExportToFolder.setBounds(700, 474, 160, 40);
		contentPane.add(btnExportToFolder);
		
		btnExportToFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportToFolder();
			}
		});
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new MatteBorder(5, 1, 1, 1, (Color) new Color(128, 128, 128)), "Keyword Management", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(25, 27, 400, 322);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Keyword List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(35, 180, 330, 105);
		panel_2.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		panel_4.add(panel);
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(list);
		list.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		
		list.setBackground(Color.WHITE);
		btnDeleteSelectedKeyword.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDeleteSelectedKeyword.setBounds(114, 288, 131, 23);
		panel_2.add(btnDeleteSelectedKeyword);
		
		JButton btnDeleteAllKeywords = new JButton("Delete All");
		btnDeleteAllKeywords.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDeleteAllKeywords.setBounds(254, 288, 111, 23);
		panel_2.add(btnDeleteAllKeywords);
		btnDeleteAllKeywords.setIcon(new ImageIcon(ToolUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/ListView.gif")));
		
		JButton btnLoadFromFile = new JButton("");
		btnLoadFromFile.setBounds(308, 112, 57, 23);
		panel_2.add(btnLoadFromFile);
		btnLoadFromFile.setIcon(new ImageIcon(ToolUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		
		chckbxOverwrite = new JCheckBox("Overwrite");
		chckbxOverwrite.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckbxOverwrite.setBounds(125, 140, 73, 23);
		panel_2.add(chckbxOverwrite);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(35, 45, 335, 2);
		panel_2.add(separator);
		
		JButton btnAddKeyManually = new JButton("Add");
		btnAddKeyManually.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAddKeyManually.setBounds(308, 50, 57, 23);
		panel_2.add(btnAddKeyManually);
		
		txtManualKeyword = new JTextField();
		txtManualKeyword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addKeyManually();
			}
		});
		txtManualKeyword.setBounds(125, 50, 176, 25);
		panel_2.add(txtManualKeyword);
		txtManualKeyword.setColumns(7);
		
		JLabel lblAddKeywordsManually = new JLabel("Add keywords manually");
		lblAddKeywordsManually.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAddKeywordsManually.setBounds(35, 30, 279, 14);
		panel_2.add(lblAddKeywordsManually);
		
		JLabel lblAddKeywordsFrom = new JLabel("Add keywords from a file");
		lblAddKeywordsFrom.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAddKeywordsFrom.setBounds(35, 85, 321, 14);
		panel_2.add(lblAddKeywordsFrom);
		
		JLabel lblKeyword = new JLabel("Keyword:");
		lblKeyword.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblKeyword.setBounds(47, 54, 68, 14);
		panel_2.add(lblKeyword);
		
		txtKeywordFileLocation = new JTextField();
		txtKeywordFileLocation.setBounds(125, 110, 176, 25);
		panel_2.add(txtKeywordFileLocation);
		txtKeywordFileLocation.setColumns(10);
		
		JLabel lblFile = new JLabel("File location:");
		lblFile.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblFile.setBounds(40, 115, 68, 14);
		panel_2.add(lblFile);
		
		JButton btnLoadKeywordFile = new JButton("Load specified file");
		btnLoadKeywordFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(txtKeywordFileLocation.getText().isEmpty()){
					showMessageBox("You haven't specified any file");
				}
				else{
					loadKeywordsFromFile(txtKeywordFileLocation.getText(), chckbxOverwrite.isSelected());
				}
				
			}
		});
		btnLoadKeywordFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnLoadKeywordFile.setBounds(205, 140, 160, 23);
		panel_2.add(btnLoadKeywordFile);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(35, 100, 335, 2);
		panel_2.add(separator_2);
		btnAddKeyManually.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addKeyManually();
			}
		});
		btnLoadFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.showOpenDialog(null);
				String selectedFileLocation = fileChooser.getSelectedFile().toString();
				
				if(selectedFileLocation.isEmpty() == false){
					txtKeywordFileLocation.setText(selectedFileLocation);
				}
				
			}
		});
		
		btnDeleteAllKeywords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchTool.ClearKeywords();
				listModel.clear();
			}
		});
		
		btnDeleteSelectedKeyword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchTool.DeleteKeywordByIndex(list.getSelectedIndex());
				updateListModel();
			}
		});
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(new MatteBorder(5, 1, 1, 1, (Color) new Color(128, 128, 128)), "Search Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(25, 360, 400, 66);
		contentPane.add(panel_6);
		
		JLabel lblVideoQuality = new JLabel("Video quality:");
		lblVideoQuality.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblVideoQuality.setBounds(41, 33, 76, 14);
		
		chckBoxHdOnly = new JCheckBox("HD+");
		chckBoxHdOnly.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckBoxHdOnly.setBounds(122, 29, 52, 23);
		
		JLabel lblDuration = new JLabel("Video length:");
		lblDuration.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblDuration.setBounds(197, 33, 76, 14);
		
		cmbBoxVideoLength = new JComboBox();
		cmbBoxVideoLength.setFont(new Font("Tahoma", Font.PLAIN, 10));
		cmbBoxVideoLength.setBounds(283, 29, 88, 22);
		cmbBoxVideoLength.setModel(new DefaultComboBoxModel(new String[] {"All", "Short(~4 mins)", "Long(~20 mins)"}));
		panel_6.setLayout(null);
		panel_6.add(lblVideoQuality);
		panel_6.add(chckBoxHdOnly);
		panel_6.add(lblDuration);
		panel_6.add(cmbBoxVideoLength);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(new MatteBorder(5, 1, 1, 1, (Color) new Color(70, 130, 180)), "Video Entries", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setBounds(460, 27, 400, 66);
		contentPane.add(panel_7);
		panel_7.setLayout(null);
		
		JLabel lblNumberOfKeywords = new JLabel("Number of keywords specified");
		lblNumberOfKeywords.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNumberOfKeywords.setBounds(30, 21, 144, 25);
		panel_7.add(lblNumberOfKeywords);
		
		JLabel lblNumberOfTotal = new JLabel("Number of videos mathcing");
		lblNumberOfTotal.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNumberOfTotal.setBounds(214, 21, 129, 25);
		panel_7.add(lblNumberOfTotal);
		
		lblNumOfKeywordsVar = new JLabel("0");
		lblNumOfKeywordsVar.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNumOfKeywordsVar.setBounds(184, 21, 27, 25);
		panel_7.add(lblNumOfKeywordsVar);
		
		lblTotalResultsVar = new JLabel("0");
		lblTotalResultsVar.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblTotalResultsVar.setBounds(346, 21, 44, 25);
		panel_7.add(lblTotalResultsVar);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new MatteBorder(5, 1, 1, 1, (Color) new Color(70, 130, 180)), "Filter Videos (Optional)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(460, 115, 400, 177);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnChooseRelatedKeywordsFile = new JButton("");
		btnChooseRelatedKeywordsFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.showOpenDialog(null);
				String selectedFileLocation = fileChooser.getSelectedFile().toString();
				
				if(selectedFileLocation.isEmpty() == false){
					txtRelatedKeywords.setText(selectedFileLocation);
				}
			}
		});
		btnChooseRelatedKeywordsFile.setBounds(322, 112, 51, 27);
		panel_1.add(btnChooseRelatedKeywordsFile);
		btnChooseRelatedKeywordsFile.setIcon(new ImageIcon(ToolUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		
		txtRelatedKeywords = new JTextField();
		txtRelatedKeywords.setBounds(150, 112, 162, 20);
		panel_1.add(txtRelatedKeywords);
		txtRelatedKeywords.setColumns(15);
		
		JLabel lblWhichDontHave = new JLabel("Keyword file location");
		lblWhichDontHave.setBounds(40, 115, 104, 14);
		panel_1.add(lblWhichDontHave);
		lblWhichDontHave.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		JLabel lblSelectVideosWhich = new JLabel("Select videos which contain one of the following keywords");
		lblSelectVideosWhich.setBounds(35, 90, 338, 14);
		panel_1.add(lblSelectVideosWhich);
		lblSelectVideosWhich.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(35, 105, 340, 2);
		panel_1.add(separator_3);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(35, 45, 340, 2);
		panel_1.add(separator_1);
		
		JLabel lblSelectVideosAccording = new JLabel("Select videos according to length");
		lblSelectVideosAccording.setBounds(35, 30, 321, 14);
		panel_1.add(lblSelectVideosAccording);
		lblSelectVideosAccording.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		txtLengthFilterStartingFrom = new JTextField();
		txtLengthFilterStartingFrom.setBounds(160, 55, 46, 19);
		panel_1.add(txtLengthFilterStartingFrom);
		txtLengthFilterStartingFrom.setFont(new Font("Tahoma", Font.PLAIN, 10));
		txtLengthFilterStartingFrom.setColumns(5);
		
		JLabel lblLessThan = new JLabel("Select videos between");
		lblLessThan.setBounds(40, 58, 105, 13);
		panel_1.add(lblLessThan);
		lblLessThan.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		JLabel lblAndMoreThan = new JLabel("-");
		lblAndMoreThan.setHorizontalAlignment(SwingConstants.CENTER);
		lblAndMoreThan.setBounds(206, 58, 19, 13);
		panel_1.add(lblAndMoreThan);
		lblAndMoreThan.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		txtLengthFilterEnd = new JTextField();
		txtLengthFilterEnd.setBounds(227, 55, 46, 19);
		panel_1.add(txtLengthFilterEnd);
		txtLengthFilterEnd.setFont(new Font("Tahoma", Font.PLAIN, 10));
		txtLengthFilterEnd.setColumns(5);
		
		JLabel lblNewLabel_1 = new JLabel("minute length");
		lblNewLabel_1.setBounds(283, 58, 90, 13);
		panel_1.add(lblNewLabel_1);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		JButton btnApplyFilter = new JButton("Apply Filter");
		btnApplyFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(txtLengthFilterStartingFrom.getText().isEmpty() && txtLengthFilterEnd.getText().isEmpty() && txtRelatedKeywords.getText().isEmpty()){
					showMessageBox("You have to specify at least one filter");
				}
				else {
					if(txtLengthFilterStartingFrom.getText().isEmpty() == false){
						searchTool.SelectVideosMoreThan(Integer.parseInt(txtLengthFilterStartingFrom.getText()));
					}
					if(txtLengthFilterEnd.getText().isEmpty() == false){
						searchTool.SelectVideosLessThan(Integer.parseInt(txtLengthFilterEnd.getText()));
					}
					if(txtRelatedKeywords.getText().isEmpty() == false ){
						
						try {
							List<String> relatedKeywords = new ArrayList<String>();
							FileInputStream fstream = new FileInputStream(txtRelatedKeywords.getText());
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(new InputStreamReader(in));
							String strLine;
							  
							while ((strLine = br.readLine()) != null)  {
								relatedKeywords.add(strLine);
							}
						
							in.close();
							
							searchTool.selectVideosContainingKeywords(relatedKeywords);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						 
					}
					
					int totalResult=0;
					lblNumOfKeywordsVar.setText(searchTool.getKeywords().size() + "");
					
					Enumeration<ArrayList<YouTubeVideo>> enumeration = searchTool.getSearchResults().elements();

					while (enumeration.hasMoreElements()) {
						totalResult += enumeration.nextElement().size();
					}
					
					lblTotalResultsVar.setText(totalResult + "");
				}
				
			}
		});
		btnApplyFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnApplyFilter.setBounds(247, 143, 126, 23);
		panel_1.add(btnApplyFilter);
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new TitledBorder(new MatteBorder(5, 1, 1, 1, (Color) new Color(70, 130, 180)), "Export", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(460, 313, 400, 113);
		contentPane.add(panel_5);
		
		
		chckbxIncludeThumbnails = new JCheckBox("Include thumbnails");
		chckbxIncludeThumbnails.setBounds(35, 29, 126, 23);
		panel_5.add(chckbxIncludeThumbnails);
		chckbxIncludeThumbnails.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckbxIncludeThumbnails.setSelected(true);
		
		chckbCombineResults = new JCheckBox("Combine results into a single file");
		chckbCombineResults.setBounds(163, 29, 167, 23);
		panel_5.add(chckbCombineResults);
		chckbCombineResults.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckbCombineResults.setSelected(true);
		
		JLabel lblSpecifyExportFolder = new JLabel("Specify export folder");
		lblSpecifyExportFolder.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblSpecifyExportFolder.setBounds(35, 65, 104, 14);
		panel_5.add(lblSpecifyExportFolder);
		
		txtExportFolder = new JTextField();
		txtExportFolder.setColumns(15);
		txtExportFolder.setBounds(147, 62, 145, 20);
		panel_5.add(txtExportFolder);
		
		JButton btnSpecifyExportFolder = new JButton("");
		btnSpecifyExportFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.showOpenDialog(null);
				String selectedFileLocation = fileChooser.getSelectedFile().toString();
				
				if(selectedFileLocation.isEmpty() == false){
					txtExportFolder.setText(selectedFileLocation);
				}
			}
		});
		btnSpecifyExportFolder.setIcon(new ImageIcon(ToolUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		btnSpecifyExportFolder.setBounds(295, 59, 51, 27);
		panel_5.add(btnSpecifyExportFolder);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(460, 433, 400, 40);
		contentPane.add(panel_3);
		panel_3.setLayout(new GridLayout(2, 1, 10, 0));
		
		lblProgress = new JLabel("");
		lblProgress.setFont(new Font("Verdana", Font.PLAIN, 9));
		panel_3.add(lblProgress);
		
		progressBar = new JProgressBar();
		panel_3.add(progressBar);
		
		lblSearchProgress = new JLabel("");
		lblSearchProgress.setForeground(Color.RED);
		lblSearchProgress.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblSearchProgress.setBounds(135, 488, 87, 14);
		contentPane.add(lblSearchProgress);
	}

	private void updateListModel() {
		listModel.clear();
		for(String str: searchTool.getKeywords()){
			listModel.addElement(str);
		}
	}
	
	private void addKeyManually() {
		
		if(txtManualKeyword.getText().isEmpty()){
			showMessageBox("You cannot add empty keyword");
		}
		else {
			searchTool.AddKeyword(txtManualKeyword.getText());
			txtManualKeyword.setText("");
			updateListModel();
		}
		
	}
	private void showMessageBox(String string) {
		javax.swing.JOptionPane.showMessageDialog(null, string);
	}

	public void updateProgressBar(int percentage, String text){
		progressBar.setValue(percentage);
		lblProgress.setText(text);
	}
	
	private void loadKeywordsFromFile(String fileLocation, boolean overwrite){
		
		if(overwrite==true){
			searchTool.ClearKeywords();
		}
		
		try{
			  FileInputStream fstream = new FileInputStream(fileLocation);
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  
			  while ((strLine = br.readLine()) != null)  {
				  searchTool.AddKeyword(strLine);
			  }
		
			  in.close();
		}catch (Exception e){
			  //System.err.println("Error: " + e.getMessage());
		}
		updateListModel();
	}
	
	
	protected void performVideoSearch(){
		CustomVideoSearchQuery query = new CustomVideoSearchQuery();
		int totalResult = 0;
		
		query.setHDOnly(chckBoxHdOnly.isSelected());
		
		if(cmbBoxVideoLength.getSelectedIndex() < 1 )
			query.setVideoLength(VideoLengthEnum.ALL);
		else if(cmbBoxVideoLength.getSelectedIndex() == 1 )
			query.setVideoLength(VideoLengthEnum.SHORT);
		else if(cmbBoxVideoLength.getSelectedIndex() == 2 )
			query.setVideoLength(VideoLengthEnum.LONG);
		lblSearchProgress.setText("Searching...");
		btnSearchVideos.setEnabled(false);
		searchTool.search(query);
		
		lblNumOfKeywordsVar.setText(searchTool.getKeywords().size() + "");
		
		Enumeration<ArrayList<YouTubeVideo>> enumeration = searchTool.getSearchResults().elements();

		while (enumeration.hasMoreElements()) {
			totalResult += enumeration.nextElement().size();
		}
		
		lblTotalResultsVar.setText(totalResult + "");
		lblSearchProgress.setText("");
		btnSearchVideos.setEnabled(true);
		setBounds(100, 100, 875, 550);
		updateProgressBar( 100, "Video list successfull");
		enableExporting();
	}
	
	private void exportToHtml(String folder){
		
		// If combine results checkbox is selected
		if(chckbCombineResults.isSelected()){
			
			String filename = "combined";
			
			try {
				
				updateProgressBar(0, "Exporting started");
				
				OutputStream htmlfile;
				PrintStream printhtml;
				File dir;
				StringBuilder keySB, indexSB;
				String newFolder, imagepath;
				YouTubeVideo vid;
				ArrayList<YouTubeVideo> uniqueVideos = new ArrayList<YouTubeVideo>();
				int progressPerc, numOfVideos;

				newFolder = folder + "\\"+filename;
				
				dir = new File(newFolder);  
				dir.mkdir();

				dir = new File(newFolder+"\\images");  
				dir.mkdir();
				
				updateProgressBar(0, "Removing dublicate videos");
				
				boolean videoExist = false;
				
				for(int ind=0; ind< searchTool.getKeywords().size(); ind++){
					
		        	numOfVideos = searchTool.getSearchResults().get(searchTool.getKeywords().get(ind)).size();
		        	
		        	for(int vidid=0; vidid < numOfVideos ;vidid++ ){
		        		
		        		vid = searchTool.getSearchResults().get(searchTool.getKeywords().get(ind)).get(vidid);
		        		videoExist = false;
		        		
		        		for(YouTubeVideo ytv: uniqueVideos){
				        	if(ytv.getVideoId().equals(vid.getVideoId())){
				        		videoExist = true;
				        		break;
				        	}
						}
		        		
		        		if(videoExist == false){
		        			uniqueVideos.add(vid);
		        		}
		        	}
				}
				
				updateProgressBar(0, "Dublicate videos removed");
				
				indexSB = new StringBuilder();
				indexSB.append(getHtmlHeader("Video Index"));
				indexSB.append("<body>");
				indexSB.append("<h2>Video Index</h2>");
				indexSB.append("<table><thead><tr>");
				indexSB.append("<th>Keyword</th>");
				indexSB.append("<th>Number of Videos</th>");
				indexSB.append("</tr></thead><tbody>");
				
				keySB = new StringBuilder();
				keySB.append(getHtmlHeader(escapeHtml3(filename)));
				keySB.append("<body>");
				keySB.append("<a href=\"../index.html\">Back</a>");
				keySB.append("<h2>" + escapeHtml3(filename)+"</h2>");
				keySB.append("<table><thead><tr>");
				keySB.append("<th>ID</th>");
				keySB.append("<th>Title</th>");
				keySB.append("<th>Description</th>");
				keySB.append("<th>Author</th>");
				keySB.append("<th>Duration</th>");
	        	
	        	if(chckbxIncludeThumbnails.isSelected())
	        		keySB.append("	<th>Thumbnail</th>");
	        	else
	        		keySB.append("	<th>Link</th>");
	        	
	        	keySB.append("</tr></thead><tbody>");
				
				
				InputStream cssFilePath = null;
				InputStream jsFilePath = null; 
				
				for(int ind=0;ind<uniqueVideos.size(); ind++){
					progressPerc = (ind*uniqueVideos.size())/100;
					vid = uniqueVideos.get(ind);

	            	keySB.append("<tr>");
	            	keySB.append("<td>" + (ind + 1) + "</td>");
	            	keySB.append("<td>" + escapeHtml3(vid.getTitle()) + "</td>");
	            	keySB.append("<td>" + escapeHtml3(vid.getDescription()) + "</td>");
	            	keySB.append("<td><a href=\"http://www.youtube.com/"+ vid.getVideoOwner() + "\">" + escapeHtml3(vid.getVideoOwner()) + "</td>");
	            	keySB.append("<td>" + (vid.getDuration()==0? "" : convertDurationToText( vid.getDuration()) ) + "</td>");
	            	
	            	if(chckbxIncludeThumbnails.isSelected())
	            		keySB.append("	<td><img src=\"images/"+ (ind +1) +".jpg\" onClick=\"showVideo('"+ vid.getVideoId() + "')\"/></td>");
	            	else
	            		keySB.append("<td><a target=\"_blank\" href=\""+ vid.getWebPlayerUrl().replaceAll("&feature=youtube_gdata_player", "&hd=1") + "\">" + vid.getWebPlayerUrl().replaceAll("&feature=youtube_gdata_player", "") + "</td>");

	            	keySB.append("</tr>");
	            	imagepath = vid.getThumbnails().get(0);
	            	saveImage(imagepath, newFolder+"\\images\\"+ (ind +1) + ".jpg");
	            	updateProgressBar(progressPerc, "Image:"+ (ind +1) +"/" +uniqueVideos.size());
				}
			     
				keySB.append("</tbody></table>");
				keySB.append("</body></html>");
				
				updateProgressBar(100, "HTML file saved.");
				
				htmlfile= new FileOutputStream(new File(newFolder+"\\list.html"));
		        printhtml = new PrintStream(htmlfile);
				printhtml.append(keySB.toString());
		        printhtml.close();
			    htmlfile.close();
			        
			    cssFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/style.css");
			    jsFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/packed.js");

			    copyfile(cssFilePath, newFolder+"\\style.css");
			    copyfile(jsFilePath, newFolder+"\\packed.js");

				indexSB.append("<tr>");
				indexSB.append("<td><a href=\""+ filename+"\\list.html\">" + escapeHtml3(filename) + "<a/></td>");
				indexSB.append("<td>" + uniqueVideos.size() + "</td>");
				indexSB.append("</tr>");
				
				indexSB.append("</tbody></table>");
				indexSB.append("</body></html>");
		        
				htmlfile= new FileOutputStream(new File(folder+"\\index.html"));
	            printhtml = new PrintStream(htmlfile);
	            printhtml.append(indexSB.toString());
	            printhtml.close();
		        htmlfile.close();
				
		        cssFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/style.css");
				copyfile(cssFilePath, folder+"\\style.css");
				updateProgressBar( 100, "Exporting successfull");
		    }
		    catch (Exception e) {
		    	
		    	e.printStackTrace();
		    }
		}
    	else{
    		// seperate video list for each keyword.
    		try {
    			
    			updateProgressBar(0, "Exporting started");
    			
    			OutputStream htmlfile;
    			PrintStream printhtml;
    			File dir;
    			StringBuilder sb, indexSB;
    			String newFolder, newFolderName, imagepath;
    			YouTubeVideo vid;
    			
    			int progressPerc, numOfVideos;
    			
    			indexSB = new StringBuilder();
    			indexSB.append(getHtmlHeader("Video Index"));
    			indexSB.append("<body>");
    			indexSB.append("<h2>Video Index</h2>");
    			indexSB.append("<table><thead><tr>");
    			indexSB.append("<th>Keyword</th>");
    			indexSB.append("<th>Number of Videos</th>");
    			indexSB.append("</tr></thead><tbody>");

    			InputStream cssFilePath = null;
    			InputStream jsFilePath = null; 
    			
    			for(int ind=0; ind < searchTool.getKeywords().size(); ind++){
    				progressPerc = (ind * searchTool.getKeywords().size())/100;

    				newFolder = folder;
    				newFolderName = searchTool.getKeywords().get(ind).replaceAll("/", "-").replaceAll(":", "-").replaceAll(" ", "_").toLowerCase();
    				newFolder += "\\"+newFolderName;
    				dir = new File(newFolder);  
    				dir.mkdir();
    				
    				dir = new File(newFolder+"\\images");  
    				dir.mkdir();
    				
    	            htmlfile= new FileOutputStream(new File(newFolder+"\\list.html"));
    	            printhtml = new PrintStream(htmlfile);
    	            sb = new StringBuilder();

    	            printhtml.append(getHtmlHeader(escapeHtml3(searchTool.getKeywords().get(ind))));
    	            printhtml.append("<body>");
    	            sb.append("<a href=\"../index.html\">Back</a>");
    	            sb.append("<h2>" + escapeHtml3(searchTool.getKeywords().get(ind))+"</h2>");
    	            sb.append("<table><thead><tr>");
    	            sb.append("<th>ID</th>");
    	        	sb.append("<th>Title</th>");
    	        	sb.append("<th>Description</th>");
    	        	sb.append("<th>Author</th>");
    	        	sb.append("<th>Duration</th>");
    	        	
    	        	if(chckbxIncludeThumbnails.isSelected())
    	        		sb.append("	<th>Thumbnail</th>");
    	        	else
    	        		sb.append("	<th>Link</th>");
    	        	
    	        	sb.append("</tr></thead><tbody>");
    	        	numOfVideos = searchTool.getSearchResults().get(searchTool.getKeywords().get(ind)).size();
    	            
    	        	for(int vidid=0; vidid < numOfVideos ;vidid++ ){
    	            	vid = searchTool.getSearchResults().get(searchTool.getKeywords().get(ind)).get(vidid);
    	            	sb.append("<tr>");
    	            	sb.append("<td>" + (vidid+1) + "</td>");
    	            	sb.append("<td>" + escapeHtml3(vid.getTitle()) + "</td>");
    	            	sb.append("<td>" + escapeHtml3(vid.getDescription()) + "</td>");
    	            	
    	            	sb.append("<td><a href=\"http://www.youtube.com/"+ vid.getVideoOwner() + "\">" + escapeHtml3(vid.getVideoOwner()) + "</td>");
    	            	sb.append("<td>" + (vid.getDuration()==0? "" : convertDurationToText( vid.getDuration()) ) + "</td>");
    	            	
    	            	if(chckbxIncludeThumbnails.isSelected())
    	            		sb.append("	<td><img src=\"images/"+ (vidid+1)+".jpg\" onClick=\"showVideo('"+ vid.getVideoId() + "')\"/></td>");
    	            	else
    	            		sb.append("<td><a target=\"_blank\" href=\""+ vid.getWebPlayerUrl().replaceAll("&feature=youtube_gdata_player", "&hd=1") + "\">" + vid.getWebPlayerUrl().replaceAll("&feature=youtube_gdata_player", "") + "</td>");

    	            	sb.append("</tr>");
    	            }
    	            sb.append("</tbody></table>");
    	            
    	            printhtml.append(sb.toString());
    	            
    	            printhtml.append("</body></html>");
    	            printhtml.close();
    		        htmlfile.close();
    		        
    		        cssFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/style.css");
    			    jsFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/packed.js");
    			    
    		        copyfile(cssFilePath, newFolder+"\\style.css");
    		        copyfile(jsFilePath, newFolder+"\\packed.js");
    		        
    		        updateProgressBar(progressPerc, "HTML file saved.");
    		        
    	            for(int vidid=0; vidid < numOfVideos; vidid++ ){
    	            	vid = searchTool.getSearchResults().get(searchTool.getKeywords().get(ind)).get(vidid);
    	            	imagepath = vid.getThumbnails().get(0);
    	            	saveImage(imagepath, newFolder+"\\images\\"+(vidid+1)+".jpg");
    	            	updateProgressBar(progressPerc, "Keyword: "+ searchTool.getKeywords().get(ind)+ " - Image:"+ (vidid+1) +"/" +numOfVideos);
    	            }
    	            
    	            indexSB.append("<tr>");
    				indexSB.append("<td><a href=\""+ newFolderName+"/list.html\">" + escapeHtml3(searchTool.getKeywords().get(ind)) + "<a/></td>");
    				indexSB.append("<td>" + numOfVideos + "</td>");
    				indexSB.append("</tr>");
    			}
    			
    			indexSB.append("</tbody></table>");
    			indexSB.append("</body></html>");
    	        
    			htmlfile= new FileOutputStream(new File(folder+"\\index.html"));
                printhtml = new PrintStream(htmlfile);
                printhtml.append(indexSB.toString());
                printhtml.close();
    	        htmlfile.close();
    			
    	        cssFilePath = ToolUI.class.getClassLoader().getResourceAsStream("com/vmasc/tool/resources/style.css");
			    
    			copyfile(cssFilePath, folder+"\\style.css");
    			updateProgressBar( 100, "Exporting successfull");
    	    }
    	    catch (Exception e) {
    	    	
    	    	e.printStackTrace();
    	    }
    	}
	}
	
	private String convertDurationToText(int duration) {     
		
		long hours = TimeUnit.SECONDS.toHours(duration);
		long minute = TimeUnit.SECONDS.toMinutes(duration) - (hours* 60);
		long second = TimeUnit.SECONDS.toSeconds(duration) - (TimeUnit.SECONDS.toMinutes(duration) *60);
		
		String str = "";
		
		if(hours > 0){
			str += hours+":";
		}
		
		if(minute > 0){
			if( minute < 10 ){
				str += "0";
			}
			str += minute+":";
		}
		
		if(second > 0){
			if( second < 10 ){
				str += "0";
			}
			str += second;
		}

		return str;
	}

	private String getHtmlHeader(String title){
		return "<html><head><LINK href=\"style.css\" rel=\"stylesheet\" type=\"text/css\"><script type=\"text/javascript\" src=\"packed.js\"></script><script type=\"text/javascript\">function showVideo(videoid){var content = '<iframe width=\"853\" height=\"480\" src=\"http://www.youtube.com/embed/'+videoid+'\" frameborder=\"0\" allowfullscreen></iframe></a>';TINY.box.show(content,0,0,0,1);}</script><title>"+title+"</title></head>";
	}
	
	public static void saveImage(String imageUrl, String destinationFile){
		try{
			
		
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
		
		}
		catch(Exception e){
			
		}
	}
	
	private void enableExporting(){
		btnExportToFolder.setEnabled(true);
	}
	
	public void exportToFolder(){
		if(txtExportFolder.getText().isEmpty()){
			showMessageBox("Please specify an export folder");
		}
		else{
			Thread thr = new Thread() {
				  public void run() {
					  exportToHtml(txtExportFolder.getText());
				  }
			};
			thr.start();
		}
	}
	
	
	
	 private void copyfile(InputStream in, String dtFile){
		  try{
			  
		  File f2 = new File(dtFile);
		  
		  OutputStream out = new FileOutputStream(f2);

		  byte[] buf = new byte[1024];
		  int len;
		  while ((len = in.read(buf)) > 0){
		  out.write(buf, 0, len);
		  }
		  in.close();
		  out.close();
		 
		  }
		  catch(FileNotFoundException ex){
		  
		 
		  }
		  catch(IOException e){
		 
		  }
	 }
}
