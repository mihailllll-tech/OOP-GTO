package ru.nsu.kurumun.blackjack.model;

import ru.nsu.kurumun.blackjack.rules.Rules;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Колода, из которой карты извлекаются по одной без возвращения. */
public final class Deck {
    private final Deque<Card> cards;

    /**
     * Создаёт и перемешивает стандартную колоду из пятидесяти двух карт.
     */
    public Deck(Random random) {
        this(createShuffledCards(random));
    }

    /**
     * Создаёт колоду с заданным порядком для воспроизводимых сценариев.
     */
    public Deck(List<Card> orderedCards) {
        cards = new ArrayDeque<>(List.copyOf(orderedCards));
    }

    private static List<Card> createShuffledCards(Random random) {
        Objects.requireNonNull(random, "Генератор случайных чисел не задан");
        List<Card> result = new ArrayList<>(Rules.DECK_SIZE);
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(result, random);
        return result;
    }

    /**
     * Извлекает следующую карту из колоды.
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.removeFirst();
    }

    /**
     * Возвращает число ещё не извлечённых карт.
     *
     * @return количество оставшихся карт
     */
    public int remaining() {
        return cards.size();
    }
}
