import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.RenderingHints.Key;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

// Last committed by: 
// 		Name: LENOVO IDEAPAD ;)
//		DT  : 03-06-2024 0317

public class Functions implements Runnable{
	MainApp gui;
	Thread t1,t2,t3,t4,t5,t6,t7,t8;
	
	// Window Variables
	private static final int FRAME_WIDTH = 300;
    private static final int FRAME_HEIGHT = 200;
    private static final int FADE_DURATION = 100;
	
    // App Path
    private String appPath;
    
	// Host Variables
	public ServerSocket ss;
	public Socket cs;
	public int serverPort = 1;
	
	public ServerSocket ssCon;
	public Socket sCon;
	public InputStream isCon;
	public OutputStream outCon;
	public BufferedReader brCon;
	
	public int hostPasscode;
	public String hostIP;
	public String hostName;
	public String hostDetails;
	public File hostIDFile;
	public String recoveryKey;
	public final int MIN_PORT_RANGE = 49152;
	public final int MAX_PORT_RANGE = 65535;
	public boolean unlocked = true;
	public boolean autoControl = false;
	public boolean stopNetChk = false;
	public boolean authFailed = false;
	public Robot rt;
	
	public boolean connectedToNetwork = false;
	public boolean retryConnection = false;
	public boolean retryConnFromDiscon = false;
	public ArrayList<String> selectedFilepaths,loadedFilepaths;
	
	// Client Variables
	public String clientID;
	public String clientIP;
	public String clientName;
	public String clientAndHostRecord;
	public boolean clientVerified = false;
	private int attempts = 3;
	
	// HOTSPOT - Client Variables
	public String hotspotIP;
	public int hotspotPasscode;
	public int connectivity = 0; // (0) no session/default, (1) wifi, (2) hotspot
	
	// Get Output
	private InputStream is;
	private BufferedReader br;
	private String cID;
	
	// Send Input
	private OutputStream out;
	private PrintWriter pw;
	
	// Processes
	private Process p;
	private ProcessBuilder exitExp,openExp,createTask,removeTask,killMgr;
	
	// KS
	public int clkCnt = 0;
	
