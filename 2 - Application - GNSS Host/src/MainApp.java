import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.CardLayout;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class MainApp extends JFrame {

	public ImageIcon icon,buffering;
	
	public JPanel titleBarPanel,customTitleBar,mainPanel;
	
	public JPanel contentPane;
	
	public JPanel createHostPanel;
	public JLabel titleLabel;
	public JLabel dialog1;
	public CustomButton createBtn;
	
	public JDialog encSetupFrame;
	public JPanel encryptionSetupPanel;
	public JList filepathList;
	public CustomButton addBtn,removeBtn,removeAllBtn,okBtn;
	public DefaultListModel<String> listModel;
	public JOptionPane decProcessing;
	public JOptionPane decFinished;
	public JDialog dialog;
	
	public JPanel authPanel;
	public JLabel titleLabel_1;
	public JLabel hostnameLabel;
	public JLabel hostIPLabel;
	public JLabel hostPortLbl;
	public JLabel clientConnectStatus;
	public JLabel generatedPasscode;
	public CustomButton cancelHostConnection;
	
	public JPanel connectToHostPanel;
	public JButton createBtn2;
	public JTextField passcodeInput;
	public CustomButton connectHostBtn;
	public CustomButton btnCancel;
	
	public JPanel connectStatusPanel;
	public JLabel hostIPHostName;
	public CustomButton dcButton;
	
	public CardLayout cardLayout;
	public Container container;
	
	public boolean clientConnected = false;
	
	public CardLayout lsCardLayout;
	public Container lsContainer;
	
	public JFrame w;
	
	public JPanel lockPanel;
	public JPanel recoveryPanel;
	
	public JLabel windowLockLabel;
	public JLabel recoveryLabel;
	public JLabel enterPasscodeLbl;
	public JLabel inputStatusLbl;
	
	public JButton recoveryBtn;
	public JButton cancelRecoveryBtn;
	public JButton recoverBtn;
	public CustomButton encSetupBtn;
	
	public JTextField uniquePasscodeInput;
	
	public CustomButton btnHotspot;
	
	public int mouseX,mouseY;
	private JLabel connectedToLbl;
	private JLabel lblRecoveryKey;
	public JLabel generatedRecoveryKey;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel logoWindow;
	public JLabel encBtn;
	private JLabel lblNewLabel_3;
	
	
	/**
	 * Create the frame.
	 */
	
	public MainApp() {
		
		icon = new ImageIcon("icons\\NSS_Icon.png");
		
		setTitle("Network Security System");
		setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		double x = 600;
	    double y = 600;
	    setUndecorated(true);
	    setShape(new RoundRectangle2D.Double(0, 0, x, y, 25, 25));
	    setSize((int)x,(int)y);
		setBounds(100, 100, 600, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		
		// Title Bar Panel
		titleBarPanel = new JPanel();
		titleBarPanel.setBounds(0, 0, 600, 42);
	    titleBarPanel.setLayout(null);
	    getContentPane().add(titleBarPanel);
		
		// Main Panel
		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(255, 128, 0));
	    mainPanel.setBounds(0, 42, 600, 558);
	    mainPanel.setLayout(null);
	    getContentPane().add(mainPanel);
		
		// Layout for the container
		cardLayout = new CardLayout(0, 0);

		// Container for all panels (uses CardLayout)
		container = mainPanel;
		container.setLayout(cardLayout);

		container.setBackground(new Color(128, 255, 128));
		//container.setBounds(0, 0, 584, 561);
		
		// Custom Menu Bar
	 	
	    customTitleBar = new JPanel();
	    customTitleBar.setBounds(0, 0, 600, 42);
	    customTitleBar.setBackground(new Color(0, 0, 0));
	    customTitleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        customTitleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - mouseX;
                int y = e.getYOnScreen() - mouseY;
                setLocation(x, y);
            }
        });
	    
	    customTitleBar.setLayout(null);
	    
	    Icon minIcon = new ImageIcon("icons\\minimize.png");
	    Icon exitIcon = new ImageIcon("icons\\exit.png");
	    
	    JButton minimizeBtn = new JButton(minIcon);
	    //UIManager.put("Button.select", Color.BLACK);
	    minimizeBtn.setFocusTraversalKeysEnabled(false);
	    minimizeBtn.setFocusPainted(false);
	    minimizeBtn.setFocusable(false);
	    minimizeBtn.setBorderPainted(false);
	    minimizeBtn.setBackground(new Color(0, 0, 0));
	    minimizeBtn.setBounds(536, 9, 20, 20);
	    minimizeBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		setState(Frame.ICONIFIED);
	    	}
	    });
	    customTitleBar.add(minimizeBtn);
	    
	    JButton exitBtn = new JButton(exitIcon);
	    exitBtn.setFocusTraversalKeysEnabled(false);
	    exitBtn.setFocusPainted(false);
	    exitBtn.setFocusable(false);
	    exitBtn.setBounds(566, 11, 20, 20);
	    customTitleBar.add(exitBtn);
	    exitBtn.setBorderPainted(false);
	    exitBtn.setBackground(new Color(0, 0, 0));
	    
	    exitBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    
	    titleBarPanel.add(customTitleBar);
	    
	    logoWindow = new JLabel("");
	    logoWindow.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\NSS_window_icon.png"));
	    logoWindow.setBounds(10, 6, 28, 28);
	    customTitleBar.add(logoWindow);
	    
	    // Create Host Panel
		createHostPanel = new JPanel();
		createHostPanel.setBackground(new Color(30, 30, 30));
		container.add(createHostPanel, "hostPanel");
		createHostPanel.setLayout(null);

		titleLabel = new JLabel("Network Security System (Host)");
		titleLabel.setForeground(new Color(255, 255, 255));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(10, 167, 564, 37);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
		createHostPanel.add(titleLabel);

		dialog1 = new JLabel("Connect via Wi-Fi");
		dialog1.setForeground(new Color(255, 255, 255));
		dialog1.setHorizontalAlignment(SwingConstants.CENTER);
		dialog1.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog1.setBounds(78, 315, 208, 37);
		createHostPanel.add(dialog1);

		createBtn = new CustomButton("Wi-Fi", new Color(0,202,0), new Color(255,255,255), 40, null);
		createBtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		createBtn.setFocusable(false);
		createBtn.setBounds(99, 363, 167, 53);
		createHostPanel.add(createBtn);
		
		JLabel dialog2 = new JLabel("Connect via Hotspot");
		dialog2.setHorizontalAlignment(SwingConstants.CENTER);
		dialog2.setForeground(Color.WHITE);
		dialog2.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog2.setBounds(296, 315, 208, 37);
		createHostPanel.add(dialog2);
		
		btnHotspot = new CustomButton("Hotspot",new Color(0,202,0), new Color(255,255,255), 40,null);
		btnHotspot.setFont(new Font("Tahoma", Font.BOLD, 25));
		btnHotspot.setFocusable(false);
		btnHotspot.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnHotspot.setBackground(new Color(0, 202, 0)); // set to 0, 202, 0
		btnHotspot.setBounds(317, 363, 167, 53);
		createHostPanel.add(btnHotspot);
		
