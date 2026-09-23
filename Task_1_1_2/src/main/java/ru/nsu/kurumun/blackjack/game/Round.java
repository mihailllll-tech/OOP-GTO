package ru.nsu.kurumun.blackjack.game;

import java.util.List;
import java.util.Optional;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Deck;

/** Управляет участниками и очерёдностью ходов и результатом одного раунда. */
public final class Round {
    private final Player player = new Player();
    private final Dealer dealer;
    private RoundState state = RoundState.PLAYER_TURN;

    /**
     * Создаёт раунд и выполняет начальную раздачу.
     */
    public Round(Deck deck) {
        dealer = new Dealer(deck);
        dealer.dealInitial(player);
        if (player.isBlackjack() && dealer.isBlackjack()) {
            state = RoundState.DRAW;
        } else if (player.isBlackjack()) {
            state = RoundState.PLAYER_WIN;
        } else if (dealer.isBlackjack()) {
            state = RoundState.DEALER_WIN;
        }
    }

    /**
     * Выдаёт игроку карту и проверяет перебор.
     */
    public Card hit() {
        requireState(RoundState.PLAYER_TURN);
        Card card = dealer.dealTo(player);
        if (player.isBust()) {
            state = RoundState.DEALER_WIN;
        }
        return card;
    }

    /** Завершает ход игрока и передаёт ход дилеру. */
    public void stand() {
        requireState(RoundState.PLAYER_TURN);
        state = RoundState.DEALER_TURN;
    }

    /**
     * Выполняет один шаг хода дилера.
     */
    public Optional<Card> advanceDealer() {
        requireState(RoundState.DEALER_TURN);
        Optional<Card> drawn = Optional.empty();
        if (dealer.needsCard()) {
            drawn = Optional.of(dealer.drawCard());
        }
        if (dealer.isBust()) {
            state = RoundState.PLAYER_WIN;
        } else if (!dealer.needsCard()) {
            compareHands();
        }
        return drawn;
    }

    private void compareHands() {
        if (player.getScore() > dealer.getScore()) {
            state = RoundState.PLAYER_WIN;
        } else if (player.getScore() < dealer.getScore()) {
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
        return player.getCards();
    }

    /**
     * Возвращает только открытые карты дилера.
     */
    public List<Card> getDealerCards() {
        if (state == RoundState.PLAYER_TURN) {
            return List.of(dealer.getCards().getFirst());
        }
        return dealer.getCards();
    }
}
