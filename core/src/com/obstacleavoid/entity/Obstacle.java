package com.obstacleavoid.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Pool;
import com.obstacleavoid.config.GameConfig;

public class Obstacle extends GameObjectBase implements Pool.Poolable {

    private static final float BOUNDS_RADIUS = 0.3f;
    public static final float SIZE = 2 * BOUNDS_RADIUS;

    private float ySpeed = GameConfig.MEDIUM_OBSTACLE_SPEED;
    private boolean hit;

    public Obstacle() {
        super(BOUNDS_RADIUS);
    }

    public void update(){
        setY(getY() - ySpeed);
    }

    public float getWidth() {
        return SIZE;
    }

    public boolean isPlayerColliding(Player player) {
        Circle playerBound = player.getBounds();
        boolean overlaps = Intersector.overlaps(playerBound, getBounds());

//        if(overlaps){
//            hit = true;
//        }
//       Better way
        hit = overlaps;

        return overlaps;
    }

    public boolean isNotHit(){
        return !hit;
    }


    public void setYSpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }


    @Override
    public void reset() {
        hit = false;
    }
}