//		ImageIcon encIcon = new ImageIcon("icons\\enc.png");
//		encSetupBtn = new CustomButton("",new Color(0, 121, 0), new Color(0,0,0), 10, encIcon);
//		encSetupBtn.setFocusable(false);
//		encSetupBtn.setBounds(9, 54, 28, 28);
//		createHostPanel.add(encSetupBtn);
		
		encBtn = new JLabel("");
		encBtn.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\enc.png"));
		encBtn.setBounds(562, 54, 28, 28);
		createHostPanel.add(encBtn);
		
		lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\NSS_frame_icon.png"));
		lblNewLabel_3.setBounds(256, 233, 64, 64);
		createHostPanel.add(lblNewLabel_3);
		
		JLabel bgImg = new JLabel("");
		bgImg.setIcon(new ImageIcon("icons\\frameBg.png"));
		bgImg.setBounds(0, 0, 600, 600);
		createHostPanel.add(bgImg);
		
		// Encryption Setup Panel
		encSetupFrame = new JDialog();
		encSetupFrame.setTitle("Encryption Setup");
		encSetupFrame.setSize(700,400);
		encSetupFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		encSetupFrame.setAlwaysOnTop(true);
		encSetupFrame.setLocationRelativeTo(null);
		ImageIcon encIcon = new ImageIcon("enc.png");
		encSetupFrame.setIconImage(encIcon.getImage());
		
		encSetupFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				listModel.clear();
				encSetupFrame.dispose();
				setAlwaysOnTop(true);
				setAlwaysOnTop(false);
				setEnabled(true);
				repaint();
			}
		});
		
		encryptionSetupPanel = new JPanel();
		encryptionSetupPanel.setBackground(new Color(15, 15, 15));
		encryptionSetupPanel.setLayout(null);
		encSetupFrame.getContentPane().add(encryptionSetupPanel);
		
		JLabel lblClickTheAdd = new JLabel("Click the ADD button to add files to encrypt.");
		lblClickTheAdd.setForeground(Color.WHITE);
		lblClickTheAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblClickTheAdd.setBounds(20, 11, 263, 23);
		encryptionSetupPanel.add(lblClickTheAdd);
		
		listModel = new DefaultListModel<String>();
		filepathList = new JList(listModel);
		filepathList.setLayout(new BorderLayout());
		encryptionSetupPanel.add(filepathList);
		
		JScrollPane sc = new JScrollPane(filepathList);
		sc.setBounds(20, 34, 642, 250);
		encryptionSetupPanel.add(sc);
		
		addBtn = new CustomButton("ADD", new Color(0,165,0),new Color(255,255,255), 10, null);
		addBtn.setBounds(228, 306, 89, 23);
		encryptionSetupPanel.add(addBtn);
		
		removeBtn = new CustomButton("REMOVE", new Color(255,100,0),new Color(255,255,255), 10, null);
		removeBtn.setBounds(327, 306, 89, 23);
		encryptionSetupPanel.add(removeBtn);
		
		removeAllBtn = new CustomButton("REMOVE ALL", new Color(255,0,0),new Color(255,255,255), 10, null);
		removeAllBtn.setBounds(426, 306, 137, 23);
		encryptionSetupPanel.add(removeAllBtn);
		
		okBtn = new CustomButton("OK", new Color(0,165,0),new Color(255,255,255), 10, null);
		okBtn.setBounds(573, 306, 89, 23);
		encryptionSetupPanel.add(okBtn);
		
		decProcessing = new JOptionPane("Decrypting files. Do not exit the app.", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		dialog = decProcessing.createDialog(null, "Decryption Process");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// Authentication Panel
		authPanel = new JPanel();
		authPanel.setFocusable(false);
		authPanel.setBackground(new Color(30, 30, 30));
		container.add(authPanel, "authPanel");
		authPanel.setLayout(null);

		titleLabel_1 = new JLabel("PC-Smartphone Connection (Wi-Fi)");
		titleLabel_1.setForeground(new Color(255, 255, 255));
		titleLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		titleLabel_1.setBounds(93, 84, 470, 37);
		authPanel.add(titleLabel_1);

		JLabel lbl_hostname = new JLabel("Host Name:");
		lbl_hostname.setForeground(new Color(255, 255, 255));
		lbl_hostname.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_hostname.setFont(new Font("Tahoma", Font.BOLD, 20));
		lbl_hostname.setBounds(67, 216, 117, 37);
		authPanel.add(lbl_hostname);

		hostnameLabel = new JLabel("Encode <Hostname> in this label.");
		hostnameLabel.setForeground(new Color(255, 255, 255));
		hostnameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		hostnameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostnameLabel.setBounds(245, 216, 318, 37);
		authPanel.add(hostnameLabel);

		buffering = new ImageIcon("icons\\buffering.gif");
		clientConnectStatus = new JLabel();
		clientConnectStatus.setIcon(buffering);
		clientConnectStatus.setBounds(250, 143, 50, 50);
		authPanel.add(clientConnectStatus);
		

		JLabel lblPasscode = new JLabel("Passcode: ");
		lblPasscode.setForeground(new Color(255, 255, 255));
		lblPasscode.setHorizontalAlignment(SwingConstants.LEFT);
		lblPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblPasscode.setBounds(67, 362, 117, 37);
		authPanel.add(lblPasscode);

		generatedPasscode = new JLabel("Encode passcode here");
		generatedPasscode.setForeground(new Color(255, 255, 255));
		generatedPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		generatedPasscode.setBounds(245, 362, 318, 37);
		authPanel.add(generatedPasscode);
		
		cancelHostConnection = new CustomButton("CANCEL", new Color(202,0,0),new Color(255,255,255), 40, null);
		cancelHostConnection.setBorderPainted(false);
		cancelHostConnection.setFont(new Font("Tahoma", Font.BOLD, 25));
		cancelHostConnection.setBounds(204, 486, 167, 53);
		authPanel.add(cancelHostConnection);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setForeground(new Color(255, 255, 255));
		lblPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblPort.setBounds(67, 314, 117, 37);
		authPanel.add(lblPort);
		
		hostPortLbl = new JLabel("Encode port here");
		hostPortLbl.setForeground(new Color(255, 255, 255));
		hostPortLbl.setHorizontalAlignment(SwingConstants.LEFT);
		hostPortLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostPortLbl.setBounds(245, 314, 318, 37);
		authPanel.add(hostPortLbl);
		
		JLabel hostIPLbl = new JLabel("Host IP:");
		hostIPLbl.setForeground(new Color(255, 255, 255));
		hostIPLbl.setHorizontalAlignment(SwingConstants.LEFT);
		hostIPLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
		hostIPLbl.setBounds(67, 266, 117, 37);
		authPanel.add(hostIPLbl);
		
		hostIPLabel = new JLabel("Encode <IPAdress> in this label.");
		hostIPLabel.setForeground(new Color(255, 255, 255));
		hostIPLabel.setHorizontalAlignment(SwingConstants.LEFT);
		hostIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostIPLabel.setBounds(245, 266, 318, 37);
		authPanel.add(hostIPLabel);
		
		lblRecoveryKey = new JLabel("Recovery Key: ");
		lblRecoveryKey.setHorizontalAlignment(SwingConstants.LEFT);
		lblRecoveryKey.setForeground(Color.WHITE);
		lblRecoveryKey.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblRecoveryKey.setBounds(67, 410, 160, 37);
		authPanel.add(lblRecoveryKey);
		
		generatedRecoveryKey = new JLabel("Encode key here");
		generatedRecoveryKey.setHorizontalAlignment(SwingConstants.LEFT);
		generatedRecoveryKey.setForeground(Color.WHITE);
		generatedRecoveryKey.setFont(new Font("Tahoma", Font.BOLD, 20));
		generatedRecoveryKey.setBounds(245, 410, 318, 37);
		authPanel.add(generatedRecoveryKey);
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\wifi.png"));
		lblNewLabel_1.setBounds(0, 0, 46, 14);
		authPanel.add(lblNewLabel_1);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\wifi.png"));
		lblNewLabel.setBounds(43, 84, 40, 40);
		authPanel.add(lblNewLabel);
		
		// Connection Status Panel
		connectStatusPanel = new JPanel();
		connectStatusPanel.setBackground(new Color(30, 30, 30));
		container.add(connectStatusPanel, "connectStatusPanel");
		connectStatusPanel.setLayout(null);

		JLabel titleLblConnected = new JLabel("Your PC is now connected.");
		titleLblConnected.setForeground(new Color(255, 255, 255));
		titleLblConnected.setBounds(81, 190, 444, 25);
		titleLblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		titleLblConnected.setFont(new Font("Tahoma", Font.BOLD, 30));
		connectStatusPanel.add(titleLblConnected);

		hostIPHostName = new JLabel("Encode Client IP here");
		hostIPHostName.setForeground(new Color(255, 255, 255));
		hostIPHostName.setHorizontalAlignment(SwingConstants.CENTER);
		hostIPHostName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostIPHostName.setBounds(171, 346, 248, 25);
		connectStatusPanel.add(hostIPHostName);
		
		dcButton = new CustomButton("DISCONNECT", new Color(255, 0, 0), new Color(255,255,255), 40, null);
		dcButton.setFocusable(false);
		dcButton.setForeground(new Color(255, 255, 255));
		dcButton.setBackground(new Color(255, 0, 0));
		dcButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		dcButton.setBorderPainted(false);
		dcButton.setBounds(199, 394, 201, 57);
		connectStatusPanel.add(dcButton);
		
		connectedToLbl = new JLabel("You are now connected to this device: ");
		connectedToLbl.setForeground(new Color(255, 255, 255));
		connectedToLbl.setHorizontalAlignment(SwingConstants.CENTER);
		connectedToLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		connectedToLbl.setBounds(161, 322, 277, 25);
		connectStatusPanel.add(connectedToLbl);
		
		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("D:\\Programming\\Java\\W2 - With Repository\\GNSS_Host\\2 - Application - GNSS Host\\icons\\wifi.gif"));
		lblNewLabel_2.setBounds(265, 252, 48, 48);
		connectStatusPanel.add(lblNewLabel_2);
		
		connectToHostPanel = new JPanel();
		connectToHostPanel.setBackground(new Color(30, 30, 30));
		container.add(connectToHostPanel, "connectToHostPanel");
		connectToHostPanel.setLayout(null);
		
		JLabel inputIPLabel = new JLabel("<HTML>Make sure you are connected to your mobile hotspot's network in order for this session to work.<HTML> ");
		inputIPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inputIPLabel.setForeground(new Color(255, 255, 255));
		inputIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		inputIPLabel.setBounds(150, 171, 285, 89);
		connectToHostPanel.add(inputIPLabel);
		
		JLabel inputPasscodeLabel = new JLabel("Passcode");
		inputPasscodeLabel.setForeground(new Color(255, 255, 255));
		inputPasscodeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		inputPasscodeLabel.setBounds(235, 271, 103, 37);
		connectToHostPanel.add(inputPasscodeLabel);
		
		JLabel connectToHostTitle = new JLabel("Enter Client Details");
		connectToHostTitle.setForeground(new Color(255, 255, 255));
		connectToHostTitle.setFont(new Font("Tahoma", Font.BOLD, 27));
		connectToHostTitle.setBounds(150, 104, 308, 37);
		connectToHostPanel.add(connectToHostTitle);
		
		passcodeInput = new JTextField();
		passcodeInput.setCaretColor(new Color(255, 255, 255));
		passcodeInput.setBorder(new LineBorder(new Color(171, 173, 179), 1, true));
		passcodeInput.setBackground(new Color(15, 15, 15));
		passcodeInput.setForeground(new Color(255, 255, 255));
		passcodeInput.setColumns(10);
		passcodeInput.setBounds(185, 319, 211, 37);
		connectToHostPanel.add(passcodeInput);
		
		connectHostBtn = new CustomButton("CONNECT", new Color(0,202,0),new Color(255,255,255), 40, null);
		connectHostBtn.setForeground(new Color(255, 255, 255));
		connectHostBtn.setBorderPainted(false);
		connectHostBtn.setBackground(new Color(0, 202, 0));
		connectHostBtn.setFont(new Font("Tahoma", Font.BOLD, 20));
		connectHostBtn.setBounds(130, 420, 158, 57);
		connectToHostPanel.add(connectHostBtn);
		
		btnCancel = new CustomButton("CANCEL", new Color(202,0,0),new Color(255,255,255), 40, null);
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnCancel.setBorderPainted(false);
		btnCancel.setBackground(new Color(255, 0, 0));
		btnCancel.setBounds(307, 420, 158, 57);
		connectToHostPanel.add(btnCancel);
		
		// Lock Screen
