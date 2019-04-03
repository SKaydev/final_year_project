package sudoku;

import java.security.SecureRandom;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

public class Crossover 
{
	int pop_size = 1000;
	int gen_size = 100;
	int CROSSOVER_CHANCE;
	int operator;
	
	
	int ROULETTE_POP = 7;
	
	public Crossover(int op, int crossoverchance) 
	{
		operator = op;
		CROSSOVER_CHANCE = crossoverchance;
	}
	
	public SudokuBoard doRoulette(SudokuBoard solutionBoard, 
			SudokuBoard sudokuPopGen[][], int currentGen, SecureRandom rand)
	{
		// K-Way Tournament Selection
		
		SudokuBoard candidates[] = new SudokuBoard[ROULETTE_POP];
		int position[] = new int[ROULETTE_POP];
		int count = 0;
		
		while (count < ROULETTE_POP)
		{
			int random = rand.nextInt(pop_size - 1);
			boolean contained = IntStream.of(position).anyMatch(x -> x == random);
			if (!contained)
			{
				position[count] = random;
				candidates[count] = sudokuPopGen[currentGen][random];
				count++;
			}
		}

		int largest = 0;
		for (int i = 0; i < ROULETTE_POP; i++)
		{
			candidates[i].calculateFitness(solutionBoard);
			if (candidates[i].getFitness() > candidates[largest].getFitness())
			{
				largest = i;
			}
		}
		return candidates[largest];
	}

