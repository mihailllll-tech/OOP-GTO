package ru.nsu.kurumun.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/** Checks user-visible transcripts and portable input handling. */
class ConsoleUiTest {
    @Test
    void assignmentExampleShowsEveryCardAndRevaluesAce() {
        List<Card> cards = List.of(
                new Card(Rank.QUEEN, Suit.SPADES),
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.THREE, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.TEN, Suit.SPADES),
                new Card(Rank.KING, Suit.DIAMONDS));
        Game game = new Game(() -> new Deck(cards));
        String transcript = play(game, "1\r\n0\r\n0\r\n");
        List<String> lines = transcript.lines().toList();

        assertTrue(lines.contains(
                "Your cards: [Queen Spades (10), Three Hearts (3)] -> 13"));
        assertTrue(lines.contains(
                "Dealer's cards: [Ace Clubs (11), <closed card>]"));
        assertTrue(lines.contains("You revealed a card Seven Spades (7)"));
        assertTrue(lines.contains(
                "Dealer reveals the face-down card Three Clubs (3)"));
        assertTrue(lines.contains(
                "Dealer reveals a card Ten Spades (10)"));
        assertTrue(lines.contains(
                "Dealer's cards: [Ace Clubs (1), Three Clubs (3), "
                        + "Ten Spades (10)] -> 14"));
        assertTrue(lines.contains(
                "Dealer reveals a card King Diamonds (10)"));
        assertTrue(lines.contains(
                "Dealer's cards: [Ace Clubs (1), Three Clubs (3), "
                        + "Ten Spades (10), King Diamonds (10)] -> 24"));
        assertTrue(lines.contains("You won the round!"));
        assertTrue(lines.contains("Score 1:0. Ties: 0"));
        assertEquals("Game over.", lines.getLast());
        assertEquals(1, game.getRoundNumber());
    }

    @Test
    void endOfInputDuringPlayerTurnDoesNotInventAnOutcomeOrLeakHoleCard() {
        Game game = new Game(() -> deck(Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.SIX));
        String transcript = play(game, "");
        List<String> dealerLines = transcript.lines()
                .filter(line -> line.startsWith("Dealer's cards:"))
                .toList();

        assertEquals(List.of(
                "Dealer's cards: [Ace Spades (11), <closed card>]"), dealerLines);
        assertFalse(transcript.contains("Six"));
        assertFalse(transcript.contains("won"));
        assertFalse(transcript.contains("Tie."));
        assertFalse(transcript.contains("Score"));
        assertEquals(RoundState.PLAYER_TURN, game.getRound().getState());
        assertEquals(0, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
        assertEquals("Game over.", transcript.lines().toList().getLast());
    }

    @Test
    void invalidPlayerInputRepeatsPromptWithoutTakingCards() {
        Game game = new Game(() -> deck(
                Rank.FIVE, Rank.TEN, Rank.SIX, Rank.SEVEN, Rank.EIGHT));
        String transcript = play(game, "error\n\n2\n 1 \n0\n0\n");

        assertEquals(3, transcript.lines()
                .filter(line -> line.equals(
                        "Invalid input. Please enter 1 or 0."))
                .count());
        assertEquals(1, transcript.lines()
                .filter(line -> line.startsWith("You revealed a card"))
                .count());
        assertEquals(3, game.getRound().getPlayerCards().size());
        assertEquals(RoundState.PLAYER_WIN, game.getRound().getState());
        assertTrue(transcript.contains("You revealed a card Eight Spades (8)"));
        assertTrue(transcript.contains("Score 1:0. Ties: 0"));
    }

    @Test
    void invalidRestartInputIsRetriedAndNextRoundKeepsScore() {
        Queue<Deck> decks = new ArrayDeque<>();
        decks.add(deck(Rank.ACE, Rank.TEN, Rank.KING, Rank.EIGHT));
        decks.add(deck(Rank.TEN, Rank.TEN, Rank.EIGHT, Rank.SEVEN));
        Game game = new Game(decks::remove);
        String transcript = play(game, "again\r\n 1 \r\n0\r\n0\r\n");

        assertTrue(transcript.contains("Invalid input. Please enter 1 or 0."));
        assertTrue(transcript.lines().anyMatch(line -> line.equals("Round 2")));
        assertFalse(transcript.contains("Round 3"));
        assertTrue(transcript.contains("Score 2:0. Ties: 0"));
        assertEquals(2, game.getRoundNumber());
        assertEquals(2, game.getPlayerWins());
    }

    @Test
    void naturalOutcomesNeedNoPlayerTurnAndExitOnEndOfInput() {
        List<Deck> decks = List.of(
                deck(Rank.ACE, Rank.TEN, Rank.KING, Rank.EIGHT),
                deck(Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.KING),
                deck(Rank.ACE, Rank.ACE, Rank.KING, Rank.QUEEN));
        List<String> results = List.of(
                "You won the round!", "Dealer won the round.", "Tie.");
        List<String> scores = List.of(
                "Score 1:0. Ties: 0",
                "Score 0:1. Ties: 0",
                "Score 0:0. Ties: 1");

        for (int index = 0; index < decks.size(); index++) {
            Deck deck = decks.get(index);
            String transcript = play(new Game(() -> deck), "");

            assertTrue(transcript.contains(results.get(index)));
            assertTrue(transcript.contains(scores.get(index)));
            assertFalse(transcript.contains("to hit"));
            assertFalse(transcript.contains("<closed card>"));
            assertFalse(transcript.contains("Dealer's turn"));
            assertEquals("Game over.", transcript.lines().toList().getLast());
            assertEquals(index != 1,
                    transcript.contains("You got a blackjack!"));
            assertEquals(index != 0,
                    transcript.contains("Dealer got a blackjack!"));
        }
    }

    @Test
    void hitBustShowsLossWithoutRunningDealerTurn() {
        Game game = new Game(() -> deck(
                Rank.TEN, Rank.FIVE, Rank.EIGHT, Rank.SIX, Rank.KING));
        String transcript = play(game, "1\n0\n");

        assertTrue(transcript.contains(
                "You revealed a card King Spades (10)"));
        assertTrue(transcript.contains("Dealer won the round."));
        assertTrue(transcript.contains("Score 0:1. Ties: 0"));
        assertFalse(transcript.contains("Dealer's turn"));
        assertEquals(2, game.getRound().getDealerCards().size());
    }

    @Test
    void freshlyDrawnAceDisplaysOneWhenElevenWouldCauseBust() {
        Game game = new Game(() -> deck(
                Rank.TEN, Rank.TEN, Rank.FOUR, Rank.SEVEN, Rank.ACE));
        String transcript = play(game, "1\n0\n0\n");

        assertTrue(transcript.contains(
                "You revealed a card Ace Spades (1)"));
        assertTrue(transcript.contains(
                "Your cards: [Ten Spades (10), Four Spades (4), "
                        + "Ace Spades (1)] -> 15"));
    }

    @Test
    void mainCanRunWithClosedInput() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream output = new PrintStream(
                buffer, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(new byte[0]));
            System.setOut(output);
            Main.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        List<String> lines = buffer.toString(StandardCharsets.UTF_8)
                .lines()
                .toList();
        assertEquals("Welcome to blackjack!", lines.getFirst());
        assertEquals("Game over.", lines.getLast());
    }

    private static String play(Game game, String commands) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (Scanner input = new Scanner(commands);
             PrintStream output = new PrintStream(
                     buffer, true, StandardCharsets.UTF_8)) {
            new ConsoleUi(input, output).run(game);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static Deck deck(Rank... ranks) {
        return new Deck(Arrays.stream(ranks)
                .map(rank -> new Card(rank, Suit.SPADES))
                .toList());
    }
}