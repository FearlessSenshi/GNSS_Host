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
	
	public JPanel connectStatusPanel;
	public JLabel hostIPHostName;
	public JButton dcButton;
	
	public CardLayout cardLayout;
	public Container container;
	
	public boolean clientConnected = false;
	public JLabel securityStatusLabel;
	public JLabel controlModeLabel;
	
	public JWindow w;
	public JLabel windowLockLabel;
	
	/**
	 * Create the frame.
	 */
	
	public MainApp() {
		
		// Layout for the container
		cardLayout = new CardLayout(0, 0);

		// Container for all panels (uses CardLayout)
		container = getContentPane();
		container.setLayout(cardLayout);

		setTitle("GeoNetwork Security System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		container.setBackground(new Color(128, 255, 128));
		container.setBounds(0, 0, 584, 561);
		// contentPane.add(container);
		container.setLayout(cardLayout);

		createHostPanel = new JPanel();
		createHostPanel.setBackground(new Color(240, 240, 240));
		container.add(createHostPanel, "hostPanel");
		createHostPanel.setLayout(null);

		titleLabel = new JLabel("GeoNetwork Security System (Host)");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(10, 122, 564, 37);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		createHostPanel.add(titleLabel);

		dialog1 = new JLabel("Connect via Wi-Fi");
		dialog1.setHorizontalAlignment(SwingConstants.CENTER);
		dialog1.setFont(new Font("Tahoma", Font.BOLD, 20));
		dialog1.setBounds(48, 264, 208, 37);
		createHostPanel.add(dialog1);

		createBtn = new JButton("Wi-Fi");
		createBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		createBtn.setBorderPainted(false);
		createBtn.setBackground(new Color(255, 128, 128));
		createBtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		createBtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		createBtn.setFocusable(false);
		createBtn.setBounds(69, 312, 167, 53);
		createHostPanel.add(createBtn);
		
		JLabel lblConnectViaHotspot = new JLabel("Connect via Hotspot");
		lblConnectViaHotspot.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnectViaHotspot.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblConnectViaHotspot.setBounds(313, 264, 220, 37);
		createHostPanel.add(lblConnectViaHotspot);
		
		JButton createBtn2 = new JButton("Hotspot");
		createBtn2.setFont(new Font("Tahoma", Font.BOLD, 25));
		createBtn2.setFocusable(false);
		createBtn2.setBorderPainted(false);
		createBtn2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		createBtn2.setBackground(new Color(128, 128, 255));
		createBtn2.setBounds(333, 312, 167, 53);
		createHostPanel.add(createBtn2);

		authPanel = new JPanel();
		authPanel.setFocusable(false);
		authPanel.setBackground(new Color(128, 255, 255));
		container.add(authPanel, "authPanel");
		authPanel.setLayout(null);

		titleLabel_1 = new JLabel("GeoNetwork Security System (Host)");
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
		
		w = new JWindow();
		w.getContentPane().setLayout(new BorderLayout());
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		//w.setSize((int)scSize.getWidth(),(int)scSize.getHeight());
		w.setSize(100,100);
		w.setLocationRelativeTo(null);
		
		windowLockLabel = new JLabel("Your PC is locked.");
		windowLockLabel.setHorizontalAlignment(SwingConstants.CENTER);
		windowLockLabel.setFont(new Font("Tahoma", Font.BOLD, 50));
		w.getContentPane().add(windowLockLabel, BorderLayout.CENTER);
	}
}