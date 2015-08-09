package com.kasra.donutdrop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kasra.donutdrop.DonutDrop;
import com.kasra.donutdrop.SizeUtil;
import com.kasra.donutdrop.models.Donut;
import com.kasra.donutdrop.models.Row;

import java.util.Random;

public class GameScreen implements Screen {
    final DonutDrop game;

    public final static int WORLD_WIDTH = 72;
    public final static int WORLD_HEIGHT = 128;

    private World world;
    private double accumulator;

    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture textureDonut;
    private Texture textureRowLeft;
    private Texture textureRow;
    private Texture textureRowRight;
    private Texture textureRowAltLeft;
    private Texture textureRowAlt;
    private Texture textureRowAltRight;

    private Donut donut;
    private Array<Row> rows;

    private Random random;

    private int lastBottomRowN = 0;

    private final static float ROW_SPEED_MIN = 20;
    private final static float ROW_SPEED_MAX = 60;

    public GameScreen(DonutDrop game) {
        this.game = game;

        random = new Random();

        Box2D.init();

        world = new World(new Vector2(0, -10), true);
        setEdges();
        loadTextures();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera), game.batch);

        spawnDonut();

        rows = new Array<Row>();
        spawnRow(ROW_SPEED_MIN);
    }

    private void setEdges() {
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.type = BodyDef.BodyType.StaticBody;
        leftWallDef.position.set(0, 0);

        Body leftWall = world.createBody(leftWallDef);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, 0, WORLD_HEIGHT);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;

        leftWall.createFixture(fixtureDef);
        shape.dispose();

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(WORLD_WIDTH, 0);

        Body rightWall = world.createBody(rightWallDef);

        shape = new EdgeShape();
        shape.set(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;

        rightWall.createFixture(fixtureDef);
        shape.dispose();
    }

    private void loadTextures() {
        textureDonut = new Texture(Gdx.files.internal("donut.png"));
        textureRowLeft = new Texture(Gdx.files.internal("tile_left.png"));
        textureRow = new Texture(Gdx.files.internal("tile.png"));
        textureRowRight = new Texture(Gdx.files.internal("tile_right.png"));
        textureRowAltLeft = new Texture(Gdx.files.internal("tile_1_left.png"));
        textureRowAlt = new Texture(Gdx.files.internal("tile_1.png"));
        textureRowAltRight = new Texture(Gdx.files.internal("tile_1_right.png"));
    }

    private void spawnDonut() {
        donut = new Donut(textureDonut, world, stage.getWidth(), stage.getHeight());
        stage.addActor(donut);
    }

    private void spawnRow(float speed) {
        Row row = new Row(world, speed, stage.getWidth(), textureRowLeft, textureRow, textureRowRight, textureRowAltLeft, textureRowAlt, textureRowAltRight);
        stage.addActor(row);
        rows.add(row);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        stage.draw();

        input();
        move();
        tick(delta);
    }

    private void input() {

   }

    private void move() {
        if (donut.getBoxPosition().y < 0 || donut.getBoxPosition().y + donut.getRadius() > WORLD_HEIGHT) {
            donut.destroy(world);
            spawnDonut();
        }

        if (rows.first().getY() > WORLD_HEIGHT) {
            stage.getActors().removeIndex(0);
            rows.removeIndex(0);
        }

        Row lastRow = rows.get(rows.size - 1);
        int rowY = (int)Math.floor(lastRow.getY());
        int rowN = (int)Math.floor(rowY / donut.getRadius());

        if (rowN != lastBottomRowN && (rowN % 2 == 1)) {
            System.out.println("rowN: " + rowN);
            lastBottomRowN = rowN;

            if (random.nextFloat() <= 0.70)
                spawnRow(getRowSpeedMultiplier());
            else if (lastBottomRowN == 3)
                spawnRow(getRowSpeedMultiplier());
        }
    }

    private void tick(float deltaTime) {
        double frameTime = Math.min(deltaTime, 0.25);
        accumulator += frameTime;

        while (accumulator >= 1.0f / 60.0f) {
            world.step(1.0f / 60.0f, 4, 3);
            accumulator -= 1.0f / 60.0f;
        }
    }

    private float getRowSpeedMultiplier() {
        return ROW_SPEED_MIN;

        // #TODO: Some easing function based on elapsed time since start of game for [ROW_SPEED_MIN, ROW_SPEED_MAX]
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        textureDonut.dispose();
        textureRowLeft.dispose();
        textureRow.dispose();
        textureRowRight.dispose();
        textureRowAltLeft.dispose();
        textureRowAlt.dispose();
        textureRowAltRight.dispose();
    }
}
