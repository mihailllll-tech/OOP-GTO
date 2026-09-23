package ru.nsu.kurumun.blackjack.game;

import java.util.List;
import java.util.Objects;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Deck;
import ru.nsu.kurumun.blackjack.model.Hand;
import ru.nsu.kurumun.blackjack.rules.Rules;

/** Хранит руку дилера, раздаёт карты и применяет правило добора. */
public final class Dealer {
    private final Deck deck;
    private final Hand hand = new Hand();

    /**
     * Создаёт дилера с колодой.
     */
    public Dealer(Deck deck) {
        this.deck = Objects.requireNonNull(deck);
    }

    /**
     * Поочерёдно раздаёт начальные карты игроку и себе.
     */
    public void dealInitial(Player player) {
        for (int index = 0; index < Rules.INITIAL_HAND_SIZE; index++) {
            dealTo(player);
            drawCard();
        }
    }

    /**
     * Выдаёт очередную карту игроку.
     */
    public Card dealTo(Player player) {
        Objects.requireNonNull(player);
        Card card = deck.draw();
        player.receiveCard(card);
        return card;
    }

    /**
     * Берёт карту в собственную руку.
     */
    public Card drawCard() {
        Card card = deck.draw();
        hand.add(card);
        return card;
    }

    /**
     * Проверяет необходимость добора дилером.
     */
    public boolean needsCard() {
        return hand.getScore() < Rules.DEALER_STOP_SCORE;
    }

    /**
     * Возвращает неизменяемый список карт дилера.
     */
    public List<Card> getCards() {
        return hand.getCards();
    }

    /**
     * Возвращает сумму очков дилера.
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