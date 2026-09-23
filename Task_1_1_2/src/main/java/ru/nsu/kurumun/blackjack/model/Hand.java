package ru.nsu.kurumun.blackjack.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.nsu.kurumun.blackjack.rules.Rules;

/** Карты участника и подсчёт очков с учётом изменения значения тузов. */
public final class Hand {
    private final List<Card> cards = new ArrayList<>();

    /** Создаёт пустую руку. */
    public Hand() {
    }

    /**
     * Добавляет карту в руку.
     */
    public void add(Card card) {
        cards.add(Objects.requireNonNull(card, "Card is required"));
    }

    /**
     * Возвращает неизменяемый снимок карт в порядке получения.
     */
    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    /**
     * Определяет значения отдельных карт с учётом всей руки.
     * Тузы первоначально дают одиннадцать очков; при переборе их значения
     * поочерёдно снижаются до единицы, пока сумма не станет допустимой
     * или не останется тузов с высоким значением.
     */
    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>(cards.size());
        int score = 0;
        for (Card card : cards) {
            int value = card.getRank().getValue();
            values.add(value);
            score += value;
        }
        for (int index = 0; index < cards.size() && score > Rules.TARGET_SCORE; index++) {
            if (cards.get(index).getRank() == Rank.ACE) {
                values.set(index, Rules.ACE_LOW);
                score -= Rules.ACE_HIGH - Rules.ACE_LOW;
            }
        }
        return List.copyOf(values);
    }

    /**
     * Возвращает наибольшую допустимую сумму или минимальную сумму при переборе.
     */
    public int getScore() {
        return getValues().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Проверяет наличие двадцати одного очка ровно из двух карт.
     */
    public boolean isBlackjack() {
        return cards.size() == Rules.INITIAL_HAND_SIZE && getScore() == Rules.TARGET_SCORE;
    }

    /**
     * Проверяет, превышает ли сумма максимально допустимую.
     */
    public boolean isBust() {
        return getScore() > Rules.TARGET_SCORE;
    }
}
