# CSC 413 Milestone 7 Submission Report

**Project:** CSC 413 Chess Engine
**Milestone:** M7 Swing and Additional Board Views
**Student:** Hsuan-Lu Yang
**Date:** November 2026

---

## 1. Project Overview

The objective of Milestone 7 was to implement a Swing chessboard interface and an additional non-Swing playable interface while preserving the existing MVC architecture.

The existing model and controller were treated as completed components. The implementation was restricted to view-related classes, command-line view selection, and permitted documentation files.

The final implementation includes:

- `SwingBoardView.java`
- `TerminalBoardView.java`
- Updated `Main.java`
- `AGENTS.md`
- `docs/milestones/m7.md`

The final Swing implementation supports selecting pieces, highlighting legal destinations, displaying board coordinates, displaying game status, and undoing moves.

The Terminal implementation provides a separate text-based chess interface with coordinate information and interactive move input.

## 2. Development Process and AI Usage

### AI tool used

- Tool: ChatGPT
- Purpose: Reviewing project APIs, generating and comparing implementation approaches, debugging guidance, and documenting implementation decisions.
- Human involvement: I inspected the project, ran the builds and manual tests, reviewed the generated code, integrated the selected implementation, and made a separate manual code change.

The exact historical model label for each earlier conversation was not recorded in the available project notes. I therefore do not claim a specific model version for those earlier runs.

### Project restrictions

The following restrictions were applied throughout implementation:

- Do not modify `model/`, `engine/`, or `factory/`.
- Do not modify `GameController.java`.
- Do not modify `BoardView.java`, `PlayerAction.java`, `ConsoleView.java`, or `EmojiView.java`.
- Do not modify `pom.xml` or add external dependencies.
- Do not directly call `game.play()` or `game.undoLastMove()` from a view.
- Use `game.legalMoves()` for legal-move information.
- Use `game.status()` for game status.
- Send player actions through the existing `GameController`.
- Do not call `controller.runLoop()` from the Swing view.
- Do not block the Swing event-dispatch thread with an input loop.

## 3. Coding Agent Instructions: AGENTS.md

I added `AGENTS.md` before the implementation runs.

**File:** `AGENTS.md`
**Commit:** `08c07bf M7: add coding agent instructions`

The instructions were added to establish a clear boundary between the completed chess engine/controller and the new view implementation.

The instructions require coding agents to:

1. Read the existing view and controller APIs before implementing a new view.
2. Implement new view classes under the existing view package.
3. Obtain legal moves from `game.legalMoves()`.
4. Obtain check, checkmate, and stalemate information from `game.status()`.
5. Send moves and undo actions through `PlayerAction` and `GameController`.
6. Keep Swing startup nonblocking.
7. Avoid modifying protected model, engine, controller, and existing view files.
8. Preserve all existing tests.
9. Run the required Maven checks and manually test both interfaces.

The instructions also identify the permitted files and require the implementation to stop rather than modify protected files if a requirement cannot be satisfied within the restrictions.

## 4. Run 1 Baseline Implementation

**Branch:** `m7-run-1`
**Commit:** `b413ce6 M7 Run 1: baseline Swing and terminal views`
**Baseline commit:** `08c07bf`

### Objective

Run 1 implemented the baseline M7 requirements without optional Swing enhancements.

The implementation created:

- `SwingBoardView.java`
- `TerminalBoardView.java`

It also modified `Main.java` to register the new view choices while preserving the existing Console and Emoji modes.

### Swing implementation

The Swing view provides:

- An 8-by-8 chessboard.
- Distinguishable light and dark squares.
- Visible chess pieces.
- Piece selection followed by destination selection.
- An Undo button.
- Current-turn and game-status messages.
- Illegal-move feedback.
- Queen as the default promotion choice.

The Swing `start()` method creates and displays the window and returns without calling `controller.runLoop()`.

### Terminal implementation

The Terminal view provides a separate text-based interface with:

- A coordinate-based board representation.
- A visible board border.
- ANSI square coloring where supported.
- Current-turn and game-status information.
- Algebraic move input such as `e2e4`.
- Undo, move listing, and quit actions.

### Validation

The implementation was tested with Maven and manually.

Manual checks included:

- Swing piece selection and movement.
- Illegal move handling.
- Undo.
- Fool's Mate.
- Terminal move input.
- Terminal undo and move listing.
- Terminal quit.

The baseline test suite contained 83 tests, and the recorded Run 1 validation passed.

## 5. Run 2 Highlight Legal Destinations

**Branch:** `m7-run-2`
**Commit:** `af4f749 M7 Run 2: highlight legal destinations`
**Baseline commit:** `08c07bf`

### Additional instruction

Run 2 used the same baseline requirements as Run 1, with one additional feature:

When a player selects one of their own pieces, the Swing view must:

