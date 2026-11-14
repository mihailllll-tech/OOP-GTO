package ru.nsu.kurumun.substring;

import java.io.IOException;
import java.util.function.LongConsumer;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2 || args[1].isEmpty()) {
            System.err.println("Укажите путь к файлу и непустую подстроку");
            System.exit(1);
        }

        System.out.print("[");
        SubstringSearch.find(args[0], args[1], new LongConsumer() {
            private boolean first = true;

            @Override
            public void accept(long position) {
                if (!first) {
                    System.out.print(", ");
                }
                System.out.print(position);
                first = false;
            }
        });
        System.out.println("]");
    }
}
