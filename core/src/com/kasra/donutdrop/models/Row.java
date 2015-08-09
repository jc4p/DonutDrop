package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.kasra.donutdrop.TextureManager;

import java.util.Random;

public class Row extends Actor {
    private Array<Tile> tiles;

    private final static int MAX_GAPS = 2;
    private Random random;
    private float worldWidth;

    public Row(World world, float speed, float worldWidth) {
        tiles = new Array<Tile>();

        random = new Random();
        this.worldWidth = worldWidth;

        generateRow(world, speed);
    }

    private void generateRow(World world, float speed) {
        float TILE_SIZE = worldWidth / 6.0f;
        float tileX = TILE_SIZE / 2.0f;

        if (random.nextFloat() < 0.5)
            generateTile(world, tileX, TILE_SIZE, speed, TextureManager.get().getTileLeftSet());

        tileX += TILE_SIZE;

        while(tileX < worldWidth - TILE_SIZE / 2.0f) {
            if (shouldMakeTile((int)(tileX / TILE_SIZE)))
                generateTile(world, tileX, TILE_SIZE, speed, TextureManager.get().getTileSet());
            tileX += TILE_SIZE;
        }

        generateTile(world, tileX, TILE_SIZE, speed, TextureManager.get().getTileRightSet());
    }

    private boolean shouldMakeTile(int tileIndex) {
        // If we just draw one lonely little land, let's always draw at least one more.
        if (tiles.size > 2 && tiles.get(tiles.size - 2).getBoxPosition().x != tileIndex - 2 && tiles.get(tiles.size - 1).getBoxPosition().x == tileIndex - 1)
            return true;
        // If there's 3 tiles, and still no air, let's make some space.
        if (tileIndex == 3 && tiles.size == 3)
            return false;
        // If the tile before this one isn't there, let's say half the time this one should match.
        if (tiles.size > 1 && tiles.get(tiles.size - 1).getBoxPosition().x != tileIndex - 1)
            return random.nextFloat() >= 0.5;

        // Default: We most likely make the tile
        return random.nextFloat() >= 0.3;
    }

    private void generateTile(World world, float x, float tileSize, float upwardsForce, Array<AtlasRegion> options) {
        TextureRegion texture;
        if (options.size == 1)
            texture = options.first();
        else {
            texture = random.nextFloat() >= 0.4 ? options.first() : options.get(options.size - 1);
        }
        tiles.add(new Tile(texture, world, tileSize, tileSize / 2.0f, x, -tileSize, upwardsForce));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setBounds(0, worldWidth, getY(), getHeight());
        for (Tile tile : tiles)
            tile.draw(batch, parentAlpha);
    }

    @Override
    public float getY() {
        if (tiles.size == 0)
            return 0;
        return tiles.first().getY();
    }

    @Override
    public float getHeight() {
        if (tiles.size == 0)
            return 0;
        return tiles.first().getHeight();
    }
}
