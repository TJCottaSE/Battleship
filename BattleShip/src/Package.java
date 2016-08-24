//package com.edwinfloyd.battleship;

import java.io.Serializable;

public class Package implements Serializable {

    private static final long serialVersionUID = 596563436346L;
    int[] playerData = new int[6];
    int[] oppLastMove = new int[6];
    boolean isValid = false;
    boolean isHit = false;
    boolean gameOver = false;
    boolean p1TurnStatus = true;
    boolean wasHit = false;
    int winner = 0;
    
    public Package() {
    	this.playerData[0] = -1;
    	this.playerData[1] = -1;
    	this.playerData[2] = -1;
    	this.playerData[3] = -1;
    	this.playerData[4] = -1;
    	this.playerData[5] = -1;
    }
    public Package(boolean S){
    	isValid = S;
    }
    public Package(int ID, int xCord, int yCord, int mode, int shipType, int orientation){
    	this.playerData[0] = ID;
    	this.playerData[1] = xCord;
    	this.playerData[2] = yCord;
    	this.playerData[3] = mode;
    	this.playerData[4] = shipType;
    	this.playerData[5] = orientation;
    }
    public void setValid (){
    	isValid = true;
    }
    public void setInvalid(){
    	isValid = false;
    }
    public void setHit(boolean H){
    	isHit = H;
    }
    public void setGameOver(boolean G){
    	gameOver = G;
    }
    public void setP1Status(boolean P){
    	p1TurnStatus = P;
    }
    public void setOppLastMove(int ID, int xCord, int yCord, int mode, int shipType, int orientation){
    	oppLastMove[0] = ID;
    	oppLastMove[1] = xCord;
    	oppLastMove[2] = yCord;
    	oppLastMove[3] = mode;
    	oppLastMove[4] = shipType;
    	oppLastMove[5] = orientation;
    }
    public void setWasHit(boolean W){
    	wasHit = W;
    }
    public void setWinner(int i){
    	winner = i;
    }
}

