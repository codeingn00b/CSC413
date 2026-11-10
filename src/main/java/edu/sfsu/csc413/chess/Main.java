package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.SwingBoardView;
import edu.sfsu.csc413.chess.view.TerminalBoardView;

public final class Main {

    public static void main(String[] args) {
        Game game = new Game();

        String mode = args.length > 0
                ? args[0].toLowerCase()
                : "console";

        BoardView view;

        switch (mode) {
            case "emoji" ->
                    view = new EmojiView();

            case "swing" ->
                    view = new SwingBoardView();

            case "terminal" ->
                    view = new TerminalBoardView();

            case "console" ->
                    view = new ConsoleView();

            default -> {
                System.out.println(
                        "Unknown view: " + mode
                                + ". Using console view.");
                view = new ConsoleView();
            }
        }

        GameController controller =
                new GameController(game, view);

        controller.start();
    }

    private Main() {
    }
}