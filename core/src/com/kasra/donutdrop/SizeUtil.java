package com.kasra.donutdrop;

public class SizeUtil {
    public final static float WORLD_TO_BOX = .01f;
    public final static float BOX_TO_WORLD = 100f;

    public static float worldToBox(float size) {
        return size * WORLD_TO_BOX;
    }

    public static float boxToWorld(float size) {
        return size * BOX_TO_WORLD;
    }
}
