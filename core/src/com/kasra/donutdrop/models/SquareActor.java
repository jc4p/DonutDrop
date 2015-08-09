package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SquareActor extends Actor {

    private ShapeRenderer shapeRenderer;

    public SquareActor(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 0.2f);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();

        batch.begin();
    }
}
