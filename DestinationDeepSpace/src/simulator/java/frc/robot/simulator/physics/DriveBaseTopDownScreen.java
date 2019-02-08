package frc.robot.simulator.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import frc.robot.Robot;
import frc.robot.simulator.physics.bodies.DriveBaseTop;
import frc.robot.simulator.physics.bodies.DriveBaseTop;
import frc.robot.simulator.physics.bodies.TopDownField;
import frc.robot.subsystem.drive.DriveSubsystem;

/**
 * A simple screen displaying the drive base in a side view
 */
public class DriveBaseTopDownScreen extends AbstractPhysicsSimulationScreen {

    private Robot robot;
    private Stage stage;
    private PhysicsSimulation physicsSimulation;
    private World world;
    private TopDownField field;
    private DriveBaseTop driveBase;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    // no gravity, let stuff float
    private Vector2 gravity = new Vector2(0, 0);


    public DriveBaseTopDownScreen(PhysicsSimulation physicsSimulation, Robot robot) {
        this.physicsSimulation = physicsSimulation;
        this.robot = robot;

        // create a world to simulate the physics in
        world = new World(gravity, true);

        // screen dimensions in pixels
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenWidth / screenHeight;

        // world size in meters, based on the entire image. Our field is shorter
        // than the screen height, so make our viewport a "sqaure" into the world, set to
        // world width and then scaled to account for the screen aspect ratio
        field = new TopDownField(world);
        float worldWidth = field.getWidth();
        float worldHeight = worldWidth/aspectRatio;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);
        camera.update();
        Viewport viewport = new FitViewport(worldWidth, worldHeight, camera);

        stage = new Stage(viewport);
        debugRenderer = new Box2DDebugRenderer();

        // center the field
        field.setTransform(worldWidth/2, worldHeight/2, 0);
        stage.addActor(field);

        Vector2 startingPositionWorld = field.getFieldCoordsForPixel(1240, 410);
        driveBase = new DriveBaseTop(world, startingPositionWorld.x, startingPositionWorld.y);
        driveBase.setTransform(startingPositionWorld.x, startingPositionWorld.y, MathUtils.degreesToRadians*90);
        stage.addActor(driveBase);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        driveBase.setFrontLeftMotorOutput(
                (float) (DriveSubsystem.instance().getLeftFrontMotor().getMotorOutputPercent()));
        driveBase.setFrontRightOutput(
                (float) (DriveSubsystem.instance().getRightFrontMotor().getMotorOutputPercent()));
        driveBase.setRearLeftMotorOutput(
                (float) (DriveSubsystem.instance().getLeftRearMotor().getMotorOutputPercent()));
        driveBase.setRearRightMotorOutput(
                (float) (DriveSubsystem.instance().getRightRearMotor().getMotorOutputPercent()));

        camera.update();

        stage.act();
        stage.draw();
        debugRenderer.render(world, stage.getCamera().combined);
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


}
