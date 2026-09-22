package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;
public abstract class Piece {
    private final Color color;
    private final PieceType type;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color color() {
        return color;
    }

    public PieceType type() {
        return type;
    }

    public char symbol() {
        char symbol = type.symbol();

        if (color == Color.BLACK) {
            return Character.toLowerCase(symbol);
        }

        return symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol());
    }
    public abstract List<Move> pseudoLegalMoves(Board board,Position from);

    public boolean attacks(Board board,Position from,Position target) {
        for (Move move : pseudoLegalMoves(board, from)) {
            if (move.to().equals(target)) {
                return true;
            }
        }
        return false;
    }
    protected List<Move> slidingMoves(
            Board board,
            Position from,
            int[][] directions) {

        List<Move> moves = new ArrayList<>();

        for (int[] direction : directions) {
            Position current = from;

            while (true) {
                current = current.offsetOrNull(
                        direction[0],
                        direction[1]
                );

                if (current == null) {
                    break;
                }

                Piece target = board.pieceAt(current);

                if (target == null) {
                    moves.add(Move.quiet(from, current, this));
                } else {
                    if (target.color() != color) {
                        moves.add(
                                Move.capture(from, current, this, target)
                        );
                    }

                    break;
                }
            }
        }

        return moves;
    }
    protected List<Move> steppingMoves(
            Board board,
            Position from,
            int[][] offsets) {

        List<Move> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            Position target = from.offsetOrNull(
                    offset[0],
                    offset[1]
            );

            if (target == null) {
                continue;
            }

            Piece targetPiece = board.pieceAt(target);

            if (targetPiece == null) {
                moves.add(Move.quiet(from, target, this));
            } else if (targetPiece.color() != color) {
                moves.add(
                        Move.capture(from, target, this, targetPiece)
                );
            }
        }

        return moves;
    }















}