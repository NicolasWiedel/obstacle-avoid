package com.obstacleavoid.util.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

public class DebugCameraController {
    private static final Logger log =new Logger(DebugCameraController.class.getName(), Logger.DEBUG);

    // == Constants ==
    private static final int DEFAULT_LEFT_KEY = Input.Keys.A;
    private static final int DEFAULT_RIGHT_KEY = Input.Keys.D;
    private static final int DEFAULT_UP_KEY = Input.Keys.W;
    private static final int DEFAULT_DOWN_KEY = Input.Keys.S;

    private static final int DEFAULT_ZOOM_IN_KEY = Input.Keys.COMMA;
    private static final int DEFAULT_ZOOM_OUT_KEY = Input.Keys.PERIOD;

    private static final int DEFAULT_RESET_KEY = Input.Keys.BACKSPACE;
    private static final int DEFAULT_LOG = Input.Keys.ENTER;

    private static final float DEFAULT_MOVE_SPEED = 20.0f;
    private static final float DEFAULT_ZOOM_SPEED = 2.0f;
    private static final float DEFAULT_MAX_ZOOM_IN = 0.2f;
    private static final float DEFAULT_MAX_ZOOM_OUT = 30.0f;

    // == Attributes ==
    private Vector2 position = new Vector2();
    private Vector2 startPosition = new Vector2();
    private float zoom = 1.0f;

    // == Constructor ==
    public DebugCameraController(){

    }

    // == public methods ==
    public void setStartPosition(float x, float y){
        startPosition.set(x, y);
        position.set(x, y);
    }

    public void applyTo(OrthographicCamera camera){
        camera.position.set(position, 0);
        camera.zoom = zoom;
        camera.update();
    }

    public void handleDebugInput(float delta){
        if(Gdx.app.getType() != Application.ApplicationType.Desktop){
            return;
        }

        float moveSpeed = DEFAULT_MOVE_SPEED * delta;
        float zoomSpeed = DEFAULT_ZOOM_SPEED * delta;

        // move controls
        if(Gdx.input.isKeyPressed(DEFAULT_LEFT_KEY)){
            moveLeft(moveSpeed);
        }else if(Gdx.input.isKeyPressed(DEFAULT_RIGHT_KEY)){
            moveRight(moveSpeed);
        }else if(Gdx.input.isKeyPressed(DEFAULT_UP_KEY)){
            moveUp(moveSpeed);
        }else if(Gdx.input.isKeyPressed(DEFAULT_DOWN_KEY)){
            moveDown(moveSpeed);
        }

        // Zoom controls
        if(Gdx.input.isKeyPressed(DEFAULT_ZOOM_IN_KEY)){
            zoomIn(zoomSpeed);
        }else if(Gdx.input.isKeyPressed(DEFAULT_ZOOM_OUT_KEY)) {
            zoomOut(zoomSpeed);
        }

        // reset controls
        if(Gdx.input.isKeyPressed(DEFAULT_RESET_KEY)) {
            reset();
        }

        if(Gdx.input.isKeyPressed(DEFAULT_LOG)){
            logDebug();
        }
    }

    // == private methods ==
    private void setPosition(float x, float y){
        position.set(x, y);
    }

    private void moveCamera(float xSpeed, float ySpeed){
        setPosition(position.x + xSpeed, position.y + ySpeed);
    }

    private void setZoom(float value){
        zoom = MathUtils.clamp(value, DEFAULT_MAX_ZOOM_IN, DEFAULT_MAX_ZOOM_OUT);
    }

    private void moveLeft(float speed){
        moveCamera(-speed,0);
    }
    private void moveRight(float speed){
        moveCamera(speed, 0);
    }
    private void moveUp(float speed){
        moveCamera(0, speed);
    }
    private void moveDown(float speed){
        moveCamera(0, -speed);
    }
}
