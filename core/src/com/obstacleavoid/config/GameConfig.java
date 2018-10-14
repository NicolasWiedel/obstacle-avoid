package com.obstacleavoid.config;

public class GameConfig {

    public static final float WIDTH = 480f;   // pixels
    public static final float HEIGHT = 800f;  // pixels

    public static final float WORLD_WIDTH = 6.0f;    // world units
    public static final float WORLD_HEIGHT = 10.0f;  // world units

    public static final float WORLD_CENTER_X = WORLD_WIDTH / 2f;   // world units
    public static final float WORLD_CENTER_Y = WORLD_HEIGHT / 2f;  // world units

    public static final float MAX_PLAYER_X_SPEED = 0.25f;
    public static final float OBSTACLE_SPAWN_TIME = 0.25f;

    private GameConfig(){

    }
}
