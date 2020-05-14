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

        // placeholder
        return false;
    } // end of validate()

} // end of class StdSudokuGrid
