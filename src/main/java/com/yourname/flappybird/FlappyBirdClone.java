package com.yourname.flappybird;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FlappyBirdClone extends Application {
    private double birdY = 250;
    private double velocity = 0;
    private final double gravity = 0.5;
    private final double jumpStrength = -8;
    private boolean gameOver = false;
    private int score = 0;

    private final List<double[]> pipes = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new javafx.scene.Group(canvas), 500, 500);
        stage.setScene(scene);
        stage.setTitle("Flappy Bird Clone 🐤");
        stage.show();

        // Spacebar flap, Enter restart
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && !gameOver) {
                velocity = jumpStrength;
            } else if (e.getCode() == KeyCode.ENTER && gameOver) {
                resetGame();
            }
        });

        addPipe(); // initial pipe

        new AnimationTimer() {
            long lastPipe = 0;

            @Override
            public void handle(long now) {
                if (!gameOver) updateGame(now);
                render(gc);
            }

            private void updateGame(long now) {
                velocity += gravity;
                birdY += velocity;

                if (birdY > 480 || birdY < 0) gameOver = true;

                Iterator<double[]> it = pipes.iterator();
                while (it.hasNext()) {
                    double[] pipe = it.next();
                    pipe[0] -= 3;

                    // Score increment
                    if (pipe[0] == 200) score++;

                    // Collision detection
                    if (pipe[0] < 250 && pipe[0] + 60 > 200) {
                        if (birdY < pipe[1] || birdY + 20 > pipe[1] + 120) {
                            gameOver = true;
                        }
                    }
                }

                pipes.removeIf(pipe -> pipe[0] < -60);

                if (now - lastPipe > 2_000_000_000L) {
                    addPipe();
                    lastPipe = now;
                }
            }
        }.start();
    }

    private void addPipe() {
        int gapY = random.nextInt(250) + 100;
        pipes.add(new double[]{500, gapY});
    }

    private void resetGame() {
        birdY = 250;
        velocity = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        addPipe();
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0, 0, 500, 500);

        // Bird
        gc.setFill(Color.YELLOW);
        gc.fillOval(200, birdY, 20, 20);

        // Pipes
        gc.setFill(Color.GREEN);
        for (double[] pipe : pipes) {
            gc.fillRect(pipe[0], 0, 60, pipe[1]);           // top pipe
            gc.fillRect(pipe[0], pipe[1] + 120, 60, 500);   // bottom pipe
        }

        // Score
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 20, 20);

        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER! Press ENTER to Restart", 100, 250);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
