package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.kasra.donutdrop.TextureManager;
import com.kasra.donutdrop.screens.GameScreen;

import java.util.Random;

public class Background {
    private float width;
    private float height;

    private Random random;
    private Array<Sprite> clouds;

    private float closeHeight;
    private float gameTime;
    private float lastCloudSpawn = 0.0f;

    public Background(float width, float height) {
        random = new Random();
        clouds = new Array<Sprite>();

        setSize(width, height);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;

        AtlasRegion close = TextureManager.get().getCloudClose();
        closeHeight = close.getRotatedPackedHeight() / (close.getRotatedPackedWidth() / width);

        spawnCloudAtPosition(GameScreen.WORLD_WIDTH * 0.2f, GameScreen.WORLD_HEIGHT * 0.6f, GameScreen.WORLD_WIDTH / 3.0f);
        spawnCloudAtPosition(GameScreen.WORLD_WIDTH * 0.7f, GameScreen.WORLD_HEIGHT * 0.2f, GameScreen.WORLD_WIDTH / 4.0f);
    }

    public void tick(float deltaTime) {
        gameTime += deltaTime;

        if (clouds.size > 1 && clouds.first().getY() > GameScreen.WORLD_HEIGHT)
            clouds.removeIndex(0);
        if ((gameTime - lastCloudSpawn) > MathUtils.random(0.5f, 3.0f)) {
            lastCloudSpawn = gameTime;
            spawnCloud();
        }

        for (Sprite cloud : clouds) {
            cloud.setPosition(cloud.getX(), cloud.getY() + (GameScreen.WORLD_HEIGHT * 0.005f));
        }
    }

    private void spawnCloud() {
        float cloudWidth = width / MathUtils.random(2.0f, 6.0f);
        float x = MathUtils.random(GameScreen.WORLD_WIDTH - cloudWidth);
        float y = 0;

        spawnCloudAtPosition(x, y, cloudWidth);
    }

    private void spawnCloudAtPosition(float x, float y, float width) {
        int index =  random.nextInt(TextureManager.get().getCloudSet().size - 1);
        Sprite cloud = TextureManager.get().createCloudSprite(index);

        cloud.setBounds(x, y, width, cloud.getHeight() / (cloud.getWidth() / width));
        clouds.add(cloud);
    }

    public void draw(Batch batch) {
        batch.draw(TextureManager.get().getSky(), 0, 0, width, height);

        for(Sprite c : clouds) {
            c.draw(batch);
        }

        batch.draw(TextureManager.get().getCloudClose(), 0, 0, width, closeHeight);
    }
}