1. Obtain legal moves from `game.legalMoves()`.
2. Filter moves originating from the selected square.
3. Highlight every legal destination.
4. Avoid highlighting illegal destinations.
5. Clear destination highlights when selection is canceled, another piece is selected, or a move is completed.
6. Preserve the selected-square highlight and existing functionality.

No other optional feature or unrelated refactoring was requested.

### Implementation

The Swing view maintains a selected position and a collection of legal destination positions.

The method `selectOwnPiece(...)` obtains legal moves from the current game and filters them by the selected piece's source position. The destinations of those matching moves are stored in `legalDestinations`.

The board rendering code uses this collection to display the legal destination highlights.

The method `clearSelection()` clears both the selected position and the legal destination collection.

### Validation

Recorded Run 2 validation:

- `mvnw.cmd clean test`: 83 tests passed.
- `git diff --check`: clean.
- Manual selection test for the white knight on `g1`: only `f3` and `h3` were highlighted.
- Manual selection test for the white knight on `b1`: only `a3` and `c3` were highlighted.

## 6. Run 3 Swing Board Coordinate Labels

**Branch:** `m7-run-3`
**Commit:** `97c00a5 M7 Run 3: add Swing board coordinates`
**Baseline commit:** `08c07bf`

### Additional instruction

Run 3 used the baseline M7 requirements with one additional feature: coordinate labels on the Swing board.

The requirements were:

- Display files `a` through `h` along the bottom edge.
- Display ranks `1` through `8` along the left edge.
- Align labels with their corresponding board columns and rows.
- Keep the labels correct when the board is rendered or updated.
- Do not change move logic or controller behavior.

Run 3 did not request legal-destination highlighting.

### Implementation

The Run 3 implementation added coordinate labels to the Swing board layout.

It used a grid layout containing the board squares and coordinate labels. The implementation displayed file labels and rank labels around the board.

### Validation

Recorded Run 3 validation included:

- `mvnw.cmd clean test`: 83 tests passed.
- Manual verification of the Swing board coordinates.
- Manual move testing.
- Illegal move testing.
- Undo testing.
- Fool's Mate testing.
- Terminal input and game-status testing.

Run 3 was conducted in a separate ChatGPT conversation from the earlier implementation work.

## 7. Comparison of the Three Runs

| Comparison criterion | Run 1 | Run 2 | Run 3 |
|---|---|---|---|
| Baseline Swing board | Yes | Yes | Yes |
| Piece selection | Yes | Yes | Yes |
| Legal-destination highlighting | No | Yes | No |
| Coordinate labels | No | No | Yes |
| Uses `game.legalMoves()` in Swing | Yes | Yes | No |
| Uses `game.status()` | Yes | Yes | Yes |
| Selection cancellation | No explicit same-square cancellation | Yes | Yes |
| Invalid destination feedback | Delegated to controller | Explicit feedback while retaining selection | Delegated to controller |
| Queen default promotion | Yes | Yes | Yes |

### Selected implementation: Run 2

I selected Run 2 as the foundation for the final implementation.

The reasons were:

1. It uses `game.legalMoves()` to calculate legal destinations for the selected piece.
2. It provides immediate visual feedback about legal destinations.
3. It handles selection cancellation and switching between friendly pieces.
4. It provides an explicit message when a selected piece is clicked on an invalid destination.
5. It retains the existing controller architecture and does not directly modify game state.

Run 2 provided the strongest overall implementation for the Swing interaction requirements.

### Ideas not selected

Run 1's baseline implementation was useful as a straightforward reference, but its selection behavior provided less visual guidance than Run 2.

Run 3's coordinate labels were not included in the original Run 2 implementation. However, the coordinate-label idea was useful and was later incorporated into the selected implementation.

The Run 3 implementation itself was not selected as the final foundation because the recorded implementation did not use `game.legalMoves()` in either its Swing or Terminal view.

## 8. Applying an Idea from a Run That Was Not Selected

I applied the coordinate-label idea from Run 3 to the selected Run 2 implementation.

**Commit:** `c3f5c89 M7: apply Run 3 coordinate label idea`

### Changes

In `SwingBoardView.java`, I:

- Changed the board layout to accommodate coordinate labels.
- Added file labels along the top and bottom.
- Added rank labels on both sides of the board.
- Added `createCoordinateLabel(String text)`.
- Preserved the original square indexing and click listeners.

The implementation uses a `GridLayout(10,10,0,0)` to accommodate the coordinate labels and the 8-by-8 board.

The existing `squares[file][rank]` references were retained, so adding labels did not change the mapping between board positions and their corresponding buttons.

### Validation

I manually checked that:

- Files `a` through `h` aligned with their respective columns.
- Ranks `8` through `1` aligned with their respective rows.
- The board continued to respond correctly to clicks.
- The coordinate labels remained in place during board updates.

