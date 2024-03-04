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
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.CardLayout;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class MainApp extends JFrame {

	public JPanel titleBarPanel,customTitleBar,mainPanel;
	
	public JPanel contentPane;
	
	public JPanel createHostPanel;
	public JLabel titleLabel;
	public JLabel dialog1;
	public JButton createBtn;
	
	public JFrame encSetupFrame;
	public JPanel encryptionSetupPanel;
	public JList filepathList;
	public JButton encSetupBtn,addBtn,removeBtn,removeAllBtn,okBtn;
	public DefaultListModel<String> listModel;
	
	public JPanel authPanel;
	public JLabel titleLabel_1;
	public JLabel hostnameLabel;
	public JLabel hostIPLabel;
	public JLabel hostPortLbl;
	public JLabel clientConnectStatus;
	public JLabel generatedPasscode;
	public JButton cancelHostConnection;
	
	public JPanel connectToHostPanel;
	public JButton createBtn2;
	public JTextField passcodeInput;
	public JButton connectHostBtn;
	public JButton btnCancel;
	
	public JPanel connectStatusPanel;
	public JLabel hostIPHostName;
	public JButton dcButton;
	
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
	
	public JTextField uniquePasscodeInput;
	
	public JButton btnHotspot;
	
	public int mouseX,mouseY;
	private JLabel connectedToLbl;
	
	
	/**
	 * Create the frame.
	 */
	
	public MainApp() {
		
		setTitle("Network Security System");
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
	    
	    
	    // Create Host Panel
		createHostPanel = new JPanel();
		createHostPanel.setBackground(new Color(15, 15, 15));
		container.add(createHostPanel, "hostPanel");
		createHostPanel.setLayout(null);

		titleLabel = new JLabel("Network Security System (Host)");
		titleLabel.setForeground(new Color(255, 255, 255));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(10, 156, 564, 37);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
		createHostPanel.add(titleLabel);

		dialog1 = new JLabel("Connect via Wi-Fi");
		dialog1.setForeground(new Color(255, 255, 255));
		dialog1.setHorizontalAlignment(SwingConstants.CENTER);
		dialog1.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog1.setBounds(72, 281, 208, 37);
		createHostPanel.add(dialog1);

		createBtn = new JButton("Wi-Fi");
		createBtn.setForeground(new Color(255, 255, 255));
		createBtn.setBorderPainted(false);
		createBtn.setBackground(new Color(0, 202, 0));
		createBtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		createBtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		createBtn.setFocusable(false);
		createBtn.setBounds(93, 329, 167, 53);
		createHostPanel.add(createBtn);
		
		JLabel dialog2 = new JLabel("Connect via Hotspot");
		dialog2.setHorizontalAlignment(SwingConstants.CENTER);
		dialog2.setForeground(Color.WHITE);
		dialog2.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog2.setBounds(290, 281, 208, 37);
		createHostPanel.add(dialog2);
		
		btnHotspot = new JButton("Hotspot");
		btnHotspot.setForeground(Color.WHITE);
		btnHotspot.setFont(new Font("Tahoma", Font.BOLD, 25));
		btnHotspot.setFocusable(false);
		btnHotspot.setBorderPainted(false);
		btnHotspot.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnHotspot.setBackground(new Color(0, 202, 0));
		btnHotspot.setBounds(311, 329, 167, 53);
		createHostPanel.add(btnHotspot);
		
		// Encryption Setup Panel
		encSetupFrame = new JFrame();
		encSetupFrame.setTitle("Encryption Setup");
		encSetupFrame.setSize(700,400);
		encSetupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		encSetupFrame.setLocationRelativeTo(null);
		
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
		
		addBtn = new JButton("ADD");
		addBtn.setBounds(258, 306, 89, 23);
		encryptionSetupPanel.add(addBtn);
		
		removeBtn = new JButton("REMOVE");
		removeBtn.setBounds(357, 306, 89, 23);
		encryptionSetupPanel.add(removeBtn);
		
		removeAllBtn = new JButton("REMOVE ALL");
		removeAllBtn.setBounds(456, 306, 107, 23);
		encryptionSetupPanel.add(removeAllBtn);
		
		okBtn = new JButton("OK");
		okBtn.setBounds(573, 306, 89, 23);
		encryptionSetupPanel.add(okBtn);
		
		// Authentication Panel
		authPanel = new JPanel();
		authPanel.setFocusable(false);
		authPanel.setBackground(new Color(15, 15, 15));
		container.add(authPanel, "authPanel");
		authPanel.setLayout(null);

		titleLabel_1 = new JLabel("PC-Smartphone Connection (Wi-Fi)");
		titleLabel_1.setForeground(new Color(255, 255, 255));
		titleLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		titleLabel_1.setBounds(10, 122, 564, 37);
		authPanel.add(titleLabel_1);

		JLabel lbl_hostname = new JLabel("Host Name:");
		lbl_hostname.setForeground(new Color(255, 255, 255));
		lbl_hostname.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_hostname.setFont(new Font("Tahoma", Font.BOLD, 20));
		lbl_hostname.setBounds(54, 194, 117, 37);
		authPanel.add(lbl_hostname);

		hostnameLabel = new JLabel("Encode <Hostname> in this label.");
		hostnameLabel.setForeground(new Color(255, 255, 255));
		hostnameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		hostnameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostnameLabel.setBounds(181, 196, 318, 37);
		authPanel.add(hostnameLabel);

		clientConnectStatus = new JLabel("Status: Encode status here...");
		clientConnectStatus.setForeground(new Color(255, 255, 255));
		clientConnectStatus.setHorizontalAlignment(SwingConstants.LEFT);
		clientConnectStatus.setFont(new Font("Tahoma", Font.BOLD, 20));
		clientConnectStatus.setBounds(54, 402, 496, 37);
		authPanel.add(clientConnectStatus);

		JLabel lblPasscode = new JLabel("Passcode: ");
		lblPasscode.setForeground(new Color(255, 255, 255));
		lblPasscode.setHorizontalAlignment(SwingConstants.LEFT);
		lblPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblPasscode.setBounds(54, 285, 117, 37);
		authPanel.add(lblPasscode);

		generatedPasscode = new JLabel("Encode passcode here");
		generatedPasscode.setForeground(new Color(255, 255, 255));
		generatedPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		generatedPasscode.setBounds(180, 285, 241, 37);
		authPanel.add(generatedPasscode);
		
		cancelHostConnection = new JButton("CANCEL");
		cancelHostConnection.setForeground(new Color(255, 255, 255));
		cancelHostConnection.setBackground(new Color(255, 0, 0));
		cancelHostConnection.setBorderPainted(false);
		cancelHostConnection.setFont(new Font("Tahoma", Font.BOLD, 25));
		cancelHostConnection.setBounds(202, 461, 167, 53);
		authPanel.add(cancelHostConnection);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setForeground(new Color(255, 255, 255));
		lblPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblPort.setBounds(54, 333, 117, 37);
		authPanel.add(lblPort);
		
		hostPortLbl = new JLabel("Encode port here");
		hostPortLbl.setForeground(new Color(255, 255, 255));
		hostPortLbl.setHorizontalAlignment(SwingConstants.LEFT);
		hostPortLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
		hostPortLbl.setBounds(181, 333, 219, 37);
		authPanel.add(hostPortLbl);
		
		JLabel hostIPLbl = new JLabel("Host IP:");
		hostIPLbl.setForeground(new Color(255, 255, 255));
		hostIPLbl.setHorizontalAlignment(SwingConstants.LEFT);
		hostIPLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
		hostIPLbl.setBounds(54, 237, 117, 37);
		authPanel.add(hostIPLbl);
		
		hostIPLabel = new JLabel("Encode <IPAdress> in this label.");
		hostIPLabel.setForeground(new Color(255, 255, 255));
		hostIPLabel.setHorizontalAlignment(SwingConstants.LEFT);
		hostIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostIPLabel.setBounds(181, 237, 318, 37);
		authPanel.add(hostIPLabel);
		
		encSetupBtn = new JButton("encSetup");
		encSetupBtn.setBounds(28, 75, 89, 23);
		
		authPanel.add(encSetupBtn);
		
		// Connection Status Panel
		connectStatusPanel = new JPanel();
		connectStatusPanel.setBackground(new Color(15, 15, 15));
		container.add(connectStatusPanel, "connectStatusPanel");
		connectStatusPanel.setLayout(null);

		JLabel titleLblConnected = new JLabel("Your PC is now connected.");
		titleLblConnected.setForeground(new Color(255, 255, 255));
		titleLblConnected.setBounds(81, 190, 444, 25);
		titleLblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		titleLblConnected.setFont(new Font("Tahoma", Font.BOLD, 30));
		connectStatusPanel.add(titleLblConnected);

		hostIPHostName = new JLabel("Encode Host IP/Hostname here");
		hostIPHostName.setForeground(new Color(255, 255, 255));
		hostIPHostName.setHorizontalAlignment(SwingConstants.LEFT);
		hostIPHostName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostIPHostName.setBounds(313, 340, 248, 25);
		connectStatusPanel.add(hostIPHostName);
		
		dcButton = new JButton("DISCONNECT");
		dcButton.setFocusable(false);
		dcButton.setForeground(new Color(255, 255, 255));
		dcButton.setBackground(new Color(255, 0, 0));
		dcButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		dcButton.setBorderPainted(false);
		dcButton.setBounds(190, 395, 201, 57);
		connectStatusPanel.add(dcButton);
		
		connectedToLbl = new JLabel("You are now connected to this device: ");
		connectedToLbl.setForeground(new Color(255, 255, 255));
		connectedToLbl.setHorizontalAlignment(SwingConstants.LEFT);
		connectedToLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		connectedToLbl.setBounds(57, 340, 256, 25);
		connectStatusPanel.add(connectedToLbl);
		
		connectToHostPanel = new JPanel();
		connectToHostPanel.setBackground(new Color(15, 15, 15));
		container.add(connectToHostPanel, "connectToHostPanel");
		connectToHostPanel.setLayout(null);
		
		JLabel inputIPLabel = new JLabel("<HTML>Make sure you are connected to your mobile hotspot's network in order for this session to work.<HTML> ");
		inputIPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inputIPLabel.setForeground(new Color(255, 255, 255));
		inputIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		inputIPLabel.setBounds(153, 243, 285, 89);
		connectToHostPanel.add(inputIPLabel);
		
		JLabel inputPasscodeLabel = new JLabel("Passcode");
		inputPasscodeLabel.setForeground(new Color(255, 255, 255));
		inputPasscodeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		inputPasscodeLabel.setBounds(139, 343, 103, 37);
		connectToHostPanel.add(inputPasscodeLabel);
		
		JLabel connectToHostTitle = new JLabel("Enter Client Details");
		connectToHostTitle.setForeground(new Color(255, 255, 255));
		connectToHostTitle.setFont(new Font("Tahoma", Font.BOLD, 27));
		connectToHostTitle.setBounds(157, 167, 308, 37);
		connectToHostPanel.add(connectToHostTitle);
		
		passcodeInput = new JTextField();
		passcodeInput.setCaretColor(new Color(255, 255, 255));
		passcodeInput.setBorder(new LineBorder(new Color(171, 173, 179), 1, true));
		passcodeInput.setBackground(new Color(15, 15, 15));
		passcodeInput.setForeground(new Color(255, 255, 255));
		passcodeInput.setColumns(10);
		passcodeInput.setBounds(247, 343, 211, 37);
		connectToHostPanel.add(passcodeInput);
		
		connectHostBtn = new JButton("CONNECT");
		connectHostBtn.setForeground(new Color(255, 255, 255));
		connectHostBtn.setBorderPainted(false);
		connectHostBtn.setBackground(new Color(0, 202, 0));
		connectHostBtn.setFont(new Font("Tahoma", Font.BOLD, 20));
		connectHostBtn.setBounds(130, 420, 158, 57);
		connectToHostPanel.add(connectHostBtn);
		
		btnCancel = new JButton("CANCEL");
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnCancel.setBorderPainted(false);
		btnCancel.setBackground(new Color(255, 0, 0));
		btnCancel.setBounds(307, 420, 158, 57);
		connectToHostPanel.add(btnCancel);
		
		// Lock Screen
		w = new JFrame();
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		w.setUndecorated(true);
		w.setSize((int)scSize.getWidth(),(int)scSize.getHeight());
		w.setLocationRelativeTo(null);
		
		lsCardLayout = new CardLayout(0,0);
		lsContainer = w.getContentPane();
		lsContainer.setLayout(lsCardLayout);
		
		lockPanel = new JPanel();
		lsContainer.add(lockPanel,"lockPanel");
		lockPanel.setLayout(null);
		
		windowLockLabel = new JLabel("Your PC is locked.");
		windowLockLabel.setBounds(541, 312, 439, 70);
		windowLockLabel.setHorizontalAlignment(SwingConstants.CENTER);
		windowLockLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		lockPanel.add(windowLockLabel);
		
		recoveryBtn = new JButton("Recovery Mode");
		recoveryBtn.setBounds(688,392,146,45);
		lockPanel.add(recoveryBtn);
		
		recoveryPanel = new JPanel();
		lsContainer.add(recoveryPanel,"recoveryPanel");
		recoveryPanel.setLayout(null);
		
		recoveryLabel = new JLabel("Recovery Mode");
		recoveryLabel.setBounds(541, 312, 439, 70);
		recoveryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		recoveryLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		recoveryPanel.add(recoveryLabel);
		
		enterPasscodeLbl = new JLabel("Enter Unique Passcode");
		enterPasscodeLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		enterPasscodeLbl.setBounds(442, 405, 159, 27);
		recoveryPanel.add(enterPasscodeLbl);
		
		inputStatusLbl = new JLabel("Enter input status here.");
		inputStatusLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		inputStatusLbl.setBounds(634, 532, 159, 27);
		recoveryPanel.add(inputStatusLbl);
		
		uniquePasscodeInput = new JTextField();
		uniquePasscodeInput.setBounds(634, 395, 416, 37);
		uniquePasscodeInput.setColumns(10);
		recoveryPanel.add(uniquePasscodeInput);
		
		cancelRecoveryBtn = new JButton("CANCEL");
		cancelRecoveryBtn.setBounds(634, 465, 117, 37);
		recoveryPanel.add(cancelRecoveryBtn);
		
		recoverBtn = new JButton("RECOVER");
		recoverBtn.setBounds(785,465,117,37);
		recoveryPanel.add(recoverBtn);
		
	}
}