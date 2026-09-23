package ru.nsu.kurumun.blackjack.rules;

/** Числовые правила консольного блэкджека. */
public final class Rules {
    /** Максимальная сумма очков без перебора. */
    public static final int TARGET_SCORE = 21;

    /** Сумма, начиная с которой дилер прекращает добор. */
    public static final int DEALER_STOP_SCORE = 17;

    /** Число карт у каждого участника после начальной раздачи. */
    public static final int INITIAL_HAND_SIZE = 2;

    /** Высокое значение туза. */
    public static final int ACE_HIGH = 11;

    /** Низкое значение туза. */
    public static final int ACE_LOW = 1;

    /** Значение десятки, валета, дамы и короля. */
    public static final int FACE_VALUE = 10;

    /** Число карт в одной стандартной колоде без джокеров. */
    public static final int DECK_SIZE = 52;

    private Rules() {
    }
}