The Maven test suite also passed with 83 tests.

This change demonstrates that I reviewed an idea from a run that was not selected and adapted that idea into the final implementation rather than copying the entire alternative implementation.

## 9. Code Review

I reviewed the Swing and Terminal implementations to verify that they follow the existing MVC architecture and use the existing game APIs.

### 9.1 Legal destination calculation

**File:** `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
**Lines:** 231 58
**Function:** `selectOwnPiece(...)`

The method clears the previous destination collection and obtains legal moves from `currentGame.legalMoves()`.

It filters moves by the selected piece's source position and stores the matching destination positions in `legalDestinations`.

**Decision:** Kept this implementation because it uses the engine's legal-move results rather than implementing chess rules in the view.

**Validation:** During debugging, `legalDestinations` contained `h3` and `f3` when the white knight on `g1` was selected.

### 9.2 Selection handling and move validation

**File:** `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
**Lines:** 172 24
**Function:** `handleSquareClick(Position clickedPosition)`

The click handler manages piece selection, selection cancellation, selecting another friendly piece, and destination clicks.

It checks the selected piece's legal destinations before submitting a move.

**Decision:** Kept the separation between UI selection state and game-state changes. The view sends the move through the controller rather than directly modifying the game.

**Validation:** The debugger confirmed that clicking `f3` after selecting `g1` matched the move `g1f3`. The knight moved to `f3`, the turn changed to BLACK, and the selection state was cleared.

### 9.3 Selection cleanup

**File:** `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
**Lines:** 265 68
**Function:** `clearSelection()`

The method clears the selected position and the legal destination collection.

**Decision:** Kept this as a separate helper so selection cleanup is consistent when a selection is canceled or a move is completed.

**Validation:** Manual Swing testing confirmed that the selection and destination highlights were cleared after the move.

### 9.4 Coordinate label layout

**File:** `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
**Lines:** 78 30
**Function:** Swing board construction

The board layout includes file labels along the top and bottom and rank labels on both sides.

The existing square components remain associated with their original file and rank positions.

**Additional method:** `createCoordinateLabel(String text)`, lines 161 69.

**Decision:** Adapted the coordinate-label idea from Run 3 while retaining the Run 2 board interaction and selection logic.

**Validation:** Manually checked the displayed coordinates against the board: files `a to h` align with their columns, and ranks `8 to 1` align with their rows.

### 9.5 Terminal view game status

**File:** `src/main/java/edu/sfsu/csc413/chess/view/TerminalBoardView.java`
**Lines:** 502
**Functions:** `render(Game game)` and its board-rendering logic

The Terminal view displays the board and current side to move, and obtains the displayed game status from `game.status()`.

**Decision:** Kept status reporting delegated to the existing game implementation.

**Validation:** Manual testing confirmed that the Terminal view displayed the current turn and game status.

### 9.6 Terminal legal-move listing and input

**File:** `src/main/java/edu/sfsu/csc413/chess/view/TerminalBoardView.java`
**Lines:** 116 72
**Function:** `nextAction(Game game)`

The method obtains legal moves through `game.legalMoves()`, presents available moves, and converts the user's selected move into a `PlayerAction.Move`.

**Decision:** Kept legal-move generation in the game layer and player-action handling in the controller architecture.

**Validation:** Manual testing confirmed that the Terminal interface supported move input, move listing, undo, and quitting.

## 10. Manual Code Change by Hand

The M7 requirements included a Swing behavior change implemented manually without an AI-generated implementation.

