package sampleCode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/* <applet code = "test" width = 300 height = 300> </applet> */
public class WinDbs extends JFrame implements ActionListener,ChangeListener
{
	/**
	 * 
	 */
	private JPanel contentPane;
	private DefaultTableModel model_FF,model_SFH,model_FH;

	private Connection con;
	private Vector<Object> dFF,dFH; 
	private Vector<Object> column_FF=new Vector<Object>();
	private Object[] rowData = new Object[5];
	private Object modulation[]= {"AM","FM"};


	public static void main(String[] args) {
		WinDbs frame = new WinDbs("Manual Frequency Entry");
		frame.setVisible(true);
	}

	public WinDbs(String T) {
		super(T);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeConnection();
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100,600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		con=getConnection();

		JPanel header_Panel = new JPanel();
		header_Panel.setBounds(29, 25, 516, 40);
		contentPane.add(header_Panel);

		JPanel panel_FF = new JPanel();
		panel_FF.setBounds(27, 78, 516, 462);
		contentPane.add(panel_FF);

		JPanel panel_FH = new JPanel();
		panel_FH.setBounds(25, 78, 545, 462);
		contentPane.add(panel_FH);
		panel_FH.setVisible(false);
		panel_FF.setVisible(true);

		ButtonGroup bg = new ButtonGroup();
		header_Panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel lblSignalType = new JLabel("Signal Type :");
		lblSignalType.setBounds(155, 37, 87, 16);
		header_Panel.add(lblSignalType);

		JRadioButton FH = new JRadioButton("FH");
		FH.setToolTipText("Frequency Hopping");
		FH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_FH.setVisible(true);
				panel_FF.setVisible(false);
			}
		});
		FH.setBounds(294, 33, 53, 25);
		header_Panel.add(FH);
		bg.add(FH);
		JRadioButton FF = new JRadioButton("FF");
		FF.setToolTipText("Frequency Fixed");
		FF.setSelected(true);
		FF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel_FH.setVisible(false);
				panel_FF.setVisible(true);
			}
		});
		FF.setBounds(234, 33, 45, 25);
		header_Panel.add(FF);
		bg.add(FF);
		panel_FH.setLayout(null);



		//-------------FF---------------------------------------------------------------		
		JLabel lblCentralFrequency_FF = new JLabel("Central Frequency :");
		lblCentralFrequency_FF.setBounds(7, 66, 118, 16);

		JTextField frequency_FF = new JTextField();
		frequency_FF.setBounds(120, 63, 84, 22);
		frequency_FF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent ke) {
				keyValidation(ke,frequency_FF.getText());
			}
		});
		frequency_FF.setText("");
		frequency_FF.setToolTipText("Range is 1.5 -3000 MHz");
		frequency_FF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					double freq = Double.parseDouble(frequency_FF.getText());
					if(freq<1.5||freq>3000) {
						JOptionPane.showMessageDialog(rootPane, "Frequency range is 1.5-3000 Mhz");
						frequency_FF.setText(null);
					}
				}
				catch(NumberFormatException ae)
				{
					JOptionPane.showMessageDialog(rootPane, "Invalid Input ");
					frequency_FF.setText(null);
				}
			}
		});
		frequency_FF.setColumns(10);

		JLabel lblMhz_FF = new JLabel("MHz");
		lblMhz_FF.setBounds(208, 66, 30, 16);

		JLabel lblBandwidth = new JLabel("Bandwidth :");
		lblBandwidth.setBounds(242, 66, 95, 16);

		JTextField bandwidth_FF = new JTextField();
		bandwidth_FF.setBounds(318, 63, 118, 22);
		bandwidth_FF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyValidation(e,bandwidth_FF.getText());
			}
		});
		bandwidth_FF.setText("");
		bandwidth_FF.setToolTipText("Range is 0.3-1000 KHz");
		bandwidth_FF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					double band = Double.parseDouble(bandwidth_FF.getText());
					if(band<0.3||band>1000) {
						JOptionPane.showMessageDialog(rootPane, "Band width Range is 0.3-1000 Khz ");
						bandwidth_FF.setText(null);
					}
				}
				catch(NumberFormatException ae)
				{
					//JOptionPane.showMessageDialog(rootPane, "Invalid ");
					bandwidth_FF.setText(null);
				}
			}
		});

		bandwidth_FF.setColumns(10);

		JLabel lblKhz_FF = new JLabel("KHz");
		lblKhz_FF.setBounds(446, 66, 63, 16);


		JLabel lblModulation_FF = new JLabel("Modulation :");
		lblModulation_FF.setBounds(7, 25, 77, 16);

		JComboBox comboBox_FF = new JComboBox(modulation);
		comboBox_FF.setBounds(81, 22, 45, 22);
		comboBox_FF.addActionListener(this);

		JSlider sl_Threshold_FF = new JSlider();
		sl_Threshold_FF.setPaintTicks(true);
		JTextField threshould_FF = new JTextField();
		sl_Threshold_FF.setBounds(208, 7, 199, 52);

		sl_Threshold_FF.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				try { 
					int trd=sl_Threshold_FF.getValue();
					threshould_FF.setText(Integer.toString(trd));
				}
				catch(NullPointerException e){
					// System.out.println(e);
				}
			}
		});


		JLabel lblThreshold = new JLabel("Threshold :");
		lblThreshold.setBounds(138, 25, 66, 16);
		sl_Threshold_FF.setPaintLabels(true);
		sl_Threshold_FF.setMinimum(-140);
		//sl_Threshold_FF.setToolTipText("");
		sl_Threshold_FF.setMaximum(20);
		//panel_FH.add(sl_Threshold);
		sl_Threshold_FF.setMajorTickSpacing(30);
		sl_Threshold_FF.setMinorTickSpacing(1);


		threshould_FF.setBounds(415, 22, 34, 22);
		threshould_FF.setEditable(false);
		//	panel_FH.add(threshould_FF);
		threshould_FF.setColumns(3);
		threshould_FF.setText(Integer.toString(sl_Threshold_FF.getValue()));

		JLabel lblDbm_FF = new JLabel("dbm");
		lblDbm_FF.setBounds(453, 25, 25, 16);


		JButton btnSubmit_FF = new JButton("Submit");
		btnSubmit_FF.setBounds(242, 89, 95, 25);


		model_FF = new DefaultTableModel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column)
			{
				return false;//This causes all cells to be not editable
			}
		};
		model_FF.setColumnIdentifiers(column_FF);
		btnSubmit_FF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try 	
				{

					Vector<Object> dbs=new Vector<Object>();
					System.out.print(comboBox_FF.getSelectedItem().toString());
					dbs.add(comboBox_FF.getSelectedItem().toString());
					dbs.add(Double.parseDouble(frequency_FF.getText()));
					dbs.add(Double.parseDouble(bandwidth_FF.getText()));
					dbs.add(Integer.parseInt(threshould_FF.getText()));
					putdata_FF(dbs);
					frequency_FF.setText(null);
					bandwidth_FF.setText(null);
				}
				//	else 
				catch(NumberFormatException ne){
					JOptionPane.showMessageDialog(rootPane, "Please fill all Entities ");
				}
			}
		});
		column_FF.add("UniqueID");
		column_FF.add("Modulation");
		column_FF.add("Central Frequency");
		column_FF.add("Bandwidth");
		column_FF.add("Threshould");

		model_FF = new DefaultTableModel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model_FF.setColumnIdentifiers(column_FF);
		getdata_FF();


		JScrollPane scrollPane_table_FF = new JScrollPane();
		scrollPane_table_FF.setBounds(51, 139, 421, 371);

		JTable table_FF = new JTable();
		scrollPane_table_FF.setViewportView(table_FF);
		table_FF.setModel(model_FF);
		panel_FF.setLayout(null);

		JButton btnExport_FF = new JButton("Export");
		btnExport_FF.setBounds(130, 89, 93, 25);
		btnExport_FF.setVisible(true);
		btnExport_FF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					try{

						XMLEncoder x = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("FF.xml")));
						x.writeObject(dFF);
						System.out.print("data export Sucessfull");
						x.close();
					}catch(FileNotFoundException ex) {
						System.out.print("file not found "+ex);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Runtime rt = Runtime.getRuntime();
				try {
					rt.exec("C:\\Windows\\System32\\notepad.exe ff.xml");
				}
				catch (IOException ex) {
					System.out.println(ex);
				}
			}
		});
		panel_FF.add(lblCentralFrequency_FF);
		panel_FF.add(frequency_FF);
		panel_FF.add(lblMhz_FF);
		panel_FF.add(lblBandwidth);
		panel_FF.add(bandwidth_FF);
		panel_FF.add(lblKhz_FF);
		panel_FF.add(lblModulation_FF);
		panel_FF.add(comboBox_FF);
		panel_FF.add(lblThreshold);
		panel_FF.add(sl_Threshold_FF);
		panel_FF.add(threshould_FF);
		panel_FF.add(lblDbm_FF);
		panel_FF.add(btnSubmit_FF);
		panel_FF.add(scrollPane_table_FF);
		panel_FF.add(btnExport_FF);

		JButton btnImport_FF = new JButton("Import");
		btnImport_FF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ar) {
				String path=importPath();
				try{					
					XMLDecoder x_in = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
					Vector<Object> readObject = (Vector<Object>) x_in.readObject();				
					//				System.out.print("data import Sucessfull\n "+readObject);
					insertdata_FF(readObject);
					x_in.close();
				}catch(FileNotFoundException ex) {
					System.out.print("file not found "+ex);
				}
			}
		});
		btnImport_FF.setBounds(352, 89, 97, 25);
		panel_FF.add(btnImport_FF);


		//----------------------------------------------FF----------------------------------------------------
		//-----------------------------------------------------------------FH----------------------------------		


		JLabel lblFrequencyMin = new JLabel("Frequency Min :");
		lblFrequencyMin.setBounds(7, 66, 92, 16);
		panel_FH.add(lblFrequencyMin);

		JTextField freqMin_FH = new JTextField();
		freqMin_FH.setText(null);
		freqMin_FH.setToolTipText("Range is 1.5-3000 Mhz");
		freqMin_FH.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyValidation(e,freqMin_FH.getText());
			}
		});
		freqMin_FH.setBounds(107, 63, 97, 22);

		freqMin_FH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					double freqMin = Double.parseDouble(freqMin_FH.getText());
					if(freqMin<1.5||freqMin>3000) {
						JOptionPane.showMessageDialog(rootPane, "Value must be between 1.5-3000 Mhz ");
					}
				}
				catch(NumberFormatException ae)
				{
					JOptionPane.showMessageDialog(rootPane, "Invalid input ");
				}
			}
		});
		panel_FH.add(freqMin_FH);
		freqMin_FH.setColumns(10);

		JLabel lblMhz_FH_Min = new JLabel("MHz");
		lblMhz_FH_Min.setBounds(208, 66, 34, 16);
		panel_FH.add(lblMhz_FH_Min);

		JLabel lblFrequencyMax = new JLabel("Frequency Max :");
		lblFrequencyMax.setBounds(242, 66, 95, 16);
		panel_FH.add(lblFrequencyMax);

		JTextField freqMax_FH = new JTextField();
		freqMax_FH.setText(null);
		freqMax_FH.setBounds(345, 63, 91, 22);
		freqMax_FH.setToolTipText(" Range is 1.5-3000 Mhz and >min Frequency");
		freqMax_FH.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyValidation(e,freqMax_FH.getText());
			}
		});
		freqMax_FH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					double freqMax = Double.parseDouble(freqMax_FH.getText());
					double freqMin = Double.parseDouble(freqMin_FH.getText());
					if(freqMax<freqMin) {
						JOptionPane.showMessageDialog(rootPane, "max frequency must be higher then minimum frequency ");
					}
					else if(freqMax<1.5||freqMax>3000) {
						JOptionPane.showMessageDialog(rootPane, "Value must be between 1.5-3000 Mhz ");
					}
				}
				catch(NumberFormatException ae)
				{
					JOptionPane.showMessageDialog(rootPane, "Invalid ");
				}
			}
		});

		panel_FH.add(freqMax_FH);
		freqMax_FH.setColumns(10);

		JLabel lblMhz_FH_Max = new JLabel("MHz");
		lblMhz_FH_Max.setBounds(446, 66, 63, 16);
		panel_FH.add(lblMhz_FH_Max);

		JLabel lblModulation_FH = new JLabel("Modulation :");
		lblModulation_FH.setBounds(7, 25, 80, 16);
		panel_FH.add(lblModulation_FH);

		JComboBox comboBox_FH = new JComboBox(modulation);
		comboBox_FH.setBounds(81, 22, 45, 22);
		panel_FH.add(comboBox_FH);
		comboBox_FH.addActionListener(this);

		JSlider sl_Threshold_FH = new JSlider();
		JTextField threshould_FH = new JTextField();
		sl_Threshold_FH.setBounds(208, 7, 199, 52);
		sl_Threshold_FH.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				try { 
					int trd=sl_Threshold_FH.getValue();
					threshould_FH.setText(Integer.toString(trd));
				}
				catch(NullPointerException e){
					// System.out.println(e);
				}
			}
		}
				);

		JLabel lblThreshold_1 = new JLabel("Threshold :");
		lblThreshold_1.setBounds(138, 25, 66, 16);
		panel_FH.add(lblThreshold_1);
		sl_Threshold_FH.setPaintLabels(true);
		sl_Threshold_FH.setMinimum(-140);
		sl_Threshold_FH.setMaximum(20);
		sl_Threshold_FH.setSnapToTicks(true);
		sl_Threshold_FH.setPaintTicks(true);
		panel_FH.add(sl_Threshold_FH);
		sl_Threshold_FH.setMajorTickSpacing(30);
		sl_Threshold_FH.setMinorTickSpacing(1);


		threshould_FH.setBounds(415, 22, 34, 22);
		threshould_FH.setEditable(false);
		panel_FH.add(threshould_FH);
		threshould_FH.setColumns(3);
		threshould_FH.setText(Integer.toString(sl_Threshold_FH.getValue()));
		JLabel lblDbm_FH = new JLabel("dbm");
		lblDbm_FH.setBounds(453, 25, 25, 16);
		panel_FH.add(lblDbm_FH);

		JButton Add_FH = new JButton("Add");
		Add_FH.setBounds(135, 89, 97, 25);

		StringBuilder temp = new StringBuilder();

		model_FH=new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model_FH.addColumn("Id");
		model_FH.addColumn("Modulation");
		model_FH.addColumn("Min Frequency");
		model_FH.addColumn("Max Frequency");
		model_FH.addColumn("Threshould");
		model_FH.addColumn("Sub Frequencies");
		getdata_FH();

		model_SFH=new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model_SFH.addColumn("Band");
		model_SFH.addColumn("Start Freq(Mhz)");
		model_SFH.addColumn("Stop Freq(Mhz)");
		Add_FH.addActionListener(new ActionListener() {

			int uid_FH=1;
			public void actionPerformed(ActionEvent e) {

				if(temp==null) model_SFH.setRowCount(0);
				try {
					double freqMax = Double.parseDouble(freqMax_FH.getText());
					double freqMin = Double.parseDouble(freqMin_FH.getText());
					double subFMin = 0;
					double subFMax = 0;

					subFMin=Double.parseDouble(JOptionPane.showInputDialog(null,"Subminimum"));
					subFMax=Double.parseDouble(JOptionPane.showInputDialog(null,"Submaximum"));

					if(freqMin<=subFMin&&freqMax>=subFMax&&subFMin<subFMax) 
					{
						model_SFH.addRow(new Object[] {Integer.toString(uid_FH++),Double.toString(subFMin),Double.toString(subFMax)});
						//						if(uid_FH!=2) temp.append('|');
						if(uid_FH!=2) temp.append(" ; ");
						temp.append(subFMin);
						temp.append('-');
						temp.append(subFMax);
					}
					else if(freqMin>subFMin&&freqMax>=subFMax&&subFMin<subFMax) {
						model_SFH.addRow(new Object[] {Integer.toString(uid_FH++),Double.toString(freqMin),Double.toString(subFMax)});
						//						if(uid_FH!=2) temp.append('|');
						if(uid_FH!=2) temp.append(" ; ");
						temp.append(subFMin);
						temp.append('-');
						temp.append(subFMax);
					}
					else if(freqMin>subFMin&&freqMax<subFMax&&subFMin<subFMax) {
						model_SFH.addRow(new Object[] {Integer.toString(uid_FH++),Double.toString(freqMin),Double.toString(freqMax)});
						//						if(uid_FH!=2) temp.append('|');
						if(uid_FH!=2) temp.append(" ; ");
						temp.append(subFMin);
						temp.append('-');
						temp.append(subFMax);
					}
					else if(freqMin<=subFMin&&freqMax<subFMax&&subFMin<subFMax) {
						model_SFH.addRow(new Object[] {Integer.toString(uid_FH++),Double.toString(subFMin),Double.toString(freqMax)});
						if(uid_FH!=2) temp.append(" ; ");
						temp.append(subFMin);
						temp.append('-');
						temp.append(subFMax);
					}else if(subFMin>subFMax) {
						JOptionPane.showMessageDialog(rootPane, "SubMinimum must be less than SubMaximum");
					}
				}catch(NullPointerException ne) {
					JOptionPane.showMessageDialog(rootPane, "Fill all details");
				}
				catch(NumberFormatException ae)
				{
					JOptionPane.showMessageDialog(rootPane, "Fill all details ");
				}
			}
		});

		panel_FH.add(Add_FH);

		JButton btnSubmit_FH = new JButton("Submit");
		btnSubmit_FH.setBounds(250, 89, 95, 25);
		btnSubmit_FH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//					temp = null;
				try {
					Vector<Object> put =new Vector<Object>();
					put.add(comboBox_FH.getSelectedItem().toString());
					put.add(Double.parseDouble(freqMin_FH.getText()));
					put.add(Double.parseDouble(freqMax_FH.getText()));
					put.add(Integer.parseInt(threshould_FH.getText()));
					put.add(temp.toString());
					putdata_FH(put);
					temp.delete(0,temp.length());
					model_FH.setRowCount(0);
					model_SFH.setRowCount(0);
					getdata_FH();

				}catch(NullPointerException ae)
				{
					//System.out.println(ae);
				}
				catch(NumberFormatException ae)
				{
					JOptionPane.showMessageDialog(rootPane, "Fill all details ");
				}
			}
		});
		panel_FH.add(btnSubmit_FH);


		JScrollPane scrollPane_Table_SFH = new JScrollPane();
		scrollPane_Table_SFH.setBounds(40, 372, 452, 77);
		panel_FH.add(scrollPane_Table_SFH);

		JTable table_SFH = new JTable();
		table_SFH.setModel(model_SFH);
		scrollPane_Table_SFH.setViewportView(table_SFH);

		JScrollPane scrollPane_Table_FH = new JScrollPane();
		scrollPane_Table_FH.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_Table_FH.setBounds(12, 139, 521, 220);
		panel_FH.add(scrollPane_Table_FH);

		JTable table_FH = new JTable();
		table_FH.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				model_SFH.setRowCount(0);
				int rno= table_FH.getSelectedRow();
				String SubS=(String) model_FH.getValueAt(rno, 5);
				String sub[]=SubS.split(";");
				for(int i=0;i<sub.length;i++) {
					String r[]=sub[i].split("-");
					Vector<Object> adr =new Vector<Object>();
					adr.add(i+1);
					adr.add(r[0]);
					adr.add(r[1]);
					model_SFH.addRow(adr);
				}

			}
		});
		scrollPane_Table_FH.setViewportView(table_FH);
		table_FH.setModel(model_FH);

		JButton btnExport_FH = new JButton("Export");
		btnExport_FH.setBounds(370, 89, 93, 25);
		btnExport_FH.setVisible(true);

		btnExport_FH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try{

					XMLEncoder x = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("FH.xml")));
					x.writeObject(dFH);
					//						System.out.print("data export Sucessfull");
					x.close();
				}catch(FileNotFoundException ex) {
					System.out.print("file not found "+ex);
				}
				Runtime rt = Runtime.getRuntime();
				try {
					rt.exec("C:\\Windows\\System32\\notepad.exe FH.xml");
				}
				catch (IOException ex) {
					System.out.println(ex);
				}
			}
		});
		panel_FH.add(btnExport_FH);

		JButton btnImport_FH = new JButton("Import");
		btnImport_FH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ar) {
				String path=importPath();
				try{					
					XMLDecoder x_in = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
					Vector<Object> readObject = (Vector<Object>) x_in.readObject();				
					//			System.out.print("data import Sucessfull\n "+readObject);
					insertdata_FH(readObject);
					x_in.close();
				}catch(FileNotFoundException ex) {
					System.out.print("file not found "+ex);
				}
			}
		});
		btnImport_FH.setBounds(20, 89, 97, 25);
		panel_FH.add(btnImport_FH);

	}

	public Connection getConnection(){
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/ferq","nssk","nssk");
		} catch (SQLException ex) {
			// Logger.getLogger(Work.class.getName()).log(Level.SEVERE, null, ex);
		}
		return con;
	}	
	public void closeConnection(){
		try {
			con.close(); 
		} catch (SQLException ex) {
			// Logger.getLogger(Work.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	public void getdata_FF(){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<Object> a;
		dFF=new Vector<Object>();

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM ff");
			while(rs.next()){
				a = new  Vector<Object>();
				a.add(rs.getInt("id"));
				a.add(rs.getString("modulation"));
				a.add(rs.getFloat("Central_frequency"));
				a.add(rs.getFloat("bandwidth"));
				a.add(rs.getInt("treshould"));
				dFF.add(a);
				model_FF.addRow(a);		
			}
		} catch (SQLException ex) {
			System.out.print(ex);
		}
	}
	public void getdata_FH(){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<Object> a;
		dFH = new Vector<Object>();
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM fh");

			while(rs.next()){

				a = new  Vector<Object>();
				a.add(rs.getInt("Id"));
				a.add(rs.getString("Modulation"));
				a.add(rs.getFloat("Frequency_min"));
				a.add(rs.getFloat("Frequency_max"));
				a.add(rs.getInt("Threshould"));
				a.add(rs.getString("SubFreqs"));
				model_FH.addRow(a);
				dFH.add(a);
			}

		} catch (SQLException ex) {
			// Logger.getLogger(Work.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	public void insertdata_FF(Vector<Object> exd) {
		Iterator<Object> i=exd.iterator();
		while(i.hasNext())
		{
			PreparedStatement pstmt = null;
			Vector<Object> next = (Vector<Object>) i.next();
			Iterator<Object> temp=next.iterator();
			try {
				pstmt = con.prepareStatement("INSERT INTO ff(id,modulation,Central_frequency,bandwidth,treshould)"+" VALUES(?,?,?,?,?)");
				pstmt.setInt(1,(int) temp.next());
				pstmt.setString(2, (String) temp.next());
				pstmt.setDouble(3, (float) temp.next());
				pstmt.setDouble(4, (float) temp.next());
				pstmt.setInt(5, (int) temp.next());
				pstmt.executeUpdate();

			} catch (SQLException ex) {
				System.out.print(ex);
			}
			model_FF.setRowCount(0);
			getdata_FF();
		}
	}
	public void putdata_FF(Vector<Object> a){

		PreparedStatement pstmt = null;
		Iterator<Object> temp=a.iterator();
		try {

			pstmt = con.prepareStatement("INSERT INTO Ff(modulation,Central_frequency,bandwidth,treshould)"+" VALUES(?,?,?,?)");
			pstmt.setString(1, (String) temp.next());
			pstmt.setDouble(2, (double) temp.next());
			pstmt.setDouble(3, (double) temp.next());
			pstmt.setInt(4, (int) temp.next());
			pstmt.executeUpdate();

		} catch (SQLException ex) {
			// Logger.getLogger(Work.class.getName()).log(Level.SEVERE, null, ex);
			System.out.print(ex);
		}
		model_FF.setRowCount(0);
		getdata_FF();
	}
	public void insertdata_FH(Vector<Object> exd) {
		Iterator<Object> i=exd.iterator();
		while(i.hasNext())
		{
			PreparedStatement pstmt = null;
			Vector<Object> next = (Vector<Object>) i.next();
			Iterator<Object> temp=next.iterator();
			try {
				pstmt = con.prepareStatement("INSERT INTO Fh(Modulation,Frequency_min,Frequency_max,Threshould,SubFreqs)"+" VALUES(?,?,?,?,?)");
				pstmt.setInt(1, (int)temp.next());
				pstmt.setString(2, (String) temp.next());
				pstmt.setDouble(3, (double) temp.next());
				pstmt.setDouble(4, (double) temp.next());
				pstmt.setInt(5, (int) temp.next());
				pstmt.setString(6, (String) temp.next());
				pstmt.executeUpdate();

			} catch (SQLException ex) {
				System.out.print(ex);
			}
			model_FH.setRowCount(0);
			getdata_FH();
		}
	}
	public void putdata_FH(Vector<Object> a){

		PreparedStatement pstmt = null;
		Iterator<Object> temp=a.iterator();

		try {

			pstmt = con.prepareStatement("INSERT INTO Fh(Modulation,Frequency_min,Frequency_max,Threshould,SubFreqs)"+" VALUES(?,?,?,?,?)");
			pstmt.setString(1, (String) temp.next());
			pstmt.setDouble(2, (double) temp.next());
			pstmt.setDouble(3, (double) temp.next());
			pstmt.setInt(4, (int) temp.next());
			pstmt.setString(5, (String) temp.next());
			pstmt.executeUpdate();

		} catch (SQLException ex) {
			// Logger.getLogger(Work.class.getName()).log(Level.SEVERE, null, ex);
			System.out.print(ex);
		}

		model_FH.setRowCount(0);
		getdata_FH();
	}
	public String importPath()
	{
		JFileChooser chooser =new JFileChooser();
		chooser.showOpenDialog(null);
		File f=chooser.getSelectedFile();
		String filename=f.getAbsolutePath();
		return filename;
	}
	@Override
	public void stateChanged(ChangeEvent e) {	}
	@Override
	public void actionPerformed(ActionEvent arg0) {	}
	public boolean isDot(String str)
	{
		boolean val = false;
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) == '.')
			{
				val = true;
				break;
			}
		}
		return val;
	}
	public void keyValidation(KeyEvent ke,String S) {
		char c=ke.getKeyChar();
		if(!(Character.isDigit(c)||c=='.'||c==KeyEvent.VK_BACK_SPACE||c==KeyEvent.VK_DELETE)) {
			ke.consume();
		}
		if(c=='.'&& isDot(S)) {
			ke.consume();	
		}
	}
}


