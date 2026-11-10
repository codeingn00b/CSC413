
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

        String viewName = args.length > 0
                ? args[0].trim().toLowerCase()
                : "console";

        BoardView view;

        switch (viewName) {
            case "console" ->
                    view = new ConsoleView();

            case "emoji" ->
                    view = new EmojiView();

            case "swing" ->
                    view = new SwingBoardView();

            case "terminal" ->
                    view = new TerminalBoardView();

            default -> {
                System.err.println(
                        "Unknown view: " + viewName);

                System.err.println(
                        "Available views: console, emoji, swing, terminal");

                return;
            }
        }

        GameController controller =
                new GameController(game, view);

        controller.start();
    }

    private Main() {
    }
}
