//package com.edwinfloyd.battleship;
// Testing
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import javax.swing.*;

public class BattleServer extends JFrame {
	private ServerSocket serverSocket = null;
	private int clientMessage;
	public int thisClient;
    String p1Name = "";
    String p2Name = "";
    int[] p1LastMove = {-1, -1, -1, -1, -1, -1};
    int[] p2LastMove = {-1, -1, -1, -1, -1, -1};
    int[][] p1ShipGrid = new int[10][10];
    int[][] p2ShipGrid = new int[10][10];
    boolean p1LastWasHit = false;
    boolean p2LastWasHit = false;
    Thread t1;
    Thread t2;
    Thread gameThread1;
    Thread gameThread2;
	int p1Hits = 0;
	int p2Hits = 0;
    boolean endGame = false;
    boolean gameOver = false;
    Socket socket;
    private final int MAX_HITS = 17; 
    Package oldOut = new Package();
	//Text area to display server log information.
	private JTextArea jta = new JTextArea();
	private final int CARRIER = 5;
	private final int B_SHIP = 1;
	private final int SUB = 2;
	private final int DESTROYER = 3;
	private final int PATROL = 4;
	
	
	
	public BattleServer(){
		// Sets up the BattleServer view window
		setLayout(new BorderLayout());
	    add(new JScrollPane(jta), BorderLayout.CENTER);
	    setTitle("BattleServer");
	    setSize(700, 750);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);

		try{
			serverSocket = new ServerSocket(8000);
			jta.append("BattleServer has been started at " + new Date() + "\n");
			jta.append("Waiting for connections.\n");
			
			int clientNumber = 1;
			while(clientNumber <= 2){
				
				this.socket = serverSocket.accept();
				jta.append("Initializing Connection...\n");
				InetAddress ia = socket.getInetAddress();
				jta.append("Client " + clientNumber + " host: " + ia.getHostName() + "\n");
				jta.append("Client " + clientNumber + " ip addr: " + ia.getHostAddress()+ "\n");
				jta.append("Client number " + clientNumber + " has been assigned to this client.\n");
				jta.append("Starting client thread for client no: " + clientNumber + "\n");
				HandleAClient cTask = new HandleAClient(socket, clientNumber);
				
				// Start a Thread for each client connection. 
				if (clientNumber == 1){
					t1 = new Thread(cTask, "Thread 1");
					jta.append("Waiting for Player 2.\n");
					t1.start();
					
					//setDefaultShips(clientNumber);
					//playBattleship gTask = new playBattleship();
					//gameThread1 = new Thread(gTask);
				}
				else{
					t2 = new Thread(cTask, "Thread 2");
					jta.append("Player 2 has connected.\n");
					t2.start();
					
					//setDefaultShips(clientNumber);
					//playBattleship gTask = new playBattleship();
					//gameThread2 = new Thread(gTask);
				}
				clientNumber++;
			}
			t1.join();
			t2.join();
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}
	public void setDefaultShips(){  //Defaults should be same for both clients
									//... so no need to differentiate between clients
		
		//EF - removed if/esle and removed else block
		
		// Carrier
		p1ShipGrid[0][0] = 5;
		p1ShipGrid[0][1] = 5;
		p1ShipGrid[0][2] = 5;
		p1ShipGrid[0][3] = 5;
		p1ShipGrid[0][4] = 5;
		// BattleShip
		p1ShipGrid[1][0] = 1;
		p1ShipGrid[1][1] = 1;
		p1ShipGrid[1][2] = 1;
		p1ShipGrid[1][3] = 1;
		// Sub
		p1ShipGrid[2][0] = 2;
		p1ShipGrid[2][1] = 2;
		p1ShipGrid[2][2] = 2;
		// Destroyer
		p1ShipGrid[3][0] = 3;
		p1ShipGrid[3][1] = 3;   //correction - EF
		p1ShipGrid[3][2] = 3;   //correction - EF
		// Patrol
		p1ShipGrid[4][0] = 4;
		p1ShipGrid[4][1] = 4;
	}
	
	class HandleAClient implements Runnable{
		private Socket socket;
		private int clientNumber;
		
