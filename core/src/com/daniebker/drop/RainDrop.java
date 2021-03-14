package com.daniebker.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class RainDrop implements Pool.Poolable{
    public final int width = 64;
    public final int height= 64;
    public Rectangle boundingBox;
    public Vector3 position;
    public boolean alive;

    public RainDrop() {
        this.position = new Vector3();
        this.alive = false;
    }

    public void init() {
        position.set(MathUtils.random(0, 800 - 64),  400, 0);
        boundingBox = new Rectangle();
        boundingBox.setPosition(position.x, position.y);
        boundingBox.setHeight(height);
        boundingBox.setWidth(width);
        alive = true;
    }

    @Override
    public void reset() {
        position.set(0,0, 0);
        alive = false;
    }

    public void update(float delta) {
        position.y -= 200 * Gdx.graphics.getDeltaTime();
        boundingBox.setPosition(position.x, position.y);
    }
}