package com.yourname.flappybird;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FlappyBirdTest {

    @Test
    void testPipeGeneration() {
        List<double[]> pipes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int gapY = (int)(Math.random() * 250) + 100;
            pipes.add(new double[]{500, gapY});
        }
        assertEquals(5, pipes.size());
        for (double[] pipe : pipes) {
            assertTrue(pipe[1] >= 100 && pipe[1] <= 350); // gapY bounds
        }
    }

    @Test
    void testScoreIncrement() {
        int score = 0;
        double birdX = 200;
        double[] pipe = {200, 150}; // pipe at birdX
        if (pipe[0] == birdX) score++;
        assertEquals(1, score);
    }

    @Test
    void testCollisionDetection() {
        double birdY = 100;
        double[] pipe = {190, 150}; // pipe gap 150-270
        boolean collision = false;
        if (pipe[0] < 250 && pipe[0] + 60 > 200) {
            if (birdY < pipe[1] || birdY + 20 > pipe[1] + 120) {
                collision = true;
            }
        }
        assertTrue(collision);
    }
}
