# Instructions for Coding Agents

This is a chess engine built with MVC. The model and controller were written by hand and are finished. Your job in M7 is to implement views only.

Read this file before making any changes. Follow every restriction. If a requirement cannot be met without changing a protected file, stop and explain the problem instead of changing that file.

## Do

- Add new view classes under `src/main/java/edu/sfsu/csc413/chess/view/` or a new package under it.
- Implement the existing `BoardView` interface. Read `BoardView`, `ConsoleView`, `EmojiView`, `PlayerAction`, and `GameController` before writing a view.
- Read the game through the existing `Game` API. Use `game.legalMoves()` to obtain legal moves and `game.status()` to determine check, checkmate, and stalemate.
- Send player actions through the existing controller using `PlayerAction`. For a move, use the existing `PlayerAction.Move` API. For undo, send `new PlayerAction.Undo()`.
- Add the new view selection to `Main.java`. This project uses `main()` to select the view; it does not use the reference handout's `Main.chooseView` method.
- Implement a Swing view that displays an 8-by-8 board, makes pieces clearly visible on both square colours, supports selecting a piece and then a destination square, displays the game status and illegal-move messages, and provides an Undo button.
- Implement at least one additional non-Swing view in its own class. It must be more than a different set of piece glyphs, must use only the JDK, and must be playable to the end of a game through the existing controller.
- Give the additional view its own command-line selection word in `Main.java`.
- Make the Swing view's `start()` method build and display the window, then return. It must not call `controller.runLoop()`.
- A modal dialog that asks one question, such as which piece to choose during promotion, is allowed. Do not use a blocking game loop or wait for the next move inside the window's `start()` method or a click handler.
- Handle pawn promotion without crashing. Always promoting to a queen is acceptable.
- Add view tests under `src/test/.../view/` only if useful. Do not modify or delete existing tests.
- Run `mvnw.cmd test` and report the actual test results.
- Keep changes within the files permitted by the M7 handout.

## Don't

- Do not change anything in `src/main/java/edu/sfsu/csc413/chess/model/`, `engine/`, or `factory/`.
- Do not change `GameController.java`.
- Do not change `BoardView.java`, `PlayerAction.java`, `ConsoleView.java`, or `EmojiView.java`.
- Do not change `pom.xml` or add dependencies. Swing is part of the JDK.
- Do not call `game.play()`, `game.undoLastMove()`, or any other method that directly changes the game state from a view. Only the existing controller may change the game.
- Do not implement chess rules in a view. Do not calculate legal moves, check, checkmate, or stalemate yourself. Obtain legal moves from `game.legalMoves()` and game status from `game.status()`.
- Do not call `controller.runLoop()` from the Swing view.
- Do not block the Swing event-dispatch thread with a game loop, input loop, or blocking read.
- Do not change existing tests to make the build pass.

## Check Your Work

- Run `mvnw.cmd test`. All existing tests must pass; the baseline is 83 tests. No existing test may be changed or deleted.
- Run `mvnw.cmd -q compile`.
- Check `git diff --stat submit-m6 HEAD`. Changes must be limited to:
    - New view files under `src/main/java/edu/sfsu/csc413/chess/view/` (or a new package under it).
    - New view tests under `src/test/.../view/`, if any.
    - `src/main/java/edu/sfsu/csc413/chess/Main.java`.
    - `AGENTS.md`, `CLAUDE.md`, and `m7-submission.md`.
    - `docs/milestones/m7.md`.
    - Any separately declared and committed M6 fix.
- Search the new view files for `game.play` and `undoLastMove`. There must be no matches.
- Read the new view code and verify that legal moves come from `game.legalMoves()` and check/checkmate/stalemate come from `game.status()`.
- Search the Swing view for `runLoop`. There must be no matches.
- Manually test the Swing view and the additional view. Test Fool's Mate (f2f3, e7e5, g2g4, d8h4), an illegal move, and Undo.
- Keep the console open while testing Swing. Inspect any exception printed on `AWT-EventQueue-0` and debug the first relevant frame in this project's code.

## Stop and Ask

If you believe a protected file must change, stop before editing it. Explain which requirement cannot be met, identify the protected file, and ask for permission or clarification. Do not work around the restriction by changing another protected class or modifying the chess rules.