**File:** `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
**Function:** `selectOwnPiece(...)`
**Commit:** `fa4aac3 M7 by hand: show legal destination count`

### Change

I manually modified the selection message so that it reports the number of legal destinations available for the selected piece.

The implementation checks whether `legalDestinations` is empty.

If there are no legal destinations, it displays:

`This piece has no legal moves.`

Otherwise, it displays the selected position and the number of legal destinations highlighted on the board.

The message uses:

`legalDestinations.size()`

to obtain the count.

### Reason for the change

The purpose was to provide additional feedback when selecting a piece. The user can see how many legal destinations are available without changing the chess rules or the controller.

### Validation

- Manually tested the Swing selection message.
- Confirmed that the message displayed correctly.
- Ran `mvnw.cmd clean test`.
- All 83 tests passed.
- `git diff --check` completed without whitespace errors.

This change was made manually and committed with the required commit-message prefix `M7 by hand:`.

## 11. Debugger Evidence

**Debugging target:** `SwingBoardView.handleSquareClick(Position clickedPosition)`

**Test scenario:** White knight moves from `g1` to `f3`.

I set a breakpoint inside `handleSquareClick(Position clickedPosition)` using IntelliJ IDEA's debugger. I then tested one complete legal move by selecting the white knight on `g1` and clicking its destination square, `f3`.

### Predicted behavior before debugging

1. The first click on `g1` should enter the click handler and select the white knight.
2. The legal destinations should be obtained from `currentGame.legalMoves()` and filtered by the selected source position.
3. The knight's legal destinations should include `f3` and `h3`.
4. Clicking `f3` should match the legal move `g1f3`.
5. The move should be sent through the existing controller, after which the selection state should be cleared and the turn should change to BLACK.

### Actual debugger observations

| Observation | Actual result |
|---|---|
| Breakpoint on the first click (`g1`) | Triggered |
| Breakpoint on the second click (`f3`) | Triggered |
| `legalDestinations` | `0 = h3`, `1 = f3` |
| Matching move | `0 = 'g1f3'` |
| Board after the move | White knight appeared on `f3` |
| Side to move after the move | BLACK |
| Selection state after the move | Cleared |
| Swing exception in the console | None observed |

### Comparison and conclusion

The actual debugger values matched the predicted behavior.

The legal destination collection contained the two observed destinations, `h3` and `f3`, and the selected move was `g1f3`.

After the move was processed, the knight appeared on `f3`, the side to move changed to BLACK, and the selection state was cleared.

No Swing exception was observed in the console during this test.

This debugging session verified the piece-selection flow, legal-destination lookup, move matching, and selection cleanup for a normal legal move.

## 12. Final Validation

The final integrated implementation was tested after the Run 2 implementation was selected, the Run 3 coordinate-label idea was applied, and the manual Swing message change was committed.

### Automated tests

Command:

`mvnw.cmd clean test`

Recorded result:

- Tests run: 83
- Failures: 0
- Errors: 0
- Skipped: 0
- Build result: `BUILD SUCCESS`

The build output included nonfatal Java/Jansi/Guava warnings. No test failures or compilation errors were reported in the recorded test run.

### Manual tests

The following behaviors were manually tested during the M7 implementation:

- Swing board display.
- Light and dark square distinction.
- Piece selection and movement.
- Legal destination highlighting.
- Illegal destination handling.
- Selection cancellation and changing selected pieces.
- Undo.
- Queen-default promotion handling.
- Coordinate labels.
- Terminal board display.
- Terminal algebraic move input.
- Terminal move listing.
- Terminal undo and quit.
- Fool's Mate:
    - `f2f3`
    - `e7e5`
    - `g2g4`
    - `d8h4`
- Checkmate status and game-end reporting.

The debugger test additionally confirmed the normal `g1f3` move and the selection cleanup behavior.

## 13. Git History and Submission Branches

The final main-branch implementation history before preparing this report is:

| Commit | Description |
|---|---|
| `08c07bf` | M7: add coding agent instructions |
| `9bd1473` | M7 Run 2: highlight legal destinations |
| `c3f5c89` | M7: apply Run 3 coordinate label idea |
| `fa4aac3` | M7 by hand: show legal destination count |

The three implementation branches are:

| Branch | Run | Commit |
|---|---|---|
| `m7-run-1` | Baseline implementation | `b413ce6` |
| `m7-run-2` | Legal destination highlighting | `af4f749` |
| `m7-run-3` | Coordinate labels | `97c00a5` |

The final implementation was integrated into `main`.

### Pre-submission Git verification

The recorded `git status --short` output was empty, indicating a clean working tree.

The recorded `git diff --stat submit-m6 HEAD` showed:

- `AGENTS.md`
- `docs/milestones/m7.md`
- `src/main/java/edu/sfsu/csc413/chess/Main.java`
- `src/main/java/edu/sfsu/csc413/chess/view/SwingBoardView.java`
- `src/main/java/edu/sfsu/csc413/chess/view/TerminalBoardView.java`

No protected model, engine, controller, existing view, or Maven configuration file appeared in the recorded change list.

### Final submission actions

The following actions remain to be performed after this report is saved and the final checks are complete:

1. Commit `m7-submission.md`.
2. Create the `submit-m7` tag on the final `main` commit.
3. Push `main`, `m7-run-1`, `m7-run-2`, `m7-run-3`, and all required tags to the origin remote.
4. Verify the pushed branches and tag on the remote repository.

The report does not claim that these final push operations have already been completed.

---

## 14. Conclusion

Milestone 7 adds a Swing chessboard and a separate Terminal interface while preserving the existing chess engine and controller architecture.

Run 2 was selected as the foundation because it provides legal-destination highlighting based on `game.legalMoves()`. The coordinate-label idea from Run 3 was then adapted into the selected implementation. A separate manual code change added the legal-destination count to the Swing selection message.

The final recorded automated validation passed all 83 tests. Manual testing and IntelliJ debugging confirmed the principal Swing and Terminal interactions, including a legal knight move, selection cleanup, undo, and Fool's Mate.

The implementation keeps chess-state changes inside the existing controller and uses the existing game APIs for legal moves and game status.
