//package com.edwinfloyd.battleship;

//-- POINT CLASS, MAY BE EADIER THAN ALWAYS ENTERING X, Y COORDS
public class Point {
	private int x;
	private int y;
	private byte bx;
	private byte by;
	
	//Generic point constructor
	public Point(){
		
	}
	
	//Explicit Point Constructor for integers
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	//Explicit Point Constructor for bytes
	public Point(byte x, byte y){
		bx = x;
		by = y;
	}
	
	//Getter / Setters for point objects
	public void setPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void setPoint(byte x, byte y){
		
	}
	
	public int getIntXPoint(){
		return x;
	}
	
	public int getIntYPoint(){
		return y;
	}
}
