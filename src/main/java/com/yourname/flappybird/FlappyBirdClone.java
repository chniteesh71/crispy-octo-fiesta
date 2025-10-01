/**
 * FlappyBirdClone is a simple JavaFX implementation of the Flappy Bird game.
 * The bird flaps with the SPACE key and the game can be restarted with ENTER.
 */
package com.yourname.flappybird;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Main class for the Flappy Bird clone game.
 */
public final class FlappyBirdClone extends Application {

    /** Canvas dimensions */
    private static final int CANVAS_WIDTH = 500;
    private static final int CANVAS_HEIGHT = 500;

    /** Bird constants */
    private static final int BIRD_X = 200;
    private static final int BIRD_SIZE = 20;

    /** Pipe constants */
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 120;
    private static final int PIPE_SPEED = 3;
    private static final int PIPE_START_X = CANVAS_WIDTH;

    /** Game constants */
    private static final int MIN_PIPE_GAP_Y = 100;
    private static final int MAX_PIPE_GAP_Y = 350;
    private static final int MAX_BIRD_Y = 480;
    private static final long PIPE_INTERVAL_NS = 2_000_000_000L;

    /** Bird position and velocity */
    private double birdY = 250;
    private double velocity = 0;

    /** Gravity and flap strength */
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -8;

    /** Game state */
    private boolean gameOver = false;
    private int score = 0;

    /** Pipes list */
    private final List<double[]> pipes = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Start the JavaFX application.
     *
     * @param stage Primary stage.
     */
    @Override
    public void start(final Stage stage) {
        final Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final Scene scene = new Scene(new Group(canvas), CANVAS_WIDTH, CANVAS_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Flappy Bird Clone 🐤");
        stage.show();

        // Handle key presses
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && !gameOver) {
                velocity = JUMP_STRENGTH;
            } else if (e.getCode() == KeyCode.ENTER && gameOver) {
                resetGame();
            }
        });

        addPipe(); // initial pipe

        new AnimationTimer() {
            private long lastPipe = 0;

            @Override
            public void handle(final long now) {
                if (!gameOver) {
                    updateGame(now);
                }
                render(gc);
            }

            /**
             * Update game state for each frame.
             *
             * @param now Current timestamp in nanoseconds.
             */
            private void updateGame(final long now) {
                velocity += GRAVITY;
                birdY += velocity;

                if (birdY > MAX_BIRD_Y || birdY < 0) {
                    gameOver = true;
                }

                final Iterator<double[]> it = pipes.iterator();
                while (it.hasNext()) {
                    final double[] pipe = it.next();
                    pipe[0] -= PIPE_SPEED;

                    // Score increment
                    if (pipe[0] == BIRD_X) {
                        score++;
                    }

                    // Collision detection
                    if (pipe[0] < BIRD_X && pipe[0] + PIPE_WIDTH > BIRD_X) {
                        if (birdY < pipe[1] || birdY + BIRD_SIZE > pipe[1] + PIPE_GAP) {
                            gameOver = true;
                        }
                    }
                }

                pipes.removeIf(pipe -> pipe[0] < -PIPE_WIDTH);

                if (now - lastPipe > PIPE_INTERVAL_NS) {
                    addPipe();
                    lastPipe = now;
                }
            }
        }.start();
    }

    /**
     * Add a new pipe at the right edge of the screen.
     */
    private void addPipe() {
        final int gapY = random.nextInt(MAX_PIPE_GAP_Y - MIN_PIPE_GAP_Y) + MIN_PIPE_GAP_Y;
        pipes.add(new double[]{PIPE_START_X, gapY});
    }

    /**
     * Reset the game state to start a new game.
     */
    private void resetGame() {
        birdY = 250;
        velocity = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        addPipe();
    }

    /**
     * Render the game frame.
     *
     * @param gc GraphicsContext to draw on.
     */
    private void render(final GraphicsContext gc) {
        // Background
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Bird
        gc.setFill(Color.YELLOW);
        gc.fillOval(BIRD_X, birdY, BIRD_SIZE, BIRD_SIZE);

        // Pipes
        gc.setFill(Color.GREEN);
        for (final double[] pipe : pipes) {
            gc.fillRect(pipe[0], 0, PIPE_WIDTH, pipe[1]);               // top pipe
            gc.fillRect(pipe[0], pipe[1] + PIPE_GAP, PIPE_WIDTH, CANVAS_HEIGHT); // bottom pipe
        }

        // Score
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 20, 20);

        // Game over message
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER! Press ENTER to Restart", 100, 250);
        }
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
