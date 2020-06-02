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

        // Fill initialAssignments, satisfiedConstraintIndices and remainingAssignments
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

        // Get the set difference between satisfiedConstraintIndices and allConstraintList to fill remainingConstraintIndices
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
        // Remove an assignment from remainingAssignments if it is covered
        List<String> toRemove = new ArrayList<>();
        for (String assignment : remainingAssignments) {
            int[] row = matrix.get(assignment);
            for (int i = 0; i < row.length; i++) {
                if (row[i] == 1 && satisfiedConstraintIndices.contains(i)) {
                    coveredAssignments.add(assignment);
                    toRemove.add(assignment);
                    break;
                }
            }
        }
        remainingAssignments.removeAll(toRemove);

        boolean result = executeAlgoX(chosenAssignments, remainingAssignments, /*coveredAssignments, satisfiedConstraintIndices,*/
                remainingConstraintIndices);
        for (String assignment : chosenAssignments) {
            String[] strings = assignment.replace("[", "").replace("]", "").split(", ");
            int[] values = new int[strings.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.parseInt(strings[i]);
            }
            grid.setValue(values[VAL], values[ROW], values[COL]);
        }
        if (result) return true;
        else return false;
    } // end of solve()

    private boolean executeAlgoX(Set<String> chosenAssignments, Set<String> remainingAssignments,
                              /*Set<String> coveredAssignments, Set<Integer> satisfiedConstraintIndices,*/
                              Set<Integer> remainingConstraintIndices) {
        // Solution found when remainingAssignments is empty
        if (remainingAssignments.isEmpty()) {
            return true;
        }

        // Select a constraint to fulfill
        int chosenConstraintIndex = remainingConstraintIndices.iterator().next();

        // Find all the rows that satisfy the chosen constraint
        List<String> potentialAssignments = new ArrayList<>();
        for (String assignment : remainingAssignments) {
            if (matrix.get(assignment)[chosenConstraintIndex] == 1) {
                potentialAssignments.add(assignment);
            }
        }

        // Backtrack if a constraint can't be satisfied with remaining rows
        if (potentialAssignments.isEmpty()) {
            return false;
        } else {
            // Select a row that satisfies the chosen constraint
            for (int j = 0; j < potentialAssignments.size(); j ++) {
                String chosenAssignment = potentialAssignments.get(j);

                // Cover satisfied rows and columns
                Set<String> tempCoveredAssignments = new HashSet<>();
                Set<Integer> tempSatisfiedConstraintIndices = new HashSet<>();
                int[] row = matrix.get(chosenAssignment);
                for (int i = 0; i < row.length; i++) {
                    if (row[i] == 1) {
                        tempSatisfiedConstraintIndices.add(i);
                        for (String assignment : remainingAssignments) {
                            if (matrix.get(assignment)[i] == 1 && !assignment.equals(chosenAssignment)) {
                                tempCoveredAssignments.add(assignment);
                            }
                        }
                    }
                }
                chosenAssignments.add(chosenAssignment);
//                coveredAssignments.addAll(tempCoveredAssignments);
                remainingAssignments.remove(chosenAssignment);
                remainingAssignments.removeAll(tempCoveredAssignments);
//                satisfiedConstraintIndices.addAll(tempSatisfiedConstraintIndices);
                remainingConstraintIndices.removeAll(tempSatisfiedConstraintIndices);
                if (executeAlgoX(chosenAssignments, remainingAssignments,/* coveredAssignments,
                        satisfiedConstraintIndices, */remainingConstraintIndices)) {
                    return true;
                } else {
                    // this row/assignment does not work, try the next row/assignment
                    // Once backtracked, uncover rows and columns covered previously
                    chosenAssignments.remove(chosenAssignment);
//                    coveredAssignments.removeAll(tempCoveredAssignments);
                    remainingAssignments.add(chosenAssignment);
                    remainingAssignments.addAll(tempCoveredAssignments);
//                    satisfiedConstraintIndices.removeAll(tempSatisfiedConstraintIndices);
                    remainingConstraintIndices.addAll(tempSatisfiedConstraintIndices);
                    continue;
                }
            }
        }

        return false;
    }

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
