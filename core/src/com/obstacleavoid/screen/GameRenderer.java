package com.obstacleavoid.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obstacleavoid.assets.AssetPaths;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Background;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;
import com.obstacleavoid.util.GdxUtils;
import com.obstacleavoid.util.ViewportUtils;
import com.obstacleavoid.util.debug.DebugCameraController;

public class GameRenderer implements Disposable {

    // == Attributes ==
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private SpriteBatch batch;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private DebugCameraController debugCameraController;

    private final GameController controller;

    private Texture playerTexture;
    private Texture obstacleTexture;
    private Texture backgroundTexture;

    // == Constructor ==
    public GameRenderer(GameController controller){
        this.controller = controller;
        init();
    }

    // == init ==
    private void init(){
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudCamera);
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal(AssetPaths.UI_FONT));

        // create Debug camera Controller
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X, GameConfig.WORLD_CENTER_Y);

        playerTexture = new Texture(Gdx.files.internal("gameplay/player.png"));
        obstacleTexture = new Texture(Gdx.files.internal("gameplay/obstacle.png"));
        backgroundTexture = new Texture(Gdx.files.internal("gameplay/background.png"));
    }

    // == public methods ==
    public void render(float delta){
        // not wrapping inside alive, for using it after game stops
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);

        if(Gdx.input.isTouched() && !controller.isGameOver()){
            Vector2 screenTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector2 worldTouch = viewport.unproject(new Vector2(screenTouch));

            System.out.println("screenTouch = " + screenTouch);
            System.out.println("worldTouch = " + worldTouch);

            Player player = controller.getPlayer();
            worldTouch.x = MathUtils.clamp(worldTouch.x, 0, GameConfig.WORLD_WIDTH - player.getWidth());
            player.setX(worldTouch.x);
        }

        // clear screen
        GdxUtils.clearScreen();

        renderGamePlay();

        // render UI / HUD
        renderUI();

        // render debug graphics
        renderDebug();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
        playerTexture.dispose();
        obstacleTexture.dispose();
        backgroundTexture.dispose();
    }

    // == private methods ==

    private void renderGamePlay(){
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // draw background
        Background background = controller.getBackground();
        batch.draw(backgroundTexture,
                background.getX(), background.getY(),
                background.getWidth(), background.getHeight());

        // draw player
        Player player = controller.getPlayer();
        batch.draw(playerTexture,
                player.getX(), player.getY(),
                player.getWidth(), player.getHeight()
        );

        // draw obstacles
        for(Obstacle obstacle : controller.getObstacles()){
            batch.draw(obstacleTexture,
                    obstacle.getX(), obstacle.getY(),
                    obstacle.getWidth(), obstacle.getHeight()
            );
        }


        batch.end();
    }

    private void renderUI(){
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String livesText = "LIVES " + controller.getLives();

        layout.setText(font, livesText);

        font.draw(batch, livesText,
                20,
                GameConfig.HUD_HEIGHT - layout.height);

        String scoreText = "SCORE = " + controller.getDisplayedScore();
        layout.setText(font, scoreText);

        font.draw(batch, scoreText,
                GameConfig.HUD_WIDTH - layout.width - 20,
                GameConfig.HUD_HEIGHT - layout.height);

        batch.end();
    }

    private void renderDebug(){
        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        ViewportUtils.drawGrid(viewport,renderer);
    }

    private void drawDebug(){
        Player player = controller.getPlayer();
        player.drawDebug(renderer);

        Array<Obstacle> obstacles = controller.getObstacles();

        for(Obstacle obstacle : obstacles){
            obstacle.drawDebug(renderer);
        }
    }
}
