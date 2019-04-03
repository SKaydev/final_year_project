package sudoku;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Properties;

public class Sudoku
{
	// --------------------------------------------------------
	// Global Constants
	// --------------------------------------------------------
	static int pop_size = 1000;
	static int gen_size = 100;

	static int CROSSOVER_CHANCE = 100;
	static int MUTATION_CHANCE = 5;
	static int BOARDS_TO_BRING_FORWARD = 10;
	static int currentGen = 0;
	
	public static void main(String[] args) throws Exception
	{	
		SudokuWorker worker = new SudokuWorker(currentGen);
		worker.doWork();
	}
}

class SudokuWorker
{
	static int pop_size = 1000;
	static int gen_size = 100;

	static int CROSSOVER_CHANCE = 100;
	static int MUTATION_CHANCE = 5;
	static int BOARDS_TO_BRING_FORWARD = (pop_size/100) * 5;
	
	int currentGen;
	
	public SudokuWorker(int gen)
	{
		currentGen = gen;
	}
	
	public void doWork() throws Exception
	{
		int arr_problem[][] = {
				{ 0, 0, 7, 8, 0, 0, 0, 0, 0 },
				{ 4, 0, 1, 7, 0, 6, 0, 0, 5 },
				{ 9, 0, 0, 0, 4, 0, 8, 0, 0 },
				{ 8, 4, 3, 0, 0, 7, 0, 6, 1 },
				{ 0, 2, 0, 0, 1, 0, 0, 4, 0 },
				{ 1, 6, 0, 2, 0, 0, 7, 3, 9 },
				{ 0, 0, 6, 0, 7, 0, 0, 0, 3 },
				{ 2, 0, 0, 3, 0, 1, 6, 0, 4 },
				{ 0, 0, 0, 0, 0, 8, 9, 0, 0 }
			};
		
		int arr_solution[][] = {
				{ 6, 5, 7, 8, 3, 9, 4, 1, 2 },
				{ 4, 8, 1, 7, 2, 6, 3, 9, 5 },
				{ 9, 3, 2, 1, 4, 5, 8, 7, 6 },
				{ 8, 4, 3, 9, 5, 7, 2, 6, 1 },
				{ 7, 2, 9, 6, 1, 3, 5, 4, 8 },
				{ 1, 6, 5, 2, 8, 4, 7, 3, 9 },
				{ 5, 9, 6, 4, 7, 2, 1, 8, 3 },
				{ 2, 7, 8, 3, 9, 1, 6, 5, 4 },
				{ 3, 1, 4, 5, 6, 8, 9, 2, 7 }
			};
		
		SudokuBoard solutionBoard = new SudokuBoard(arr_solution);
		SudokuBoard checkBoard = new SudokuBoard(arr_problem);
		
		System.out.println("Using the problem board listed below:");
		checkBoard.printBoard();
		
		System.out.println("We're maximising towards:");
		solutionBoard.printBoard();

		System.out.println("Will begin solving using the following parameters:");
		System.out.println("Population Size: " +  pop_size);
		System.out.println("Number of generations: " + gen_size);
		
		populateRandomly(arr_problem, rand);
		
		Properties p = new Properties();
		
		p.load(new FileInputStream("settings.ini"));
		
		int cross = Integer.valueOf(p.getProperty("CrossoverOP"));
		int mutat = Integer.valueOf(p.getProperty("MutationOP"));
		
		System.out.println("We are using the following numbered operators:");
		System.out.println("Crossover Operator: " +  cross);
		System.out.println("Mutation Operator: " + mutat);
		
		Crossover crossover = new Crossover(cross, CROSSOVER_CHANCE);
		Mutation mutation = new Mutation(mutat, MUTATION_CHANCE);
	
		while (currentGen < gen_size - 1)
		{
			if(!checkIfSolutionFound(solutionBoard, sudokuPopGen, currentGen))
			{				
				weighFitness(solutionBoard);
				crossover.performCrossover(solutionBoard, sudokuPopGen, currentGen, rand);
				mutation.performMutation(solutionBoard, sudokuPopGen, currentGen, rand);
				weighFitness(solutionBoard);
				performSelectiveElitism();
				
				System.out.println("Current Generation: " + (currentGen + 1));
				System.out.println("Current average fitness: " + getAverageFitness());
				System.out.println("Current total fitness: " + getTotalFitness());
				
				currentGen++;
			}
			else
			{
				System.out.println("Solution Found!");
				System.out.println("Solution found at generation: " + currentGen);
			}
		}
		System.out.println("No solution has been found.");
	}
	
