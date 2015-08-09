package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface GameObject {
    void draw(SpriteBatch batch, Viewport viewport);
    Vector2 getBoxPosition();
    void destroy(World world);
}
