/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.StdSudokuGrid;
import grid.SudokuGrid;


/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver
{
    // TODO: Add attributes as needed.

    public BackTrackingSolver() {
        // TODO: any initialisation you want to implement.
    } // end of BackTrackingSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        // Find the first elem from the top and left that is empty
        for (int i = 0; i < grid.getGridSize(); i ++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                if (grid.getElement(i, j) == 0) {
                    return backtrack(grid, i, j);
                }
            }
        }

        return true;
    } // end of solve()

    /**
     *
     * @param grid
     * @param row the row number of the empty cell to be assigned
     * @param col the col number of the empty cell to be assigned
     * @return true if a valid solution is found
     */
    private boolean backtrack(SudokuGrid grid, int row, int col) {
        // Find the next empty cell after (row, col)
        int nextRow = 0,
            nextCol = 0;
        boolean hasNextEmptyCell = false;
        for (int i = row; i < grid.getGridSize(); i ++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                if (grid.getElement(i, j) == 0 && !(j == col && i == row)) {
                    nextRow = i;
                    nextCol = j;
                    hasNextEmptyCell = true;
                    break;
                }
            }
            if (hasNextEmptyCell) {
                break;
            }
        }

        for (int symbol : grid.getValidSymbols()) {
            grid.setValue(symbol, row, col);
            if (!grid.validate()) {
                continue;
            } else {
                // No more empty cells, hence solution found!
                if (!hasNextEmptyCell || backtrack(grid, nextRow, nextCol)) {
                    return true;
                } else {
                    // Can't find a valid solution with symbol, so try the next symbol
                    continue;
                }
            }
        }

        // Can't find a valid assignment to (row, col) with current config, so reset (row, col) to 0
        grid.setValue(0, row, col);
        return false;
    }


} // end of class BackTrackingSolver()
