package ru.nsu.kurumun.blackjack.model;

/** Неизменяемая карта, определяемая достоинством и мастью. */
public final class Card {
    private final Rank rank;
    private final Suit suit;

    /**
     * Создаёт карту с указанными достоинством и мастью.
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Возвращает достоинство карты.
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Возвращает масть карты.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Сравнивает карты по достоинству и масти.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card card)) {
            return false;
        }
        return rank == card.rank && suit == card.suit;
    }

    /**
     * Возвращает название карты без зависимого от руки значения очков.
     */
    @Override
    public String toString() {
        return rank.getDisplayName() + " " + suit.getDisplayName();
    }
}