		public HandleAClient(Socket socket, int clientNumber){
			this.socket = socket;
			this.clientNumber = clientNumber;
		}

		public void run(){
			
			try{
				DataOutputStream playerNumberSender = new DataOutputStream(socket.getOutputStream()); 
				boolean init = true;
				// Assign the client an ID number.
				while(init == true){
					playerNumberSender.writeInt(this.clientNumber);
					playerNumberSender.flush();
					jta.append("The client has been sent an ID number.\n");
					init = false;
				}
				
				
				// Initial Ship Setup
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.flush();  // Needed to prevent hanging on initialization
				boolean setup = true;
				jta.append("Setup has started.\n");
				int numShips = 0;
				// int[idNumber][x-cord][y-cord][setup=0, play=1][shipType][orientation 0=horizontal, 1=Vert, -1=N/A]
				// Expected array format
				while(setup == true){
					jta.append("Waiting on Client... \n");
					Package clientInMessage = (Package) inputStream.readObject();
					jta.append("A Package has been received.\n");
					if (clientInMessage.playerData[3] == 0){
						// Store Ship Locations
						storeShip(clientInMessage.playerData);
						
						// Print Package Received
						for (int i=0; i<clientInMessage.playerData.length; i++)
							jta.append(clientInMessage.playerData[i] + ", ");
						jta.append("\n");
						// For Testing Print the 10x10 Array for verification.
						if (clientInMessage.playerData[0]==1){
							System.out.println("Player 1 Ship Grid");
							for(int i=0; i<10; i++){
									for(int j=0; j<10; j++){
										System.out.print(p1ShipGrid[i][j] + ", ");	
									}
									System.out.println("");
								}
								System.out.println("");
							}
						else{
							System.out.println("Player 2 Ship Grid");
							for(int i=0; i<10; i++){
								for(int j=0; j<10; j++){
									System.out.print(p2ShipGrid[i][j] + ", ");	
								}
								System.out.println("");
								
							}
						}
						
						Package pkg = new Package(true); // Assumes valid input
						outputStream.writeObject(pkg);
						outputStream.flush();
						
						
						// Run Setup Code here
						// call is validPlacement() for each ship and location sent.
						// Store valid ship locations in p1ShipGrid and p2ShipGrid
						
						// Uncomment line 108 - 122 when validPlacement() is built.
//						if ( validPlacement() ){
//							jta.append("Valid ship placement for ship number: " + numShips);
//							numShips++;
//							// Insert code here to send info back to the player
//							Package clientOutMessage = new Package(9,9,9,9,9,9); // Should be called with returned values
//							outputStream.writeObject(clientOutMessage);
//							outputStream.flush();
//						}
//						else{
//							jta.append("Invalid ship placement for ship number: " + numShips);
//							// Insert code here to send info back to the player
//							Package clientOutMessage = new Package(9,9,9,9,9,9); // Should be called with returned values
//							outputStream.writeObject(clientOutMessage);
//							outputStream.flush();
//						}	
					}
					
					// Exits Setup Mode
					// Client GUI "Play" clicked should trigger this block
					else if(clientInMessage.playerData[3] == 99){
						outputStream.writeObject(new Package(99,99,99,99,99,99));
						outputStream.flush();
						setup = false;
					}
				} // Ends While Loop setup = true

				
				// Get and Set Names
				boolean names = false;
				boolean p1Sent = false;
				boolean p2Sent = false;
				Player tempPlayer = new Player();
				tempPlayer = (Player) inputStream.readObject();
				while( (p1Name.equals("") || p2Name.equals("")) && names == false ){
					// Get Player1's Name
					if (tempPlayer.getPlayerId() == 1){
						p1Name = tempPlayer.getName();
						jta.append("Player 1's name is " + p1Name + "\n");
						if (p2Name.equals(""))
							TimeUnit.MILLISECONDS.sleep(1000);
					}
					// Get Player2's Name
					else{
						p2Name = tempPlayer.getName();
						jta.append("Player 2's name is " + p2Name + "\n");
						if (p1Name.equals(""))
							TimeUnit.MILLISECONDS.sleep(1000);
					}
					// Send the names of the respective Opponents.
					if( !p1Name.contentEquals("") && !p2Name.contentEquals("") ){
						if(tempPlayer.getPlayerId() == 1){
							Player oppPlayer = new Player(p2Name);
							outputStream.writeObject(oppPlayer);
							outputStream.flush();
							jta.append("Player2's name has been sent to Player1.\n");
							p1Sent = true;
						}
						else{
							Player oppPlayer = new Player(p1Name);
							outputStream.writeObject(oppPlayer);
							outputStream.flush();
							jta.append("Player1's name has been sent to Player2.\n");
							p2Sent = true;
						}
						// Make sure both names are sent before exiting setup mode. 
						if (p1Sent && p2Sent)
							names = true;
					}
				} // Ends Transfer of Player names
				
					
				// Play battleship...
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				out.reset();
				
				Package outPkg = new Package();
				Package tempPkg = new Package();
				int count = 0;
				// int[] oppLastMove = new int[6];
				jta.append("The game has started.\n");
				while (gameOver == false){
				// Insert Code here to play the game
					try{
						if (p1Hits == MAX_HITS || p2Hits == MAX_HITS){
							gameOver = true;
						}
						else{
							jta.append("Waiting for player move.\n");
							
							tempPkg = (Package) in.readObject();
							
							// check for hits and misses from Player 1
							if (tempPkg.playerData[0] == 1){
								jta.append("Player 1 has guessed: ");
								p1LastMove = tempPkg.playerData;
								jta.append(tempPkg.playerData[1] + ", " + tempPkg.playerData[2] + ".\n");
								// If it is a hit...
								if ( (p2ShipGrid[tempPkg.playerData[1]][tempPkg.playerData[2]]) != 0){
									p1Hits++;
									outPkg = tempPkg;
									outPkg.setHit(true);
									outPkg.setOppLastMove(p2LastMove[0], p2LastMove[1], p2LastMove[2], p2LastMove[3], p2LastMove[4], p2LastMove[5]);
									outPkg.setWasHit(p2LastWasHit);
									out.writeObject(outPkg);
									out.flush();
	
									oldOut = outPkg;
									p1LastWasHit = true;
									jta.append("It was a hit.\n");
								}
								// If it is a miss...
								else{
									outPkg = tempPkg;
									outPkg.setHit(false);
									outPkg.setOppLastMove(p2LastMove[0], p2LastMove[1], p2LastMove[2], p2LastMove[3], p2LastMove[4], p2LastMove[5]);
									outPkg.setWasHit(p2LastWasHit);
									out.writeObject(outPkg);
									out.flush();
	
									oldOut = outPkg;
									p1LastWasHit = false;
									jta.append("It was a miss.\n");
								}
	
							}
							
							// Check for hits and misses from Player 2
							else if(tempPkg.playerData[0] == 2){
								jta.append("Player 2 has guessed: ");
								p2LastMove = tempPkg.playerData;
								jta.append(tempPkg.playerData[1] + ", " + tempPkg.playerData[2] + ".\n");
								// If its a hit...
								if ( (p1ShipGrid[tempPkg.playerData[1]][tempPkg.playerData[2]]) != 0){
									p2Hits++;
									outPkg = tempPkg;
									outPkg.setHit(true);
									outPkg.setOppLastMove(p1LastMove[0], p1LastMove[1], p1LastMove[2], p1LastMove[3], p1LastMove[4], p1LastMove[5]);
									outPkg.setWasHit(p1LastWasHit);
									out.writeObject(outPkg);
									out.flush();
	
									oldOut = outPkg;
									p2LastWasHit = true;
									jta.append("It was a hit.\n");
								}
								// If it is a miss...
								else{
									outPkg = tempPkg;
									outPkg.setHit(false);
									outPkg.setOppLastMove(p1LastMove[0], p1LastMove[1],p1LastMove[2], p1LastMove[3], p1LastMove[4], p1LastMove[5]);
									outPkg.setWasHit(p1LastWasHit);
									out.writeObject(outPkg);
									out.flush();
	
									oldOut = outPkg;
									p2LastWasHit = false;
									jta.append("It was a miss.\n");
								}
	
							}
						}
					}
					finally{
						// Not sure this is needed
					}
				}
				
				// End Game Logic Here
				try{
					int i=0;
					while (i<100000){
						Package endPkg = new Package();
						endPkg.setGameOver(true);
						if (p1Hits == 17){
							endPkg.setWinner(1);
							jta.append("Player 1 is the winner.\n");
						}
						else{
							endPkg.setWinner(2);
							jta.append("Player 2 is the winner.\n");
						}
						out.writeObject(endPkg);
						out.flush();
						i++;
					}
					jta.append("Game Over.");
					socket.close();
				}
				catch(SocketException se){
					TimeUnit.MILLISECONDS.sleep(12000);
				}
				System.exit(0);
				
			}catch(IOException ex){
				ex.printStackTrace();
			}
			catch (ClassNotFoundException clf){
				clf.printStackTrace();
			}
			catch (InterruptedException ie){
				ie.printStackTrace();
			}
			
		}
		// int[idNumber][x-cord][y-cord][setup=0, play=1][shipType][orientation 0=horizontal, 1=Vert, -1=N/A]
		public void storeShip(int[] message){
			int[] m = null;
			m = message;
			if (m[0] == 1){
				// Store the ship to Player1's grid
				if(m[4] == CARRIER){
					// CARRIER
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<5; i++){
							p1ShipGrid[m[1]+i][m[2]] = CARRIER;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<5; i++){
							p1ShipGrid[m[1]][m[2]+i] = CARRIER;
						}
					}
				}
				else if(m[4] == B_SHIP){
					// B_SHIP
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<4; i++){
							p1ShipGrid[m[1]+i][m[2]] = B_SHIP;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<4; i++){
							p1ShipGrid[m[1]][m[2]+i] = B_SHIP;
						}
					}
				}


				else if(m[4] == SUB){
					// SUB
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<3; i++){
							p1ShipGrid[m[1]+i][m[2]] = SUB;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<3; i++){
							p1ShipGrid[m[1]][m[2]+i] = SUB;
						}
					}
				}


				else if(m[4] == DESTROYER){
					//DESTROYER
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<3; i++){
							p1ShipGrid[m[1]+i][m[2]] = DESTROYER;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<3; i++){
							p1ShipGrid[m[1]][m[2]+i] = DESTROYER;
						}
					}
				}


				else if(m[4] == PATROL){
					// Patrol
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<2; i++){
							p1ShipGrid[m[1]+i][m[2]] = PATROL;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<2; i++){
							p1ShipGrid[m[1]][m[2]+i] = PATROL;
						}
					}
				}
			}	
	//-----------------------------------------------------------------------------
	// PLACE SHIP IN PLAYER 2'S GRID
	//-----------------------------------------------------------------------------
			
			else{
				// Store the ship to Player2's grid
				if(m[4] == CARRIER){
					// CARRIER
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<5; i++){
							p2ShipGrid[m[1]+i][m[2]] = CARRIER;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<5; i++){
							p2ShipGrid[m[1]][m[2]+i] = CARRIER;
						}
					}
				}


				else if(m[4] == B_SHIP){
					// B_SHIP
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<4; i++){
							p2ShipGrid[m[1]+i][m[2]] = B_SHIP;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<4; i++){
							p2ShipGrid[m[1]][m[2]+i] = B_SHIP;
						}
					}
				}


				else if(m[4] == SUB){
					// SUB
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<3; i++){
							p2ShipGrid[m[1]+i][m[2]] = SUB;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<3; i++){
							p2ShipGrid[m[1]][m[2]+i] = SUB;
						}
					}
				}


				else if(m[4] == DESTROYER){
					//DESTROYER
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<3; i++){
							p2ShipGrid[m[1]+i][m[2]] = DESTROYER;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<3; i++){
							p2ShipGrid[m[1]][m[2]+i] = DESTROYER;
						}
					}
				}


				else if(m[4] == PATROL){
					// Patrol
					if (m[5] == 0){
						// Place ship Horizontal
						for(int i=0; i<2; i++){
							p2ShipGrid[m[1]+i][m[2]] = PATROL;
						}
					}
					else{
						// Place ship Vertical
						for(int i=0; i<2; i++){
							p2ShipGrid[m[1]][m[2]+i] = PATROL;
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args){
		new BattleServer();
	}
}
