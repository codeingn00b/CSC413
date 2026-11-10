
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import java.awt.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Event-driven Swing chess board.
 *
 * The view displays the board and sends player actions
 * through the existing GameController.
 */
public class SwingBoardView implements BoardView {

    private static final Color LIGHT_SQUARE =
            new Color(240, 217, 181);

    private static final Color DARK_SQUARE =
            new Color(181, 136, 99);

    private static final Color WHITE_PIECE =
            new Color(255, 255, 255);

    private static final Color BLACK_PIECE =
            new Color(35, 35, 35);

    private static final Color COORDINATE_COLOR =
            new Color(90, 90, 90);

    private final JButton[][] squares = new JButton[8][8];
    

    private final Border normalBorder =
            BorderFactory.createEmptyBorder(2, 2, 2, 2);

    private final Border selectedBorder =
            BorderFactory.createLineBorder(
                    new Color(30, 120, 220), 4);

    private JFrame frame;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;

    private GameController controller;

    private Position selectedPosition;

    @Override
    public void start(GameController controller) {
        this.controller = controller;

        // Build the window on Swing's event-dispatch thread.
        // Do not block this method or call controller.runLoop().
        SwingUtilities.invokeLater(() -> {
            buildWindow();
            render(controller.game());
        });
    }

    private void buildWindow() {
        frame = new JFrame("CSC 413 Chess");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        turnLabel = new JLabel("White to move");
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        statusLabel = new JLabel("Status: IN_PROGRESS");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel informationPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        informationPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10));
        informationPanel.add(turnLabel);
        informationPanel.add(statusLabel);

        frame.add(informationPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(10, 10, 0, 0));
        boardPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Top coordinate row.
        boardPanel.add(createCoordinateLabel(""));

        for (int file = 0; file < 8; file++) {
            boardPanel.add(
                    createCoordinateLabel(
                            String.valueOf((char) ('a' + file))));
        }

        boardPanel.add(createCoordinateLabel(""));

        // Eight board rows, rank 8 through rank 1.
        for (int row = 0; row < 8; row++) {
            int rank = 7 - row;

            boardPanel.add(createCoordinateLabel(
                    String.valueOf(rank + 1)));

            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);

                JButton button = new JButton();
                button.setFont(new Font("Serif", Font.BOLD, 42));
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setBorder(normalBorder);

                button.setToolTipText(position.toString());

                button.addActionListener(
                        event -> handleSquareClick(position));

                squares[row][file] = button;
                boardPanel.add(button);
            }

            boardPanel.add(createCoordinateLabel(
                    String.valueOf(rank + 1)));
        }

        // Bottom coordinate row.
        boardPanel.add(createCoordinateLabel(""));

        for (int file = 0; file < 8; file++) {
            boardPanel.add(
                    createCoordinateLabel(
                            String.valueOf((char) ('a' + file))));
        }

        boardPanel.add(createCoordinateLabel(""));

        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10));

        messageLabel = new JLabel("Select a piece to begin.");

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(event -> {
            selectedPosition = null;
            controller.handle(new PlayerAction.Undo());
        });

        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(undoButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setMinimumSize(new Dimension(650, 720));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createCoordinateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(COORDINATE_COLOR);

        return label;
    }

    private void handleSquareClick(Position position) {
        if (controller == null || controller.game().isGameOver()) {
            return;
        }

        Game game = controller.game();

        Piece clickedPiece = game.board().pieceAt(position);

        // First click: select a piece belonging to the current player.
        if (selectedPosition == null) {
            if (clickedPiece != null
                    && clickedPiece.color() == game.sideToMove()) {

                selectedPosition = position;
                showMessage("Selected " + position);

                render(game);
            } else {
                showMessage("Select one of your own pieces.");
            }

            return;
        }

        // Clicking the selected square cancels selection.
        if (selectedPosition.equals(position)) {
            selectedPosition = null;
            showMessage("Selection cancelled.");

            render(game);
            return;
        }

        // Clicking another friendly piece changes the selection.
        if (clickedPiece != null
                && clickedPiece.color() == game.sideToMove()) {

            selectedPosition = position;
            showMessage("Selected " + position);

            render(game);
            return;
        }

        // Send the move through the existing controller.
        // The controller handles legality and promotion.
        String notation =
                selectedPosition.toString() + position.toString();

        selectedPosition = null;

        controller.handle(new PlayerAction.Move(notation));
    }

    @Override
    public void render(Game game) {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> render(game));
            return;
        }

        if (frame == null || !frame.isDisplayable()) {
            return;
        }

        turnLabel.setText(game.sideToMove() + " to move");
        statusLabel.setText("Status: " + game.status());

        if (game.status() == GameStatus.CHECK) {
            statusLabel.setText(
                    "Status: CHECK - " + game.sideToMove() + " is in check");
        } else if (game.status() == GameStatus.CHECKMATE) {
            statusLabel.setText("Status: CHECKMATE");
        } else if (game.status() == GameStatus.STALEMATE) {
            statusLabel.setText("Status: STALEMATE");
        }

        for (int row = 0; row < 8; row++) {
            int rank = 7 - row;

            for (int file = 0; file < 8; file++) {
                JButton button = squares[row][file];

                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);

                button.setBackground(
                        (file + rank) % 2 == 0
                                ? LIGHT_SQUARE
                                : DARK_SQUARE);

                if (piece == null) {
                    button.setText("");
                } else {
                    button.setText(
                            String.valueOf(piece.symbol()));

                    button.setForeground(
                            piece.color() == edu.sfsu.csc413.chess.model.Color.WHITE
                                    ? WHITE_PIECE
                                    : BLACK_PIECE);
                }

                button.setBorder(
                        position.equals(selectedPosition)
                                ? selectedBorder
                                : normalBorder);

                button.setEnabled(!game.isGameOver());
            }
        }
    }

    @Override
    public void showMessage(String message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showMessage(message));
            return;
        }

        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    @Override
    public void close() {

    }
}
