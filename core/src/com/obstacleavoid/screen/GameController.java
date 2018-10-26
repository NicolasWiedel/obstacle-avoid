package com.obstacleavoid.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.obstacleavoid.config.DifficultyLevel;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;

public class GameController {

    // == Constants ==
    private static final Logger log = new Logger(GameController.class.getName(), Logger.DEBUG);

    // == Attributes
    private Player player;
    private Array<Obstacle> obstacles = new Array<Obstacle>();
    private float obstacleTime;
    private float scoreTimer;
    private int lives = GameConfig.LIVES_START;
    private int score;
    private int displayedScore;
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
    private Pool<Obstacle> obstaclePool;

    // == Constructor
    public GameController() {
        init();
    }

    // init
    private void init(){
        // create player
        player = new Player();

        // calculate position
        float startPlayerX = GameConfig.WORLD_WIDTH / 2;
        float startPlayerY = 1;

        // position player
        player.setPosition(startPlayerX, startPlayerY);

        // create obstaclePool
        obstaclePool =Pools.get(Obstacle.class, 40);
    }

    // == public methods ==
    public void update(float delta){
        if(isGameOver()){
            log.debug("Game Over!!!");
            return;
        }

        updatePlayer();
        updateObstacles(delta);
        updateScore(delta);
        updateDisplayedScore(delta);

        if(isPlayerCollidingWithObstacle()){
            log.debug("Collision detected!");
            lives--;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getLives() {
        return lives;
    }

    public int getDisplayedScore() {
        return displayedScore;
    }

    // == private methods ==
    private boolean isGameOver(){

        return false;

//        return lives <= 0;
    }

    private boolean isPlayerCollidingWithObstacle(){
        for(Obstacle obstacle : obstacles){
            if(obstacle.isNotHit() && obstacle.isPlayerColliding(player)){
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
        removePassedObstacles();
    }

    private void createNewObstacle(float delta){
        obstacleTime += delta;

        if(obstacleTime >= GameConfig.OBSTACLE_SPAWN_TIME){
            float min = Obstacle.SIZE / 2;
            float max = GameConfig.WORLD_WIDTH - Obstacle.SIZE / 2;

            float obstacleX = MathUtils.random(max, min);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = obstaclePool.obtain();
            obstacle.setYSpeed(difficultyLevel.getObstacleSpeed());
            obstacle.setPosition(obstacleX, obstacleY);

            obstacles.add(obstacle);
            obstacleTime = 0;
        }
    }

    private void removePassedObstacles(){
        if(obstacles.size > 0){
            Obstacle first = obstacles.first();

            float minObstacleY = -Obstacle.SIZE;

            if(first.getY() < minObstacleY){
                obstacles.removeValue(first, true);
                obstaclePool.free(first);
            }
        }
    }

    private void updateScore(float delta){
        scoreTimer += delta;

        if(scoreTimer >= GameConfig.SCORE_MAX_TIME){
            score += MathUtils.random(1, 5);
            scoreTimer = 0.0f;
        }
    }

    private void updateDisplayedScore(float delta){
        if(displayedScore < score){
            displayedScore = Math.min(
                    score,
                    displayedScore + (int)(60 * delta)
            );
        }
    }
}
