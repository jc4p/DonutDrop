package com.kasra.donutdrop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kasra.donutdrop.DonutDrop;
import com.kasra.donutdrop.TextureManager;
import com.kasra.donutdrop.models.Background;
import com.kasra.donutdrop.models.Donut;
import com.kasra.donutdrop.models.Row;

import java.util.Random;

public class GameScreen implements Screen {
    final DonutDrop game;

    public final static int WORLD_WIDTH = 72;
    public final static int WORLD_HEIGHT = 128;

    private World world;
//    private Box2DDebugRenderer renderer;
    private double accumulator;

    private Background background;
    private Stage stage;
    private OrthographicCamera camera;

    private Donut donut;
    private Array<Row> rows;

    private Random random;

    private int lastBottomRowN = 0;

    private final static float ROW_SPEED_MIN = 40;
    private final static float ROW_SPEED_MAX = 100;
    private final static float SECONDS_UNTIL_MAX_SPEED = 30.0f;

    private final static float DONUT_MIN_HORIZONTAL_SPEED = 60;
    private final static float DONUT_MAX_HORIZONTAL_SPEED = 160;

    private Interpolation speedInterpolation;
    private float gameTime;

    private int numLives = 3;

//    private FPSLogger fpsLogger;

    public GameScreen(DonutDrop game) {
        this.game = game;
        random = new Random();

        Box2D.init();

        world = new World(new Vector2(0, -10), true);
//        renderer = new Box2DDebugRenderer(true, false, true, false, false, false);
//        fpsLogger = new FPSLogger();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera), game.batch);
        background = new Background(stage.getWidth(), stage.getHeight());

        setEdges();
        spawnDonut();

        speedInterpolation = Interpolation.sineIn;
        gameTime = 0;

        rows = new Array<Row>();
        spawnRow(ROW_SPEED_MIN);
    }

    private void setEdges() {
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.type = BodyDef.BodyType.StaticBody;
        leftWallDef.position.set(0, 0);

        Body leftWall = world.createBody(leftWallDef);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, 0, stage.getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;

        leftWall.createFixture(fixtureDef);
        shape.dispose();

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(stage.getWidth(), 0);

        Body rightWall = world.createBody(rightWallDef);

        shape = new EdgeShape();
        shape.set(0, 0, 0, stage.getHeight());

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;

        rightWall.createFixture(fixtureDef);
        shape.dispose();
    }

    private void spawnDonut() {
        if (donut != null) {
            donut.remove();
            numLives -= 1;
            if (numLives < 0)
                numLives = 3;
        }
        donut = new Donut(world, stage.getWidth(), stage.getHeight());
        stage.addActor(donut);
    }

    private void spawnRow(float speed) {
        Row row = new Row(world, speed, stage.getWidth());
        stage.addActor(row);
        rows.add(row);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        background.draw(game.batch);
        game.batch.end();

        stage.draw();

        drawScore(game.batch);

        input();
        move();
        tick(delta);
    }

    private void drawScore(Batch batch) {
        batch.begin();

        AtlasRegion donut = TextureManager.get().getDonut();
        float lifeWidth = WORLD_WIDTH / 24.0f;
        float lifeHeight = donut.getRotatedPackedHeight() / (donut.getRotatedPackedWidth() / lifeWidth);

        float padding = 1;
        float lifeX = padding;
        float lifeY = WORLD_HEIGHT - lifeHeight - padding;

        for (int i = 0; i < numLives; i++) {
            batch.draw(TextureManager.get().getDonut(), lifeX, lifeY, lifeWidth, lifeHeight);
            lifeX += lifeWidth + padding;
        }

        batch.end();
    }

    private void input() {
        float accel = 0.0f;

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            accel = Gdx.input.getAccelerometerX() / -10.0f;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) accel = -0.5f;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) accel = 0.5f;
        }

        if (Math.abs(accel) < 0.1)
            donut.move(0.0f);
        else {
            float intensity = Math.abs(accel);
            float finalSpeed = speedInterpolation.apply(DONUT_MIN_HORIZONTAL_SPEED, DONUT_MAX_HORIZONTAL_SPEED, intensity);
            if (accel < 0.0f)
                finalSpeed *= -1.0f;
            System.out.println("Speed: " + finalSpeed);
            donut.move(finalSpeed);
        }
   }

    private void move() {
        if (donut.getBoxPosition().y < 0 || donut.getBoxPosition().y + donut.getRadius() > WORLD_HEIGHT) {
            donut.destroy(world);
            spawnDonut();
        }

        if (rows.size > 0 && rows.first().getY() > WORLD_HEIGHT) {
            rows.removeIndex(0).remove();
        }

        if (rows.size > 0) {
            Row lastRow = rows.get(rows.size - 1);
            int rowY = (int) Math.floor(lastRow.getY());
            int rowN = (int) Math.floor(rowY / (donut.getRadius() * 2));

            if (rowN != lastBottomRowN && (rowN % 2 == 1)) {
                lastBottomRowN = rowN;

                if (donut.getBoxPosition().y < WORLD_HEIGHT / 2.0f)
                    spawnRow(getRowSpeedMultiplier());
                else if (random.nextFloat() <= 0.70)
                    spawnRow(getRowSpeedMultiplier());
                else if (lastBottomRowN == 3)
                    spawnRow(getRowSpeedMultiplier());
            }
        }
    }

    private void tick(float deltaTime) {
        double frameTime = Math.min(deltaTime, 0.25);
        accumulator += frameTime;

        while (accumulator >= 1.0f / 60.0f) {
            world.step(1.0f / 60.0f, 4, 3);
            background.tick(deltaTime);
            accumulator -= 1.0f / 60.0f;
        }

        gameTime += deltaTime;
    }

    private float getRowSpeedMultiplier() {
        float distanceToMaxSpeed = gameTime / SECONDS_UNTIL_MAX_SPEED;
        if (distanceToMaxSpeed > 1)
            distanceToMaxSpeed = 1.0f;

        return speedInterpolation.apply(ROW_SPEED_MIN, ROW_SPEED_MAX, distanceToMaxSpeed);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        background.setSize(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
