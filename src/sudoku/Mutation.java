package sudoku;

import java.security.SecureRandom;

public class Mutation 
{
	int pop_size = 1000;
	int gen_size = 100;
	int MUTATION_CHANCE;
	int operator;
	
	int ROULETTE_POP = 7;
	
	public Mutation(int op, int mutationchance) 
	{
		operator = op;
		MUTATION_CHANCE = mutationchance;
	}
	
	void performMutation(SudokuBoard board, SudokuBoard sudokuPopGen[][], int currentGen, SecureRandom rand)
	{
		switch(operator)
		{
		case 1:
			doNonUniformMutation(board, sudokuPopGen, currentGen, rand);
		case 2:
			doUniformMutation(board, sudokuPopGen, currentGen, rand);
		}
	}

	private void doUniformMutation(SudokuBoard board, SudokuBoard[][] sudokuPopGen, int currentGen,
			SecureRandom rand) {
		for (int i = 0; i < pop_size; i++)
		{
			int[][] map = new int[9][9];
			map = sudokuPopGen[currentGen + 1][i].getSudokuBoardMap();
			for(int j = 0; j < 9; j++)
			{
				for (int k = 0; k < 9; k++) 
				{
					if ((rand.nextInt(100) + 1) <= MUTATION_CHANCE)
					{
						map[j][k] = (rand.nextInt(9) + 1);
					}
				}
			}
			sudokuPopGen[currentGen + 1][i].setSudokuBoardMap(map);
			sudokuPopGen[currentGen + 1][i].calculateFitness(board);
		}
	}
	
	private void doNonUniformMutation(SudokuBoard board, SudokuBoard[][] sudokuPopGen, int currentGen,
			SecureRandom rand) {
		for (int i = 0; i < pop_size; i++)
		{
			int[][] map = new int[9][9];
			map = sudokuPopGen[currentGen + 1][i].getSudokuBoardMap();
			for(int j = 0; j < 9; j++)
			{
				for (int k = 0; k < 9; k++) 
				{
					if ((rand.nextInt(100) + 1) <= (MUTATION_CHANCE - (currentGen/gen_size)))
					{
						map[j][k] = (rand.nextInt(9) + 1);
					}
				}
			}
			sudokuPopGen[currentGen + 1][i].setSudokuBoardMap(map);
			sudokuPopGen[currentGen + 1][i].calculateFitness(board);
		}
	}
}
