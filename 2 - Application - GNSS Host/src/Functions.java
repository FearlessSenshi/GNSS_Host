import java.awt.Dimension;
import java.awt.RenderingHints.Key;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

import javax.swing.JOptionPane;

// Committed by: SENSHI ;) 10-18-2023 @2115

public class Functions implements Runnable{
	MainApp gui;
	Thread t1;
	Thread t2;
	Thread t3;
	Thread t4;
	
	// Host Variables
	public ServerSocket ss;
	public Socket cs;
	public int serverPort;
	
	public int hostPasscode;
	public String hostIP;
	public String hostName;
	public final int MIN_PORT_RANGE = 1;
	public final int MAX_PORT_RANGE = 65536;
	public boolean unlocked = true;
	public boolean autoControl = false;
	public boolean stopNetChk = false;
	public boolean authFailed = false;
	
	// Client Variables
	public String clientIP;
	public String clientName;
	public boolean clientVerified = false;
	private int attempts = 3;
	
	// Get Output
	private InputStream is;
	private BufferedReader br;
	
	// Send Input
	private OutputStream out;
	private PrintWriter pw;
	
	
	
	
	// Constructor - Assigning buttons with functions
	
	public Functions() {
		gui = new MainApp();
		
		gui.createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					createHost();
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				showAuthPanel();
			}
		});
		
