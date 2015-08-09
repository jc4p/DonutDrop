package com.kasra.donutdrop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kasra.donutdrop.screens.MainMenuScreen;

public class DonutDrop extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public TextureAtlas atlas;

    public void create() {
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();

        atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
        TextureManager.create(atlas);

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        atlas.dispose();
        font.dispose();
    }
}
