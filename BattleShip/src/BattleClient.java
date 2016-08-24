//package com.edwinfloyd.battleship;
//Testing

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


//-- BATTLESHIP CLIENT, COMMUNICATES TO MULTITHREADED SERVER
public class BattleClient {
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private int[] playerData = new int[6];
	private int thisPlayer;
	private BattleshipGUI p1GUI;
	private BattleshipGUI p2GUI;


	public static void main(String[] args){
		new BattleClient();
	}
	
	public BattleClient(){
		
		//Create a socket to connect to the host server
		try{
			Socket socket = new Socket("localhost", 8000);
			boolean init = true;
			Player player = new Player();
			
			
			// Initialization of player numbers and GUI Instantiations.
			while(init == true){
				fromServer = new DataInputStream(socket.getInputStream());
				toServer = new DataOutputStream(socket.getOutputStream());
				
				thisPlayer = fromServer.readInt();
				if(thisPlayer == 1){
					player.setPlayerId(1);
					p1GUI = new BattleshipGUI(player, this);
					System.out.println("Player 1");
					init = false;
					//At this point the GUI Is running for player 1.
				}
				else if(thisPlayer == 2){
					player.setPlayerId(2);
					p2GUI = new BattleshipGUI(player, this);
					System.out.println("Player 2");
					init = false;
					//At this point the GUI is running for player 2.
				}
				else
					System.exit(0);
				// Exits the system if a third client tries to join
				// this might need not be here. Could be managed from the server
			}
			
			
			// Set up ships on game board
			boolean setup = true;
			Package pkgOld = new Package();
			Package inPkgOld = new Package();
			Package inPackage = new Package();
			ObjectOutputStream ObjToServer = new ObjectOutputStream(socket.getOutputStream());
			ObjToServer.flush();  // Needed to prevent hanging on initialization
			ObjectInputStream ObjFromServer = new ObjectInputStream(socket.getInputStream());
			boolean waitOnPlayer = false;
			while (setup == true){
				Package pkg = new Package(); 
				pkg = packPlayerData();
				// Wait for new ship placement before sending
				if ( Arrays.equals(pkg.playerData, pkgOld.playerData) ){
					// Allows time for the player to make another move.
					try{
						TimeUnit.MILLISECONDS.sleep(400);
						waitOnPlayer = true;
					}
					catch (InterruptedException IE){
						IE.printStackTrace();
					}
				}
				
				// Send the new placement
				else{
					ObjToServer.writeObject(pkg);
					ObjToServer.flush();
					waitOnPlayer = false;
				}
				pkgOld = pkg;
				
				// Get reply from server
				if(!waitOnPlayer){
					inPackage = (Package) ObjFromServer.readObject();
					// Check if still in setup mode
					if ( !(inPackage.playerData[3] == 99) ){
						// Check if valid ship placement
						if (!inPackage.isValid){
							System.out.println("That is an invalid ship locaton");
							// Probably need to do some thing with the GUI here. 
						}
					}
					// Exit Setup Mode
					else{
						setup = false;
					}
				}
			}	// If inPackage.playerData[3] == 99 -> Exit Setup Mode.
			
			
			// Send Player Name and get opponent name
			Player tempPlayer = new Player();
			ObjToServer.writeObject(player);
			ObjToServer.flush();
			tempPlayer = (Player) ObjFromServer.readObject();
			if(thisPlayer == 1){
				p1GUI.setOpponentName(tempPlayer.getName());
			}
			if(thisPlayer == 2){
				p2GUI.setOpponentName(tempPlayer.getName());
			}	

			
			// Play the game
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			waitOnPlayer = false;
			boolean gameOver = false;
			Package pkg1st = new Package();
			pkg1st = packPlayerData();
			out.writeObject(pkg1st);
			pkgOld = pkg1st;
			while(gameOver == false){
				Package pkg = new Package(); 
				pkg = packPlayerData();
				// Wait for new Guess
				if ( Arrays.equals(pkg.playerData, pkgOld.playerData) ){
					// Allows time for the player to make another move.
					try{
						TimeUnit.MILLISECONDS.sleep(400);
						waitOnPlayer = true;
					}
					catch (InterruptedException IE){
						IE.printStackTrace();
					}
				}
				// Send the new guess
				else{
					ObjToServer.writeObject(pkg);
					ObjToServer.flush();
					waitOnPlayer = false;
				}
				pkgOld = pkg;
				// Get reply from server
				if(!waitOnPlayer){
					try{
						TimeUnit.MILLISECONDS.sleep(100);
					}
					catch (InterruptedException IE){
						IE.printStackTrace();
					}
					inPackage = (Package) in.readObject();
					if(inPackage.playerData[3] == 99){
					}	// Catches leftover setup values.
				else{
					// This may not need to be here.
					if( Arrays.equals(inPackage.playerData, inPkgOld.playerData) ){
						try{
							TimeUnit.MILLISECONDS.sleep(1000);
						}
						catch(InterruptedException ex){
							ex.printStackTrace();
						}
					}
					else{
						// Check if the game is over
						if ( inPackage.gameOver ){
							gameOver = true;
							// Call pop-up to notify game over
							if (thisPlayer == 1){
								if(inPackage.winner == 1)
									p1GUI.showWinnerLoser(true);
								else
									p1GUI.showWinnerLoser(false);
							}
							else{
								if(inPackage.winner == 2)
									p2GUI.showWinnerLoser(true);
								else
									p2GUI.showWinnerLoser(false);
							}
							try{
								TimeUnit.MILLISECONDS.sleep(10000);
							}
							catch(InterruptedException ex){
								ex.printStackTrace();
							}
							System.exit(0);
						}
						
						else if(inPackage.isHit){
							// Call the Hit marker on the GUI
							System.out.print("Hit was received");
							if(thisPlayer == 1){
								if (inPackage.playerData[0] == 1)
									p1GUI.markMyGuess(inPackage.playerData[1], inPackage.playerData[2], inPackage.isHit);
							}
							if(thisPlayer == 2){
								if (inPackage.playerData[0] == 2)
									p2GUI.markMyGuess(inPackage.playerData[1], inPackage.playerData[2], inPackage.isHit);
							}	
						}
						
						else{
							// Call the Miss marker on the GUI
							System.out.print("Miss was received");
							if(thisPlayer == 1){
								if (inPackage.playerData[0] == 1)
									p1GUI.markMyGuess(inPackage.playerData[1], inPackage.playerData[2], inPackage.isHit);
							}
							if(thisPlayer == 2){
								if (inPackage.playerData[0] == 2)
									p2GUI.markMyGuess(inPackage.playerData[1], inPackage.playerData[2], inPackage.isHit);
							}
						}
						
						// Call hits and misses from opponent
						if (inPackage.oppLastMove[0] != -1){
							if(thisPlayer == 1)
								p1GUI.markOpponentGuess(inPackage.oppLastMove[1], inPackage.oppLastMove[2], inPackage.wasHit);
							else
								p2GUI.markOpponentGuess(inPackage.oppLastMove[1], inPackage.oppLastMove[2], inPackage.wasHit);
						}
					inPkgOld = inPackage;
					}
				}
			}
		}
			
		socket.close();
		System.exit(0);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		catch(ClassNotFoundException clf){
			clf.printStackTrace();
		}
	}
	
	public void messageForClient(int[] data){
		//array elements = [ID, X, Y, MODE, SHIPTYPE, ORIENTATION]
		for(int i=0; i<6; i++){
			playerData[i] = data[i];
			System.out.println("Element " + i + ": " + playerData[i]);
		}
	}
	
	public Package packPlayerData(){
		Package pkg = new Package(this.playerData[0],this.playerData[1],this.playerData[2],this.playerData[3],this.playerData[4],this.playerData[5]);
		return pkg;
	}
	
}
