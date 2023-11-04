import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.CardLayout;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

public class MainApp extends JFrame {

	public JPanel contentPane;
	
	public JPanel createHostPanel;
	public JLabel titleLabel;
	public JLabel dialog1;
	public JButton createBtn;
	
	public JPanel authPanel;
	public JLabel titleLabel_1;
	public JLabel hostIPLabel;
	public JLabel clientConnectStatus;
	public JLabel generatedPasscode;
	public JButton cancelHostConnection;
	
	public JPanel connectToHostPanel;
	public JButton createBtn2;
	public JTextField ipTextInput;
	public JTextField portInput;
	public JTextField passcodeInput;
	public JButton connectHostBtn;
	public JButton btnCancel;
	
	public JPanel connectStatusPanel;
	public JLabel hostIPHostName;
	public JButton dcButton;
	
	public CardLayout cardLayout;
	public Container container;
	
	public boolean clientConnected = false;
	public JLabel securityStatusLabel;
	public JLabel controlModeLabel;
	
	public CardLayout lsCardLayout;
	public Container lsContainer;
	
	public JWindow w;
	
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
	
	/**
	 * Create the frame.
	 */
	
	public MainApp() {
		
		// Layout for the container
		cardLayout = new CardLayout(0, 0);

		// Container for all panels (uses CardLayout)
		container = getContentPane();
		container.setLayout(cardLayout);

		setTitle("Network Security System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		container.setBackground(new Color(128, 255, 128));
		container.setBounds(0, 0, 584, 561);

		createHostPanel = new JPanel();
		createHostPanel.setBackground(new Color(240, 240, 240));
		container.add(createHostPanel, "hostPanel");
		createHostPanel.setLayout(null);

		titleLabel = new JLabel("Network Security System (Host)");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(10, 122, 564, 37);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		createHostPanel.add(titleLabel);

		dialog1 = new JLabel("Connect via Wi-Fi");
		dialog1.setHorizontalAlignment(SwingConstants.CENTER);
		dialog1.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog1.setBounds(178, 265, 208, 37);
		createHostPanel.add(dialog1);

		createBtn = new JButton("Wi-Fi");
		createBtn.setBorderPainted(false);
		createBtn.setBackground(new Color(255, 128, 128));
		createBtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		createBtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		createBtn.setFocusable(false);
		createBtn.setBounds(199, 313, 167, 53);
		createHostPanel.add(createBtn);
		
//		JLabel lblConnectViaHotspot = new JLabel("Connect via Hotspot");
//		lblConnectViaHotspot.setHorizontalAlignment(SwingConstants.CENTER);
//		lblConnectViaHotspot.setFont(new Font("Tahoma", Font.BOLD, 20));
//		lblConnectViaHotspot.setBounds(313, 264, 220, 37);
//		createHostPanel.add(lblConnectViaHotspot);
//		
//		createBtn2 = new JButton("Hotspot");
//		createBtn2.setFont(new Font("Tahoma", Font.BOLD, 25));
//		createBtn2.setFocusable(false);
//		createBtn2.setBorderPainted(false);
//		createBtn2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
//		createBtn2.setBackground(new Color(128, 128, 255));
//		createBtn2.setBounds(333, 312, 167, 53);
//		createHostPanel.add(createBtn2);

		authPanel = new JPanel();
		authPanel.setFocusable(false);
		authPanel.setBackground(new Color(128, 255, 255));
		container.add(authPanel, "authPanel");
		authPanel.setLayout(null);

		titleLabel_1 = new JLabel("Network Security System (Host)");
		titleLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		titleLabel_1.setBounds(10, 80, 564, 37);
		authPanel.add(titleLabel_1);

		JLabel lblHostConnectionCreated = new JLabel("Host Connection Created.");
		lblHostConnectionCreated.setHorizontalAlignment(SwingConstants.CENTER);
		lblHostConnectionCreated.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblHostConnectionCreated.setBounds(10, 128, 564, 37);
		authPanel.add(lblHostConnectionCreated);

		hostIPLabel = new JLabel("Encode (IP Address + Host Name) in this label.");
		hostIPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		hostIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		hostIPLabel.setBounds(10, 154, 564, 37);
		authPanel.add(hostIPLabel);

		clientConnectStatus = new JLabel("Status: Encode status here...");
		clientConnectStatus.setHorizontalAlignment(SwingConstants.CENTER);
		clientConnectStatus.setFont(new Font("Tahoma", Font.BOLD, 20));
		clientConnectStatus.setBounds(10, 218, 564, 37);
		authPanel.add(clientConnectStatus);

		JLabel lblInYourClient = new JLabel("In your client app,");
		lblInYourClient.setHorizontalAlignment(SwingConstants.CENTER);
		lblInYourClient.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblInYourClient.setBounds(10, 301, 564, 26);
		authPanel.add(lblInYourClient);

		JLabel lblPleaseTypeThe = new JLabel("please type the passcode to verify connection to this PC");
		lblPleaseTypeThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseTypeThe.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPleaseTypeThe.setBounds(10, 324, 564, 26);
		authPanel.add(lblPleaseTypeThe);

		JLabel lblPasscode = new JLabel("Passcode: ");
		lblPasscode.setHorizontalAlignment(SwingConstants.CENTER);
		lblPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblPasscode.setBounds(180, 349, 138, 37);
		authPanel.add(lblPasscode);

		generatedPasscode = new JLabel("Encode passcode here");
		generatedPasscode.setFont(new Font("Tahoma", Font.BOLD, 20));
		generatedPasscode.setBounds(302, 349, 241, 37);
		authPanel.add(generatedPasscode);
		
		cancelHostConnection = new JButton("CANCEL");
		cancelHostConnection.setBackground(new Color(255, 128, 128));
		cancelHostConnection.setBorderPainted(false);
		cancelHostConnection.setFont(new Font("Tahoma", Font.BOLD, 25));
		cancelHostConnection.setBounds(201, 412, 167, 53);
		authPanel.add(cancelHostConnection);

		connectStatusPanel = new JPanel();
		connectStatusPanel.setBackground(new Color(255, 255, 128));
		container.add(connectStatusPanel, "connectStatusPanel");
		connectStatusPanel.setLayout(null);

		JLabel lblConnected = new JLabel("Connected.");
		lblConnected.setBounds(10, 96, 564, 25);
		lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnected.setFont(new Font("Tahoma", Font.BOLD, 20));
		connectStatusPanel.add(lblConnected);

		hostIPHostName = new JLabel("Encode Host IP/Hostname here");
		hostIPHostName.setHorizontalAlignment(SwingConstants.CENTER);
		hostIPHostName.setFont(new Font("Tahoma", Font.PLAIN, 20));
		hostIPHostName.setBounds(10, 132, 564, 25);
		connectStatusPanel.add(hostIPHostName);
		
		JLabel disconnectFromClient = new JLabel("Disconnect from client");
		disconnectFromClient.setHorizontalAlignment(SwingConstants.CENTER);
		disconnectFromClient.setFont(new Font("Tahoma", Font.PLAIN, 20));
		disconnectFromClient.setBounds(10, 359, 564, 25);
		connectStatusPanel.add(disconnectFromClient);
		
		dcButton = new JButton("DISCONNECT");
		dcButton.setFocusable(false);
		dcButton.setForeground(new Color(255, 255, 255));
		dcButton.setBackground(new Color(255, 0, 0));
		dcButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		dcButton.setBorderPainted(false);
		dcButton.setBounds(190, 395, 201, 57);
		connectStatusPanel.add(dcButton);
		
		securityStatusLabel = new JLabel("Security Status: Unlocked");
		securityStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		securityStatusLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		securityStatusLabel.setBounds(10, 226, 564, 25);
		connectStatusPanel.add(securityStatusLabel);
		
		controlModeLabel = new JLabel("Control Mode: Manual");
		controlModeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlModeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		controlModeLabel.setBounds(10, 262, 564, 25);
		connectStatusPanel.add(controlModeLabel);
		
		connectToHostPanel = new JPanel();
		connectToHostPanel.setBackground(new Color(255, 0, 255));
		container.add(connectToHostPanel, "connectToHostPanel");
		connectToHostPanel.setLayout(null);
		
		JLabel inputIPLabel = new JLabel("IP");
		inputIPLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		inputIPLabel.setBounds(131, 185, 128, 37);
		connectToHostPanel.add(inputIPLabel);
		
		JLabel inputPortLabel = new JLabel("Port");
		inputPortLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		inputPortLabel.setBounds(131, 233, 96, 25);
		connectToHostPanel.add(inputPortLabel);
		
		JLabel inputPasscodeLabel = new JLabel("Passcode");
		inputPasscodeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		inputPasscodeLabel.setBounds(131, 269, 128, 37);
		connectToHostPanel.add(inputPasscodeLabel);
		
		JLabel connectToHostTitle = new JLabel("Connect to Host");
		connectToHostTitle.setFont(new Font("Tahoma", Font.BOLD, 27));
		connectToHostTitle.setBounds(131, 93, 232, 37);
		connectToHostPanel.add(connectToHostTitle);
		
		ipTextInput = new JTextField();
		ipTextInput.setBounds(277, 197, 189, 20);
		connectToHostPanel.add(ipTextInput);
		ipTextInput.setColumns(10);
		
		portInput = new JTextField();
		portInput.setColumns(10);
		portInput.setBounds(277, 239, 189, 20);
		connectToHostPanel.add(portInput);
		
		passcodeInput = new JTextField();
		passcodeInput.setColumns(10);
		passcodeInput.setBounds(277, 281, 189, 20);
		connectToHostPanel.add(passcodeInput);
		
		connectHostBtn = new JButton("CONNECT");
		connectHostBtn.setBorderPainted(false);
		connectHostBtn.setBackground(new Color(128, 128, 192));
		connectHostBtn.setFont(new Font("Tahoma", Font.BOLD, 20));
		connectHostBtn.setBounds(131, 346, 158, 57);
		connectToHostPanel.add(connectHostBtn);
		
		btnCancel = new JButton("CANCEL");
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnCancel.setBorderPainted(false);
		btnCancel.setBackground(new Color(255, 0, 0));
		btnCancel.setBounds(308, 346, 158, 57);
		connectToHostPanel.add(btnCancel);
		
		
		
		w = new JWindow();
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
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