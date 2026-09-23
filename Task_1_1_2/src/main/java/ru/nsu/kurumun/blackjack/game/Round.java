package ru.nsu.kurumun.blackjack.game;

import java.util.List;
import java.util.Optional;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Deck;
import ru.nsu.kurumun.blackjack.model.Hand;

/** Хранит руки, контролирует очерёдность ходов и определяет исход одного раунда. */
public final class Round {
    private final Hand playerHand = new Hand();
    private final Hand dealerHand = new Hand();
    private final Dealer dealer;
    private RoundState state = RoundState.PLAYER_TURN;

    Round(Deck deck) {
        dealer = new Dealer(deck);
        dealer.dealInitial(playerHand, dealerHand);
        if (playerHand.isBlackjack() && dealerHand.isBlackjack()) {
            state = RoundState.DRAW;
        } else if (playerHand.isBlackjack()) {
            state = RoundState.PLAYER_WIN;
        } else if (dealerHand.isBlackjack()) {
            state = RoundState.DEALER_WIN;
        }
    }

    Card hit() {
        requireState(RoundState.PLAYER_TURN);
        Card card = dealer.dealTo(playerHand);
        if (playerHand.isBust()) {
            state = RoundState.DEALER_WIN;
        }
        return card;
    }

    void stand() {
        requireState(RoundState.PLAYER_TURN);
        state = RoundState.DEALER_TURN;
    }

    Optional<Card> advanceDealer() {
        requireState(RoundState.DEALER_TURN);
        Optional<Card> drawn = Optional.empty();
        if (dealer.needsCard(dealerHand)) {
            drawn = Optional.of(dealer.dealTo(dealerHand));
        }
        if (dealerHand.isBust()) {
            state = RoundState.PLAYER_WIN;
        } else if (!dealer.needsCard(dealerHand)) {
            compareHands();
        }
        return drawn;
    }

    private void compareHands() {
        if (playerHand.getScore() > dealerHand.getScore()) {
            state = RoundState.PLAYER_WIN;
        } else if (playerHand.getScore() < dealerHand.getScore()) {
            state = RoundState.DEALER_WIN;
        } else {
            state = RoundState.DRAW;
        }
    }

    private void requireState(RoundState expected) {
        if (state != expected) {
            throw new IllegalStateException("Action is not available in the current round phase.");
        }
    }

    /**
     * Возвращает фазу или окончательный исход.
     */
    public RoundState getState() {
        return state;
    }

    /**
     * Возвращает карты игрока без возможности изменить руку.
     */
    public List<Card> getPlayerCards() {
        return playerHand.getCards();
    }

    /**
     * Возвращает только открытые карты дилера.
     */
    public List<Card> getDealerCards() {
        if (state == RoundState.PLAYER_TURN) {
            return List.of(dealerHand.getCards().getFirst());
        }
        return dealerHand.getCards();
    }
}
