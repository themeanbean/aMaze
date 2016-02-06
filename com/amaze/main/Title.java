package com.amaze.main;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class holds information about Title.
 */
public class Title extends RectangleShape {

    private Vector2f size;
    private Texture icon;


    /**
     * Construct a button with following parameters:
     *
     * @param width - width of the title
     * @param height - height of the title
     */
    public Title(float width, float height) throws IOException {

        size = new Vector2f(width,height);

        this.setSize(size);

        icon = new Texture();
        icon.loadFromFile(Paths.get("res/menuGraphics/logo.png"));
        this.setTexture(icon);
    }

}