package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Interactive terminal chess view using two-step coordinate input.
 *
 * <p>All chess actions are submitted through GameController.
 * This view does not apply moves or implement chess rules.
 */
public class TerminalBoardView implements BoardView {

    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.LETTERS);

    private final PrintStream out;
    private final Scanner in;

    /**
     * Creates a terminal view using standard input and output.
     */
    public TerminalBoardView() {
        this(System.out, new Scanner(System.in));
    }

    /**
     * Allows input and output streams to be supplied independently.
     */
    public TerminalBoardView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        out.println();
        out.println(renderer.render(game.board()));
        out.println();
        out.println(game.sideToMove() + " to move  |  " + game.status());
        out.println();
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    /**
     * Starts the controller's normal game loop.
     */
    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    /**
     * Reads the origin and destination as separate inputs.
     *
     * <p>Commands such as undo, moves, and quit can be entered
     * instead of an origin square.
     */
    @Override
    public PlayerAction nextAction(Game game) {
        out.print(
                "Your move - enter origin (e.g. e2), "
                        + "or undo/moves/quit: ");
        out.flush();

        if (!in.hasNextLine()) {
            return new PlayerAction.Quit();
        }

        String origin = in.nextLine().trim().toLowerCase();

        // Allow commands and full move notation as well.
        PlayerAction action = PlayerAction.parse(origin);

        if (!(action instanceof PlayerAction.Move)) {
            return action;
        }

        if (origin.length() == 4) {
            return action;
        }

        // Validate the origin coordinate.
        try {
            Position.parse(origin);
        } catch (IllegalArgumentException exception) {
            return new PlayerAction.Move(origin);
        }

        out.print("Enter destination (e.g. e4): ");
        out.flush();

        if (!in.hasNextLine()) {
            return new PlayerAction.Quit();
        }

        String destination = in.nextLine().trim().toLowerCase();

        // The controller validates the completed move.
        return new PlayerAction.Move(origin + destination);
    }
}