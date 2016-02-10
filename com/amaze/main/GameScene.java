package com.amaze.main;
import com.amaze.entities.Avatar;
import org.jsfml.audio.Music;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2i;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class will Game and all the elements associated with it.
 */
public class GameScene extends Scene {

	private static int blockSize;       //Size of each block. W and H

	private int blockX;                 //Number of blocks in X direction
	private int blockY;                 //Number of blocks in Y direction
	private Tile[][] tileMap;           //Represents the maze
	private Avatar player;              //Represents the player(avatar)
	private Battery battery;            //
	private Music music;                //Background music
	private FogOfWar fog;
	private Text txtScore;
	private Text txtTime;
	private Vector2i startTile;
	private Vector2i endTile;

	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;

	/**
	 * This constructor creates an instance of a GameScene.
	 * Within this class all the game logic should be handled.
	 * If needed, supplement with additional classes for OO.
	 *
	 * @param sceneTitle - sets title of the window.
	 *                   set to "aMaze" when creating
	 *                   an instance of the GameScene.
	 */

	public GameScene(String sceneTitle, Window window, int blocks, int blockSize, Tile.BlockType[][] level) throws Exception {
		super(sceneTitle, window);

        Tile currentlyLoaded;

		GameScene.blockSize = blockSize;

		blockX = level.length;
		blockY = level.length;

		tileMap = new Tile[blocks][blocks];
		player = new Avatar(0, 0, blockSize);

        /* Cache textures before we start using them in order to increase performance */
		Texture tileTexture[] = new Texture[7];
		for (int i = 0; i < tileTexture.length; i++) {
			tileTexture[i] = new Texture();
			tileTexture[i].loadFromFile(Paths.get("res/images/" + Tile.BlockType.values()[i].toString().toLowerCase() + ".png"));
		}

        /* Create new instances of tiles */
		for (int j = 0; j < blocks; j++) {
			for (int i = 0; i < blocks; i++) {
				tileMap[i][j] = new Tile("", translateX(i), translateY(j), GameScene.blockSize, GameScene.blockSize, level[i][j], tileTexture);
			}
		}

		window.create(new VideoMode((int)tileMap[blocks - 1][blocks - 1].getPosition().x + blockSize, (int)(tileMap[blocks - 1][blocks - 1].getPosition().y + blockSize) + 60),"Game");

        /* Create instance of battery */
		battery = new Battery(window.getScreenHeight(), window.getScreenHeight(), 6);

        /* Load background music */
		music = new Music();
		try {
			music.openFromFile(Paths.get("res/music/move.ogg"));
		} catch (IOException e) {
			System.out.println("There was a problem loading the background music \n Error: " + e);
		}

		/* Load font and text*/
		Font scoreFont = new Font();
		try {
			scoreFont.loadFromFile(Paths.get("res/fonts/Arial.ttf"));
		} catch (IOException e) {
			System.out.println("Could not load the font!");
		}

        /* Create fog of war */
		fog = new FogOfWar(FogOfWar.MAX_SIZE / 2, this.getWindow(), battery, this);

		txtScore = new Text("Score: \t100", scoreFont);
		txtScore.setPosition(15, window.getScreenHeight() - 40);

		txtTime = new Text("Time: \t1:23", scoreFont);
		txtTime.setPosition(window.getScreenWidth() - 180, window.getScreenHeight() - 40);

        /* Change avatar location */
        for(int i = 0;i < blocks; i++){
            for(int j = 0; j < blocks; j++){
                currentlyLoaded = tileMap[i][j];

                if (currentlyLoaded.getTileType() == Tile.BlockType.START) {
                    player.setPosition(currentlyLoaded.getPosition());
					startTile = new Vector2i(Math.round(player.getPosition().x/blockSize), Math.round(player.getPosition().y/blockSize));
                }
				if (currentlyLoaded.getTileType() == Tile.BlockType.FINISH) {
					endTile = new Vector2i(Math.round(currentlyLoaded.getPosition().x/blockSize), Math.round(currentlyLoaded.getPosition().y/blockSize));
				}
            }
        }
	}

	/**
	 * (
	 * Translates X to raw pixels
	 *
	 * @param blockX Block number
	 * @return Raw pixel value
	 */

	public int translateX(int blockX) {
		return blockSize * blockX;
	}

	/**
	 * Translates Y to raw pixels
	 *
	 * @param blockY Block number
	 * @return Raw pixel value
	 */

	public int translateY(int blockY) {
		return blockSize * blockY;
	}

	/**
	 * When called, this function displays all the graphics on the main window.
	 */
	public void display() {
		setRunning(true);
		getWindow().setTitle(getSceneTitle());

		music.play();
		music.setLoop(true);
		Clock clock = new Clock();
		Clock timer = new Clock();

		int minute = 0;

		while (isRunning()) try {
			getWindow().clear(Color.BLACK);
			drawGraphics(getWindow());

			fog.update(clock);

			int second = (int) timer.getElapsedTime().asSeconds();
			txtTime.setString("Time: \t" + minute + ":" + ((second < 10) ? "0" + second : second));

			if (second >= 60) {
				timer.restart();
				minute++;
			}

			for (Event event : getWindow().pollEvents()) {
				executeEvent(event);
			}
			getWindow().display();
		} catch (Exception e) {
			setRunning(false);
		}
	}

