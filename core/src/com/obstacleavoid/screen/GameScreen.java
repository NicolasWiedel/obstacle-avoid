package com.obstacleavoid.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;
import com.obstacleavoid.util.GdxUtils;
import com.obstacleavoid.util.ViewportUtils;
import com.obstacleavoid.util.debug.DebugCameraController;

public class GameScreen implements Screen {

    private static final Logger log = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private Player player;
    private Array<Obstacle> obstacles = new Array<Obstacle>();
    private float obstacleTime;

    private boolean alive = true;

    private DebugCameraController debugCameraController;

    @Override
    public void show () {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        // create player
        player = new Player();

        // calculate position
        float startPlayerX = GameConfig.WORLD_WIDTH / 2;
        float startPlayerY = 1;

        // position player
        player.setPosition(startPlayerX, startPlayerY);

        // create Debug camera Controller
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X, GameConfig.WORLD_CENTER_Y);

    }

    @Override
    public void render (float delta) {
        // not wrapping inside alive, for using it after game stops
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);

        // update world
        if(alive){
            update(delta);
        }

        // clear screen
        GdxUtils.clearScreen();

        // render debug graphics
        renderDebug();
    }

    private void update(float delta){
        updatePlayer();
        updateObstacles(delta);

        if(isPlayerCollidingWithObstacle()){
            alive = false;
        }
    }

    private boolean isPlayerCollidingWithObstacle(){
        for(Obstacle obstacle : obstacles){
            if(obstacle.isPlayerColliding(player)){
               return true;
            }
        }
        return false;
    }

    private void updatePlayer(){
        player.update();
        blockPlayerFromLeavingTheWorld();
    }

    private void blockPlayerFromLeavingTheWorld(){
        float playerX = MathUtils.clamp(player.getX(),
                player.getWidth() / 2f,
                GameConfig.WORLD_WIDTH - player.getWidth() / 2f);

        player.setPosition(playerX, player.getY());
    }

    private void updateObstacles(float delta){
        for(Obstacle obstacle : obstacles){
            obstacle.update();
        }

        createNewObstacle(delta);
    }

    private void createNewObstacle(float delta){
        obstacleTime += delta;

        if(obstacleTime >= GameConfig.OBSTACLE_SPAWN_TIME){
            float min = 0;
            float max = GameConfig.WORLD_WIDTH;
            float obstacleX = MathUtils.random(min, max);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = new Obstacle();
            obstacle.setPosition(obstacleX, obstacleY);

            obstacles.add(obstacle);
            obstacleTime = 0;
        }
    }

    private void renderDebug(){

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        ViewportUtils.drawGrid(viewport,renderer);
    }

    private void drawDebug(){
        player.drawDebug(renderer);

        for(Obstacle obstacle : obstacles){
            obstacle.drawDebug(renderer);
        }
    }

    @Override
    public void dispose () {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        renderer.dispose();
    }

}
