package edu.sfsu.csc413.chess.model;

import edu.sfsu.csc413.chess.factory.PieceFactory;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Piece[][] squares;

    public Board() {
        squares = new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];
    }

    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    /**
     * Applies a move without checking whether the move is legal.
     *
     * <p>Game is responsible for deciding whether a move is allowed.
     * Board only stores the resulting position.
     *
     * <p>For a promotion, the pawn is replaced by a new piece of the
     * promoted type and the pawn's colour.
     */
    public void apply(Move move) {
        place(move.from(), null);

        Piece piece = move.moved();

        if (move.isPromotion()) {
            piece = PieceFactory.create(
                    move.promotesTo(),
                    move.moved().color());
        }

        place(move.to(), piece);
    }

    /**
     * Undoes a move by restoring the moved piece to its original square
     * and restoring the captured piece, if any, to the destination square.
     */
    public void undo(Move move) {
        place(move.from(), move.moved());
        place(move.to(), move.captured());
    }

    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();

        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Piece piece = squares[file][rank];

                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }

        return positions;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int rank = Position.BOARD_SIZE - 1; rank >= 0; rank--) {
            int emptyCount = 0;

            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                Piece piece = squares[file][rank];

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        result.append(emptyCount);
                        emptyCount = 0;
                    }

                    result.append(piece.symbol());
                }
            }

            if (emptyCount > 0) {
                result.append(emptyCount);
            }

            if (rank > 0) {
                result.append('/');
            }
        }

        return result.toString();
    }
}