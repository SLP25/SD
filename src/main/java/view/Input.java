package view;

import java.io.IOException;
import java.util.Scanner;

public final class Input {

    public static String[] read() throws IOException {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    public static Boolean isExit(String string) {
        return string.equals("quit") || string.equals("q") || string.equals("exit");
    }
}
