package ru.nsu.kurumun.blackjack;

import java.util.Objects;

/** Раздаёт карты из колоды и применяет правило добора дилера. */
public final class Dealer {
    private final Deck deck;

    /**
     * Создаёт дилера с колодой текущего раунда.
     */
    public Dealer(Deck deck) {
        this.deck = Objects.requireNonNull(deck);
    }

    /**
     * Поочерёдно раздаёт игроку и дилеру по две карты.
     */
    public void dealInitial(Hand player, Hand dealerHand) {
        for (int index = 0; index < Rules.INITIAL_HAND_SIZE; index++) {
            dealTo(player);
            dealTo(dealerHand);
        }
    }

    /**
     * Выдаёт очередную карту указанной руке.
     */
    public Card dealTo(Hand hand) {
        Objects.requireNonNull(hand);
        Card card = deck.draw();
        hand.add(card);
        return card;
    }

    /**
     * Проверяет необходимость добора дилером, включая руки с тузом.
     */
    public boolean needsCard(Hand hand) {
        return hand.getScore() < Rules.DEALER_STOP_SCORE;
    }
}
