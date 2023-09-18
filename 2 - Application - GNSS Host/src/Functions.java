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
	
	public Functions() {
		gui = new MainApp();
		
		gui.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getKeyChar() + " - " + e.getKeyCode());
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
		            System.out.println("Ctrl+C pressed (Copy)");
		        } 
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X) {
		            System.out.println("Ctrl+X pressed (Cut)");
		        }
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
		            System.out.println("Ctrl+V pressed (Paste)");
		        }
				if (e.isControlDown() && e.isAltDown()) {
					System.out.println("Admin Functions");
					e.consume();
				}
				if(e.getKeyCode() == KeyEvent.VK_WINDOWS) {
					System.out.println("bruh");
				}
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					System.out.println("bruh2");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
	
	public static void main(String[] args) {
		Functions f = new Functions();
		f.gui.setVisible(true);
	}
	
	@Override
	public void run() {
		while(!authFailed) {
			try {
				if(!clientVerified) {
					// Host established connection to client
					attempts = 3;
					clientVerified = false;
					serverPort = generatePort();
					// checkNetworkConnection();
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
								// Verify if the client is also using the client app
								output = br.readLine();
								System.out.println(output);
								// This condition checks the client's input for the secret passphrase "connectVerify<passcode>"
								if (output.equals("connectVerify" + hostPasscode)) {
									System.out.println("Client Verified!");
									gui.cardLayout.show(gui.container, "connectStatusPanel");
									gui.hostIPHostName.setText(hostIP + " - " + "Port: " + serverPort + " - " + hostName);
									pw.println("gnssVerified" + hostPasscode);
									clientVerified = true;

									InetAddress ia = cs.getInetAddress();
									System.out.println("Client Local IP Address: " + ia.getHostAddress());
									
									//t4.interrupt();
									runInputListener(br);
									break;
								} else {
									// Decrease attempt by -1
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
									else {
										// If attempt ran out to 0, disconnect connecting client
										pw.println("accessDenied");
										cs.close();
										ss.close();
										is.close();
										out.close();
										pw.close();
										br.close();
										//t4.interrupt();
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
						catch (IOException e) {
							// Disconnect the socket and all streams
							e.printStackTrace();
							cs.close();
							ss.close();
							is.close();
							out.close();
							br.close();
							pw.close();
							//t4.interrupt();
							break;
						}
			        }
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				//t4.interrupt();
				break;
			}
		}
	}
	
	void createHost() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getByName("localhost");
		hostIP = inetAddress.getLocalHost().getHostAddress();
		hostName = inetAddress.getLocalHost().getHostName();
		gui.clientConnectStatus.setText("Status: Waiting client connection...");
		authFailed = false;
		t1 = new Thread(this);
		t1.start();
	}
	
	void showAuthPanel() {
		gui.cardLayout.show(gui.container, "authPanel");
		gui.clientConnectStatus.setText("Status: Waiting for client connection...");
		gui.hostIPLabel.setText(getHostDetails());
		gui.generatedPasscode.setText(String.valueOf(generatePasscode()));
	}
	
	// Host Functions
	
	private String getHostDetails() {
		return hostIP + " - " + "Port: " + serverPort + " - " + hostName;
	}
	
	private int generatePort() {
		// return (int)(Math.random()*(MAX_PORT_RANGE-MIN_PORT_RANGE))+MIN_PORT_RANGE;
		return 1;
	}
	
	private int generatePasscode() {
		hostPasscode = (int)(Math.random()*(100000-10000))+10000;
		return hostPasscode;
	}
	
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
							// TODO Auto-generated catch block
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
	
	private void lockPC() {
		System.out.println("[!] Locking PC!");
		gui.securityStatusLabel.setText("Security Status: Locked");
		// Locks pc (shows whitescreen that cannot be exited or moved, some hotkeys are disabled)
		gui.w.setVisible(true);
		// gui.w.setAlwaysOnTop(true); DO NOT UNCOMMENT, UNLESS A FUNCTION IS CREATED TO DESTROY THE APP
	}
	private void unlockPC() {
		System.out.println("[!] Unlocking PC!");
		gui.securityStatusLabel.setText("Security Status: Unlocked");
		gui.w.setVisible(false);
	}
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
	
	private void cancelHostConnection() throws IOException {
		System.out.println("[!] Disconnecting!");
		ss.close();
		//t4.interrupt();
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