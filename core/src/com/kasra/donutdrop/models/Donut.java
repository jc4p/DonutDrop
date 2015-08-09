package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kasra.donutdrop.SizeUtil;

public class Donut extends Actor {
    private Body body;
    private Texture texture;
    private float radius;

    public Donut(Texture texture, World world, float worldWidth, float worldHeight) {
        this.texture = texture;
        setScale(SizeUtil.WORLD_TO_BOX);
        radius = worldWidth / 16.0f;

        float x = MathUtils.random(radius, worldWidth - radius * 2);
        float y = worldHeight - radius - (worldWidth / 8.0f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        setBounds(x, y, radius * 2, radius * 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public float getRadius() {
        return radius;
    }

    public Vector2 getBoxPosition() {
        return body.getPosition();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(getBoxPosition().x - getRadius(), getBoxPosition().y - getRadius());
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void destroy(World world) {
        world.destroyBody(body);
    }
}
