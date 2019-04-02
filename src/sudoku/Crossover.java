package sudoku;

import java.security.SecureRandom;
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
					performOnePointCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand);
				case 2:
					performTwoPointCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand);
				case 3:
					performPMXCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand);
				case 4:
					performUniformSwapCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand);
				case 5:
					performCycleCrossover(num_elements, solutionBoard, sudokuPopGen, currentGen, rand);
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

		SudokuBoard childA = parentA;
		SudokuBoard childB = parentB;
		
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

		SudokuBoard childA = parentA;
		SudokuBoard childB = parentB;
		
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

		SudokuBoard child = parentA;
		
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

		SudokuBoard child = parentA;
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
//       final int length = first.getLength();
//        if (length != second.getLength()) 
//        {
//            throw new DimensionMismatchException(second.getLength(), length);
//        }
//
//        // array representations of the parents
//        final List<T> parent1Rep = first.getRepresentation();
//        final List<T> parent2Rep = second.getRepresentation();
//        // and of the children: do a crossover copy to simplify the later processing
//        final List<T> child1Rep = new ArrayList<T>(second.getRepresentation());
//        final List<T> child2Rep = new ArrayList<T>(first.getRepresentation());
//
//        // the set of all visited indices so far
//        final Set<Integer> visitedIndices = new HashSet<Integer>(length);
//        // the indices of the current cycle
//        final List<Integer> indices = new ArrayList<Integer>(length);
//
//        // determine the starting index
//        int idx = randomStart ? GeneticAlgorithm.getRandomGenerator().nextInt(length) : 0;
//        int cycle = 1;
//
//        while (visitedIndices.size() < length) 
//        {
//            indices.add(idx);
//
//            T item = parent2Rep.get(idx);
//            idx = parent1Rep.indexOf(item);
//
//            while (idx != indices.get(0))
//            {
//                // add that index to the cycle indices
//                indices.add(idx);
//                // get the item in the second parent at that index
//                item = parent2Rep.get(idx);
//                // get the index of that item in the first parent
//                idx = parent1Rep.indexOf(item);
//            }
//
//            // for even cycles: swap the child elements on the indices found in this cycle
//            if (cycle++ % 2 != 0) 
//            {
//                for (int i : indices) 
//                {
//                    T tmp = child1Rep.get(i);
//                    child1Rep.set(i, child2Rep.get(i));
//                    child2Rep.set(i, tmp);
//                }
//            }
//
//            visitedIndices.addAll(indices);
//            // find next starting index: last one + 1 until we find an unvisited index
//            idx = (indices.get(0) + 1) % length;
//            while (visitedIndices.contains(idx) && visitedIndices.size() < length) 
//            {
//                idx++;
//                if (idx >= length) 
//                {
//                    idx = 0;
//                }
//            }
//            indices.clear();
//        }
	}
}
