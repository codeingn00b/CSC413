
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * A terminal chess view that presents legal moves as a numbered menu.
 *
 * Unlike ConsoleView and EmojiView, players select moves by number
 * instead of typing coordinate notation.
 */
public class TerminalBoardView implements BoardView {

    private final PrintStream out;
    private final Scanner in;

    /**
     * Creates a terminal view using standard input and output.
     */
    public TerminalBoardView() {
        this(System.out, new Scanner(System.in));
    }

    /**
     * Creates a terminal view with injectable input and output.
     */
    public TerminalBoardView(PrintStream out, Scanner in) {
        if (out == null || in == null) {
            throw new IllegalArgumentException(
                    "Output and input must not be null.");
        }

        this.out = out;
        this.in = in;
    }

    /**
     * Starts the controller's normal input loop.
     *
     * This view is terminal-based, so blocking input is appropriate.
     */
    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    /**
     * Displays the chessboard and current game status.
     */
    @Override
    public void render(Game game) {
        out.println();
        out.println("========================================");
        out.println("             TERMINAL CHESS");
        out.println("========================================");

        renderBoard(game.board());

        out.println();
        out.println("Turn: " + game.sideToMove());
        out.println("Status: " + statusText(game.status()));
        out.println("Moves played: " + game.history().size());
        out.println("========================================");
    }

    /**
     * Displays the board using coordinates and piece symbols.
     */
    private void renderBoard(Board board) {
        out.println();

        for (int rank = 7; rank >= 0; rank--) {
            out.print((rank + 1) + "  ");

            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                Piece piece = board.pieceAt(position);

                if (piece == null) {
                    out.print(". ");
                } else {
                    out.print(piece.symbol() + " ");
                }
            }

            out.println();
        }

        out.println();
        out.println("   a b c d e f g h");
    }

    /**
     * Displays a message from the controller.
     */
    @Override
    public void showMessage(String message) {
        out.println();
        out.println("[Message] " + message);
    }

    /**
     * Presents the current legal moves and reads a player's selection.
     */
    @Override
    public PlayerAction nextAction(Game game) {

        List<Move> legalMoves =
                new ArrayList<>(game.legalMoves());

        legalMoves.sort(
                Comparator.comparing(Move::toString));

        out.println();
        out.println("--------------- LEGAL MOVES ---------------");

        for (int i = 0; i < legalMoves.size(); i++) {
            out.printf("%3d. %s%n",
                    i + 1,
                    legalMoves.get(i));
        }

        out.println("-------------------------------------------");
        out.println("Enter a move number, U (Undo), M (Show moves),");
        out.println("or Q (Quit).");

        out.print("> ");
        out.flush();

        if (!in.hasNextLine()) {
            return new PlayerAction.Quit();
        }

        String input = in.nextLine().trim();

        if (input.equalsIgnoreCase("q")
                || input.equalsIgnoreCase("quit")) {
            return new PlayerAction.Quit();
        }

        if (input.equalsIgnoreCase("u")
                || input.equalsIgnoreCase("undo")) {
            return new PlayerAction.Undo();
        }

        if (input.equalsIgnoreCase("m")
                || input.equalsIgnoreCase("moves")) {
            return new PlayerAction.ShowMoves();
        }

        try {
            int selection = Integer.parseInt(input);

            if (selection < 1 || selection > legalMoves.size()) {
                showMessage("Invalid menu number: " + input);
                return new PlayerAction.Move("invalid");
            }

            Move selectedMove = legalMoves.get(selection - 1);

            return new PlayerAction.Move(
                    selectedMove.toString());

        } catch (NumberFormatException exception) {
            showMessage(
                    "Please enter a move number, U, M, or Q.");

            return new PlayerAction.Move("invalid");
        }
    }

    /**
     * Returns a readable description of the game status.
     */
    private String statusText(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "In Progress";
            case CHECK -> "Check!";
            case CHECKMATE -> "Checkmate!";
            case STALEMATE -> "Stalemate!";
        };
    }
}
