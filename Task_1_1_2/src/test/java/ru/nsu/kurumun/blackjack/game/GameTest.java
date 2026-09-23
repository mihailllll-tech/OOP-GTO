package ru.nsu.kurumun.blackjack.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.blackjack.model.Card;
import ru.nsu.kurumun.blackjack.model.Deck;
import ru.nsu.kurumun.blackjack.model.Rank;
import ru.nsu.kurumun.blackjack.model.Suit;

/** Проверяет счетчики сеанса и защитные механизмы между раундами. */
class GameTest {

    @Test
    void unfinishedRoundCannotBeReplacedAndDoesNotConsumeNextDeck() {
        AtomicInteger decksRequested = new AtomicInteger();
        Game game = new Game(() -> {
            decksRequested.incrementAndGet();
            return deck(Rank.TEN, Rank.TEN, Rank.EIGHT, Rank.SEVEN);
        });
        game.startRound();
        assertThrows(IllegalStateException.class, game::startRound);
        assertEquals(1, decksRequested.get());
        assertEquals(1, game.getRoundNumber());
        assertCounts(game, 0, 0, 0);
    }

    @Test
    void allResultsAreCountedExactlyOnceAcrossRounds() {
        Queue<Deck> decks = new ArrayDeque<>();
        decks.add(deck(Rank.ACE, Rank.TEN, Rank.KING, Rank.EIGHT));
        decks.add(deck(Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.KING));
        decks.add(deck(Rank.ACE, Rank.ACE, Rank.KING, Rank.QUEEN));
        decks.add(deck(Rank.TEN, Rank.TEN, Rank.EIGHT, Rank.SEVEN));
        Game game = new Game(decks::remove);
        game.startRound();
        assertCounts(game, 1, 0, 0);
        assertThrows(IllegalStateException.class, game::hit);
        assertCounts(game, 1, 0, 0);
        game.startRound();
        assertCounts(game, 1, 1, 0);
        game.startRound();
        assertCounts(game, 1, 1, 1);
        game.startRound();
        assertEquals(4, game.getRoundNumber());
        assertCounts(game, 1, 1, 1);
        game.stand();
        assertCounts(game, 1, 1, 1);
        game.advanceDealer();
        assertCounts(game, 2, 1, 1);
        assertThrows(IllegalStateException.class, game::advanceDealer);
        assertThrows(IllegalStateException.class, game::stand);
        assertCounts(game, 2, 1, 1);
    }

    @Test
    void hitBustUpdatesTheSessionScoreImmediately() {
        Game game = new Game(() -> deck(
                Rank.TEN, Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.FIVE));
        game.startRound();
        assertEquals(new Card(Rank.FIVE, Suit.SPADES), game.hit());
        assertEquals(RoundState.DEALER_WIN, game.getRound().getState());
        assertCounts(game, 0, 1, 0);
        assertThrows(IllegalStateException.class, game::hit);
        assertCounts(game, 0, 1, 0);
    }

    @Test
    void forbiddenActionDoesNotChangeCountersOrPhase() {
        Game game = new Game(() -> deck(
                Rank.TEN, Rank.TEN, Rank.EIGHT, Rank.SEVEN));
        game.startRound();
        assertThrows(IllegalStateException.class, game::advanceDealer);
        assertEquals(RoundState.PLAYER_TURN, game.getRound().getState());
        assertCounts(game, 0, 0, 0);
        game.stand();
        assertThrows(IllegalStateException.class, game::hit);
        assertThrows(IllegalStateException.class, game::stand);
        assertEquals(RoundState.DEALER_TURN, game.getRound().getState());
        assertCounts(game, 0, 0, 0);
    }

    private static Deck deck(Rank... ranks) {
        return new Deck(Arrays.stream(ranks)
                .map(rank -> new Card(rank, Suit.SPADES)).toList());
    }

    private static void assertCounts(Game game, int player, int dealer, int draws) {
        assertEquals(player, game.getPlayerWins());
        assertEquals(dealer, game.getDealerWins());
        assertEquals(draws, game.getDraws());
    }
}
