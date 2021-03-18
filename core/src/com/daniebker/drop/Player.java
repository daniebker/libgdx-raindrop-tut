package com.daniebker.drop;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private final Rectangle boundingBox;
    private final float width = 64f;
    private final float height = 64f;
    private final Vector2 position;

    public Player() {
        position = new Vector2();
        boundingBox = new Rectangle();
    }

    public void init() {
        position.set(400, 64);
        boundingBox.setPosition(position.x, position.y);
        boundingBox.setHeight(height);
        boundingBox.setWidth(width);
    }

    public void update(float delta) {
        boundingBox.setPosition(position);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        x -= width / 2f;
        if (x < 0) {
            x = 0;
        }
        else if (x > 800 - width) {
            x = 800 - width;
        }

        this.position.set(x, y);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
