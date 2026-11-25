package ru.nsu.kurumun.gradebook.model;

public enum Grade {
    UNSATISFACTORY(2),
    SATISFACTORY(3),
    GOOD(4),
    EXCELLENT(5);
    private final int score;
    Grade(int score) {
        this.score = score;
    }
    int score() {
        return score;
    }
    boolean isGoodOrExcellent() {
        return this == GOOD || this == EXCELLENT;
    }
}
