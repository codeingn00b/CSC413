package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Swing-based chess board.
 *
 * The view displays the game and sends player actions to GameController.
 * It does not apply moves directly to Game or Board.
 */
public class SwingBoardView implements BoardView {

    private static final java.awt.Color LIGHT_SQUARE =
            new java.awt.Color(240, 217, 181);

    private static final java.awt.Color DARK_SQUARE =
            new java.awt.Color(181, 136, 99);

    private static final java.awt.Color SELECTED_SQUARE =
            new java.awt.Color(246, 246, 105);

    private static final java.awt.Color LEGAL_DESTINATION =
            new java.awt.Color(130, 190, 115);

    private static final java.awt.Color COORDINATE_COLOR =
            new java.awt.Color(90, 90, 90);

    private final JButton[][] squares = new JButton[8][8];

    private GameController controller;
    private Game currentGame;

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel messageLabel;

    private Position selectedPosition;

    private final Set<Position> legalDestinations =
            new HashSet<>();

    @Override
    public void start(GameController controller) {
        this.controller = controller;
        this.currentGame = controller.game();

        // Swing creates and updates its components on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> createWindow(currentGame));
    }

    private void createWindow(Game game) {
        frame = new JFrame("Chess - CSC 413");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JPanel boardPanel = new JPanel(new GridLayout(10, 10, 0, 0));

        // Top coordinate row.
        boardPanel.add(createCoordinateLabel(""));

        for (int file = 0; file < 8; file++) {
            boardPanel.add(
                    createCoordinateLabel(
                            String.valueOf((char) ('a' + file))));
        }

        boardPanel.add(createCoordinateLabel(""));

        // Eight board rows, rank 8 through rank 1.
        for (int rank = 7; rank >= 0; rank--) {

            // Left-side rank label.
            boardPanel.add(createCoordinateLabel(
                    String.valueOf(rank + 1)));

            for (int file = 0; file < 8; file++) {
                final int squareFile = file;
                final int squareRank = rank;

                JButton button = new JButton();
                button.setFont(
                        new Font("Segoe UI Symbol", Font.PLAIN, 36));
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorder(
                        BorderFactory.createLineBorder(java.awt.Color.BLACK));

                button.addActionListener(event ->
                        handleSquareClick(
                                new Position(squareFile, squareRank)));

                // Keep Run 2's original file/rank indexing.
                squares[file][rank] = button;
                boardPanel.add(button);
            }

            // Right-side rank label.
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

        JPanel informationPanel =
                new JPanel(new GridLayout(3, 1, 4, 4));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel = new JLabel(
                "Select one of your pieces.",
                SwingConstants.CENTER);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(event -> {
            clearSelection();
            controller.handle(new PlayerAction.Undo());
        });

        informationPanel.add(statusLabel);
        informationPanel.add(messageLabel);
        informationPanel.add(undoButton);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(informationPanel, BorderLayout.SOUTH);

        updateBoard(game);

        frame.pack();
        frame.setSize(620, 690);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createCoordinateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(COORDINATE_COLOR);

        return label;
    }
    
    /**
     * Handles a click on one board square.
     */
    private void handleSquareClick(Position clickedPosition) {
        if (currentGame == null || currentGame.isGameOver()) {
            return;
        }

        Piece clickedPiece =
                currentGame.board().pieceAt(clickedPosition);

        // No piece is currently selected.
        if (selectedPosition == null) {
            selectOwnPiece(clickedPosition, clickedPiece);
            return;
        }

        // Clicking the selected square cancels the selection.
        if (selectedPosition.equals(clickedPosition)) {
            clearSelection();
            updateBoard(currentGame);
            setMessage("Selection canceled.");
            return;
        }

        // Clicking another piece belonging to the current player
        // changes the selection.
        if (clickedPiece != null
                && clickedPiece.color() == currentGame.sideToMove()) {

            selectOwnPiece(clickedPosition, clickedPiece);
            return;
        }

        // Find a legal move from the selected square to the clicked square.
        List<Move> matchingMoves = currentGame.legalMoves().stream()
                .filter(move -> move.from().equals(selectedPosition))
                .filter(move -> move.to().equals(clickedPosition))
                .toList();

        if (matchingMoves.isEmpty()) {
            setMessage("That is not a legal destination.");
            return;
        }

        // Queen is the default promotion choice.
        Move chosenMove = matchingMoves.stream()
                .filter(move ->
                        move.isPromotion()
                                && move.promotesTo() == PieceType.QUEEN)
                .findFirst()
                .orElse(matchingMoves.get(0));

        String notation = chosenMove.toString();

        clearSelection();
        controller.handle(new PlayerAction.Move(notation));
    }

    /**
     * Selects a piece belonging to the player whose turn it is.
     */
    private void selectOwnPiece(
            Position position,
            Piece piece) {

        if (piece == null
                || piece.color() != currentGame.sideToMove()) {
            setMessage("Select one of your own pieces.");
            return;
        }

        selectedPosition = position;
        legalDestinations.clear();

        // The legal destinations are derived only from Game.legalMoves().
        for (Move move : currentGame.legalMoves()) {
            if (move.from().equals(selectedPosition)) {
                legalDestinations.add(move.to());
            }
        }

        updateBoard(currentGame);

        if (legalDestinations.isEmpty()) {
            setMessage("This piece has no legal moves.");
        } else {
            setMessage(
                    "Selected " + position
                            + ". Legal destinations are highlighted.");
        }
    }

    /**
     * Clears both the selected square and all legal destination highlights.
     */
    private void clearSelection() {
        selectedPosition = null;
        legalDestinations.clear();
    }

    @Override
    public void render(Game game) {
        currentGame = game;

        if (SwingUtilities.isEventDispatchThread()) {
            updateBoard(game);
        } else {
            SwingUtilities.invokeLater(() -> updateBoard(game));
        }
    }

    /**
     * Redraws all 64 squares and their current highlight states.
     */
    private void updateBoard(Game game) {
        if (frame == null && statusLabel == null) {
            return;
        }

        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                JButton button = squares[file][rank];

                if (button == null) {
                    continue;
                }

                Piece piece = game.board().pieceAt(position);

                button.setText(piece == null
                        ? ""
                        : pieceGlyph(piece));

                java.awt.Color squareColor =
                        ((file + rank) % 2 == 0)
                                ? LIGHT_SQUARE
                                : DARK_SQUARE;

                if (legalDestinations.contains(position)) {
                    squareColor = LEGAL_DESTINATION;
                }

                if (position.equals(selectedPosition)) {
                    squareColor = SELECTED_SQUARE;
                }

                button.setBackground(squareColor);
            }
        }

