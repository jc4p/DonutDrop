package com.kasra.donutdrop;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class TextureManager {
    private HashMap<String, Array<AtlasRegion>> cachedRegions;
    private TextureAtlas atlas;

    private static TextureManager manager;

    protected TextureManager(TextureAtlas atlas) {
        this.atlas = atlas;
        cachedRegions = new HashMap<String, Array<AtlasRegion>>();
    }

    public static void create(TextureAtlas atlas) {
        manager = new TextureManager(atlas);
    }

    public static TextureManager get() {
        if (manager == null)
            throw new RuntimeException("TextureManager.get() called before TextureManager.create()");
        return manager;
    }

    private AtlasRegion getRegion(String name) {
        if (cachedRegions.containsKey(name))
            return cachedRegions.get(name).first();
        Array<AtlasRegion> array = new Array<AtlasRegion>(1);
        array.add(atlas.findRegion(name));
        cachedRegions.put(name, array);
        return array.first();
    }

    private Array<AtlasRegion> getRegions(String name) {
        if (cachedRegions.containsKey(name))
            return cachedRegions.get(name);
        cachedRegions.put(name, atlas.findRegions(name));
        return cachedRegions.get(name);
    }

    public Array<AtlasRegion> getTileLeftSet() {
        return getRegions("tile_left");
    }

    public Array<AtlasRegion> getTileRightSet() {
        return getRegions("tile_right");
    }

    public Array<AtlasRegion> getTileSet() {
        return getRegions("tile");
    }

    public Array<AtlasRegion> getCloudSet() {
        return getRegions("cloud");
    }

    public Sprite createCloudSprite(int index) {
        return atlas.createSprite("cloud", index);
    }

    public AtlasRegion getCloudClose() {
        return getRegion("cloud_close");
    }

    public AtlasRegion getSky() {
        return getRegion("sky");
    }

    public AtlasRegion getDonut() {
        return getRegion("donut");
    }

    public AtlasRegion getLedgeLeft() {
        return getRegion("ledge_left");
    }

    public AtlasRegion getLedgeRight() {
        return getRegion("ledge_right");
    }
}