//		gui.createBtn2.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				connectToHost();
//			}
//		});
		
		gui.connectHostBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String ip = gui.ipTextInput.getText();
				int port = Integer.parseInt(gui.portInput.getText());
				int passcode = Integer.parseInt(gui.passcodeInput.getText());
				joinHost(ip, port, passcode);
			}
		});
		
		gui.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.cardLayout.show(gui.container, "hostPanel");
			}
			
		});
		
		gui.dcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(clientVerified) {
					try {
						disconnect();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					System.out.println("[!] No access.");
				}
			}
		});
		
		gui.cancelHostConnection.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cancelHostConnection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
	}
	
	// MAIN - Runs the application from GUI
	public static void main(String[] args) {
		Functions f = new Functions();
		f.gui.setVisible(true);
	}
	
	@Override
	public void run() {
		
		// AUTHENTICATION - When authentication between host and client didn't fail, continue
		while(!authFailed) {
			try {
				
				// Creates a host connection session if the client is NOT verified by the PC
				if(!clientVerified) {
					attempts = 3;
					clientVerified = false;
					serverPort = generatePort();
					
			        while(true) {
						ss = new ServerSocket(serverPort);
						System.out.println("Waiting for client connection...");
						gui.clientConnectStatus.setText("Status: Waiting for client connection...");
				        cs = ss.accept();
				        if(cs.isConnected()) {
				        	is = cs.getInputStream();
				    		br = new BufferedReader(new InputStreamReader(is));
				    		out = cs.getOutputStream();
				    		pw = new PrintWriter(out,true);
				    		
				        }
			        	// Get Passcode
			    		String output = "";
			    		String senderAddress;
			    		try {
			    			
							// Check if client is connected
							if (cs.isConnected()) {
								gui.clientConnectStatus.setText("Status: Awaiting client verification...");
								
								// Waits the client's response for the passcode
								output = br.readLine();
								System.out.println(output);
								
								// Verifies client connection and opens access control
								if (output.equals("connectVerify" + hostPasscode)) {
									System.out.println("Client Verified!");
									gui.cardLayout.show(gui.container, "connectStatusPanel");
									gui.hostIPHostName.setText(hostIP + " - " + "Port: " + serverPort + " - " + hostName);
									pw.println("gnssVerified" + hostPasscode);
									clientVerified = true;

									InetAddress ia = cs.getInetAddress();
									System.out.println("Client Local IP Address: " + ia.getHostAddress());
									runInputListener(br);
									break;
								} 
								
								// When client inputs wrong passcode, decrease attempt by -1
								else {
									System.out.println("Incorrect Passcode! ");
									if(attempts > 0) {
										// Sends out an input saying that the given passcode was incorrect
										pw.println("incorrectPasscode");
										if(cs.isConnected()) System.out.println("Client is still connected.");
										attempts--;
										cs.close();
										ss.close();
										is.close();
										out.close();
										pw.close();
										br.close();
										System.out.println(attempts);
								}
									
								// Disconnect connecting client if attempt ran out to 0
								else {
										pw.println("accessDenied");
										cs.close();
										ss.close();
										is.close();
										out.close();
										pw.close();
										br.close();
										
										// Shows the home panel
										gui.cardLayout.show(gui.container, "hostPanel");
										authFailed = true;
										break;
								}
							}
						}
							
							// if client is not connected...
							else {
								runOutputSender(pw, "gnssHostDisconnected");
							}
						}
			    		
			    		// Disconnect the socket and all streams
						catch (IOException e) {
							e.printStackTrace();
							cs.close();
							ss.close();
							is.close();
							out.close();
							br.close();
							pw.close();
							break;
						}
			        }
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	// Opens a panel for CREATING a Host Connection
		void showAuthPanel() {
			gui.cardLayout.show(gui.container, "authPanel");
			gui.clientConnectStatus.setText("Status: Waiting for client connection...");
			gui.hostIPLabel.setText(hostIP + " - " + "Port: " + serverPort + " - " + hostName);
			gui.generatedPasscode.setText(String.valueOf(generatePasscode()));
		}
	
	// Gets this PC's private IP Address
	void createHost() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostIP = inetAddress.getLocalHost().getHostAddress();
		hostName = inetAddress.getLocalHost().getHostName();
		gui.clientConnectStatus.setText("Status: Waiting client connection...");
		authFailed = false;
		t1 = new Thread(this);
		t1.start();
	}
	
	// Creates a Socket Connection in this PC
		void joinHost(String ip, int port, int passcode) {
			try {
				System.out.println("Connecting to hotspot...");
				Socket s = new Socket(ip,port);
				System.out.println("Connected!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	// Opens a panel for JOINING a Host Connection
	void connectToHost() {
		gui.cardLayout.show(gui.container, "connectToHostPanel");
	}
	
	// Generates a random port
	private int generatePort() {
		// return (int)(Math.random()*(MAX_PORT_RANGE-MIN_PORT_RANGE))+MIN_PORT_RANGE;
		return 1;
	}
	
	// Generates a random passcode
	private int generatePasscode() {
		hostPasscode = (int)(Math.random()*(100000-10000))+10000;
		return hostPasscode;
	}
	
	// Runs the InputListener
	public void runInputListener(BufferedReader br) {
		// InputStream
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						String output = br.readLine();
						if(clientVerified) {
							command(output);
							System.out.println(output);
						}
						
					} catch (IOException e) {
						try {
							br.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
						break;
					}
				}
			}
		};
		
		t2 = new Thread(runnable);
		t2.start();
	}
	
	public void runOutputSender(PrintWriter pw, String message) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				pw.println(message);
			}
			
		};
		t3 = new Thread(runnable);
		t3.start();
	}
	
	public void checkNetworkConnection() {
		// Checks network connection if available or not.
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						InetAddress ia = InetAddress.getByName("www.google.com");
						if(!ia.isReachable(1000)){
							System.out.println("Unreachable");
							gui.clientConnectStatus.setText("Status: Network lost. Retrying connection...");
						}
						else {
							System.out.println("Connected");
							gui.clientConnectStatus.setText("Status: Waiting for client connection...");
						}
						Thread.sleep(1000);
					} catch(IOException e) {
						System.out.println("No internet connection." + e);
						gui.cardLayout.show(gui.container, "hostPanel");
						JOptionPane.showMessageDialog(null, "Network Error", "No internet connection",JOptionPane.WARNING_MESSAGE);
						try {
							cancelHostConnection();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					} catch (InterruptedException e1) {
						System.out.println("Thread killed or program stopped.");
						break;
					}
				}
			}
		};
		
		t4 = new Thread(runnable);
		t4.start();
	}
	
	public void command(String cmd) throws IOException {
		if(clientVerified) {
			switch(cmd) {
			// Lock PC
			case "1":
				lockPC();
				break;
			// Unlock PC
			case "2":
				unlockPC();
				break;
			// Client Direct Disconnection
			case "3":
				unlockPC();
				disconnect();
			// Switch Security Mode
			case "toggleControlMode":
				setControlMode();
				break;
			// Host Direct Disconnection
			case "disconnect":
				disconnect();
				break;
			default:
				System.out.println("[!] Unknown command.");
			}
		}
		else {
			System.out.println("[!] No access.");
		}
	}
	
	// Locks PC (activates all security measures and denies access to user in selected terms)
	private void lockPC() {
		System.out.println("[!] Locking PC!");
		gui.securityStatusLabel.setText("Security Status: Locked");
		gui.w.setVisible(true);
		gui.w.setAlwaysOnTop(true); // DO NOT UNCOMMENT, UNLESS A FUNCTION IS CREATED TO DESTROY THE APP
	}
	
	// Unlocks PC (removes all security measures and user gains access to its PC)
	private void unlockPC() {
		System.out.println("[!] Unlocking PC!");
		gui.securityStatusLabel.setText("Security Status: Unlocked");
		gui.w.setVisible(false);
	}
	
	// Automatically locks/unlocks PC
	private void setControlMode() {
		System.out.println("[!] Control mode set!");
		if(!autoControl) {
			autoControl = true;
			gui.controlModeLabel.setText("Control Mode: Automatic");
		}
		else {
			autoControl = false;
			gui.controlModeLabel.setText("Control Mode: Manual");
		}
	}
	
	// Closes the socket connection, therefore two devices will disconnect
	private void disconnect() throws IOException {
		System.out.println("[!] Disconnecting!");
		clientVerified = false;
		cs.close();
		ss.close();
		is.close();
		out.close();
		br.close();
		pw.close();
		gui.cardLayout.show(gui.container, "hostPanel");
	}
	
	// Cancels the host connection
	private void cancelHostConnection() throws IOException {
		System.out.println("[!] Disconnecting!");
		ss.close();
		gui.cardLayout.show(gui.container, "hostPanel");
	}
	
	/*
	 Prohibited Hotkeys in Lock Mode:
	 
	 Win + L = Windows Lock Screen
	 Alt + F4 = Exit Application/Shutdown
	 Ctrl + Shift + Esc = Open Task Manager
	 Ctrl + Alt + Del = Open Admin Functions
	 Win + R = Run command
	 
	*/
}