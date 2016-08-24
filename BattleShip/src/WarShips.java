//package com.edwinfloyd.battleship;

//-- WARSHIP CLASS CREATES WARSHIP OBJECTS -------------------------
public class WarShips {
	//Constants for warship types, maybe make static somehow so
	//... only have to be on one java file? Don't know, probably not though
	private final int A_CARRIER = 5;
	private final int B_SHIP = 1;
	private final int SUB = 2;
	private final int DEST  = 3;
	private final int PAT = 4;
	
	//Ship Elements
	private int shipType;	//Type of ship
	private Point p;		//Significant point location of the ship
	private Point ep;		//<-- other end of the ship location, probably don't need this
	private boolean orientation;	//Ship's orientation
	
	//General constructor
	public WarShips(){
		
	}
	
	//Explicit constructor
	public WarShips(int shipType, Point p, boolean orientation){
		this.shipType = shipType;
		this.p = p;
		this.orientation = orientation;
	}
	
	//Setters and Getters for warship objects are below...
	public void setupWarship(int shipType, Point p, boolean orientation){
		this.shipType = shipType;
		this.p = p;
		this.orientation = orientation;
	}
	
	public void setWarShipLocation(Point p){
		this.p = p;
	}
	
	public void setWarShipOrientation(boolean orientation){
		this.orientation = orientation;
	}
	
	public void setWarShipType(int shipType){
		this.shipType = shipType;
	}
	
	public Point getWarShipLocation(){
		return this.p;
	}
	
	public boolean getWarShipOrientation(){
		return this.orientation;
	}
	
	public int getWarShipType(){
		return shipType;
	}
}
