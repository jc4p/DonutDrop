package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Tile extends Actor {
    private Body body;
    private TextureRegion texture;

    private float width;
    private float height;

    public Tile(TextureRegion textureRegion, World world, float width, float height, float x, float y, float velocity) {
        this.texture = textureRegion;
        this.width = width;
        this.height = height;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        body.setLinearVelocity(0, velocity);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        setBounds(x, y, width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(getBoxPosition().x - (width / 2.0f), getBoxPosition().y - (height / 2.0f));
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public Vector2 getBoxPosition() {
        return body.getPosition();
    }
}
