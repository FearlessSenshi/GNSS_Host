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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import java.util.Scanner;

import javax.swing.JOptionPane;

// Last committed by: 
// 		Name: LENOVO IDEAPAD ;)
//		DT  : 01-28-2024 1852

public class Functions implements Runnable{
	MainApp gui;
	Thread t1,t2,t3,t4,t5,t6,t7,t8;
	
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
	public final int MIN_PORT_RANGE = 49152;
	public final int MAX_PORT_RANGE = 65535;
	public boolean unlocked = true;
	public boolean autoControl = false;
	public boolean stopNetChk = false;
	public boolean authFailed = false;
	public Robot rt;
	
	public boolean connectedToNetwork = false;
	public boolean retryConnection = false;
	
	// Client Variables
	public String clientIP;
	public String clientName;
	public boolean clientVerified = false;
	private int attempts = 3;
	
	// HOTSPOT - Client Variables
	public String hotspotIP;
	public int hotspotPasscode;
	public int connectivity = 0; // (0) no session/default, (1) wifi, (2) hotspot
	
	// Get Output
	private InputStream is;
	private BufferedReader br;
	
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
		exitExp = new ProcessBuilder("taskkill", "/F", "/IM", "explorer.exe");
		openExp = new ProcessBuilder("explorer.exe");
		
		createTask = new ProcessBuilder("schtasks", "/Create", "/TN", "NetSecuritySystem", "/TR", "<Directory to the app>", "/SC", "ONLOGON");
		removeTask = new ProcessBuilder("schtasks", "/Delete", "/TN", "NetSecuritySystem", "/F");
		
		killMgr = new ProcessBuilder("taskkill", "/F", "/IM", "taskmgr.exe");
		
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
		
