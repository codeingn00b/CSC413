package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The pawn — the piece that breaks every rule the others follow.
 *
 * <p>It is the only piece that moves in just one direction, the only one whose
 * capture differs from its move, the only one with a special first move, and
 * the only one that turns into something else. It is worth noticing that all of
 * that awkwardness is contained in this one file. No other class in the engine
 * knows that pawns are strange. That containment is the payoff of polymorphism:
 * the irregular case costs one class, not a special case in every method that
 * touches a piece.
 *
 * <p>En passant is not handled here. Like castling, it depends on the previous
 * move rather than on the current board, so it waits for Week 15 when
 * {@code Game} owns the move history.
 */
public class Pawn extends Piece {

    /**
     * What a pawn may become on reaching the far rank.
     */
    private static final PieceType[] PROMOTION_CHOICES = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        int direction = color().pawnDirection();

        // One square forward
        Position oneForward = from.offsetOrNull(0, direction);

        if (oneForward != null && board.isEmpty(oneForward)) {
            if (oneForward.rank() == color().promotionRank()) {
                addPromotions(moves, from, oneForward);
            } else {
                moves.add(Move.quiet(from, oneForward, this));

                // Two squares forward from starting rank
                if (from.rank() == color().pawnStartRank()) {
                    Position twoForward =
                            from.offsetOrNull(0, direction * 2);

                    if (twoForward != null && board.isEmpty(twoForward)) {
                        moves.add(
                                Move.quiet(from, twoForward, this)
                        );
                    }
                }
            }
        }

        // Diagonal captures
        int[] captureFiles = {-1, 1};

        for (int fileOffset : captureFiles) {
            Position target =
                    from.offsetOrNull(fileOffset, direction);

            if (target == null) {
                continue;
            }

            Piece targetPiece = board.pieceAt(target);

            if (targetPiece != null
                    && targetPiece.color() != color()) {

                if (target.rank() == color().promotionRank()) {
                    addPromotions(moves, from, target, targetPiece);
                } else {
                    moves.add(
                            Move.capture(
                                    from,
                                    target,
                                    this,
                                    targetPiece
                            )
                    );
                }
            }
        }

        return moves;
    }

    private void addPromotions(List<Move> moves, Position from, Position to) {

        moves.add(Move.promotion(from, to, this, null, PieceType.QUEEN));

        moves.add(Move.promotion(from, to, this, null, PieceType.ROOK));

        moves.add(Move.promotion(from, to, this, null, PieceType.BISHOP));

        moves.add(Move.promotion(from, to, this, null, PieceType.KNIGHT));
    }
    private void addPromotions(List<Move> moves, Position from, Position to, Piece captured) {

        moves.add(Move.promotion(from, to, this, captured, PieceType.QUEEN));

        moves.add(Move.promotion(from, to, this, captured, PieceType.ROOK));

        moves.add(Move.promotion(from, to, this, captured, PieceType.BISHOP));

        moves.add(Move.promotion(from, to, this, captured, PieceType.KNIGHT));
    }

    /**
     * A pawn attacks the two squares diagonally ahead of it, whether or not
     * anything stands there.
     *
     * <p>This override exists because the inherited version answers "can this
     * piece move to that square", and for a pawn that is the wrong question.
     * An empty square in front of a pawn is a square the pawn can move to but
     * does <em>not</em> attack — which matters enormously for king safety: a
     * king may not be blocked from a square merely because a pawn could advance
     * onto it, but it certainly may not step onto a square a pawn guards.
     */
    @Override
    public boolean attacks(
            Board board,
            Position from,
            Position target) {

        int direction = color().pawnDirection();

        int fileDifference =
                target.file() - from.file();

        int rankDifference =
                target.rank() - from.rank();

        return Math.abs(fileDifference) == 1
                && rankDifference == direction;
    }
}
