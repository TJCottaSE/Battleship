//package com.edwinfloyd.battleship;
import java.io.Serializable;

//-- DEFINE A PLAYER --------------------------------------
public class Player implements Serializable {
	private static final long serialVersionUID = 596463436346L;
	private String playerName;
	private int playerTurnState;
	private int playerId;
	
	public Player(){
		
	}
	
	//Alternate constructor to explicitly define a name
	public Player(String name){	
		playerName = name;
	}
	
	// Stand alone method to define a name
	public void setName(String name){
		playerName = name;
		System.out.println(playerName);
	}
	
	// get a name
	public String getName(){
		return playerName;
	}
	
	public void setPlayerId(int id){
		playerId = id;
	}
	
	public int getPlayerId(){
		return playerId;
	}
	
	//Turnstates to keep track of, not sure if this is needed
	//... doesn't hurt anything here though, can remove if not
	//... needed.
	public void setTurnState(int state){
		playerTurnState = state;
	}
	
	public int getTurnState(){
		return playerTurnState;
	}
}
