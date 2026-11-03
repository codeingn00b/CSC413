\# CSC 413 — M6 Submission



\## 1. Which class decides whether the game is checkmate, and which class displays the result?



`MoveGenerator` determines whether the current player is in check and whether any legal moves remain. `Game` exposes this result through `status()` and `isGameOver()`. `GameController` handles the game-over condition and announces the result through the `BoardView` interface. This keeps chess rules in the engine and presentation in the view.



\## 2. Which classes should import the `view` package, and why?



`Main` should import classes from the `view` package because it is responsible for selecting and connecting the user interface. `GameController` belongs to the view layer and imports the engine classes it controls. Classes in the model and engine packages should not import the view package, because that would make the game rules depend on presentation details and weaken the separation of concerns.



\## 3. When adding a Swing view in M7, which classes should change?



M7 should add a new Swing view that implements the existing `BoardView` interface. `Main` should be updated to select the Swing view when requested. The existing `GameController`, `Game`, `MoveGenerator`, and model classes should not need changes because the controller already communicates through the `BoardView` interface.