        if (statusLabel != null) {
            statusLabel.setText(
                    game.sideToMove()
                            + " to move | "
                            + game.status());
        }
    }

    /**
     * Uses Unicode chess symbols instead of platform-dependent emoji.
     */
    private String pieceGlyph(Piece piece) {
        int codePoint;

        if (piece.color() == Color.WHITE) {
            codePoint = switch (piece.type()) {
                case KING -> 0x2654;
                case QUEEN -> 0x2655;
                case ROOK -> 0x2656;
                case BISHOP -> 0x2657;
                case KNIGHT -> 0x2658;
                case PAWN -> 0x2659;
            };
        } else {
            codePoint = switch (piece.type()) {
                case KING -> 0x265A;
                case QUEEN -> 0x265B;
                case ROOK -> 0x265C;
                case BISHOP -> 0x265D;
                case KNIGHT -> 0x265E;
                case PAWN -> 0x265F;
            };
        }

        return new String(Character.toChars(codePoint));
    }

    @Override
    public void showMessage(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            setMessage(message);
        } else {
            SwingUtilities.invokeLater(() -> setMessage(message));
        }
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    /**
     * The default BoardView promotion choice is Queen.
     */
    @Override
    public PieceType promotionChoice(Game game) {
        return PieceType.QUEEN;
    }

    /**
     * Keep the final board visible after checkmate or stalemate.
     */
    @Override
    public void close() {
        // Intentionally do not dispose the frame.
    }
}