//		w = new JFrame();
//		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
//		//w.setUndecorated(true);
//		w.setSize((int)scSize.getWidth(),(int)scSize.getHeight());
//		w.setLocationRelativeTo(null);
//		
//		lsCardLayout = new CardLayout(0,0);
//		lsContainer = w.getContentPane();
//		lsContainer.setLayout(lsCardLayout);
//		
//		// Lock Panel
//		lockPanel = new JPanel();
//		lsContainer.add(lockPanel,"lockPanel");
//		lockPanel.setLayout(new GridLayout(0,1,0,0));
//		
//		JPanel panel1 = new JPanel();
//		JPanel panel2 = new JPanel();
//		panel1.setLayout(new BorderLayout());
//		lockPanel.add(panel1);
//		lockPanel.add(panel2);
//		
//		windowLockLabel = new JLabel("Your PC is locked.");
//		windowLockLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		windowLockLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
//		panel1.add(windowLockLabel, BorderLayout.SOUTH);
//		
		recoveryBtn = new JButton("Recovery Mode");
		recoveryBtn.setPreferredSize(new Dimension(150,40));
//		panel2.add(recoveryBtn);
//		
//		// Recovery Mode Panel
//		GridBagLayout gbl = new GridBagLayout();
//		recoveryPanel = new JPanel();
//		lsContainer.add(recoveryPanel,"recoveryPanel");
//		recoveryPanel.setLayout(gbl);
//		
//		GridBagConstraints gbc = new GridBagConstraints();
//		
		recoveryLabel = new JLabel("Recovery Mode");
		recoveryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		recoveryLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
