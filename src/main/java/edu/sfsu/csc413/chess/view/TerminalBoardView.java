package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * A separate terminal-based chess view.
 *
 * It uses a coordinate board with ANSI square colours and numbered legal
 * moves. It does not depend on Swing or external libraries.
 */
public class TerminalBoardView implements BoardView {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private static final String LIGHT_SQUARE =
            "\u001B[48;5;230m";

    private static final String DARK_SQUARE =
            "\u001B[48;5;94m";

    private static final String WHITE_PIECE =
            "\u001B[38;5;16m";

    private static final String BLACK_PIECE =
            "\u001B[38;5;15m";

    private final PrintStream out;
    private final Scanner in;

    public TerminalBoardView() {
        this(System.out, new Scanner(System.in));
    }

    public TerminalBoardView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        out.println();
        out.println("       CHESS");
        out.println("   +-------------------------------+");

        for (int rank = 7; rank >= 0; rank--) {
            out.print(" " + (rank + 1) + " | ");

            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);

                String background =
                        ((file + rank) % 2 == 0)
                                ? LIGHT_SQUARE
                                : DARK_SQUARE;

                String symbol = piece == null
                        ? " "
                        : String.valueOf(piece.symbol());

                String foreground = "";

                if (piece != null) {
                    foreground = piece.color() == Color.WHITE
                            ? WHITE_PIECE + BOLD
                            : BLACK_PIECE + BOLD;
                }

                out.print(background
                        + foreground
                        + "  " + symbol + "  "
                        + RESET);
            }

            out.println(" |");
        }

        out.println("   +-------------------------------+");
        out.println("      a     b     c     d     e     f     g     h");
        out.println();
        out.println(game.sideToMove()
                + " to move | " + game.status());

        if (!game.history().isEmpty()) {
            out.println("Moves played: " + game.history().size());
        }

        out.println();
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    /**
     * Shows the current legal moves as a numbered list.
     * The player can enter a number to choose a move.
     */
    @Override
    public PlayerAction nextAction(Game game) {
        List<Move> legalMoves = game.legalMoves().stream()
                .sorted(Comparator.comparing(Move::toString))
                .toList();

        out.println("Available commands:");
        out.println("  Enter a move number to play it");
        out.println("  Enter a move such as e2e4");
        out.println("  undo   - take back the last move");
        out.println("  moves  - show all legal moves");
        out.println("  quit   - exit the game");
        out.println();

        if (!legalMoves.isEmpty()) {
            out.println("Legal moves:");

            for (int i = 0; i < legalMoves.size(); i++) {
                out.printf("%2d. %s%n",
                        i + 1,
                        legalMoves.get(i));
            }
        } else {
            out.println("No legal moves.");
        }

        out.print("Your choice: ");
        out.flush();

        if (!in.hasNextLine()) {
            return new PlayerAction.Quit();
        }

        String input = in.nextLine().trim();

        if (input.equalsIgnoreCase("undo")
                || input.equalsIgnoreCase("u")) {
            return new PlayerAction.Undo();
        }

        if (input.equalsIgnoreCase("moves")
                || input.equalsIgnoreCase("m")) {
            return new PlayerAction.ShowMoves();
        }

        if (input.equalsIgnoreCase("quit")
                || input.equalsIgnoreCase("q")
                || input.equalsIgnoreCase("exit")) {
            return new PlayerAction.Quit();
        }

        // A numeric choice selects an item from the legal move list.
        try {
            int choice = Integer.parseInt(input);

            if (choice >= 1 && choice <= legalMoves.size()) {
                return new PlayerAction.Move(
                        legalMoves.get(choice - 1).toString());
            }

            showMessage("Invalid move number: " + choice);
            return new PlayerAction.ShowMoves();

        } catch (NumberFormatException ignored) {
            // Non-numeric input is treated as move notation.
        }

        return PlayerAction.parse(input);
    }
}