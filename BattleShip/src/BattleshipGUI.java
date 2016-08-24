//package com.edwinfloyd.battleship;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//Testing push

//-- BATTLESHIP GUI, SHOWS GAME, GAME PROGRESS, ETC -------------------------
public class BattleshipGUI extends JFrame{
	// Ship type constants ------------------
	private final int CARRIER = 5;
	private final int B_SHIP = 1;
	private final int SUB = 2;
	private final int DESTROYER = 3;
	private final int PATROL = 4;
	
	private final int ID = 0;
	private final int X_LOC = 1;
	private final int Y_LOC = 2;
	private final int MODE = 3;
	private final int SHIP_TYPE = 4;
	private final int ORIENTATION = 5;
	//---------------------------------------
	
	// GUI Elements -----------------------------------------------
	private JPanel banner = new JPanel();
	private JTextField player1 = new JTextField("Enter Name");
	JLabel player2 = new JLabel("Opponent");
	private JPanel gameGrid = new JPanel(new GridLayout(10, 10));
	private JPanel shotsFired = new JPanel();
	private JPanel opponentGrid = new JPanel(new GridLayout(10, 10));
	private JPanel shotsReceived = new JPanel();
	//private JPanel statusPane = new JPanel();
	private JTextPane status = new JTextPane();
	private JLabel[][] playerBox;
	private JLabel[][] opponentBox;
	private final String s = "[Status] ";
	private boolean clicked_ship = false;
	private String lastShip = "";
	private int last_i = 0, last_j = 0;
	private boolean last_orientation = false;
	private boolean lockLowerGrid = false;
	private boolean beforePlayButtonHit = true;
	//--------------------------------------------------------------
	
	//Player and Piece Elements ------------------------------------
	private Player player;
	private WarShips aircraftcarrier = new WarShips(CARRIER, new Point(0,0), false);
	private WarShips battleship = new WarShips(B_SHIP, new Point(0,1), false);
	private WarShips submarine = new WarShips(SUB, new Point(0,2), false);
	private WarShips destroyer = new WarShips(DESTROYER, new Point(0,3), false);
	private WarShips patrol = new WarShips(PATROL, new Point(0,4), false);
	private int[] playerData = new int[6];
	//---------------------------------------------------------------
	
	BattleClient bc; //static means visible to all, not sure if there are repercussions for this?
	
	//Instantiate and run a GUI and a Client for each player that connects
	
	//Constructor
	BattleshipGUI(Player p, BattleClient bc ){
		//Super class elements for root frame (the extended JFrame)
		this.bc = bc;
		player = p;
		setTitle("Battleship");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setPreferredSize(new Dimension(750, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(Color.darkGray);
		
		//Configure each of the panels for the GUI
		setupBanner();
		setupShotsFired();
		setupGameGrid();
		setupOpponentGrid();
		setupShotsReceived();
		setupStatusPane();
		
		//Modify the text int he stats pane
		addToStatusPane("Player id: " + player.getPlayerId());
		
		//Layout each of the panes so they look decent
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(banner, c);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(shotsFired, c);
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(gameGrid,c);
		c.gridx = 0;
		c.gridy = 3;
		add(shotsReceived,c);
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(opponentGrid,c);
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(status,c);
		
		//Initialize ship locations
		initShips();
		
		GamePanelsRepaint rp = new GamePanelsRepaint();
		new Thread(rp).start();
		
		//Make root frame visible (and internals) and pack the gui elements
		setVisible(true);
		pack();
	}
	
	//Configure the banner panel at the top
	private void setupBanner(){
		JLabel p1Name = new JLabel("Player Name");
		player1.addActionListener(new TextFieldListener());
		JButton play = new JButton("Play");
		JLabel p2Name = new JLabel("Opponent Name");
		player2.setForeground(Color.red); //<-- Need to populate with opponent name (other player name)
		
		play.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int[] someData = {99, 99, 99, 99, 99,99};
				bc.messageForClient(someData);
				lockLowerGrid = true;
				beforePlayButtonHit = false;
			}
		});
		
		banner.setBackground(Color.white);
		player1.setPreferredSize(new Dimension(100, 30));
		player2.setPreferredSize(new Dimension(100, 30));
		
		
		
		BannerRepaint br = new BannerRepaint();
		new Thread(br).start();
		