//		gbc.ipady = 10;
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.gridwidth = 5;
//		recoveryPanel.add(recoveryLabel,gbc);
//		
		enterPasscodeLbl = new JLabel("Enter Unique Passcode");
		enterPasscodeLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
//		gbc = new GridBagConstraints();
//		gbc.insets = new Insets(20,200,0,20);
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		recoveryPanel.add(enterPasscodeLbl,gbc);
//		
		uniquePasscodeInput = new JTextField();
		uniquePasscodeInput.setColumns(10);
//		gbc.insets = new Insets(20,-150,0,100);
//		gbc.ipadx = 300;
//		gbc.ipady = 15;
//		gbc.gridx = 2;
//		gbc.gridy = 1;
//		gbc.gridwidth = 3;
//		recoveryPanel.add(uniquePasscodeInput,gbc);
//		
		cancelRecoveryBtn = new JButton("CANCEL");
		recoverBtn = new JButton("RECOVER");
//		
//		gbc = new GridBagConstraints();
//		gbc.insets = new Insets(20,120,0,0);
//		gbc.ipadx = 10;
//		gbc.ipady = 10;
//		gbc.weightx = 0.0;
//		gbc.gridx = 3;
//		gbc.gridy = 3;
//		recoveryPanel.add(cancelRecoveryBtn,gbc);
//		
//		gbc.insets = new Insets(20,10,0,400);
//		gbc.gridx = 4;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		recoveryPanel.add(recoverBtn,gbc);
		
	}
}

class CustomButton extends JButton{
	
	int cornerRadius;
	ImageIcon icon;
	
	CustomButton(String text, Color bgcolor, Color fgcolor, int cornerRadius, ImageIcon icon){
		super(text);
		
		this.cornerRadius = cornerRadius;
        this.icon = icon;
		
        setOpaque(false); // Make the button transparent
        setContentAreaFilled(true); // Don't paint the content area
        setBorderPainted(false); // Don't paint the border
        setForeground(fgcolor); // Set text color
        setBackground(bgcolor); // Set background color
        setFocusPainted(false); // Don't paint focus state
        
        addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				setBackground(new Color(255,219,88));
				setForeground(new Color(235,235,235));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				setBackground(bgcolor);
				setForeground(fgcolor);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(Color.white);
				setForeground(Color.black);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(bgcolor);
				setForeground(fgcolor);
			}
        	
        });
	}
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the rounded rectangle
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        // Draw the text centered on the button
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setColor(getForeground());
        g2d.drawString(getText(), x, y);
        
        g2d.dispose();
        
     // Draw the icon centered on the button
        if (icon != null) {
            int iconX = (getWidth() - icon.getIconWidth()) / 2;
            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2d, iconX, iconY);
        }
	}
}