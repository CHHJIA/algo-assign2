/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import grid.SudokuGrid;

import java.util.*;


/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver
{
    // TODO: Add attributes as needed.
    private static final int ROW = 0;
    private static final int COL = 1;
    private static final int VAL = 2;

    private Map<String, int[]> matrix;
    private int cellConstraintIndex;
    private int rowConstraintIndex;
    private int colConstraintIndex;
    private int boxConstraintIndex;
    private int gridSize;


    public AlgorXSolver() {
        // TODO: any initialisation you want to implement.
    } // end of AlgorXSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        // TODO: your implementation of the Algorithm X solver for standard Sudoku.
        gridSize = grid.getGridSize();
        buildMatrix(grid);

        Set<String> initialAssignments = new HashSet<>();
        Set<String> chosenAssignments = new HashSet<>();
        Set<String> remainingAssignments = new HashSet<>();
        Set<String> coveredAssignments = new HashSet<>();
        Set<Integer> satisfiedConstraintIndices = new TreeSet<>();
        Set<Integer> remainingConstraintIndices = new HashSet<>();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int value = grid.getElement(i, j);
                if (grid.hasValue(i, j)) {
                    String assignment = Arrays.toString(new int[]{i, j, value});
                    initialAssignments.add(assignment);
                    int[] row = matrix.get(assignment);
                    for (int k = 0; k < row.length; k++) {
                        if (row[k] == 1) satisfiedConstraintIndices.add(k);
                    }
                } else {
                    for (int symbol: grid.getValidSymbols()) {
                        String assignment = Arrays.toString(new int[]{i, j, symbol});
                        remainingAssignments.add(assignment);
                    }
                }
            }
        }

        // Just getting a random assignment to get the constraintListSize
        String randAssignment = Arrays.toString(new int[]{0, 0, grid.getValidSymbols().get(0)});
        int constraintListSize = matrix.get(randAssignment).length;

        // Get the set difference between satisfiedConstraintIndices and allConstraintList
        int tempIndex = 0;
        for (Integer index : satisfiedConstraintIndices) {
            for (int i = tempIndex; i < index; i ++) {
                remainingConstraintIndices.add(i);
            }
            tempIndex = index + 1;
        }
        while (tempIndex < constraintListSize) {
            remainingConstraintIndices.add(tempIndex ++);
        }

        // Cover rows that have 1 at indices in satisfiedConstraintIndices
        for (String assignment : remainingAssignments) {
            int[] row = matrix.get(assignment);
            for (int i = 0; i < row.length; i++) {
                if (row[i] == 1 && satisfiedConstraintIndices.contains(i)) {
                    coveredAssignments.add(assignment);
                    break;
                }
            }
        }


        // placeholder
        return false;
    } // end of solve()

    private void buildMatrix(SudokuGrid grid) {
        matrix = new HashMap<>();

        // Starting index of each type of constraint in the list of constraints in the matrix
        // the structure of the list of constraints: [[cell constraints], [row constraints], [column constraints], [box constraints]]
        cellConstraintIndex = 0;
        rowConstraintIndex = gridSize * gridSize;
        colConstraintIndex = 2 * gridSize * gridSize;
        boxConstraintIndex = 3 * gridSize * gridSize;

        // Note that the row and column start from 0
        for (int i = 0; i < gridSize; i ++) {
            for (int j = 0; j < gridSize; j++) {
                for (int symbol : grid.getValidSymbols()) {
                    int[] assignment = new int[]{i, j, symbol};
                    matrix.put(Arrays.toString(assignment), buildConstraints(assignment));
                }
            }
        }
    }

    private int[] buildConstraints(int[] assignment) {
        // 4 types of constraints, and each type has gridSize*gridSize constraints
        int[] constraints = new int[4 * gridSize * gridSize];

        // Get the indices with value 1; Note that the row and column start from 0
        int validCellConstraintIndex = assignment[ROW] * gridSize + assignment[COL];
        int validRowConstraintIndex = assignment[ROW] * gridSize + (assignment[VAL] - 1) + rowConstraintIndex;
        int validColConstraintIndex = assignment[COL] * gridSize + (assignment[VAL] - 1) + colConstraintIndex;
        int sqrt = (int) Math.sqrt(gridSize);
        int boxNumber = sqrt * (assignment[ROW] / sqrt) + assignment[COL] / sqrt;
        int validBoxConstraintIndex = boxNumber * gridSize + (assignment[VAL] - 1) + boxConstraintIndex;

        int[] indicesWithValue1 = new int[]{validCellConstraintIndex, validRowConstraintIndex, validColConstraintIndex, validBoxConstraintIndex};
        for (int index : indicesWithValue1) {
            constraints[index] = 1;
        }

        return constraints;
    }

} // end of class AlgorXSolver
