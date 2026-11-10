
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.List;

public class SwingBoardView implements BoardView {

    private static final java.awt.Color LIGHT_SQUARE =
            new java.awt.Color(240, 217, 181);

    private static final java.awt.Color DARK_SQUARE =
            new java.awt.Color(181, 136, 99);

    private static final java.awt.Color SELECTED_SQUARE =
            new java.awt.Color(246, 246, 105);

    private final JButton[][] squares = new JButton[8][8];

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private JButton undoButton;

    private GameController controller;
    private Position selectedPosition;

    private String lastMessage = "";

    @Override
    public void start(GameController controller) {
        this.controller = controller;

        SwingUtilities.invokeLater(() ->
                createWindow(controller.game()));
    }

    private void createWindow(Game game) {
        frame = new JFrame("CSC 413 Chess");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                int file = column;
                int rank = 7 - row;

                JButton square = new JButton();

                square.setPreferredSize(new Dimension(75, 75));
                square.setFont(new Font("Serif", Font.PLAIN, 40));
                square.setMargin(new Insets(0, 0, 0, 0));
                square.setFocusPainted(false);
                square.setOpaque(true);

                squares[row][column] = square;

                square.addActionListener(event ->
                        handleSquareClick(file, rank));

                boardPanel.add(square);
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
                new BorderLayout(8, 8));

        messageLabel = new JLabel(
                lastMessage, SwingConstants.CENTER);

        undoButton = new JButton("Undo");

        undoButton.addActionListener(event -> {
            selectedPosition = null;
            controller.handle(new PlayerAction.Undo());
        });

        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(undoButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        renderBoard(game);
    }

    @Override
    public void render(Game game) {
        if (SwingUtilities.isEventDispatchThread()) {
            renderBoard(game);
        } else {
            SwingUtilities.invokeLater(() ->
                    renderBoard(game));
        }
    }

    private void renderBoard(Game game) {
        if (frame == null || !frame.isDisplayable()) {
            return;
        }

        Board board = game.board();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                int file = column;
                int rank = 7 - row;

                Position position = new Position(file, rank);

                Piece piece = board.pieceAt(position);
                JButton square = squares[row][column];

                java.awt.Color background =
                        (row + column) % 2 == 0
                                ? DARK_SQUARE
                                : LIGHT_SQUARE;

                square.setBackground(
                        position.equals(selectedPosition)
                                ? SELECTED_SQUARE
                                : background);

                square.setForeground(
                        isDarkSquare(background)
                                ? java.awt.Color.WHITE
                                : java.awt.Color.BLACK);

                square.setText(
                        piece == null ? "" : pieceGlyph(piece));

                square.setToolTipText(
                        piece == null
                                ? position.toString()
                                : piece.color() + " "
                                + piece.type() + " "
                                + position);
            }
        }

        statusLabel.setText(
                game.sideToMove() + " to move | "
                        + statusText(game.status()));

        messageLabel.setText(lastMessage);
    }

    private void handleSquareClick(int file, int rank) {
        if (controller == null) {
            return;
        }

        Game game = controller.game();

        if (game.isGameOver()) {
            return;
        }

        Position clickedPosition = new Position(file, rank);

        Piece clickedPiece =
                game.board().pieceAt(clickedPosition);

        if (selectedPosition == null) {
            if (clickedPiece != null
                    && clickedPiece.color() == game.sideToMove()) {

                selectedPosition = clickedPosition;
                render(game);
            }
            return;
        }

        if (clickedPiece != null
                && clickedPiece.color() == game.sideToMove()) {

            selectedPosition = clickedPosition;
            render(game);
            return;
        }

        List<Move> legalMoves = game.legalMoves();

        Move selectedMove = null;

        for (Move move : legalMoves) {
            if (move.from().equals(selectedPosition)
                    && move.to().equals(clickedPosition)) {

                if (!move.isPromotion()) {
                    selectedMove = move;
                    break;
                }

                if (move.promotesTo() == PieceType.QUEEN) {
                    selectedMove = move;
                }
            }
        }

        String notation;

        if (selectedMove != null) {
            notation = selectedMove.toString();
        } else {
            notation = selectedPosition.toString()
                    + clickedPosition.toString();
        }

        selectedPosition = null;

        controller.handle(new PlayerAction.Move(notation));
    }

    @Override
    public void showMessage(String message) {
        lastMessage = message;

        if (SwingUtilities.isEventDispatchThread()) {
            updateMessage();
        } else {
            SwingUtilities.invokeLater(this::updateMessage);
        }
    }

    private void updateMessage() {
        if (messageLabel != null) {
            messageLabel.setText(lastMessage);
        }
    }

    private String pieceGlyph(Piece piece) {
        if (piece.color() == Color.WHITE) {
            return switch (piece.type()) {
                case KING -> "♔";
                case QUEEN -> "♕";
                case ROOK -> "♖";
                case BISHOP -> "♗";
                case KNIGHT -> "♘";
                case PAWN -> "♙";
            };
        }

        return switch (piece.type()) {
            case KING -> "♚";
            case QUEEN -> "♛";
            case ROOK -> "♜";
            case BISHOP -> "♝";
            case KNIGHT -> "♞";
            case PAWN -> "♟";
        };
    }

    private String statusText(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "In Progress";
            case CHECK -> "Check!";
            case CHECKMATE -> "Checkmate!";
            case STALEMATE -> "Stalemate!";
        };
    }

    private boolean isDarkSquare(java.awt.Color color) {
        return color.equals(DARK_SQUARE);
    }

    @Override
    public void close() {
        // Keep the Swing window open after checkmate or stalemate.
        // The user can close the window manually.
    }
}
