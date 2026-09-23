package ru.nsu.kurumun.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Проверяет номиналы карт, подсчет очков в руке и принадлежность колоды. */
class ModelTest {
    @Test
    void ranksHaveTheValuesRequiredByTheGame() {
        Rank[] numberRanks = {
            Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN
        };
        for (int index = 0; index < numberRanks.length; index++) {
            assertEquals(index + 2, numberRanks[index].getValue());
        }
        assertEquals(10, Rank.KING.getValue());
        assertEquals(11, Rank.ACE.getValue());
    }

    @Test
    void cardsAreValuesWithRussianNames() {
        Card card = new Card(Rank.QUEEN, Suit.SPADES);
        Card equalCard = new Card(Rank.QUEEN, Suit.SPADES);
        assertEquals(Rank.QUEEN, card.getRank());
        assertEquals(Suit.SPADES, card.getSuit());
        assertEquals(card, card);
        assertEquals(card, equalCard);
        assertNotEquals(card, new Card(Rank.KING, Suit.SPADES));
        assertNotEquals(card, new Card(Rank.QUEEN, Suit.HEARTS));
        assertNotEquals(card, null);
        assertNotEquals(card, "Queen Spades");
        assertTrue(card.toString().contains(Rank.QUEEN.getDisplayName()));
        assertTrue(card.toString().contains(Suit.SPADES.getDisplayName()));
    }

    @Test
    void emptyHandIsNeitherBlackjackNorBust() {
        Hand hand = new Hand();
        assertEquals(0, hand.getScore());
        assertEquals(List.of(), hand.getCards());
        assertEquals(List.of(), hand.getValues());
        assertFalse(hand.isBlackjack());
        assertFalse(hand.isBust());
    }

    @Test
    void severalAcesAreReducedOnlyAsMuchAsNecessary() {
        Hand hand = hand(Rank.ACE, Rank.ACE, Rank.NINE);
        assertEquals(21, hand.getScore());
        List<Integer> previousValues = hand.getValues();
        assertEquals(List.of(1, 9, 11), previousValues.stream().sorted().toList());
        assertFalse(hand.isBlackjack());
        hand.add(card(Rank.KING));
        assertEquals(21, hand.getScore());
        assertEquals(List.of(1, 1, 9, 10), hand.getValues().stream().sorted().toList());
        assertEquals(List.of(1, 9, 11), previousValues.stream().sorted().toList());
        assertFalse(hand.isBust());
        hand.add(card(Rank.TWO));
        assertEquals(23, hand.getScore());
        assertTrue(hand.isBust());
    }

    @Test
    void naturalNeedsExactlyTwoCards() {
        assertTrue(hand(Rank.ACE, Rank.JACK).isBlackjack());
        assertFalse(hand(Rank.SEVEN, Rank.SEVEN, Rank.SEVEN).isBlackjack());
        assertFalse(hand(Rank.TEN, Rank.NINE).isBlackjack());
        assertFalse(hand(Rank.ACE, Rank.ACE).isBlackjack());
    }

    @Test
    void handReturnsImmutableSnapshots() {
        Hand hand = hand(Rank.ACE);
        List<Card> cards = hand.getCards();
        List<Integer> values = hand.getValues();
        assertThrows(UnsupportedOperationException.class, () -> cards.add(card(Rank.TWO)));
        assertThrows(UnsupportedOperationException.class, () -> values.add(2));
        hand.add(card(Rank.TEN));
        assertEquals(1, cards.size());
        assertEquals(List.of(11), values);
        assertThrows(NullPointerException.class, () -> hand.add(null));
    }


    @Test
    void sameSeedMakesShufflingReproducible() {
        Deck first = new Deck(new Random(321));
        Deck second = new Deck(new Random(321));
        while (first.remaining() > 0) {
            assertEquals(first.draw(), second.draw());
        }
        assertEquals(0, second.remaining());
    }

    @Test
    void suppliedDeckCopiesItsInputAndPreservesDrawOrder() {
        List<Card> source = new ArrayList<>(List.of(card(Rank.TWO), card(Rank.KING)));
        Deck deck = new Deck(source);
        source.clear();
        assertEquals(2, deck.remaining());
        assertEquals(card(Rank.TWO), deck.draw());
        assertEquals(card(Rank.KING), deck.draw());
        assertThrows(IllegalStateException.class, deck::draw);
    }

    private static Hand hand(Rank... ranks) {
        Hand hand = new Hand();
        for (Rank rank : ranks) {
            hand.add(card(rank));
        }
        return hand;
    }

    private static Card card(Rank rank) {
        return new Card(rank, Suit.SPADES);
    }
}
