
package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the moves a side may play in a given position.
 *
 * <p>This class holds no state. All required information is passed
 * through method parameters, so its methods are static.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Returns every move of the specified color that does not leave
     * its own king in check.
     *
     * @param board the current board
     * @param color the color whose legal moves are requested
     * @return the list of legal moves
     */
    public static List<Move> legalMoves(Board board, Color color) {
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : pseudoLegalMoves(board, color)) {
            board.apply(move);

            boolean leavesKingInCheck;
            try {
                leavesKingInCheck = isInCheck(board, color);
            } finally {
                board.undo(move);
            }

            if (!leavesKingInCheck) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Returns whether the specified square is attacked by any piece
     * of the attacking color.
     *
     * @param board the board to inspect
     * @param target the square being attacked
     * @param attacker the color of the attacking pieces
     * @return true if the target square is attacked
     */
    public static boolean isAttacked(
            Board board, Position target, Color attacker) {

        for (Position from : board.positionsOf(attacker)) {
            Piece piece = board.pieceAt(from);

            if (piece.attacks(board, from, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the king of the specified color is attacked
     * by a piece of the opposite color.
     *
     * @param board the board to inspect
     * @param color the color of the king
     * @return true if the king is in check; false if no king exists
     */
    public static boolean isInCheck(Board board, Color color) {
        Position king = board.kingPosition(color);

        if (king == null) {
            return false;
        }

        Color attacker =
                color == Color.WHITE ? Color.BLACK : Color.WHITE;

        return isAttacked(board, king, attacker);
    }

    /**
     * Returns every geometrically possible move for the specified color,
     * without filtering for king safety.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();

        for (Position from : board.positionsOf(color)) {
            moves.addAll(
                    board.pieceAt(from).pseudoLegalMoves(board, from)
            );
        }

        return moves;
    }
}
