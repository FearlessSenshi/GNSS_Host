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
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

// Last committed by: 
// 		Name: SENSHI PC ;)
//		DT  : 03-19-2024 0437

public class Functions implements Runnable{
	MainApp gui;
	Thread t1,t2,t3,t4,t5,t6,t7,t8,t9,t10;
	
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
	public boolean authFailed = false;
	public boolean aesRunning = false;
	public boolean earlyUnlock = false;
	public boolean earlyLock = false;
	public boolean displayFinishDialog = false;
	public Robot rt;
	
	public boolean connectedToNetwork = false;
	public boolean retryConnection = false;
	public ArrayList<String> selectedFilepaths,loadedFilepaths;
	
	// Client Variables
	public String clientID;
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
        appPath = System.getProperty("user.dir") + "\\NSS.exe";

        // (DEBUG) Print the current working directory
        // JOptionPane.showMessageDialog(null, currentDirectory);
		
		exitExp = new ProcessBuilder("taskkill", "/F", "/IM", "explorer.exe"); // exits explorer.exe (file explorer and the taskbar)
		openExp = new ProcessBuilder("explorer.exe"); // opens explorer.exe (file explorer and the taskbar)
		
		createTask = new ProcessBuilder("schtasks", "/Create", "/TN", "NetSecuritySystem", "/TR", appPath, "/SC", "ONLOGON", "/RL", "HIGHEST");
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
				try {
					if(chkHostID()) {
						hostIDFile = new File("myID.txt");
						Scanner fs = new Scanner(hostIDFile);
						hostDetails = fs.nextLine();
						fs.close();
						
						if(hostDetails.contains("wifi|")) {
							hostDetails = generateHostDeviceDetails("hotspot");
							File file = new File("myID.txt");
							FileWriter fw = new FileWriter(file);
							fw.write(hostDetails + "\n");
							fw.close();
						}
						else if(hostDetails.contains("hotspot|")) {
							// continue
						}
						
					}
					else {
						hostDetails = generateHostDeviceDetails("hotspot");
						File file = new File("myID.txt");
						FileWriter fw = new FileWriter(file);
						fw.write(hostDetails + "\n");
						fw.close();
					}
				}
				catch(IOException e1) {
					System.out.println("[BTN_HOTSPOT]");
				}
				
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
					disconnect();
				} catch (IOException e1) {
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
					e1.printStackTrace();
				}
			}
			
		});
		
		gui.encSetupBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.setEnabled(false);
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
						displayFinishDialog = true;
						displayUnclosingDialog();
						decrypt();
						gui.w.dispose();
						gui.dispose();
						
						updateStatus("no");
						if(!unlocked) {
							try {
								p = openExp.start();
								p = removeTask.start();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						connectivity = 0;
						disconnect();
					}
				} catch (IOException e1) {
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
				gui.setAlwaysOnTop(true);
				gui.setAlwaysOnTop(false);
				gui.setEnabled(true);
				gui.repaint();
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
	
	// This is the main thread
	@Override 
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
							
							if(hostDetails.contains("wifi|")) {
								FileWriter fw = new FileWriter(hostIDFile);
								String[] data = hostDetails.split("\\|");
								data[2] = String.valueOf(serverPort);
								fw.write(data[0]+"|"+data[1]+"|"+data[2]+"|"+data[3]+"\n"); // replace with newly generated port
								fw.close();
								
								fs = new Scanner(hostIDFile);
								hostDetails = fs.nextLine();
								fs.close();
							}
							else if(hostDetails.contains("hotspot|")) {
								hostDetails = generateHostDeviceDetails("wifi");
								File file = new File("myID.txt");
								FileWriter fw = new FileWriter(file);
								fw.write(hostDetails + "\n");
								fw.close();
							}
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
							gui.clientConnectStatus.setIcon(new ImageIcon("buffering.gif"));
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
									gui.clientConnectStatus.setIcon(new ImageIcon("buffering.gif"));
									
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
				
				clientID = generateClientDeviceID();
				recoveryKey = generateRecoveryKey();
				gui.generatedRecoveryKey.setText(recoveryKey);
				
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
                                System.out.println("[HS_MODE] Successful Connection! Disconnecting auth bridge...");
                                clientVerified = true;
                                gui.cardLayout.show(gui.container, "connectStatusPanel");
								gui.repaint();
								gui.hostIPHostName.setText(hostIP + " - " + hostName);
								cs.close();
                                pw.close();
                                br.close();
                                createRecoveryKey();
                                updateStatus("yes");
                                
                                try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
                                cs = new Socket(getDefaultGateway(), 45451);
                				System.out.println("[HS_MODE] Connected Again!");
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
				e.printStackTrace();
			}
			
		}
		
	}
	
	// Opens a panel for CREATING a Host Connection
	void showAuthPanel() {
		gui.cardLayout.show(gui.container, "authPanel");
		// gui.clientConnectStatus.setText("Status: Waiting for client connection...");
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
		} catch (Exception e) {
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
		
		// If the connecting device has no ID (hence, a new device)
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
		
		// If the connecting device has an existing ID
		else if(!newConnection) {
			try {
				if(file.exists()) {
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
						System.out.println("[SEND_CLIENT_IDS] Client has been previously registered!");
					}
				}
				else {
					file.createNewFile();
					fw = new FileWriter(file,true);
					fw.write(cID + "\n");
					fw.close();
					System.out.println("[SEND_CLIENT_IDS] New Client Registered!");
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
		String hostDeviceDetails = null;
		
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);

        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
		
		// Generate ID
		if(connectivity.equals("wifi")) {
	        hostDeviceDetails = connectivity + "|" + String.valueOf(hostIP) + "|" + String.valueOf(serverPort) + "|" + sb.toString();
		}
		else if(connectivity.equals("hotspot")) {
			hostDeviceDetails = connectivity + "|" + sb.toString();
		}
		else {
			System.out.println("[GEN_HOST_ID] Invalid connectivity!");
		}
		return hostDeviceDetails;
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
				if(aesRunning)
					System.out.println("[CMD] Cannot lock. Encryption/Decryption is in progress.");
				else
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
					sendClientIDs();
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
			encrypt();
			
			// 3. Disable selected hotkeys (loop exec)
			disableAltKey();
			
			// 4. Disable taskmgr when opened (loop exec !CAUTION!)
			disableTaskMgr();
			
			// 5. Enable application to open on startup (exec once)
			try {
				p = createTask.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			if(aesRunning) {
				// When the user unlocks the PC while encrypt() is running.
				System.out.println("[UNLOCK PC] Early Unlock has been detected!");
				earlyUnlock = true; // this flag will activate decrypt() after the encrypt() process.
			}
			else {
				decrypt();
			}
			
			// 3. Enable hotkeys
			if(t5.isAlive())
				t5.interrupt();
			
			// 4. Enable taskmanager when opened
			if(t4.isAlive()) {
				t4.interrupt();
				System.out.println("[UNLOCK PC] Taskmanager killer has stopped.");
			}
			
			// 5. Remove application to open on startup
			try {
				p = removeTask.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("[UNLOCK PC] Device is already unlocked!");
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
								System.out.println("[HEARTBEAT] " + heartbeat);
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
		
		File file = new File("directories.txt");
		file.delete();
		
		updateStatus("no");
		
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
						if(ss != null) {
							if(ss.isBound()) {
								ss.close();
								cs.close();
							}
						}
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
						
						if (cs.isConnected()) {
							String message = br.readLine();
							
							System.out.println("[CREATE_CONN] Connected! Authenticating client...");
							if(message.contains("gnssReconnect|")) {
								String[] data = message.split("\\|");
								if(isIDVerified(data[1])) {
									System.out.println(data[1]);
									System.out.println("[CREATE_CONN] Server has connected to client successfully!");
									Thread.sleep(1000);
									unlockPC();
									runInputListener(br);
									chkDeviceConnection();
									if(connectivity == 2) {
										chkNetworkConnection();
									}
									pw.println("gnssReconnectSuccess");
									break;
								}
								else {
									pw.println("gnssReconnectFailed");
									System.out.println("[CREATE_CONN] Authentication failed. Unknown device attempts to reconnect.");
								}
							}
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
	
	private boolean isIDVerified(String ID) {
		boolean result = false;
		
		File file = new File("deviceList.txt");
		try {
			Scanner scan = new Scanner(file);
			
			
			while(scan.hasNextLine()) {
				String scannedID = scan.nextLine();
				if(ID.equals(scannedID)) {
					result = true;
				}
				
			}
		} catch (FileNotFoundException e) {
			System.out.println("[VER_ID] File not found.");
			result = false;
			e.printStackTrace();
		}
		
		return result;
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
			if(file.exists()) {
				fs = new Scanner(file);
				if (fs.hasNextLine()) {
					if (fs.nextLine().equals("yes")) {
						result = true;
					} else
						result = false;
				}
				fs.close();
			}
			else if(!file.exists()) {
				file.createNewFile();
				System.out.println("[CHK_SESSION] Blank status file!");
				fw = new FileWriter(file);
				fw.write("no" + "\n");
				fw.close();
				result = false;
			}
		} catch(IOException e) {
			try {
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
		String[] data = null;
		try {
			fs = new Scanner(new File("myID.txt"));
			data = fs.nextLine().split("\\|");
			fs.close();
			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initialize WIFI SOCKETS
		if (data[0].equals("wifi")) {
			try {
				System.out.println("[RECONN_DEVICE] PC has wifi session.");

				lockPC();
				
				gui.cardLayout.show(gui.container, "lockPanel");
				InetAddress inetAddress = InetAddress.getLocalHost();
				hostIP = inetAddress.getLocalHost().getHostAddress();
				hostName = inetAddress.getLocalHost().getHostName();
				gui.hostIPHostName.setText(hostIP + " - " + hostName);
				
				connectivity = 1;
				serverPort = Integer.valueOf(data[2]);
				
				while(true) {
					ss = new ServerSocket(serverPort);
					cs = new Socket();
					cs = ss.accept();
					if (cs.isConnected()) {
						
						is = cs.getInputStream();
						br = new BufferedReader(new InputStreamReader(is));
						out = cs.getOutputStream();
						pw = new PrintWriter(out, true);
						
						String message = br.readLine();
						
						System.out.println("[CREATE_CONN] Connected! Authenticating client...");
						if(message.contains("gnssReconnect|")) {
							String[] msgdata = message.split("\\|");
							if(isIDVerified(msgdata[1])) {
								System.out.println(msgdata[1]);
								
								unlockPC();
								clientVerified = true;
								
								System.out.println("[CREATE_CONN] Client has connected to host successfully!");
								Thread.sleep(1000);
								
								runInputListener(br);
								chkNetworkConnection();
								chkDeviceConnection();
								
								pw.println("gnssReconnectSuccess");
								break;
							}
							else {
								pw.println("gnssReconnectFailed");
								System.out.println("[CREATE_CONN] Authentication failed. Unknown device attempts to reconnect.");
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// Initialize HOTSPOT SOCKETS
		else if (data[0].equals("hotspot")) {
			System.out.println("[RECONN_DEVICE] PC has hotspot session.");

			lockPC();
			gui.cardLayout.show(gui.container, "lockPanel");

			connectivity = 2;

			while (true) {
				try {
					cs = new Socket();
					cs.connect(new InetSocketAddress(getDefaultGateway(), 45451), 3000);
					//cs.setSoTimeout(2000);

					if (cs.isConnected()) {
						pw = new PrintWriter(cs.getOutputStream(), true);
        	            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        	            String message = br.readLine();
        	            
        	            System.out.println("[CREATE_CONN] Connected! Authenticating client...");
        	            if(message.contains("gnssReconnect|")) {
        	            	String[] msgdata = message.split("\\|");
        	            	
        	            	if(isIDVerified(msgdata[1])) {
        	            		System.out.println(msgdata[1]);
        	            		unlockPC();
        						clientVerified = true;
        						
        						System.out.println("[CREATE_CONN] Client has connected to host successfully!");
        						Thread.sleep(2000);

        						runInputListener(br);
        						chkNetworkConnection();
        						chkDeviceConnection();
        						
        						pw.println("gnssReconnectSuccess");
        						break;
        	            	}
        	            }
        	            else if(message.contains("gnssReconnectFailed")) {
        	            	pw.println("gnssReconnectFailed");
							System.out.println("[CREATE_CONN] Authentication failed. Unknown device attempts to reconnect.");
        	            }
        	            
						
					}
				} catch (IOException e) {
					try {
						cs.close();
						System.out.println("[RECONN_DEVICE] Socket closed.");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Gets the directories to encrypt
	private void getDirectories() {
		// Create a file chooser
		gui.encSetupFrame.setAlwaysOnTop(false);
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
        gui.encSetupFrame.setAlwaysOnTop(true);
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
			System.out.println("[UPDATE_SEL_DIRS] " + e);
		}
	}
	
	// Encrypts the selected directories/files
	private void encrypt() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					if(new File("directories.txt").exists()) {
						if(fileIsEmpty("directories.txt")) {
							System.out.println("[ENC] There's nothing to encrypt!");
						}
						else {
							Encryptor en = new Encryptor();
							String secretKey = generateKey();
							
							aesRunning = true;
							
							File encDir = new File("encFiles.txt");
					        encDir.createNewFile();
					        
					        File keyFile = new File("encKey.txt");
					        keyFile.createNewFile();
					        
					        PrintWriter pw = new PrintWriter(new FileOutputStream("encKey.txt"));
					        pw.println(secretKey);
					        pw.close();
					        
							Scanner fscan = new Scanner(new File("directories.txt"));
							loadedFilepaths = new ArrayList<>();

							while (fscan.hasNextLine()) {
								loadedFilepaths.add(fscan.nextLine());
							}
							fscan.close();

							for (String s : loadedFilepaths) {
								File f = new File(s);
								en.encryptFile(s, f.getParent(), secretKey);
							}
							
							aesRunning = false;
							
							System.out.println("Process done!");
							
							if(earlyUnlock) {
								System.out.println("[ENC] Early unlock detected! Decrypting files!");
								earlyUnlock = false;
								decrypt();
							}
						}
					}
					else if(!new File("directories.txt").exists()) {
						System.out.println("[ENC] There's nothing to encrypt!");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					aesRunning = false;
				}
			}
		};
		t9 = new Thread(runnable);
		t9.start();
	}
	
	// Decrypts the encrypted files
	private void decrypt() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				try {
					if(new File("encFiles.txt").exists() && new File("encKey.txt").exists()) {
						if(fileIsEmpty("encFiles.txt") || fileIsEmpty("encKey.txt")) {
							System.out.println("[DEC] Insufficient resources. Missing enc directory or enc key.");
						}
						else {
							Decryptor de = new Decryptor();
							Scanner fscan = new Scanner(new File("encFiles.txt"));
							loadedFilepaths = new ArrayList<>();
							
							aesRunning = true;
							
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
							
							System.out.println("[DEC] Process done!");
							
							aesRunning = false;
							
							if(displayFinishDialog) {
								displayFinishDialog = false;
								
								gui.dialog.dispose();
								gui.w.dispose();
								gui.dispose();
								
								JOptionPane.showMessageDialog(null, "Decryption complete. You may use your device again.", "Decryption Process", JOptionPane.INFORMATION_MESSAGE);
								
								System.exit(0);
							}
						}
					}
					else if(!new File("encFiles.txt").exists() && !new File("encKey.txt").exists()) {
						System.out.println("[DEC] There's nothing to decrypt!");
						if(displayFinishDialog) {
							displayFinishDialog = false;
							
							gui.dialog.dispose();
							gui.w.dispose();
							gui.dispose();
							
							JOptionPane.showMessageDialog(null, "Device recovered! You may use your device again.", "Recovery Mode", JOptionPane.INFORMATION_MESSAGE);
							
							System.exit(0);
						}
					}
				}
				catch(Exception e) {
					aesRunning = false;
					System.out.println("[DECRYPT] CANNOT DECRYPT AT THIS MOMENT.");
					System.out.println(e);
				}
			}
			
		};
		t10 = new Thread(runnable);
		t10.start();
	}
	
	private void displayUnclosingDialog() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if(displayFinishDialog)
					gui.dialog.setVisible(true);
			}
			
		};
		Thread t = new Thread(runnable);
		t.start();
	}
	
	private boolean fileIsEmpty(String inputFile) {
		boolean result = false;
        try {
            Path path = Paths.get(inputFile);

            if (Files.isRegularFile(path) && Files.size(path) == 0 && Files.size(path) == 0) {
                System.out.println("[FILE_IS_EMPTY] (true) " + inputFile + " is empty.");
                result = true;
            } else {
                System.out.println("[FILE_IS_EMPTY] (false) " + inputFile + " has content.");
                result = false;
            }
        } catch (IOException e) {
            System.err.println("[FILE_IS_EMPTY] Error reading the file: " + e.getMessage());
        }
		
		return result;
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