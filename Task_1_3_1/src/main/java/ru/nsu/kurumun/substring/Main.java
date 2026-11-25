package ru.nsu.kurumun.substring;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Точка входа для подсчёта совпадений в текстовом файле.
 */
public class Main {
    private Main() {
    }

    /**
     * Печатает количество вхождений заданной подстроки.
     *
     * @param args путь к UTF-8-файлу и искомая подстрока
     * @throws IOException если файл не удалось открыть или прочитать
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2 || args[1].isEmpty()) {
            System.err.println("Укажите путь к файлу и непустую подстроку");
            System.exit(1);
        }

        AtomicLong count = new AtomicLong();
        SubstringSearch.find(args[0], args[1], ignored -> count.incrementAndGet());
        System.out.println(count.get());
    }
}
