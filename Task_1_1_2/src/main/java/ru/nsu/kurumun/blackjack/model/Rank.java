package ru.nsu.kurumun.blackjack.model;

import ru.nsu.kurumun.blackjack.rules.Rules;

/** Достоинства карт с русскими названиями и исходными значениями очков. */
public enum Rank {
    /** Двойка. */
    TWO("Two", 2),
    /** Тройка. */
    THREE("Three", 3),
    /** Четвёрка. */
    FOUR("Four", 4),
    /** Пятёрка. */
    FIVE("Five", 5),
    /** Шестёрка. */
    SIX("Six", 6),
    /** Семёрка. */
    SEVEN("Seven", 7),
    /** Восьмёрка. */
    EIGHT("Eight", 8),
    /** Девятка. */
    NINE("Nine", 9),
    /** Десятка. */
    TEN("Ten", Rules.FACE_VALUE),
    /** Валет. */
    JACK("Jack", Rules.FACE_VALUE),
    /** Дама. */
    QUEEN("Queen", Rules.FACE_VALUE),
    /** Король. */
    KING("King", Rules.FACE_VALUE),
    /** Туз: значение одиннадцать может быть понижено рукой до единицы. */
    ACE("Ace", Rules.ACE_HIGH);

    private final String displayName;
    private final int value;

    Rank(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    /**
     * Возвращает название достоинства.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Возвращает исходное число очков (туз изначально оценивается в одиннадцать).
     */
    public int getValue() {
        return value;
    }
}