	private boolean checkIfSolutionFound(SudokuBoard solutionBoard, SudokuBoard[][] sudokuPopGen, int currentGen) 
	{		
		for (int i = 0; i < pop_size; i++)
		{
			if (sudokuPopGen[currentGen][i].getSudokuBoardMap() == solutionBoard.getSudokuBoardMap())
				return true;
		}
		return false;
	}

	SecureRandom rand = new SecureRandom();
	
	SudokuBoard sudokuPopGen[][] = new SudokuBoard[gen_size][pop_size];
	
	public SudokuBoard[][] getSudokuPopGen() {
		return sudokuPopGen;
	}

	public void setSudokuPopGen(SudokuBoard[][] sudokuPopGen) {
		this.sudokuPopGen = sudokuPopGen;
	}

	public void bubbleSort(int a, int n)
	{
		if (n == 1)
			return;

		for (int j = 0; j < n - 1; j++)
		{
			if (sudokuPopGen[a][j].getFitness() > sudokuPopGen[a][j + 1].getFitness())
			{
				SudokuBoard board = sudokuPopGen[a][j + 1];
				sudokuPopGen[a][j + 1] = sudokuPopGen[a][j];
				sudokuPopGen[a][j] = board;
			}
		}

		bubbleSort(a, n - 1);
	}
	
	public void weighFitness(SudokuBoard solutionBoard)
	{
		for (int j = 0; j < gen_size; j++)
		{			
			for (int i = 0; i < pop_size; i++)
			{
				sudokuPopGen[j][i].calculateFitness(solutionBoard);
			}
		}
	}

	public int getAverageFitness()
	{
		int averageFitness = 0;

		for (int i = 0; i < pop_size; i++)
		{
			averageFitness += sudokuPopGen[0][i].getFitness();
		}

		return averageFitness / pop_size;
	}
	
	public int getTotalFitness()
	{
		int totalFitness = 0;

		for (int i = 0; i < pop_size; i++)
		{
			totalFitness += sudokuPopGen[0][i].getFitness();
		}

		return totalFitness;
	}

	public void populateRandomly(int[][] arr, SecureRandom rand)
	{
		for (int j = 0; j < gen_size; j++)
		{			
			for (int i = 0; i < pop_size; i++)
			{
				SudokuBoard board = new SudokuBoard(arr, rand);
				sudokuPopGen[j][i] = board;
			}
		}
	}

	public int isFitnessGreaterThanAnyElement(SudokuBoard board, int arr[])
	{
		for (int i = 0; i < arr.length; i++)
		{
			int fitness = board.getFitness();
			if (fitness > arr[i])
			{
				return i;
			}
		}
		return -1;
	}

	public int isFitnessSmallerThanAnyElement(SudokuBoard board, int arr[])
	{
		for (int i = 0; i < arr.length; i++)
		{
			int fitness = board.getFitness();
			if (fitness <= arr[i])
			{
				return i;
			}
		}
		return -1;
	}

	public void performSelectiveElitism()
	{
		bubbleSort(currentGen, pop_size);
		bubbleSort(currentGen + 1, pop_size);

		int temp = pop_size - 1;
		for (int i = 0; i < BOARDS_TO_BRING_FORWARD; i++)
		{
			if(currentGen < 99)
			{	
				sudokuPopGen[currentGen + 1][i] = sudokuPopGen[currentGen][temp - i];
			}
		}

		bubbleSort(currentGen, pop_size);
		bubbleSort(currentGen + 1, pop_size);
	}
	
}