		gui.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectivity = 0;
				gui.cardLayout.show(gui.container, "hostPanel");
				gui.repaint();
			}
			
		});
		
		gui.dcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(clientVerified) {
					try {
						connectivity = 0;
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
					connectivity = 0;
					cancelHostConnection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
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

	}
	
	// MAIN - Runs the application from GUI
	public static void main(String[] args) {
		MainApp gui = new MainApp();
		Functions f = new Functions();
		f.gui.setVisible(true);
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
						//serverPort = generatePort();
						
						// Establish a ServerSocket connection
				        while(true) {
							ss = new ServerSocket(serverPort);
							System.out.println(ss.getLocalPort());
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
										gui.repaint();
										gui.hostIPHostName.setText(hostIP + " - " + hostName);
										pw.println("gnssVerified" + hostPasscode);
										clientVerified = true;

										InetAddress ia = cs.getInetAddress();
										System.out.println("Client Local IP Address: " + ia.getHostAddress());
										runInputListener(br);
										chkNetworkConnection();
										chkDeviceConnection();
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
                                System.out.println("Connected but incorrect passcode.");
                                cs.close();
                                pw.close();
                                br.close();
                            } else if (message.equals("accessDenied")) {
                                System.out.println("Attempts ran out. Disconnected from session.");
                                cs.close();
                                pw.close();
                                br.close();
                                break;
                            } else if (message.equals("gnssVerified"+hotspotPasscode)) {
                                System.out.println("Successful Connection");
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
								//chkNetworkConnection();
								//chkDeviceConnection();
                                break;
                            }
                        }

                        // Detects if the client socket connection disconnects to the server.
                        else{
                            System.out.println("Server disconnected!");
                            System.out.println("Client/Server disconnected!");
                            break;
                        }

                    } else {
                        System.out.println("Socket isn't connected.");
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
			System.out.println("Connecting to hotspot...");
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
	
	// Generates a random port
	private int generatePort() {
		return (int)(Math.random()*(MAX_PORT_RANGE-MIN_PORT_RANGE))+MIN_PORT_RANGE;
	}
	
	// Generates a random passcode
	private int generatePasscode() {
		hostPasscode = (int)(Math.random()*(100000-10000))+10000;
		return hostPasscode;
	}
	
	// Runs the InputListener (listens for incoming input)
	public void runInputListener(BufferedReader br) {
		// InputStream
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						System.out.println("Running in input function");
						String output = br.readLine();
						if(clientVerified) {
							command(output);
							System.out.println(output);
						}
					} catch (IOException e) {
						try {
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
//			case "toggleControlMode":
//				setControlMode();
//				break;
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
	private void lockPC() throws IOException {
		if(unlocked) {
			unlocked = false;
			System.out.println("[!] Locking PC!");
			gui.lsCardLayout.show(gui.lsContainer, "lockPanel");
			gui.uniquePasscodeInput.setText("");
			gui.inputStatusLbl.setVisible(false);
			//gui.w.setVisible(true);
			//gui.w.setAlwaysOnTop(true); // ONLY SET TO "true" when app is done ;)
			
			// (Activate all security measures)
			
			// 1. Disable explorer.exe (exec once)
			//p = exitExp.start();
			
			// 2. Disable selected hotkeys (loop exec)
			disableAltKey();
			
			// 3. Disable taskmgr when opened (loop exec !CAUTION!)
			disableTaskMgr();
			
			// 4. Enable application to open on startup (exec once)
		}
		else {
			System.out.println("Device is already locked!");
		}
	}
	
	// Unlocks PC (removes all security measures and user gains access to its PC)
	private void unlockPC() throws IOException {
		if(!unlocked) {
			unlocked = true;
			System.out.println("[!] Unlocking PC!");
			gui.w.setVisible(false);
			
			// (Deactivate all security measures)
			
			// 1. Enable explorer.exe
			//p = openExp.start();
			
			// 2. Enable hotkeys
			t5.interrupt();
			
			// 3. Enable taskmanager when opened
			t4.interrupt();
			System.out.println("Taskmanager killer has stopped.");
			
			// 4. Remove application to open on startup
		}
		else {
			System.out.println("Device is already unlocked!");
		}
	}
	
	// Sets control mode (Automatically/Manually locks/unlocks PC)
	private void setControlMode() {
		System.out.println("[!] Control mode set!");
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
						System.out.println("Waiting for client connection for connection checker...");
						sCon = ssCon.accept();
						sCon.setSoTimeout(2000);
					}
					else if(connectivity == 2) {
						sCon = new Socket("",1);
						sCon.setSoTimeout(2000);
					}

					isCon = sCon.getInputStream();
					brCon = new BufferedReader(new InputStreamReader(isCon));

					String heartbeatMessage = "HEARTBEAT";
					outCon = sCon.getOutputStream();

					byte[] buffer = new byte[1024];
					int bytesRead;

					String line;

					while (true) {

						if (t6.isInterrupted()) {
							sCon.close();
							if(connectivity == 1)
								ssCon.close();
							isCon.close();
							brCon.close();
							break;
						} else if ((bytesRead = isCon.read(buffer)) != -1) {
							String message = new String(buffer, 0, bytesRead);
							if (message.equals("HEARTBEAT")) {
								// System.out.println("Received heartbeat from client: " +
								// sCon.getInetAddress().toString().replace('/', '\s'));
								outCon.write(heartbeatMessage.getBytes());
								outCon.flush();
							}
						} else
							break;
					}

				} catch (IOException e) {
					System.out.println("Client disconnected!");
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
						e1.printStackTrace();
					}
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
							System.out.println("Connected to a network.");
							if(retryConnection) {
								retryConnection = false;
							}
						} 
						else {
							System.out.println("Not connected to a network...");
							if(!retryConnection) {
								if(t6.isAlive()) {
									t6.interrupt();
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
						System.out.println("Network Connection checker interrupted!");
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
		if(connectivity == 1)
			ssCon.close();
		is.close();
		out.close();
		br.close();
		pw.close();
	}
	
	// Closes the socket connection, therefore two devices will disconnect
	private void disconnect() throws IOException {
		System.out.println("[!] Disconnecting!");
		clientVerified = false;
		pw.println("disconnect");
		t6.interrupt(); // chkDeviceConnection
		t7.interrupt(); // chkNetworkConnection
		if(t8 != null)
			if(t8.isAlive())
				t8.interrupt();
		
		unlockPC();
		
		cs.close();
		ss.close();
		is.close();
		out.close();
		br.close();
		pw.close();
		gui.cardLayout.show(gui.container, "hostPanel");
		t1.interrupt();
		gui.repaint();
	}
	
	// Cancels the host connection
	private void cancelHostConnection() throws IOException {
		System.out.println("[!] Disconnecting!");
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
						System.out.println("Retrying connection to client...");

						ss = new ServerSocket(serverPort);
						ss.setSoTimeout(5000);
						cs = ss.accept();
						Thread.sleep(0);

						is = cs.getInputStream();
						br = new BufferedReader(new InputStreamReader(is));
						out = cs.getOutputStream();
						pw = new PrintWriter(out, true);
						if (cs.isConnected()) {
							System.out.println("Server has connected to client successfully!");
							unlockPC();
							runInputListener(br);
							chkDeviceConnection();
							break;
						}
					} catch (IOException e) {
						try {
							ss.close();
							cs.close();
						} catch (IOException e1) {
							System.out.println("Error in closing serversocket.");
							e1.printStackTrace();
						}
						e.printStackTrace();
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		t8 = new Thread(r);
		t8.start();
	}
	
	/*
		 Prohibited Hotkeys in Lock Mode:
		 
		 Win + L = Windows Lock Screen
		 Alt + F4 = Exit Application/Shutdown
		 Alt + Tab = Change application view
		 Win + R = Run command
	*/
}