	// Constructor - Assigning buttons with functions
	public Functions() {
		
		gui = new MainApp();
		
		// Get the current working directory
        String currentDirectory = System.getProperty("user.dir") + "\\NSS.exe";

        // Print the current working directory
        JOptionPane.showMessageDialog(null, currentDirectory);
		
		exitExp = new ProcessBuilder("taskkill", "/F", "/IM", "explorer.exe"); // exits explorer.exe (file explorer and the taskbar)
		openExp = new ProcessBuilder("explorer.exe"); // opens explorer.exe (file explorer and the taskbar)
		
		createTask = new ProcessBuilder("schtasks", "/Create", "/TN", "NetSecuritySystem", "/TR", appPath, "/SC", "ONLOGON");
		removeTask = new ProcessBuilder("schtasks", "/Delete", "/TN", "NetSecuritySystem", "/F");
		
		killMgr = new ProcessBuilder("taskkill", "/F", "/IM", "taskmgr.exe");
		
		// Home Buttons
		gui.createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					createHost();
					connectivity = 1;
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				showAuthPanel();
			}
		});
		
		gui.connectHostBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinHost();
			}
		});
		
		gui.btnHotspot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectivity = 2;
				gui.cardLayout.show(gui.container, "connectToHostPanel");
				gui.repaint();
				System.out.println(getDefaultGateway());
			}
		});
		
		// Authentication Buttons
		gui.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectivity = 0;
				gui.cardLayout.show(gui.container, "hostPanel");
				gui.repaint();
			}
			
		});
		
		// Connection Status Buttons
		gui.dcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					connectivity = 0;
					updateStatus("no");
					disconnect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		gui.cancelHostConnection.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					connectivity = 0;
					cancelHostConnection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		gui.encSetupBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.encSetupFrame.setVisible(true);
				loadDirectories();
				gui.repaint();
			}
		});
		
		// Lock Screen Buttons and Textfields
		gui.recoveryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.lsCardLayout.show(gui.lsContainer, "recoveryPanel");
			}
		});
		
		gui.cancelRecoveryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.lsCardLayout.show(gui.lsContainer, "lockPanel");
				clkCnt = 0;
			}
		});
		
		gui.recoverBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(chkRecoveryKey()) {
						gui.w.dispose();
						gui.dispose();
						updateStatus("no");
						if(!unlocked) {
							try {
								p = openExp.start();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						System.exit(0);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		// Encryption Setup Buttons
		gui.addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDirectories();
				addDirectories();
			}
			
		});
		
		gui.removeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.listModel.remove(gui.filepathList.getSelectedIndex());
				updateSelectedDirectories();
			}
		});
		
		gui.removeAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.listModel.clear();
				gui.repaint();
				new File("directories.txt").delete();
			}
			
		});
		
		gui.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.listModel.clear();
				gui.encSetupFrame.dispose();
			}
		});
		
		// KS Button
		gui.enterPasscodeLbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clkCnt++;
				System.out.println(clkCnt);
				if(clkCnt == 20) {
					gui.w.dispose();
					gui.dispose();
					if(!unlocked) {
						try {
							p = openExp.start();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					System.exit(0);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//placeholder
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//placeholder
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				//placeholder
			}

			@Override
			public void mouseExited(MouseEvent e) {
				//placeholder
			}

		});
		
		gui.w.setVisible(true);
		if(chkSession()) {
			gui.setVisible(true);
			gui.cardLayout.show(gui.container, "connectStatusPanel");
			gui.repaint();
			reconnectToDevice();
		}

	}
	
	// MAIN - Runs the application from GUI
	public static void main(String[] args) {
		MainApp gui = new MainApp();
		Functions f = new Functions();
		f.gui.setVisible(true);
		f.gui.setOpacity(0.0f);
		fadeTransition(f.gui);
	}
	
	@Override // This is the main thread
	public void run() {
		if(connectivity == 1) {
			// AUTHENTICATION - When authentication between host and client didn't fail, continue
			while(!authFailed) {
				try {
					
					// Creates a host connection session if the client is NOT verified by the PC
					if(!clientVerified) {
						attempts = 3;
						clientVerified = false;
						serverPort = generatePort();
						clientID = generateClientDeviceID();
						recoveryKey = generateRecoveryKey();
						gui.generatedRecoveryKey.setText(recoveryKey);
						
						if(chkHostID()) {
							hostIDFile = new File("myID.txt");
							Scanner fs = new Scanner(hostIDFile);
							hostDetails = fs.nextLine();
							fs.close();
						}
						else {
							hostDetails = generateHostDeviceDetails("wifi");
							File file = new File("myID.txt");
							FileWriter fw = new FileWriter(file);
							fw.write(hostDetails + "\n");
							fw.close();
						}
						
						System.out.println("[WIFI_MODE] gnssGenID|" + clientID + "|" + hostDetails);
						
						// Establish a ServerSocket connection
				        while(true) {
							ss = new ServerSocket(serverPort);
							System.out.println("[WIFI_MODE] " + ss.getLocalPort());
							System.out.println("[WIFI_MODE] Waiting for client connection...");
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
									gui.clientConnectStatus.setText("[WIFI_MODE] Status: Awaiting client verification...");
									
									// Waits the client's response for the passcode
									output = br.readLine();
									System.out.println("[WIFI_MODE] Client Message: " + output);
									
									// Verifies client connection and opens access control
									if (output.equals("connectVerify" + hostPasscode)) {
										System.out.println("[WIFI_MODE] Client Verified!");
										gui.cardLayout.show(gui.container, "connectStatusPanel");
										gui.repaint();
										gui.hostIPHostName.setText(hostIP + " - " + hostName);
										createRecoveryKey();
										updateStatus("yes");
										
										Thread.sleep(2000);
										
										pw.println("gnssVerified" + hostPasscode);
										clientVerified = true;
										
										InetAddress ia = cs.getInetAddress();
										System.out.println("[WIFI_MODE] Client Local IP Address: " + ia.getHostAddress());
										runInputListener(br);
										chkNetworkConnection();
										chkDeviceConnection();
										break;
									} 
									
									// When client inputs wrong passcode, decrease attempt by -1
									else {
										System.out.println("[WIFI_MODE] Incorrect Passcode! ");
										if(attempts > 0) {
											// Sends out an input saying that the given passcode was incorrect
											pw.println("incorrectPasscode");
											if(cs.isConnected()) System.out.println("[WIFI_MODE] Client is still connected.");
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
		else if(connectivity == 2) {
			try {
				hotspotPasscode = Integer.parseInt(gui.passcodeInput.getText());
				cs = new Socket(getDefaultGateway(), 45451);
				System.out.println("Connected!");
				pw = new PrintWriter(cs.getOutputStream(), true);
	            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
	            System.out.println("connectVerifyFromHotspot" + hotspotPasscode);
	            pw.println("connectVerifyFromHotspot" + hotspotPasscode);
	            while (true) {
                    if (cs.isConnected()) {
                        String message = br.readLine();
                        System.out.println(message);
                        if(message!=null){
                            if (message.equals("incorrectPasscode")) {
                                System.out.println("[HS_MODE] Connected but incorrect passcode.");
                                cs.close();
                                pw.close();
                                br.close();
                            } else if (message.equals("accessDenied")) {
                                System.out.println("[HS_MODE] Attempts ran out. Disconnected from session.");
                                cs.close();
                                pw.close();
                                br.close();
                                break;
                            } else if (message.equals("gnssVerified"+hotspotPasscode)) {
                                System.out.println("[HS_MODE] Successful Connection");
                                clientVerified = true;
                                gui.cardLayout.show(gui.container, "connectStatusPanel");
								gui.repaint();
								gui.hostIPHostName.setText(hostIP + " - " + hostName);
								cs.close();
                                pw.close();
                                br.close();
                                try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                                cs = new Socket(getDefaultGateway(), 45451);
                				System.out.println("Connected! Again!");
                				pw = new PrintWriter(cs.getOutputStream(), true);
                	            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                                runInputListener(br);
								chkNetworkConnection();
								chkDeviceConnection();
                                break;
                            }
                        }

                        // Detects if the client socket connection disconnects to the server.
                        else{
                            System.out.println("[HS_MODE] Server disconnected!");
                            System.out.println("[HS_MODE] Client/Server disconnected!");
                            break;
                        }

                    } else {
                        System.out.println("[HS_MODE] Socket isn't connected.");
                    }
                }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	// Opens a panel for CREATING a Host Connection
	void showAuthPanel() {
		gui.cardLayout.show(gui.container, "authPanel");
		gui.clientConnectStatus.setText("Status: Waiting for client connection...");
		gui.hostnameLabel.setText(hostName);
		gui.hostIPLabel.setText(hostIP);
		gui.hostPortLbl.setText(Integer.toString(serverPort));
		gui.generatedPasscode.setText(String.valueOf(generatePasscode()));
	}
	
	// Gets this PC's private IP Address
	void createHost() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostIP = inetAddress.getLocalHost().getHostAddress();
		hostName = inetAddress.getLocalHost().getHostName();
		gui.clientConnectStatus.setText("Status: Waiting client connection...");
		authFailed = false;
		gui.repaint();
		t1 = new Thread(this); // runs the runnable thread
		t1.start();
	}
	
	// Creates a Socket Connection in this PC
	void joinHost() {
		try {
			System.out.println("[JOIN_HS] Connecting to hotspot...");
			t1 = new Thread(this);
			t1.start();
			//Socket s = new Socket(getDefaultGateway(), 1);
			//System.out.println("Connected!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getDefaultGateway() {
		String defaultGateway = "";
		try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "ipconfig | findstr /i \"Default Gateway\"");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                defaultGateway = line.split(":")[1].trim();
            }

            process.waitFor();
            reader.close();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
		return defaultGateway;
		
	}
	
	// Opens a panel for JOINING a Host Connection
	void connectToHost() {
		gui.cardLayout.show(gui.container, "connectToHostPanel");
		gui.repaint();
	}
	
	// Creates a new client record for new devices
	private void sendClientIDs() {
		String clientID = this.clientID;
		String hostDetails = this.hostDetails;
		
		pw.println("gnssGenID|" + clientID + "|" + hostDetails);
	}
	
	private void createClientRecord(Boolean newConnection) {
		File file = new File("deviceList.txt");
		Scanner fs = null;
		FileWriter fw = null;
		String regID = "";

		if(newConnection) {
			try {
				if(!file.exists()) {
					file.createNewFile();
					fw = new FileWriter(file,true);
					fw.write(clientID + "\n");
					fw.close();
				}
				else {
					boolean insert = false;
					fs = new Scanner(file);
					while(fs.hasNextLine()) {
						regID = fs.nextLine();
						if(!regID.equals(clientID)) {
							insert = true;
						}
					}
					fs.close();
					if(insert) {
						fw = new FileWriter(file,true);
						fw.write(clientID + "\n");
						fw.close();
					}
					else {
						clientID = generateClientDeviceID();
						fw = new FileWriter(file,true);
						fw.write(clientID + "\n");
						fw.close();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(!newConnection) {
			try {
				boolean exists = false;
				fs = new Scanner(file);
				while(fs.hasNextLine()) {
					regID = fs.nextLine();
					if(regID.equals(cID)) {
						exists = true;
						break;
					}
					else {
						exists = false;
					}
				}
				fs.close();
				if(!exists) {
					fw = new FileWriter(file,true);
					fw.write(cID + "\n");
					fw.close();
				}
				else {
					System.out.println("[SEND_CLIENT_IDS] Client Already Registered!");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createRecoveryKey() throws IOException {
		File file = new File("recoveryKey.txt");
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		fw.write(recoveryKey + "\n");
		fw.close();
	}
	
	// Generates a random client ID
	private String generateClientDeviceID() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
	}
	
	// Generates a random host ID
	private String generateHostDeviceDetails(String connectivity) {
		// Generate ID
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);

        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        
        return "wifi|" + String.valueOf(hostIP) + "|" + String.valueOf(serverPort) + "|" + sb.toString();
	}
	
	// Generates a random port
	private int generatePort() {
		return (int) (Math.random() * (MAX_PORT_RANGE - MIN_PORT_RANGE)) + MIN_PORT_RANGE;
	}

	// Generates a random passcode
	private int generatePasscode() {
		hostPasscode = (int) (Math.random() * (100000 - 10000)) + 10000;
		return hostPasscode;
	}
	
	// Generates a recovery key
	private String generateRecoveryKey() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
	}
	
	private boolean chkHostID() {
		hostIDFile = new File("myID.txt");
		boolean result = false;
		if(hostIDFile.exists()) {
			result = true;
		}
		return result;
	}
	
	
	// Runs the InputListener (listens for incoming input)
	public void runInputListener(BufferedReader br) {
		// InputStream
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						System.out.println("[runInputListener] Running in input function");
						String output = br.readLine();
						if(clientVerified) {
							if(output == null)
								System.out.println("[runInputListener] Client Device disconnected!");
							command(output);
							System.out.println("[runInputListener] Client Message: " + output);
						}
					} catch (Exception e) {
						try {
							e.printStackTrace();
							br.close();
							break;
						} catch (Exception e1) {
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
	
	// Runs output listener (sends an output to the client)
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
	
	// Command method (executes commands depending on the input of the user)
	public void command(String cmd) throws IOException{
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
//			case "toggleControlMode":
//				setControlMode();
//				break;
			// Host Direct Disconnection
			case "disconnect":
				disconnect();
				break;
			case "newConnection":
				cID = "newConnection";
				sendClientIDs();
				createClientRecord(true);
				break;
			default:
				if(cmd.contains("gnssClientID")) {
					System.out.println("[CMD] Client has an ID! Checking existence.");
					String[] data = cmd.split("\\|");
					cID = data[1];
					createClientRecord(false);
				}
				else
					System.out.println("[CMD] ! Unknown command.");
			}
		}
		else {
			System.out.println("[CMD] ! No access.");
		}
	}
	
	// Locks PC (activates all security measures and denies access to user in selected terms)
	private void lockPC() {
		if(unlocked) {
			unlocked = false;
			System.out.println("[LOCK PC] Locking PC!");
			gui.lsCardLayout.show(gui.lsContainer, "lockPanel");
			gui.uniquePasscodeInput.setText("");
			// gui.inputStatusLbl.setVisible(false);
			gui.w.setVisible(true);
			//gui.w.setAlwaysOnTop(true); // ONLY SET TO "true" when app is done ;)
			
			// (Activate all security measures)
			
			// 1. Disable explorer.exe (exec once)
//			try {
//				p = exitExp.start();
//				p = createTask.start();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			// 2. Encrypt Files
			//encrypt();
			
			// 3. Disable selected hotkeys (loop exec)
			disableAltKey();
			
			// 4. Disable taskmgr when opened (loop exec !CAUTION!)
			disableTaskMgr();
			
			// 5. Enable application to open on startup (exec once)
//			try {
//				p = createTask.start();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		else {
			System.out.println("[LOCK PC] Device is already locked!");
		}
	}
	
	// Unlocks PC (removes all security measures and user gains access to its PC)
	private void unlockPC(){
		if(!unlocked) {
			unlocked = true;
			System.out.println("[UNLOCK PC] Unlocking PC!");
			gui.w.setVisible(false);
			
			// (Deactivate all security measures)
			
			// 1. Enable explorer.exe
//			try {
//				p = openExp.start();
//				p = removeTask.start();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			// 2. Decrypt Files
			//decrypt();
			
			// 3. Enable hotkeys
			t5.interrupt();
			
			// 4. Enable taskmanager when opened
			t4.interrupt();
			System.out.println("[UNLOCK PC] Taskmanager killer has stopped.");
			
			// 5. Remove application to open on startup
//			try {
//				p = removeTask.start();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		else {
			System.out.println("[UNLOCK PC] Device is already unlocked!");
		}
	}
	
	// Sets control mode (Automatically/Manually locks/unlocks PC)
	private void setControlMode() {
		System.out.println("[!] (DEPRECATED) Control mode set!");
		if(!autoControl) {
			autoControl = true;
		}
		else {
			autoControl = false;
		}
	}
	
	// Check if taskmgr is open (closes taskmgr when open)
	private void disableTaskMgr() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						p = killMgr.start();
						Thread.sleep(1000);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						break;
					}
				}
			}
		};
		t4 = new Thread(r);
		t4.start();
	}
	
	private void disableAltKey(){
		try {
			rt = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while(true) {
					rt.setAutoDelay(1);
					rt.keyRelease(KeyEvent.VK_ALT);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		t5 = new Thread(r);
		t5.start();
	}
	
	// Checks connection using HEARTBEAT technique
	private void chkDeviceConnection() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					if(connectivity == 1) {
						ssCon = new ServerSocket(11111);
						System.out.println("[CHK_DEVICE_CONN] Waiting for client connection for connection checker...");
						sCon = ssCon.accept();
						sCon.setSoTimeout(2000);
					}
					else if(connectivity == 2) {
						System.out.println("[CHK_DEVICE_CONN] Waiting for client connection for connection checker...");
						sCon = new Socket(getDefaultGateway(),54541);
						System.out.println("[CHK_DEVICE_CONN] Connected to Device Conn checker!");
						//sCon.setSoTimeout(2000);
					}
					
					isCon = sCon.getInputStream();
					brCon = new BufferedReader(new InputStreamReader(isCon));

					String heartbeatMessage = "HEARTBEAT";
					outCon = sCon.getOutputStream();

					byte[] buffer = new byte[1024];
					int bytesRead;

					String line;
					String heartbeat = "-DUTUM";

					while (true) {
						Thread.sleep(2000);
						if (t6.isInterrupted()) {
							sCon.close();
							ssCon.close();
							if(connectivity == 1)
								ssCon.close();
							isCon.close();
							brCon.close();
							break;
						} else if ((bytesRead = isCon.read(buffer)) != -1) {
							String message = new String(buffer, 0, bytesRead);
							if (message.equals("HEARTBEAT")) {
								if(heartbeat.equals("-DUTUM"))
									heartbeat = "DUTUM-";
								else
									heartbeat = "-DUTUM";
								System.out.println(heartbeat);
								outCon.write(heartbeatMessage.getBytes());
								outCon.flush();
								
							}
						} else {
							sCon.close();
							ssCon.close();
							isCon.close();
							brCon.close();
							outCon.close();
							break;
						}
					}

				} catch (IOException e) {
					System.out.println("[CHK_DEVICE_CONN] Client disconnected!");
					e.printStackTrace();
					try {
						sCon.close();
						if(connectivity == 1)
							ssCon.close();
						isCon.close();
						brCon.close();
						indirectDc();
						lockPC();
						if (!retryConnection)
							createNewConnection();
					} catch (IOException e1) {
						System.out.println("[CHK_DEVICE_CONN] " + e);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						ssCon.close();
						sCon.close();
						isCon.close();
						brCon.close();
						outCon.close();
					}catch(Exception e1) {
						System.out.println(e);
					}
					System.out.println("[CHK_DEVICE_CONN] Interrupted!");
				}
			}
		};
		t6 = new Thread(r);
		t6.start();
	}
	
	// Checks network connectivity
	public void chkNetworkConnection() {
		// Checks network connection if host is connected to any network. (Local or with internet.)
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						connectedToNetwork = false;
						Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
						while (networkInterfaces.hasMoreElements()) {
							NetworkInterface networkInterface = networkInterfaces.nextElement();
							if (networkInterface.isUp() && !networkInterface.isLoopback()) {
								connectedToNetwork = true;
							}
						}
						if (connectedToNetwork) {
							//System.out.println("Connected to a network.");
							if(retryConnection) {
								if(t8 != null) {
									if(t8.isAlive()) {
										t8.interrupt();
									}
								}
								createNewConnection();
								retryConnection = false;
							}
						} 
						else {
							//System.out.println("Not connected to a network...");
							if(!retryConnection) {
								if(t6.isAlive()) {
									t6.interrupt(); // interrupt network checker
									sCon.close();
									ssCon.close();
									isCon.close();
									brCon.close();
									indirectDc();
									lockPC();
									retryConnection = true;
								}
								else if(!t6.isAlive()) {
									indirectDc();
									lockPC();
									retryConnection = true;
								}
							}
						}
					} catch (SocketException e) {
						e.printStackTrace();
						break;
					} catch (InterruptedException e) {
						System.out.println("[CHK_NET] Network Connection checker interrupted!");
						e.printStackTrace();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		t7 = new Thread(runnable);
		t7.start();
	}
	// Closes the socket connection when client disconnects without confirmation
	private void indirectDc() throws IOException {
		cs.close();
		if(connectivity == 1) {
			ssCon.close();
			is.close();
			out.close();
		}
		br.close();
		pw.close();
	}
	
	// Closes the socket connection, therefore two devices will disconnect
	private void disconnect() throws IOException {
		System.out.println("[DC] Disconnecting!");
		gui.cardLayout.show(gui.container, "hostPanel");
		gui.repaint();
		clientVerified = false;
		pw.println("disconnect");
		t6.interrupt(); // chkDeviceConnection
		t7.interrupt(); // chkNetworkConnection
		if(t8 != null)
			if(t8.isAlive())
				t8.interrupt();
		
		unlockPC();
		
		cs.close();
		if(connectivity == 1) {
			ss.close();
			is.close();
			out.close();
		}
		br.close();
		pw.close();
		t1.interrupt();
	}
	
	// Cancels the host connection
	private void cancelHostConnection() throws IOException {
		System.out.println("[CANCEL_HOST_CONN] Disconnecting!");
		ss.close();
		gui.cardLayout.show(gui.container, "hostPanel");
		gui.repaint();
	}
	
	// Creates new connection - creates a new connection for the verified client to connect
	private void createNewConnection() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(2);
						System.out.println("[CREATE_CONN] Retrying connection to client...");
						if(connectivity == 1) {
							ss = new ServerSocket(serverPort);
							ss.setSoTimeout(5000);
							cs = ss.accept();
							is = cs.getInputStream();
							br = new BufferedReader(new InputStreamReader(is));
							out = cs.getOutputStream();
							pw = new PrintWriter(out, true);
						}
						else if(connectivity == 2) {
							cs = new Socket(getDefaultGateway(),45451);
							//cs.setSoTimeout(1000);
							br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
							pw = new PrintWriter(cs.getOutputStream(), true);
						}
						
						Thread.sleep(0);
						
						if(ss != null) {
							if(ss.isBound()) {
								ss.close();
								cs.close();
							}
						}
						ss = new ServerSocket(serverPort);
						ss.setSoTimeout(5000);
						System.out.println(ss != null);
						System.out.println(cs != null);
						cs = ss.accept();
						
						is = cs.getInputStream();
						br = new BufferedReader(new InputStreamReader(is));
						out = cs.getOutputStream();
						pw = new PrintWriter(out, true);
						if (cs.isConnected()) {
							System.out.println("[CREATE_CONN] Server has connected to client successfully!");
							unlockPC();
							runInputListener(br);
							chkDeviceConnection();
							if(connectivity == 2) {
								chkNetworkConnection();
							}
							break;
						}
					} catch (IOException e) {
						try {
							if(connectivity == 1) {
								ss.close();
								cs.close();
							}
							else if(connectivity == 2)
								cs.close();
						} catch (IOException e1) {
							System.out.println("[CREATE_CONN] Error in closing serversocket.");
							e1.printStackTrace();
						}
						e.printStackTrace();
					} catch (InterruptedException e) {
						try {
							cs.close();
							ss.close();
							is.close();
							br.close();
							out.close();
							pw.close();
							System.out.println("[CREATE_CONN] Thread Closed.");
						} catch (Exception e1) {
							System.out.println("[CREATE_CONN] Can't close server and client socket");
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		};
		t8 = new Thread(r);
		t8.start();
	}
	
	// Checks if inputted recovery key is valid
	private boolean chkRecoveryKey() throws IOException {
		File file = new File("recoveryKey.txt");
		Scanner fs = new Scanner(file);
		String recoveryKey;
		String input = gui.uniquePasscodeInput.getText();
		boolean result;
		
		recoveryKey = fs.nextLine();
		fs.close();
		
		if(input.equals(recoveryKey)) {
			updateStatus("no");
			result = true;
		}
		else {
			result = false;
		}
		
		return result;
	}
	
	// Checks if the app has previous running session
	private boolean chkSession(){
		// When the app is opened and has a previous ongoing session, the security system will activate again.
		File file = new File("status.txt");
		Scanner fs = null;
		FileWriter fw = null;
		boolean result = false;
		
		try {
			if(!file.exists())
				file.createNewFile();
			fs = new Scanner(file);
			if(fs.hasNextLine()) {
				if(fs.nextLine().equals("yes")) {
					result = true;
				}
				else
					result = false;
			}
			
			else {
				System.out.println("[CHK_SESSION] Blank status file!");
				fw = new FileWriter(file);
				fw.write("no" + "\n");
				fw.close();
				result = false;
			}
		} catch(IOException e) {
			fs.close();
		}
		return result;
	}
	
	private void updateStatus(String status) throws IOException {
		File file = new File("status.txt");
		FileWriter fw = null;
		
		fw = new FileWriter(file);
		fw.write(status);
		fw.close();
	}
	
	private void reconnectToDevice() {
		Scanner fs = null;
		try {
			fs = new Scanner(new File("myID.txt"));
			String[] data = fs.nextLine().split("\\|");
			fs.close();
			// Initialize WIFI SOCKETS
			if(data[0].equals("wifi")) {
				System.out.println("[RECONN_DEVICE] PC has wifi session.");
				
				lockPC();
				
				gui.cardLayout.show(gui.container,"lockPanel");
				InetAddress inetAddress = InetAddress.getLocalHost();
				hostIP = inetAddress.getLocalHost().getHostAddress();
				hostName = inetAddress.getLocalHost().getHostName();
				gui.hostIPHostName.setText(hostIP + " - " + hostName);
				
				connectivity = 1;
				serverPort = Integer.valueOf(data[2]);
				
				ss = new ServerSocket(serverPort);
				cs = new Socket();
				cs = ss.accept();
				if(cs.isConnected()) {
					unlockPC();
					clientVerified = true;
					is = cs.getInputStream();
		    		br = new BufferedReader(new InputStreamReader(is));
		    		out = cs.getOutputStream();
		    		pw = new PrintWriter(out,true);
		    		Thread.sleep(2000);
		    		runInputListener(br);
					chkNetworkConnection();
					chkDeviceConnection();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Gets the directories to encrypt
	private void getDirectories() {
		// Create a file chooser
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle("Choose directories");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        selectedFilepaths = new ArrayList<>();
        
        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);
        
        // Check if the user selected directories
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected directories
            File[] selectedDirectories = fileChooser.getSelectedFiles();

            // List files in each selected directory (including subdirectories) with full paths
            for (File directory : selectedDirectories) {
                System.out.println("[GET_DIR] Files in directory: " + directory.getAbsolutePath());
                if(directory.isFile()) {
                	System.out.println(directory.getAbsolutePath());
                	saveDirectories(directory.getAbsolutePath().toString());
                }
                else
                	listFiles(directory);
                
                System.out.println(); // Add a newline between directories
            }
        } else {
            System.out.println("[GET_DIR] No directories selected.");
        }
	}
	
	// Saving the directories to a flatfile
	private void saveDirectories(String filepath) {
		try {
			File dirFile = new File("directories.txt");
			if(!dirFile.exists()) {
				dirFile.createNewFile();
			}
    		Scanner fscan = new Scanner(new File("directories.txt"));
    		String path = "";
    		boolean exists = false;
    		if(!fscan.hasNextLine()) {
    			FileWriter fw = new FileWriter("directories.txt",true);
    			selectedFilepaths.add(filepath);
				fw.write(filepath + "\n");
				fw.close();
    		}
    		else {
    			while(fscan.hasNextLine()) {
    				path = fscan.nextLine();
        			if(filepath.equals(path)) {
        				System.out.println("[SAVE_DIRS] File has been already added.");
        				exists = true;
        				break;
        			}
        		}
        		if(!exists) {
    				FileWriter fw = new FileWriter("directories.txt",true);
    				selectedFilepaths.add(filepath);
    				fw.write(filepath + "\n");
    				fw.close();
    			}
    		}
    		fscan.close();
		}
		catch(Exception e) {
			System.out.println("[SAVE_DIRS] ERROR " + e);
			e.printStackTrace();
		}
	}
	
	// Lists files in that given directory
	private void listFiles(File directory) {
		File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // If the file is a directory, recursively list its files
                    listFiles(file);
                } else {
                    // If the file is a regular file, print its full path
                    System.out.println(file.getAbsolutePath());
                    saveDirectories(file.getAbsolutePath().toString());
                }
            }
        } else {
            System.out.println("[LIST_DIRS] Error listing files in directory.");
        }
	}
	
	// Loads the directory flatfile to a list model
	private void loadDirectories() {
		File dirFile = new File("directories.txt");
		if(dirFile.exists()) {
			Scanner fscan = null;
			try {
				fscan = new Scanner(dirFile);
				while(fscan.hasNextLine()) {
					gui.listModel.addElement(fscan.nextLine());
					gui.repaint();
				}
				fscan.close();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
				fscan.close();
			}
		}
	}
	
	// Adds directory to the list model
	private void addDirectories() {
		for(String s: selectedFilepaths) {
			gui.listModel.addElement(s);
		}
		gui.repaint();
	}
	
	// Updates the directory flatfile for any changes
	private void updateSelectedDirectories() {
		try {
			File dirFile = new File("directories.txt");
			dirFile.delete();
			dirFile.createNewFile();
			
			FileWriter fw = new FileWriter(dirFile);
			
			int modelSize = gui.listModel.getSize();
			
			for(int i = 0; i < modelSize; i++) {
				fw.write(gui.listModel.get(i) + "\n");
			}
			fw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	// Encrypts the selected directories/files
	private void encrypt() {
		try {
			Encryptor en = new Encryptor();
			String secretKey = generateKey();
			
			File encDir = new File("encFiles.txt");
	        encDir.createNewFile();
	        
	        File keyFile = new File("encKey.txt");
	        keyFile.createNewFile();
	        
	        PrintWriter pw = new PrintWriter(new FileOutputStream("encKey.txt"));
	        pw.println(secretKey);
	        pw.close();
			
			Scanner fscan = new Scanner(new File("directories.txt"));
			loadedFilepaths = new ArrayList<>();
			
			while(fscan.hasNextLine()) {
				loadedFilepaths.add(fscan.nextLine());
			}
			fscan.close();
			
			for(String s: loadedFilepaths) {
				File f = new File(s);
				en.encryptFile(s,f.getParent(),secretKey);
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Decrypts the encrypted files
	private void decrypt() {
		try {
			Decryptor de = new Decryptor();
			Scanner fscan = new Scanner(new File("encFiles.txt"));
			loadedFilepaths = new ArrayList<>();
			
			while(fscan.hasNextLine()) {
				loadedFilepaths.add(fscan.nextLine());
			}
			fscan.close();
			fscan = new Scanner(new File("encKey.txt"));
			String key = fscan.nextLine();
			fscan.close();
			for(String s: loadedFilepaths) {
				File f = new File(s);
				de.decryptFile(s,f.getParent(),key);
			}
		}
		catch(Exception e) {
			System.out.println("[DECRYPT] CANNOT DECRYPT AT THIS MOMENT.");
		}
	}
	
	// Generates an 128bit alphanumerical key
	private String generateKey() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
	}
	
	// Adds fade in transition for the window when opening the app
	public static void fadeTransition(JFrame frame) {
		final javax.swing.Timer[] timer = {null};

        timer[0] = new javax.swing.Timer(10, new ActionListener() {
            private long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }

                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;

                if (elapsedTime < FADE_DURATION) {
                    float opacity = (float) elapsedTime / FADE_DURATION;
                    frame.setOpacity(opacity);
                } else {
                    frame.setOpacity(1.0f); // Ensure opacity is set to 1 at the end
                    timer[0].stop();
                }
            }
        });

        timer[0].start();
	}
	
	/*
		 Prohibited Hotkeys in Lock Mode:
		 
		 Win + L = Windows Lock Screen
		 Alt + F4 = Exit Application/Shutdown
		 Alt + Tab = Change application view
		 Win + R = Run command
	*/
}