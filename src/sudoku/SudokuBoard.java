package sudoku;

import java.security.SecureRandom;
import java.util.Arrays;

public class SudokuBoard
{
	private int[][] SudokuBoardMap;
	private boolean[][] isImmutable;
	int SIZE = 9;
	int THIRD = 3;
	private int fitness;
	
	public SudokuBoard(SecureRandom rand)
	{
		this.SudokuBoardMap = new int[this.SIZE][this.SIZE];
		this.isImmutable = new boolean[this.SIZE][this.SIZE];
		
		//Fill the diagonal matrices
		fillDiagonal(rand);
		//Fill in the empty cells
		fillEmpty(0, this.THIRD);
		this.fitness = 0;
	}

	public SudokuBoard(int[][] boardNumber, SecureRandom rand)
	{
		this.SudokuBoardMap = new int[this.SIZE][this.SIZE];
		this.isImmutable = new boolean[this.SIZE][this.SIZE];
		for (int j = 0; j < 9; j++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.SudokuBoardMap[j][k] = boardNumber[j][k];
			}
		}
		this.fitness = 0;
		for (int j = 0; j < 9; j++)
		{
			for (int k = 0; k < 9; k++)
			{
				if(this.SudokuBoardMap[j][k] == 0)
				{
					int random = rand.nextInt(9) + 1;
					this.SudokuBoardMap[j][k] = random;
				}
			}
		}
	}
	
	public SudokuBoard(boolean fillRandom, SecureRandom rand)
	{
		this.SudokuBoardMap = new int[this.SIZE][this.SIZE];
		this.isImmutable = new boolean[this.SIZE][this.SIZE];
		for (int j = 0; j < 9; j++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.SudokuBoardMap[j][k] = (rand.nextInt(9) + 1);
			}
		}
		this.fitness = 0;
	}
	
	public SudokuBoard(int[][] boardNumber)
	{
		this.SudokuBoardMap = new int[this.SIZE][this.SIZE];
		this.isImmutable = new boolean[this.SIZE][this.SIZE];
		for (int j = 0; j < 9; j++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.SudokuBoardMap[j][k] = boardNumber[j][k];
				if(boardNumber[j][k] != 0)
					this.isImmutable[j][k] = true;
			}
		}
		this.fitness = 0;	
	}
	
	public SudokuBoard(int[][] boardNumber, SecureRandom rand, boolean doFill)
	{
		this.SudokuBoardMap = new int[this.SIZE][this.SIZE];
		this.isImmutable = new boolean[this.SIZE][this.SIZE];
		for (int j = 0; j < 9; j++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.SudokuBoardMap[j][k] = boardNumber[j][k];
				if(boardNumber[j][k] != 0)
					this.isImmutable[j][k] = true;
			}
		}
		fillDiagonal(rand);
		fillEmpty(0, this.THIRD);
		this.fitness = 0;	
	}

	public int[][] getSudokuBoardMap() {
		return SudokuBoardMap;
	}
	
	public void setSudokuBoardMap(int[][] sudokuBoardMap) {
		this.SudokuBoardMap = sudokuBoardMap;
	}
	
	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	private void fillDiagonal(SecureRandom rand)
	{
		for (int i = 0; i < this.SIZE; i += this.THIRD)
			fillSubSquare(i, i, rand);
	}
	
	private boolean fillEmpty(int i, int j)
	{
		if (j >= this.SIZE && i < this.SIZE - 1)
		{
			i = i + 1;
			j = 0;
		}
		if (i >= this.SIZE && j >= this.SIZE)
			return true;

		if (i < this.THIRD)
		{
			if (j < this.THIRD)
				j = this.THIRD;
		}
		else if (i < this.SIZE - this.THIRD)
		{
			if (j == (int)(i / this.THIRD)*this.THIRD)
				j = j + this.THIRD;
		}
		else
		{
			if (j == this.SIZE - this.THIRD)
			{
				i = i + 1;
				j = 0;
				if (i >= this.SIZE)
					return true;
			}
		}

		for (int num = 1; num <= this.SIZE; num++)
		{
			if (uniqueInAllScenarios(i, j, num))
			{
				this.SudokuBoardMap[i][j] = num;
				if (fillEmpty(i, j + 1))
					return true;

				this.SudokuBoardMap[i][j] = 0;
			}
		}
		return false;
	}
	
	private void fillSubSquare(int row, int column, SecureRandom rand)
	{
		int randNum = 0;
		for (int i = 0; i < this.THIRD; i++)
		{
			for (int j = 0; j < this.THIRD; j++)
			{
				do
				{
					randNum = (rand.nextInt(9) + 1);
				} 
				while (!uniqueInSubSquare(row, column, randNum));

				this.SudokuBoardMap[row + i][column + j] = randNum;
			}
		}
	}
	
	private boolean uniqueInAllScenarios(int row, int column, int num)
	{
		return (uniqueInRow(row, num) &&
			uniqueInColumn(column, num) &&
			uniqueInSubSquare(row - (row % this.THIRD), column - (column % this.THIRD), num));
	}
	
	private boolean uniqueInSubSquare(int row, int column, int num)
	{
		for (int i = 0; i < this.THIRD; i++)
			for (int j = 0; j < this.THIRD; j++)
				if (this.SudokuBoardMap[row + i][column + j] == num)
					return false;

		return true;
	}
	
	private boolean uniqueInRow(int i, int num)
	{
		for (int j = 0; j < this.SIZE; j++)
			if (this.SudokuBoardMap[i][j] == num)
				return false;

		return true;
	}
	
	private boolean uniqueInColumn(int i, int num)
	{
		for (int j = 0; j < this.SIZE; j++)
			if (this.SudokuBoardMap[j][i] == num)
				return false;

		return true;
	}
	
	public void printBoard() 
	{
		System.out.println("----------------------------------");

		for (int i = 0; i < this.SIZE; i++) {
			System.out.print("| ");

			for (int j = 0; j < this.SIZE; j++) {
				if (this.SudokuBoardMap[i][j] > 0) {
					System.out.print(this.SudokuBoardMap[i][j] + "  ");
				}
				else {
					System.out.print("   ");
				}

				if (j % this.THIRD == this.THIRD - 1) {
					System.out.print("| ");
				}
			}
			if (i % this.THIRD == this.THIRD - 1) {
				System.out.print('\n' + "----------------------------------");
			}
			System.out.print('\n');
		}
	}
	
	public void calculateFitness(SudokuBoard solutionBoard)
	{
		// The this.fitness function should be 
		// 9x9 + 9x9 + 9x9

		int fitness_calc = 0;
		
		//Correct elements on a row
		for (int a = 0; a < this.SIZE; a++)
		{
			int arr[] = new int[this.SIZE];
			for (int i = 0; i < this.SIZE; i++)
			{
				arr[i] = this.SudokuBoardMap[a][i];
			}
			int arrCount[] = Arrays.stream(arr)
			        .sorted()
			        .distinct()
			        .toArray();
			
			for(int i = 0; i < arrCount.length; i++)
			{
				if(arrCount[i] > 0)
					fitness_calc++;
			}
		}

		//Correct elements on a column
		for (int b = 0; b < this.SIZE; b++)
		{
			int arr[] = new int[this.SIZE];
			for (int i = 0; i < this.SIZE; i++)
			{
				arr[i] = this.SudokuBoardMap[i][b];
			}
			int arrCount[] = Arrays.stream(arr)
			        .sorted()
			        .distinct()
			        .toArray();
			for(int i = 0; i < arrCount.length; i++)
			{
				if(arrCount[i] > 0)
					fitness_calc++;
			}
		}
		
		//Correct subsquares, and their elements
		for (int i = 0; i < this.SIZE; i += this.THIRD)
			for (int j = 0; j < this.SIZE; j += this.THIRD)
				fitness_calc += countValidSubSquareValues(i, j, solutionBoard);

		this.fitness = fitness_calc;
	}
	
	private int countValidSubSquareValues(int row, int column, SudokuBoard solutionBoard)
	{
		int count = 0;
		int arr[] = new int[this.SIZE];
		for (int i = 0; i < this.THIRD; i++)
		{
			for (int j = 0; j < this.THIRD; j++)
			{
				arr[count] = this.SudokuBoardMap[row + i][column + j];
				count++;
			}
		}
		int arrCount[] = Arrays.stream(arr)
		        .sorted()
		        .distinct()
		        .toArray();
		int fitness_calc = 0;
		for(int i = 0; i < arrCount.length; i++)
		{
			if(arrCount[i] > 0)
				fitness_calc++;
		}
		return fitness_calc;
	}

	public boolean[][] getIsImmutable() {
		return isImmutable;
	}

	public void setIsImmutable(boolean[][] isImmutable) {
		this.isImmutable = isImmutable;
	}
}