	public void performCrossover(SudokuBoard solutionBoard, 
			SudokuBoard sudokuPopGen[][], int currentGen, SecureRandom rand)
	{
		if ((rand.nextInt(100) + 1) <= CROSSOVER_CHANCE)
		{
			int num_elements = 0;
			while (num_elements < pop_size)
			{
				switch(operator)
				{
					case 1:
						performOnePointCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand); break;
					case 2:
						performTwoPointCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand); break;
					case 3:
						performPMXCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand); break;
					case 4:
						performUniformSwapCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand); break;
					case 5:
						performCycleCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand); break;
				}
				num_elements++;
			}
		}
	}
	
	private void performOnePointCrossover(int pos, SudokuBoard solutionBoard, 
			SudokuBoard sudokuPopGen[][], int currentGen, SecureRandom rand)
	{
		SudokuBoard parentA = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		SudokuBoard parentB = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		
		SudokuBoard childA = new SudokuBoard(true, rand);
		childA.setSudokuBoardMap(parentA.getSudokuBoardMap());
		childA.setIsImmutable(parentA.getIsImmutable());
		
		SudokuBoard childB = new SudokuBoard(true, rand);
		childB.setSudokuBoardMap(parentB.getSudokuBoardMap());
		childB.setIsImmutable(parentB.getIsImmutable());
		
		int length = rand.nextInt(9);
		int row = rand.nextInt(9);
		
		int mapA[][] = new int[9][9];
		int mapB[][] = new int[9][9];
		
		mapA = childA.getSudokuBoardMap();
		mapB = childB.getSudokuBoardMap();
		
		boolean bMapA[] = new boolean[9];
		bMapA = childA.getIsImmutable()[row];
		
		int arrRowA[] = new int[9];
		int arrRowB[] = new int[9];	
		
		arrRowA = mapA[row];
		arrRowB = mapB[row];	

		for (int j = 0; j < length; j++)
		{	
			if(!bMapA[j])
			{
				arrRowA[j] = arrRowA[j] ^ arrRowB[j];	
				arrRowB[j] = arrRowA[j] ^ arrRowB[j];	
				arrRowA[j] = arrRowA[j] ^ arrRowB[j];	
			}
		}
		
		mapA[row] = arrRowA;
		mapB[row] = arrRowB;
		
		childB.setSudokuBoardMap(mapB);
		childA.setSudokuBoardMap(mapA);
		
		childA.calculateFitness(solutionBoard);
		childB.calculateFitness(solutionBoard);
		
		sudokuPopGen[currentGen + 1][pos] = (childA.getFitness() > childB.getFitness() ? childA : childB);	
	}
	
	private void performTwoPointCrossover(int pos, SudokuBoard solutionBoard, 
			SudokuBoard[][] sudokuPopGen, int currentGen, SecureRandom rand)
	{
		SudokuBoard parentA = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		SudokuBoard parentB = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);

		SudokuBoard childA = new SudokuBoard(true, rand);
		childA.setSudokuBoardMap(parentA.getSudokuBoardMap());
		childA.setIsImmutable(parentA.getIsImmutable());
		
		SudokuBoard childB = new SudokuBoard(true, rand);
		childB.setSudokuBoardMap(parentB.getSudokuBoardMap());
		childB.setIsImmutable(parentB.getIsImmutable());
		
		int lengthA = rand.nextInt(9);
		int lengthB = rand.nextInt(9);
		
		if(lengthA > lengthB)
		{
			lengthA = lengthA ^ lengthB;
			lengthB = lengthA ^ lengthB;
			lengthA = lengthA ^ lengthB;
		}
		
		int row = rand.nextInt(9);
		
		int mapA[][] = new int[9][9];
		int mapB[][] = new int[9][9];
		
		mapA = childA.getSudokuBoardMap();
		mapB = childB.getSudokuBoardMap();
		
		boolean bMapA[] = new boolean[9];
		bMapA = childA.getIsImmutable()[row];
		
		int arrRowA[] = new int[9];
		int arrRowB[] = new int[9];	
		
		arrRowA = mapA[row];
		arrRowB = mapB[row];		

		for (int j = lengthA; j < lengthB; j++)
		{
			if(!bMapA[j])
			{
				arrRowA[j] = arrRowA[j] ^ arrRowB[j];	
				arrRowB[j] = arrRowA[j] ^ arrRowB[j];	
				arrRowA[j] = arrRowA[j] ^ arrRowB[j];	
			}
		}
		
		mapA[row] = arrRowA;
		mapB[row] = arrRowB;
		
		childB.setSudokuBoardMap(mapB);
		childA.setSudokuBoardMap(mapA);
		
		childA.calculateFitness(solutionBoard);
		childB.calculateFitness(solutionBoard);
		
		sudokuPopGen[currentGen + 1][pos] = (childA.getFitness() > childB.getFitness() ? childA : childB);		
	}
	
	private void performPMXCrossover(int pos, SudokuBoard solutionBoard, SudokuBoard[][] sudokuPopGen, int currentGen, SecureRandom rand)
	{
		SudokuBoard parentA = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		SudokuBoard parentB = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);

		SudokuBoard child = new SudokuBoard(true, rand);
		child.setSudokuBoardMap(parentA.getSudokuBoardMap());
		child.setIsImmutable(parentA.getIsImmutable());
		
		int lengthA = rand.nextInt(9);
		int lengthB = rand.nextInt(9);
		
		if(lengthA > lengthB)
		{
			lengthA = lengthA ^ lengthB;
			lengthB = lengthA ^ lengthB;
			lengthA = lengthA ^ lengthB;
		}
		
		int row = rand.nextInt(9);
		
		int mapA[][] = new int[9][9];
		int mapB[][] = new int[9][9];
		int mapC[][] = new int[9][9];
		
		mapA = parentA.getSudokuBoardMap();
		mapB = parentB.getSudokuBoardMap();
		mapC = child.getSudokuBoardMap();
		
		boolean bMapA[] = new boolean[9];
		bMapA = child.getIsImmutable()[row];
		
		int arrRowA[] = new int[9];
		int arrRowB[] = new int[9];		
		int arrRowC[] = new int[9];
		
		arrRowA = mapA[row];
		arrRowB = mapB[row];		
		arrRowC = mapC[row];

		int itemA = 0;
		int itemB = 0;
		
		int posA = 0;
		int posB = 0;
		
        for(int i = lengthA; i <= lengthB; i++)
        {
        	itemA = arrRowA[i];
        	itemB = arrRowB[i];
            for(int k = 0; k < 9; k++)
            {
                if(arrRowA[k] == itemA)
                {
                	posA = k;
                }
                else if(arrRowA[k] == itemB)
                {
                	posB = k;
                }
            }

            if(itemA != itemB)
            {
    			if(!bMapA[i])
    			{
	            	arrRowC[posA] = itemB;
	            	arrRowC[posB] = itemA;
    			}
            }

        }
		mapC[row] = arrRowC;
		child.setSudokuBoardMap(mapC);
		child.calculateFitness(solutionBoard);
		
		sudokuPopGen[currentGen + 1][pos] = child;	
	}
	
	private void performUniformSwapCrossover(int pos, SudokuBoard solutionBoard, 
			SudokuBoard[][] sudokuPopGen, int currentGen, SecureRandom rand)
	{
		SudokuBoard parentA = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		SudokuBoard parentB = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);

		SudokuBoard child = new SudokuBoard(true, rand);
		child.setSudokuBoardMap(parentA.getSudokuBoardMap());
		child.setIsImmutable(parentA.getIsImmutable());
		
		int map[][] = new int[9][9];
		map = child.getSudokuBoardMap();
		
		boolean bMap[][] = new boolean[9][9];
		bMap = child.getIsImmutable();

		for (int j = 0; j < 9; j++)
		{	
			for (int k = 0; k < 9; k++)
			{
				if(!bMap[j][k])
				{
					if(rand.nextDouble() < 0.5)
					{
						map[j][k] = parentB.getSudokuBoardMap()[j][k];
					}
				}
			}
		}
		child.setSudokuBoardMap(map);
		child.calculateFitness(solutionBoard);
		
		sudokuPopGen[currentGen + 1][pos] = child;	
	}
	
	private void performCycleCrossover(int num_elements, SudokuBoard solutionBoard, SudokuBoard[][] sudokuPopGen, int currentGen, SecureRandom rand)
	{
		SudokuBoard parentA = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);
		SudokuBoard parentB = doRoulette(solutionBoard, sudokuPopGen, currentGen, rand);

		SudokuBoard childA = new SudokuBoard(parentA.getSudokuBoardMap());
		SudokuBoard childB = new SudokuBoard(parentB.getSudokuBoardMap());
		
		List<Integer> cycles = new Vector<Integer>();
		int cycle_1 = rand.nextInt(9);
		
		cycles.add(cycle_1);
		
		int row = rand.nextInt(9);
		
		int mapA[][] = new int[9][9];
		mapA = childA.getSudokuBoardMap();
		
		int mapB[][] = new int[9][9];
		mapB = childB.getSudokuBoardMap();
		
		int cycle_2 = mapB[row][cycle_1];
		cycle_1 = mapA[row][cycle_2];

	    while (cycle_1 != cycles.get(0)) 
	    {
	    	cycles.add(cycle_1);
	    	cycle_2 = mapB[row][cycle_1 - 1];
	    	cycle_1 = mapA[row][cycle_2 - 1];
	    }
	    
	    for (final int index : cycles) 
	    {
	    	mapA[row][index] = mapA[row][index] ^ mapB[row][index];	
	    	mapB[row][index] = mapA[row][index] ^ mapB[row][index];	
	    	mapA[row][index] = mapA[row][index] ^ mapB[row][index];
	    }
	}
}
