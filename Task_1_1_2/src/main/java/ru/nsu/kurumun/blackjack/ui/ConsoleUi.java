package ru.nsu.kurumun.blackjack.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;
import ru.nsu.kurumun.blackjack.game.Game;
import ru.nsu.kurumun.blackjack.game.Round;
import ru.nsu.kurumun.blackjack.game.RoundState;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Hand;

/** Читает команды и показывает игру, правила выполняет Game. */
public final class ConsoleUi {
    private static final String YES = "1";
    private static final String NO = "0";
    private final Scanner input;
    private final PrintStream output;

    /**
     * Создаёт консольный интерфейс с указанными потоками.
     */
    public ConsoleUi(Scanner input, PrintStream output) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    /**
     * Проводит игру до отказа от следующего раунда или конца ввода.
     */
    public void run(Game game) {
        Objects.requireNonNull(game);
        int roundNumber = 0;
        output.println("Welcome to blackjack!");
        do {
            game.startRound();
            roundNumber++;
            output.println("Round " + roundNumber);
            output.println("Dealer dealt the cards");
            showHands(game.getRound());
            if (!playPlayerTurn(game)) {
                break;
            }
            playDealerTurn(game);
            showResult(game);
            output.println("Another round? Enter 1 to continue, or 0 to exit:");
        } while (readChoice().orElse(false));
        output.println("Game over.");
    }

    private boolean playPlayerTurn(Game game) {
        while (game.getRound().getState() == RoundState.PLAYER_TURN) {
            output.println("Enter 1 to hit, and 0 to stand:");
            Optional<Boolean> choice = readChoice();
            if (choice.isEmpty()) {
                return false;
            }
            if (choice.get()) {
                Card card = game.hit();
                Hand hand = handOf(game.getRound().getPlayerCards());
                output.println("You revealed a card "
                        + formatCard(card, hand.getValues().getLast()));
                showHands(game.getRound());
            } else {
                game.stand();
            }
        }
        return true;
    }

    private void playDealerTurn(Game game) {
        if (game.getRound().getState() != RoundState.DEALER_TURN) {
            return;
        }
        Hand hand = handOf(game.getRound().getDealerCards());
        output.println("Dealer's turn");
        output.println("Dealer reveals the face-down card "
            + formatCard(hand.getCards().getLast(), hand.getValues().getLast()));
        showHands(game.getRound());
        while (game.getRound().getState() == RoundState.DEALER_TURN) {
            Optional<Card> drawn = game.advanceDealer();
            if (drawn.isPresent()) {
                hand = handOf(game.getRound().getDealerCards());
                output.println("Dealer reveals a card "
                    + formatCard(drawn.get(), hand.getValues().getLast()));
                showHands(game.getRound());
            }
        }
    }

    private Optional<Boolean> readChoice() {
        while (input.hasNextLine()) {
            String choice = input.nextLine().trim();
            if (YES.equals(choice)) {
                return Optional.of(true);
            }
            if (NO.equals(choice)) {
                return Optional.of(false);
            }
            output.println("Invalid input. Please enter 1 or 0.");
        }
        return Optional.empty();
    }

    private void showHands(Round round) {
        output.println("Your cards: " + formatHand(round.getPlayerCards(), false));
        boolean hidden = round.getState() == RoundState.PLAYER_TURN;
        output.println("Dealer's cards: " + formatHand(round.getDealerCards(), hidden));
    }

    private String formatHand(List<Card> cards, boolean hidden) {
        Hand hand = handOf(cards);
        List<Integer> values = hand.getValues();
        StringJoiner text = new StringJoiner(", ", "[", "]");
        for (int index = 0; index < cards.size(); index++) {
            text.add(formatCard(cards.get(index), values.get(index)));
        }
        if (hidden) {
            text.add("<closed card>");
            return text.toString();
        }
        return text + " -> " + hand.getScore();
    }

    private Hand handOf(List<Card> cards) {
        Hand hand = new Hand();
        for (Card card : cards) {
            hand.add(card);
        }
        return hand;
    }

    private String formatCard(Card card, int value) {
        return card + " (" + value + ")";
    }

    private void showResult(Game game) {
        Round round = game.getRound();
        if (handOf(round.getPlayerCards()).isBlackjack()) {
            output.println("You got a blackjack!");
        }
        if (handOf(round.getDealerCards()).isBlackjack()) {
            output.println("Dealer got a blackjack!");
        }
        switch (round.getState()) {
            case PLAYER_WIN -> output.println("You won the round!");
            case DEALER_WIN -> output.println("Dealer won the round.");
            case DRAW -> output.println("Tie.");
            default -> throw new IllegalStateException(
                    "Cannot display the outcome of an unfinished round.");
        }
        output.println("Score " + game.getPlayerWins() + ":" + game.getDealerWins()
            + ". Ties: " + game.getDraws());
    }
}