	/**
	 * When event is performed (e.g - user clicks on the button) Appropriate function
	 * should be called within this function to handle the event.
	 *
	 * @param event - user event.
	 */
	public void executeEvent(Event event) {

		/* Sets flag to true when key pressed*/
		if(event.type == Event.Type.KEY_PRESSED) {

			switch (event.asKeyEvent().key) {
						case UP:
							up = true;
							break;
						case DOWN:
							down = true;
							break;
						case LEFT:
							left = true;
							break;
						case RIGHT:
							right = true;
						break;
					case ESCAPE:
						music.stop();
						exitScene(this);
						break;
				}
		}else if(event.type == Event.Type.CLOSED){
			systemExit();
		}

		/* Sets boolean if the key has been released */
		if(event.type == Event.Type.KEY_RELEASED){
			if(event.asKeyEvent().key == event.asKeyEvent().key.UP){
				up = false;
			}else if(event.asKeyEvent().key == event.asKeyEvent().key.DOWN){
				down = false;
			}else if(event.asKeyEvent().key == event.asKeyEvent().key.LEFT){
				left = false;
			}else if(event.asKeyEvent().key == event.asKeyEvent().key.RIGHT){
				right = false;
			}
		}
	}

	/**
	 * Function to detect if the player has moved onto a tile.
	 */
	public Tile.BlockType detectCollision() {
		//Find the block location from the pixel X&Y
		int playerX = Math.round(getPlayerX() / blockSize);
		int playerY = Math.round(getPlayerY() / blockSize);

		//Debugging - enable to display Player X & Y
		//System.out.println("Player X: " + playerX + " - Player Y: " + playerY);

		//Return the block the player is behind
		return tileMap[playerX][playerY].getTileType();
	}

	/**
	 * Function to see what type of block you have collided with and act accordingly.
	 *
	 * @param reboundDir The direction the avatar should be rebounded.
	 * @param type       The type of block that has been detected.
	 */
	public void detectionHandler(Tile.BlockType type, String reboundDir) {
		switch (type) {
			case WALL:
				reboundPlayer(reboundDir);
				break;
			case DOOR:
				//TODO Insert the door handling code here.
				break;
			case START:
				break;
			case FINISH:
				//TODO Insert the finish handling code here.
				break;
			case VOID:
				//TODO Insert the void handling code here.
				break;
			case CHARGE:
				//TODO Insert the charge handling code here.
				//battery.changeChargeLevel(battery.getChargeLevel() + 1);
				//battery.increaseChargeLevel(1);
				fog.increase();
				break;
			case FLOOR:
				break;
			default:
				System.out.println("Please select a defined BlockType.");
		}
	}

	/**
	 * Function to rebound the player the amount of steps defined, given a direction.
	 *
	 * @param dir The direction the avatar should be rebounded.
	 */
	public void reboundPlayer(String dir) {
		int reboundStep = 7; //Number of steps to rebound the player.

		switch (dir) {
			case "UP":
				player.move(0, -reboundStep);
				break;
			case "DOWN":
				player.move(0, reboundStep);
				break;
			case "LEFT":
				player.move(-reboundStep, 0);
				break;
			case "RIGHT":
				player.move(reboundStep, 0);
				break;
			default:
				System.out.println("Please select a direction defined.");
				break;
		}
	}

	/**
	 * Function to return the X pixels of the player.
	 */
	public float getPlayerX() {
		return player.getPosition().x;
	}

	/**
	 * Function to return the Y pixels of the player.
	 */


	public float getPlayerY() {
		return player.getPosition().y;
	}

	/**
	 * This function is responsible for drawing graphics on the main window
	 *
	 * @param window - reference to the main window.
	 */


	public void drawGraphics(RenderWindow window) {
		for (int j = 0; j < blockY; j++) {
			for (int i = 0; i < blockX; i++) {
				if (fog.getView(i, j, player)) {
					window.draw(tileMap[i][j]);
				}
			}
		}

		/* Check if the key has been pressed with window edge detection*/
		if (up) {
			if(getPlayerY() >= 0) {
				player.move(0, -1);
				detectionHandler(detectCollision(), "DOWN");
			}
		}else if(down){
			if(getPlayerY() <= translateY(blockY-1)){
				player.move(0, 1);
				detectionHandler(detectCollision(), "UP");
			}
		}else if(left){
			if(getPlayerX() >= 0){
				player.move(-1, 0);
				detectionHandler(detectCollision(), "RIGHT");
			}
		}else if(right){
			if(getPlayerX() < translateY(blockX - 1)){
				player.move(1, 0);
				detectionHandler(detectCollision(), "LEFT");
			}
		}

		//Draw the player
		window.draw(player);

		//Draw the battery
		window.draw(battery);

		//Draw score text
		window.draw(txtScore);

		//Draw time text
		window.draw(txtTime);
	}

	public Vector2i getStartTilePos() {
		return startTile;
	}

	public Vector2i getEndTilePos() {
		return endTile;
	}

	public static int getBlockSize() {
		return blockSize;
	}

	public static void setBlockSize(int blockSize) {
		if (blockSize > 0) {
			GameScene.blockSize = blockSize;
		}
	}

}