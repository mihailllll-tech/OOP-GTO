package ru.nsu.kurumun.blackjack;

/** Масти стандартной колоды с названиями. */
public enum Suit {
    /** Трефы. */
    CLUBS("Clubs"),
    /** Бубны. */
    DIAMONDS("Diamonds"),
    /** Червы. */
    HEARTS("Hearts"),
    /** Пики точеные. */
    SPADES("Spades");

    private final String displayName;

    Suit(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает название масти.
     */
    public String getDisplayName() {
        return displayName;
    }
}
