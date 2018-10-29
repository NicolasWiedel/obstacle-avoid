package com.obstacleavoid.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.obstacleavoid.ObstacleAvoidGame;
import com.obstacleavoid.assets.AssetDescriptors;

public class GameScreen implements Screen {

    private final ObstacleAvoidGame game;
    private AssetManager assetManager;

    private GameController controller;
    private GameRenderer renderer;

    public GameScreen(ObstacleAvoidGame game){
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {

        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.GAME_PLAY);

        assetManager.finishLoading();

        controller = new GameController();
        renderer = new GameRenderer(assetManager, controller);
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
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

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
