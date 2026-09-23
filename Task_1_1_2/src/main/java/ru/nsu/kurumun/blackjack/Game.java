package ru.nsu.kurumun.blackjack;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/** Управляет последовательностью раундов и общим счётом, не обращаясь к консоли. */
public final class Game {
    private final Supplier<Deck> deckFactory;
    private Round round;
    private int roundNumber;
    private int playerWins;
    private int dealerWins;
    private int draws;
    private boolean resultRecorded;

    /**
     * Создаёт игру с новой перемешанной колодой для каждого раунда.
     */
    public Game(Random random) {
        Objects.requireNonNull(random);
        deckFactory = () -> new Deck(random);
    }

    Game(Supplier<Deck> deckFactory) {
        this.deckFactory = Objects.requireNonNull(deckFactory);
    }

    /**
     * Начинает следующий раунд и учитывает возможный начальный блэкджек.
     */
    public void startRound() {
        if (round != null && !round.getState().isFinished()) {
            throw new IllegalStateException("Finish the current round first.");
        }
        round = new Round(deckFactory.get());
        roundNumber++;
        resultRecorded = false;
        recordResult();
    }

    /**
     * Выдаёт игроку карту и учитывает возможный перебор.
     */
    public Card hit() {
        Card card = getRound().hit();
        recordResult();
        return card;
    }

    /**
     * Передаёт ход дилеру и открывает его вторую карту.
     */
    public void stand() {
        getRound().stand();
    }

    /**
     * Выполняет один шаг дилера, чтобы консоль могла показать каждую новую карту.
     */
    public Optional<Card> advanceDealer() {
        Optional<Card> card = getRound().advanceDealer();
        recordResult();
        return card;
    }

    private void recordResult() {
        if (resultRecorded || !round.getState().isFinished()) {
            return;
        }
        switch (round.getState()) {
            case PLAYER_WIN -> playerWins++;
            case DEALER_WIN -> dealerWins++;
            case DRAW -> draws++;
            default -> throw new IllegalStateException("Round is not ended.");
        }
        resultRecorded = true;
    }

    /**
     * Возвращает текущий раунд для чтения состояния и открытых карт.
     */
    public Round getRound() {
        return round;
    }

    /**
     * Возвращает количество начатых раундов.
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Возвращает число побед игрока.
     */
    public int getPlayerWins() {
        return playerWins;
    }

    /**
     * Возвращает число побед дилера.
     */
    public int getDealerWins() {
        return dealerWins;
    }

    /**
     * Возвращает число ничьих.
     */
    public int getDraws() {
        return draws;
    }
}