		//add widgets to the banner panel
		banner.add(p1Name);
		banner.add(player1);
		banner.add(play);
		banner.add(p2Name);
		banner.add(player2);
	}
	
	//Configure the shots fired panel, may need to add widgets here?
	private void setupShotsFired(){
		JLabel pl = new JLabel();
		pl.setText("Shots Fired");
		shotsFired.setBackground(Color.lightGray);
		shotsFired.add(pl);
	}
	
	//Setup the bombing guesses grid
	private void setupGameGrid(){
		gameGrid.setBackground(Color.cyan);
		gameGrid.setPreferredSize(new Dimension(700, 200));
		playerBox = new JLabel[10][10];
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				playerBox[i][j] = new JLabel("-", JLabel.CENTER);
				playerBox[i][j].setBackground(Color.white);
				playerBox[i][j].addMouseListener(new playerListener(i,j));
				gameGrid.add(playerBox[i][j]);
			}
		}
	}
	
	//Configure the shots received pane, may need to add widgets here?
	private void setupShotsReceived(){
		JLabel pl = new JLabel("Shots Received");
		shotsReceived.setBackground(Color.lightGray);
		shotsReceived.add(pl);
	}
	
	//Configure the opponents grid where attempt to sink a players battlehsips are displayed
	private void setupOpponentGrid(){
		opponentGrid.setBackground(Color.blue);
		opponentGrid.setPreferredSize(new Dimension(700, 200));
		opponentBox = new JLabel[10][10];
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				opponentBox[i][j] = new JLabel("-", JLabel.CENTER);
				opponentBox[i][j].setBackground(Color.white);
				opponentBox[i][j].addMouseListener(new oppListener(i,j));
				opponentGrid.add(opponentBox[i][j]);
			}
		}
	}
	
	//Status pane shows game information and details, should probably
	//... make it so the text moves up with each new addition so 
	//... previous details can still be seen, maybe scrollpane?
	private void setupStatusPane(){
		status.setBackground(Color.white);
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_LEFT);
		status.setParagraphAttributes(attribs,true);
		//statusPane.add(status);
	}
	
	//Method to add text to the status pane...
	private void addToStatusPane(String textToAdd){
		status.setText("\n" + s + textToAdd);
	}
	
	//Initial ship positions
	private void initShips(){
		Point acp = aircraftcarrier.getWarShipLocation();
		Point bsp = battleship.getWarShipLocation();
		Point sp = submarine.getWarShipLocation();
		Point dp = destroyer.getWarShipLocation();
		Point pp = patrol.getWarShipLocation();
		
		boolean aco = aircraftcarrier.getWarShipOrientation();
		boolean bso = battleship.getWarShipOrientation();
		boolean so = submarine.getWarShipOrientation();
		boolean dor = destroyer.getWarShipOrientation();
		boolean po = patrol.getWarShipOrientation();
		
		placeShipsOnGrid(aircraftcarrier, acp, aco, false);
		placeShipsOnGrid(battleship, bsp, bso, false);
		placeShipsOnGrid(submarine, sp, so, false);
		placeShipsOnGrid(destroyer, dp, dor, false);
		placeShipsOnGrid(patrol, pp, po, false);
	}
	
	//Method to place ships, would only be used prior to game play
	//... player should be able to move ships and change orientation 
	//... of the ships, moves must be legal and locked out after
	//... gameplay has started
	public void placeShipsOnGrid(WarShips w, Point p, boolean o, boolean reset){
		int shipLength = 0;
		String designator = "A";
		
		if(w.getWarShipType() == CARRIER){
			shipLength = 5;
			designator = "A";
		}
		else if(w.getWarShipType() == B_SHIP){
			shipLength = 4;
			designator = "B";
		}
		else if(w.getWarShipType() == SUB){
			shipLength = 3;
			designator = "S";
		}
		else if(w.getWarShipType() == DESTROYER){
			shipLength = 3;
			designator = "D";
		}
		else{
			shipLength = 2;
			designator = "P";
		}
		
		if(reset)
			designator = "-";
		
		if(o){	//if the orientation is horizontal
			//Place the warship type horizontally from point on board
			for(int i=0; i<shipLength; i++){
				opponentBox[p.getIntXPoint()][p.getIntYPoint()+i].setText(designator);
				if(reset)
					opponentBox[p.getIntXPoint()][p.getIntYPoint()+i].setForeground(Color.black);
				else
					opponentBox[p.getIntXPoint()][p.getIntYPoint()+i].setForeground(Color.white);
			}
		}
		else{	//otherwise the orientation is vertical
			for(int i=0; i<shipLength; i++){
				opponentBox[p.getIntXPoint()+i][p.getIntYPoint()].setText(designator);
				if(reset)
					opponentBox[p.getIntXPoint()+i][p.getIntYPoint()].setForeground(Color.black);
				else
					opponentBox[p.getIntXPoint()+i][p.getIntYPoint()].setForeground(Color.white);
			}
		}	
	}
	
	public void markOpponentGuess(int x, int y, boolean hit) {
		if(hit){
			opponentBox[x][y].setText("X");
			opponentBox[x][y].setForeground(Color.red);
		}
		else{
			opponentBox[x][y].setText("O");
			opponentBox[x][y].setForeground(Color.cyan);
		}
	}
	public void markMyGuess(int x, int y, boolean hit) throws ArrayIndexOutOfBoundsException{
		if(hit){
			playerBox[x][y].setText("X");
			playerBox[x][y].setForeground(Color.red);
		}
		else{
			playerBox[x][y].setText("O");
			playerBox[x][y].setForeground(Color.blue);
		}
	}
	
	public void setOpponentName(String s){
		player2.setText(s);
	}
	
	public void showWinnerLoser(boolean winOrLose){
		JOptionPane jp = new JOptionPane();
		String winner = new String(player.getName() + " is the winner");
		String loser = new String(player.getName() + " is the loser");
		
		if(winOrLose)
			jp.showMessageDialog(null, winner, "Winner", JOptionPane.INFORMATION_MESSAGE);
		else
			jp.showMessageDialog(null, loser, "Loser", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	//Listen for mouse clicks on the cyan board
	class playerListener extends MouseAdapter{
		int i, j;
		
		playerListener(int i, int j){
			this.i = i;
			this.j = j;
		}
		
		public void mousePressed(MouseEvent e){
			//bc.bombLocation(i, j);	//Send mouse click coordinates to the client
			if(!beforePlayButtonHit){
				playerData[ID] = player.getPlayerId();
				playerData[X_LOC] = i;
				playerData[Y_LOC] = j;
				playerData[MODE] = 1;
				playerData[SHIP_TYPE] = 0;
				playerData[ORIENTATION] = 0;
				bc.messageForClient(playerData);
				//System.out.println("Player Location: " + i + ", " + j); //display mouse click coords
				addToStatusPane("Bombing Location: " + "(" + i + " " + j + ")");
			}
			addToStatusPane("Still in setup mode");
		}
	}
	
	
	//Listen for mouse clicks on the blue board
	class oppListener extends MouseAdapter{
		int i, j;
		
		oppListener(int i, int j){
			this.i = i;
			this.j = j;
		}
		
		public void mousePressed(MouseEvent e){
			String s = opponentBox[i][j].getText();
			int thisShipIs = 0;
			boolean desiredOrientation = false;
			int dOrientation = 0;
			
			if(!lockLowerGrid){
				//If there's a right click, the place ships horizontally
				if(SwingUtilities.isRightMouseButton(e)){
					desiredOrientation = true;
				}
				
				//Move ships according to type and orientation
				if(s == "A" && clicked_ship == false){
					clicked_ship = true;
					addToStatusPane("clicked a ship: " + s);
					lastShip = "A";
					last_i = i;
					last_j = j;
					last_orientation = aircraftcarrier.getWarShipOrientation();
					if(desiredOrientation)
						aircraftcarrier.setWarShipOrientation(true);
					else
						aircraftcarrier.setWarShipOrientation(false);
				}
				else if(s == "B" && clicked_ship == false){
					clicked_ship = true;
					addToStatusPane("clicked a ship: " + s);
					lastShip = "B";
					last_i = i;
					last_j = j;
					last_orientation = battleship.getWarShipOrientation();
					if(desiredOrientation)
						battleship.setWarShipOrientation(true);
					else
						battleship.setWarShipOrientation(false);
				}
				else if(s == "S" && clicked_ship == false){
					clicked_ship = true;
					addToStatusPane("clicked a ship: " + s);
					lastShip = "S";
					last_i = i;
					last_j = j;
					last_orientation = submarine.getWarShipOrientation();
					if(desiredOrientation)
						submarine.setWarShipOrientation(true);
					else
						submarine.setWarShipOrientation(false);
				}
				else if(s == "D" && clicked_ship == false){
					clicked_ship = true;
					addToStatusPane("clicked a ship: " + s);
					lastShip = "D";
					last_i = i;
					last_j = j;
					last_orientation = destroyer.getWarShipOrientation();
					if(desiredOrientation)
						destroyer.setWarShipOrientation(true);
					else
						destroyer.setWarShipOrientation(false);
				}
				else if(s == "P" && clicked_ship == false){
					clicked_ship = true;
					addToStatusPane("clicked a ship: " + s);
					lastShip = "P";
					last_i = i;
					last_j = j;
					last_orientation = patrol.getWarShipOrientation();
					if(desiredOrientation)
						patrol.setWarShipOrientation(true);
					else
						patrol.setWarShipOrientation(false);
				}
				else if(s == "-" && clicked_ship == true){
					addToStatusPane("You want to put ship at: " + i + ", " + j);
					
					//Send location to server and wait for response that the location is okay
					// ... at this point?
					
					if(lastShip == "A"){	//<-- Get the ship that was last clicked
						placeShipsOnGrid(aircraftcarrier, new Point(last_i, last_j), 
								aircraftcarrier.getWarShipOrientation(), true);
						placeShipsOnGrid(aircraftcarrier, new Point(i, j), desiredOrientation, false);
						
						thisShipIs = 5;
					}
					else if(lastShip == "B"){
						placeShipsOnGrid(battleship, new Point(last_i, last_j),
								battleship.getWarShipOrientation(), true);
						placeShipsOnGrid(battleship, new Point(i, j), desiredOrientation, false);
						thisShipIs = 1;
					}
					else if(lastShip == "S"){
	
						placeShipsOnGrid(submarine, new Point(last_i, last_j),
								submarine.getWarShipOrientation(), true);
						placeShipsOnGrid(submarine, new Point(i, j), desiredOrientation, false);
						thisShipIs = 2;
					}
					else if(lastShip == "D"){
						placeShipsOnGrid(destroyer, new Point(last_i, last_j), 
								destroyer.getWarShipOrientation(), true);
						placeShipsOnGrid(destroyer, new Point(i, j), desiredOrientation, false);
						thisShipIs = 3;
					}
					else if(lastShip == "P"){
						placeShipsOnGrid(patrol, new Point(last_i, last_j),
								patrol.getWarShipOrientation(), true);
						placeShipsOnGrid(patrol, new Point(i, j), desiredOrientation, false);
						thisShipIs = 4;
					}
					
					if(desiredOrientation)
						dOrientation = 1;
					else
						dOrientation = 0;
					
					playerData[ID] = player.getPlayerId();
					playerData[X_LOC] = i;
					playerData[Y_LOC] = j;
					playerData[MODE] = 0;
					playerData[SHIP_TYPE] = thisShipIs;
					playerData[ORIENTATION] = dOrientation;
					bc.messageForClient(playerData);
					clicked_ship = false;
					lastShip = "";
				}
				else{
					addToStatusPane("Picked Ambiguous Location: " + "(" + i + " " + j + ")");
					clicked_ship = false;
					lastShip = "";
				}
			}
			addToStatusPane("Lower Grid Locked");
		}
	}
	
	
	//watch for textfield events where the player name goes
	class TextFieldListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String pName = player1.getText(); //Get the text entered into the field
			player.setName(pName);			 //and set the name for the player object
			addToStatusPane("Player name entered: " + player.getName());
		}									//... this is what should go in "opponent" on 
	}										//... the other players screen, how? don't know yet.
	
	
	class BannerRepaint implements Runnable{
		
		@Override
		public void run() {
			while(player2.getText().equals("Opponent")){
				try {
					banner.repaint();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	class GamePanelsRepaint implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
					
					gameGrid.repaint();
					opponentGrid.repaint();
					
					Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			
		}
		
	}
// Just testing Git (TJC)
}
