package ru.nsu.kurumun.blackjack.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.blackjack.model.*;

/** Проверяет правила посредством полных детерминированных раундов. */
class RoundTest {
    @Test
    void assignmentExampleRevaluesDealerAceAndEndsOnBust() {
        Round round = round(
                Rank.QUEEN, Rank.ACE, Rank.THREE, Rank.THREE,
                Rank.SEVEN, Rank.TEN, Rank.KING);
        assertEquals(RoundState.PLAYER_TURN, round.getState());
        assertEquals(13, score(round.getPlayerCards()));
        assertEquals(List.of(card(Rank.ACE)), round.getDealerCards());
        assertEquals(card(Rank.SEVEN), round.hit());
        assertEquals(20, score(round.getPlayerCards()));
        round.stand();
        assertEquals(RoundState.DEALER_TURN, round.getState());
        assertEquals(14, score(round.getDealerCards()));
        assertEquals(Optional.of(card(Rank.TEN)), round.advanceDealer());
        assertEquals(14, score(round.getDealerCards()));
        assertEquals(RoundState.DEALER_TURN, round.getState());
        assertEquals(Optional.of(card(Rank.KING)), round.advanceDealer());
        assertEquals(24, score(round.getDealerCards()));
        assertEquals(RoundState.PLAYER_WIN, round.getState());
    }

    @Test
    void initialBlackjacksResolveBeforeAnyInput() {
        Round player = round(Rank.ACE, Rank.TEN, Rank.KING, Rank.EIGHT);
        assertEquals(RoundState.PLAYER_WIN, player.getState());
        Round dealer = round(Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.KING);
        assertEquals(RoundState.DEALER_WIN, dealer.getState());
        Round both = round(Rank.ACE, Rank.ACE, Rank.KING, Rank.QUEEN);
        assertEquals(RoundState.DRAW, both.getState());
        assertEquals(2, both.getDealerCards().size());
        assertThrows(IllegalStateException.class, both::hit);
    }

    @Test
    void playerBustEndsRoundImmediately() {
        Round round = round(Rank.TEN, Rank.TWO, Rank.NINE, Rank.THREE, Rank.KING);
        round.hit();
        assertEquals(29, score(round.getPlayerCards()));
        assertEquals(RoundState.DEALER_WIN, round.getState());
        assertEquals(2, round.getDealerCards().size());
        assertThrows(IllegalStateException.class, round::hit);
        assertThrows(IllegalStateException.class, round::stand);
        assertThrows(IllegalStateException.class, round::advanceDealer);
    }

    @Test
    void dealerStopsOnSoftSeventeenWithoutDrawing() {
        Round round = round(Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.SIX, Rank.KING);
        round.stand();
        assertEquals(Optional.empty(), round.advanceDealer());
        assertEquals(2, round.getDealerCards().size());
        assertEquals(17, score(round.getDealerCards()));
        assertEquals(RoundState.PLAYER_WIN, round.getState());
    }

    @Test
    void dealerDrawsAtSixteenThenStopsAtSeventeen() {
        Round round = round(Rank.TEN, Rank.TEN, Rank.SEVEN, Rank.SIX, Rank.ACE);
        round.stand();
        assertEquals(Optional.of(card(Rank.ACE)), round.advanceDealer());
        assertEquals(17, score(round.getDealerCards()));
        assertEquals(RoundState.DRAW, round.getState());
    }

    @Test
    void threeCardTwentyOneIsNotAnAutomaticWin() {
        Round round = round(
                Rank.SEVEN, Rank.TEN, Rank.SEVEN, Rank.SIX, Rank.SEVEN, Rank.FIVE);
        round.hit();
        assertEquals(21, score(round.getPlayerCards()));
        assertEquals(RoundState.PLAYER_TURN, round.getState());
        round.stand();
        round.advanceDealer();
        assertEquals(RoundState.DRAW, round.getState());
    }

    @Test
    void higherDealerScoreWinsWithoutBust() {
        Round round = round(Rank.TEN, Rank.TEN, Rank.SEVEN, Rank.NINE);
        round.stand();
        assertEquals(Optional.empty(), round.advanceDealer());
        assertEquals(RoundState.DEALER_WIN, round.getState());
    }

    @Test
    void hiddenCardAndLaterChangesDoNotLeakIntoSnapshots() {
        Round round = round(Rank.FIVE, Rank.KING, Rank.SIX, Rank.FIVE, Rank.TWO);
        List<Card> playerSnapshot = round.getPlayerCards();
        List<Card> dealerSnapshot = round.getDealerCards();
        assertEquals(List.of(card(Rank.KING)), dealerSnapshot);
        assertThrows(UnsupportedOperationException.class, () -> playerSnapshot.clear());
        assertThrows(UnsupportedOperationException.class, () -> dealerSnapshot.clear());
        round.hit();
        assertEquals(2, playerSnapshot.size());
        assertEquals(3, round.getPlayerCards().size());
        round.stand();
        assertEquals(1, dealerSnapshot.size());
        assertEquals(2, round.getDealerCards().size());
    }

    @Test
    void actionsBelongToTheirOwnTurn() {
        Round round = round(Rank.FIVE, Rank.TEN, Rank.SIX, Rank.SEVEN);
        assertThrows(IllegalStateException.class, round::advanceDealer);
        assertEquals(RoundState.PLAYER_TURN, round.getState());
        round.stand();
        assertThrows(IllegalStateException.class, round::hit);
        assertThrows(IllegalStateException.class, round::stand);
        round.advanceDealer();
        assertEquals(RoundState.DEALER_WIN, round.getState());
        assertThrows(IllegalStateException.class, round::advanceDealer);
    }

    @Test
    void onlyOutcomeStatesAreFinished() {
        assertFalse(RoundState.PLAYER_TURN.isFinished());
        assertFalse(RoundState.DEALER_TURN.isFinished());
        assertTrue(RoundState.PLAYER_WIN.isFinished());
        assertTrue(RoundState.DEALER_WIN.isFinished());
        assertTrue(RoundState.DRAW.isFinished());
    }

    private static Round round(Rank... ranks) {
        return new Round(new Deck(Arrays.stream(ranks).map(RoundTest::card).toList()));
    }

    private static int score(List<Card> cards) {
        Hand hand = new Hand();
        cards.forEach(hand::add);
        return hand.getScore();
    }

    private static Card card(Rank rank) {
        return new Card(rank, Suit.SPADES);
    }
}
