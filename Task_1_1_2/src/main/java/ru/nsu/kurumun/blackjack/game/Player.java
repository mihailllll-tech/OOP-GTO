package ru.nsu.kurumun.blackjack.game;

import java.util.List;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Hand;

/** Игрок хранит свои карты и получает карты от дилера. */
public final class Player {
    private final Hand hand = new Hand();

    /**
     * Принимает выданную карту.
     */
    public void receiveCard(Card card) {
        hand.add(card);
    }

    /**
     * Возвращает неизменяемый список карт.
     */
    public List<Card> getCards() {
        return hand.getCards();
    }

    /**
     * Возвращает сумму очков.
     */
    public int getScore() {
        return hand.getScore();
    }

    /**
     * Наличие блэкджека.
     */
    public boolean isBlackjack() {
        return hand.isBlackjack();
    }

    /**
     * Наличие перебора.
     */
    public boolean isBust() {
        return hand.isBust();
    }
}