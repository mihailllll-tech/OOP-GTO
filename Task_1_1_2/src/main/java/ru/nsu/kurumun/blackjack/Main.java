package ru.nsu.kurumun.blackjack;

import ru.nsu.kurumun.blackjack.game.Game;
import ru.nsu.kurumun.blackjack.ui.ConsoleUi;

import java.util.Random;
import java.util.Scanner;

/** Точка входа. */
public final class Main {
    private Main() {
    }
    /**
     * Запускает консольный блэкджек.
     */
    public static void main(String[] args) {
        Game game = new Game(new Random());
        ConsoleUi console = new ConsoleUi(new Scanner(System.in), System.out);
        console.run(game);
    }
}
