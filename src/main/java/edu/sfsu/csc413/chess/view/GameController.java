
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Connects the player's decisions to the game: carry out what was asked, then
 * ask the view to redraw.
 */
public class GameController {

    private final Game game;
    private final BoardView view;
    private boolean quitRequested;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    public Game game() {
        return game;
    }

    /**
     * Greets the player, draws the opening position, and hands control to the
     * view.
     */
    public void start() {
        view.showMessage("Welcome to Chess!");
        view.render(game);

        if (game.isGameOver()) {
            announceResult();
            view.close();
            return;
        }

        view.start(this);
    }

    /**
     * Runs the ask-and-handle loop for views that wait for input.
     */
    public void runLoop() {
        while (!quitRequested && !game.isGameOver()) {
            PlayerAction action = view.nextAction(game);
            handle(action);
        }
    }

    /**
     * Handles one player action and redraws when appropriate.
     */
    public void handle(PlayerAction action) {
        switch (action) {
            case PlayerAction.Move move -> handleMove(move.notation());

            case PlayerAction.Undo ignored -> {
                Optional<Move> undone = game.undoLastMove();

                if (undone.isEmpty()) {
                    view.showMessage("Nothing to undo.");
                }
            }

            case PlayerAction.ShowMoves ignored -> {
                List<Move> moves = game.legalMoves().stream()
                        .sorted(Comparator.comparing(Move::toString))
                        .toList();

                String notation = moves.stream()
                        .map(Move::toString)
                        .reduce((a, b) -> a + " " + b)
                        .orElse("");

                view.showMessage(
                        moves.size() + " legal moves: " + notation);
            }

            case PlayerAction.Quit ignored -> {
                quitRequested = true;
                view.showMessage("Goodbye.");
                view.close();
                return;
            }
        }

        view.render(game);

        if (game.isGameOver()) {
            announceResult();
            view.close();
        }
    }

    /**
     * Handles a move written in long algebraic notation.
     */
    private void handleMove(String notation) {
        Optional<Move> selected = game.findLegalMove(notation);

        // If the player omitted the promotion letter, ask the view.
        if (selected.isEmpty() && notation.length() == 4) {
            boolean promotionAvailable = game.legalMoves().stream()
                    .anyMatch(move ->
                            move.isPromotion()
                                    && move.toString().substring(0, 4)
                                    .equalsIgnoreCase(notation));

            if (promotionAvailable) {
                PieceType choice = view.promotionChoice(game);
                String promotionNotation =
                        notation + Character.toLowerCase(choice.symbol());

                selected = game.findLegalMove(promotionNotation);
            }
        }

        if (selected.isPresent()) {
            game.play(selected.get());
        } else {
            view.showMessage(
                    "Not a legal move: '" + notation + "'.");
        }
    }

    /**
     * Announces checkmate or stalemate.
     */
    private void announceResult() {
        GameStatus status = game.status();

        switch (status) {
            case CHECKMATE -> view.showMessage(
                    "Checkmate. " + game.sideToMove().opposite() + " wins.");

            case STALEMATE -> view.showMessage(
                    "Stalemate. The game is a draw.");

            case IN_PROGRESS, CHECK -> {
                // No final result to announce.
            }
        }
    }
}
