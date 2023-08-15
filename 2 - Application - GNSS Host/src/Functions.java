import java.awt.Dimension;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
// Git comment
public class Functions implements Runnable, KeyListener{
	MainApp gui;
	Thread t1;
	Thread t2;
	Thread t3;
	
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
	}
	
	public static void main(String[] args) {
		Functions f = new Functions();
		f.gui.setVisible(true);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				if(!clientVerified) {
					// Host established connection to client
					clientVerified = false;
					serverPort = generatePort();
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
			        while(true) {
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
									runInputListener(br);
									break;
								} else {
									// Decrease attempt by -1
									System.out.println("Incorrect Passcode! ");
									attempts--;
									System.out.println(attempts);

									// If attempt ran out to 0, disconnect connecting client
									if (attempts <= 0) {
										cs.close();
										ss.close();
										is.close();
										out.close();
										pw.close();
										br.close();
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
	
	void createHost() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getByName("localhost");
		hostIP = inetAddress.getLocalHost().getHostAddress();
		hostName = inetAddress.getLocalHost().getHostName();
		gui.clientConnectStatus.setText("Status: Waiting client connection...");
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
	
	private boolean waitForPasscode(String output) {
		if (output.equals("verifyConnection" + hostPasscode)) {
			clientVerified = true;
			gui.cardLayout.show(gui.container, "connectStatusPanel");
			gui.hostIPHostName.setText(hostIP + " - " + "Port: " + serverPort + " - " + hostName);
			System.out.println("Client connection verified!");
			return true;
		} 
		else {
			System.out.println("Incorrect Passcode! ");
			attempts--;
			System.out.println(attempts);
			return false;
		}
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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}