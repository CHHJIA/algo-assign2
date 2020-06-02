/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Class implementing the grid for standard Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task A and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class StdSudokuGrid extends SudokuGrid
{
    // TODO: Add your own attributes
    private int gridSize;
    private List<Integer> validSymbols;
    private int[][] grid;

    public StdSudokuGrid() {
        super();

        // TODO: any necessary initialisation at the constructor
    } // end of StdSudokuGrid()



    /* ********************************************************* */
    @Override
    public void initGrid(String filename)
        throws FileNotFoundException, IOException
    {
        File myFile = new File(filename);
        Scanner myReader = new Scanner(myFile);
        gridSize = Integer.parseInt(myReader.nextLine());
        validSymbols = new ArrayList<>();
        for (String symbol : myReader.nextLine().split(" ")) {
            validSymbols.add(Integer.parseInt(symbol));
        }

        grid = new int[gridSize][gridSize];
        final int POSITION = 0;
        final int VALUE = 1;
        final int ROW = 0;
        final int COL = 1;

        while (myReader.hasNextLine()) {
            String[] data = myReader.nextLine().split(" ");
            String[] position = data[POSITION].split(",");
            int row = Integer.parseInt(position[ROW]);
            int col = Integer.parseInt(position[COL]);
            int value = Integer.parseInt(data[VALUE]);
            grid[row][col] = value;
        }
        myReader.close();
    } // end of initBoard()


    @Override
    public void outputGrid(String filename)
        throws FileNotFoundException, IOException
    {
        // TODO
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred in writing to file.");
            e.printStackTrace();
        }

    } // end of outputBoard()


    @Override
    public String toString() {
        // TODO
        StringBuilder sb = new StringBuilder();
        for (int[] row : grid) {
            for (int value : row) {
                sb.append(value + ",");
            }
            sb.append("\n");
        }
        return sb.toString();
    } // end of toString()


    @Override
    public boolean validate() {
        // TODO
        for (int row = 0; row < gridSize; row ++) {
            for (int col = 0; col < gridSize; col ++) {
                int value = grid[row][col];
                if (value != 0) {
                    if (isUniqueInRow(row, col) && isUniqueInCol(row, col) && isUniqueInBox(row, col)) {
                        continue;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    } // end of validate()

    @Override
    public boolean partialValidate() {
        // TODO
        for (int row = 0; row < gridSize; row ++) {
            for (int col = 0; col < gridSize; col ++) {
                int value = grid[row][col];
                if (value != 0) {
                    if (isUniqueInRow(row, col) && isUniqueInCol(row, col) && isUniqueInBox(row, col)) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    } // end of validate()

    private boolean isUniqueInBox(int row, int col) {
        int sqrt = (int) Math.sqrt(gridSize);
        int boxRowNum = row / sqrt;
        int boxColNum = col / sqrt;
        int boxFirstElemRow = boxRowNum * sqrt;
        int boxFirstElemCol = boxColNum * sqrt;
        int value = grid[row][col];
        for (int i = 0; i < sqrt; i ++) {
            for (int j = 0; j < sqrt; j ++) {
                if ((i + boxFirstElemRow == row) && (j + boxFirstElemCol == col)) {
                    continue;
                }
                if (value == grid[i + boxFirstElemRow][j + boxFirstElemCol]) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isUniqueInCol(int row, int col) {
        int value = grid[row][col];
        for (int i = 0; i < gridSize; i ++) {
            if (i == row) continue;
            if (grid[i][col] == value) return false;
        }

        return true;
    }

    private boolean isUniqueInRow(int row, int col) {
        int value = grid[row][col];
        for (int i = 0; i < gridSize; i ++) {
            if (i == col) continue;
            if (grid[row][i] == value) return false;
        }

        return true;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getElement(int row, int col) {
        return grid[row][col];
    }

    public List<Integer> getValidSymbols() {
        return validSymbols;
    }

    @Override
    public void setValue(int value, int row, int col) {
        grid[row][col] = value;
    }

    @Override
    public boolean hasValue(int row, int col) {
        if (grid[row][col] != 0) {
            return true;
        } else {
            return false;
        }
    }

} // end of class StdSudokuGrid
