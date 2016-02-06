package com.amaze.MapMaker;

import com.amaze.main.Scene;
import com.amaze.main.Window;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Jay Feng
 */
public class MapMakerScene extends Scene {

    private Tile[][] tiles;
    private Texture[] blockTextures;

    private int blocks;
    private int blockSize;

    private Tile.BlockType[] allValues = Tile.BlockType.values();
    private Window window;
    private Boolean enterPressed = false;

    private RectangleShape textBackground;
    Text userLevel;

    public MapMakerScene(String sceneTitle, Window window, int blocks, int blockSize) throws IOException {
        super(sceneTitle, window);
        this.window = window;
        tiles = new Tile[blocks][blocks];

        this.blocks = blocks;
        this.blockSize = blockSize;

        blockTextures = new Texture[7];
        for (int i = 0; i < blockTextures.length; i++) {
            blockTextures[i] = new Texture();
            blockTextures[i].loadFromFile(Paths.get("res/images/" + allValues[i].toString().toLowerCase() + ".png"));
            blockTextures[i].setSmooth(true);
        }
        for (int y = 0; y < blocks; y++) {
            for (int x = 0; x < blocks; x++) {
                tiles[x][y] = new Tile(blockTextures, x, y);
                tiles[x][y].setTexture(blockTextures[0]);
                tiles[x][y].setPosition(new Vector2f(translateX(blockSize, x), translateY(blockSize, y)));
                tiles[x][y].setSize(new Vector2f(blockSize, blockSize));
            }
        }
        exportSuccessful();
    }

    public void display(RenderWindow window) {
        setRunning(true);
        window.setTitle(getSceneTitle());

        while(this.isRunning()) try {
            window.clear(Color.WHITE);
            drawTile(window);

            for (Event event : window.pollEvents()) {
                executeEvent(event);
            }
            window.display();

        }catch (Exception e) {
            setRunning(false);
        }
    }

    public void executeEvent(Event event) {
        switch(event.type) {
            case CLOSED:
                getWindow().close();
                System.exit(0);
                break;
            case MOUSE_BUTTON_PRESSED:
                for (Tile[] rows: tiles) {
                    for (Tile tile: rows) {
                        if (mouseIsOnTile(tile)) {
                            changeTexture(tile);
                        }
                    }
                }
                break;
            case KEY_PRESSED:
                switch (event.asKeyEvent().key) {
//                    case ESCAPE:
//                        getWindow().setScene(0);
//                        getWindow().getScene(0).display(getWindow());
//                        this.setRunning(false);
//                        break;
                    case RETURN:
                        enterPressed = true;
                        outputLevel();
                            //window.clear();
                            drawExportWindow(window);
                            window.display();
                            pause(2000);
                            getWindow().setScene(0);
                            getWindow().getScene(0).display(getWindow());
                            this.setRunning(false);
                        break;
                }
        }
    }

    public void changeTexture(Tile tile) {
        int nextImageIndex = Arrays.asList(allValues).indexOf(tile.getBlockType()) + 1;
        if (nextImageIndex > allValues.length - 1) nextImageIndex = 0;
        tile.changeBlockType(allValues[nextImageIndex]);
    }

    public void drawTile(RenderWindow window) {
        for (Tile[] rows: tiles) {
            for (Tile tile: rows) {
                window.draw(tile);
            }
        }
    }

    /**(
     * Translates X to raw pixels
     * @param blockX Block number
     * @return Raw pixel value
     */

    public int translateX(int blockSize, int blockX){
        return blockSize * blockX;
    }

    /**
     * Translates Y to raw pixels
     * @param blockY Block number
     * @return Raw pixel value
     */

    public int translateY(int blockSize, int blockY){
        return blockSize * blockY;
    }

    public boolean mouseIsOnTile(Tile tile) {
        Vector2i mousePos = Mouse.getPosition(getWindow());
        Vector2f tilePos = tile.getPosition();

        return (mousePos.x > tilePos.x &&
                mousePos.y > tilePos.y &&
                mousePos.x < tilePos.x + blockSize &&
                mousePos.y < tilePos.y + blockSize
        );
    }

    public void outputLevel() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("Levels2.txt", true));

            for (int y = 0; y < blocks; y++) {
                for (int x = 0; x < blocks; x++) {
                    writer.print(tiles[x][y].getBlockType().toString() + ",");
                }
                writer.println("");
            }
            writer.close();
            System.out.println("Export Successful");
        }
        catch (IOException f) {
            System.err.println("Export Failed");
        }
    }

    public void exportSuccessful()
    {
        try {
            Vector2f size = new Vector2f(window.getScreenWidth() / 1.2F, (window.getScreenHeight() / 5));
            textBackground = new RectangleShape(size);
            textBackground.setPosition(window.getScreenWidth() / 12F, (window.getScreenHeight() / 2.5F)-65);

            Font maze = new Font();
            maze.loadFromFile(Paths.get("res/fonts/Maze.ttf"));

            Texture backgroundImage = new Texture();
            backgroundImage.loadFromFile(Paths.get("res/menuGraphics/Wall.png"));
            textBackground.setTexture(backgroundImage);

            //Create text
            userLevel = new Text("Export Successful", maze, 75);
            userLevel.setColor(Color.BLACK);
            userLevel.setStyle(Text.BOLD);
            userLevel.setOrigin((window.getScreenWidth() / 9.5F) * -1, (window.getScreenHeight() / 3F) * -1);
        }
        catch (Exception e){

        }
    }

    public void drawExportWindow(RenderWindow window) {

        window.draw(textBackground);
        window.draw(userLevel);
    }

    public static void pause(int time)
    {
        try {
            Thread.sleep(time);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}