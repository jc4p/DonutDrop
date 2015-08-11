package com.kasra.donutdrop.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kasra.donutdrop.SizeUtil;
import com.kasra.donutdrop.TextureManager;

public class Donut extends Actor {
    private Body body;
    private TextureRegion texture;
    private float radius;

    private float VERTICAL_SPEED = -30.0f;

    public Donut(World world, float worldWidth, float worldHeight) {
        texture = TextureManager.get().getDonut();
        setScale(SizeUtil.WORLD_TO_BOX);
        radius = worldWidth / 16.0f;

        float x = MathUtils.random(radius, worldWidth - radius * 2);
        float y = worldHeight - radius - (worldWidth / 12.0f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        setBounds(x, y, radius * 2, radius * 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    public void move(float force) {
        float velocity = body.getLinearVelocity() == null ? 0.0f : body.getLinearVelocity().x;
        float velocityDelta = force - velocity;
        float impulse = body.getMass() * velocityDelta;
        body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
        body.setLinearVelocity(getLinearVelocity().x, VERTICAL_SPEED);
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
