package main;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.ZipEntry;

public class Algorithm {

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.2;
    private static final boolean elitism = true;
    private static final double value = 50;

    /**
     * Evolves population
     * @param popEvolved
     * @param popSize
     * @return
     */
    public static Population evolvePopulation(Population popEvolved, int popSize, int generationCount) {
        popEvolved.getFittest(); 
        
        // Is it elitism?
        int elitismOffset;
        if (elitism)
            elitismOffset = 1;
        else
            elitismOffset = 0;
        
        // Bubble Sort
    	Individual temp1 = new Individual();
        for(int i = 0; i < popSize-1; i++){
        	for(int j = 0; j < (popSize-i-1); j++) {
        		if(popEvolved.getIndividual(j+1).getOnlyFitness() > popEvolved.getIndividual(j).getOnlyFitness()) {
        			temp1 = popEvolved.getIndividual(j);
        			popEvolved.saveIndividual(j, popEvolved.getIndividual(j+1));
        			popEvolved.saveIndividual(j+1, temp1);
        		}
        	}
        }

        // Store indexes and associated probabilities
    	WeightedRandomSelect<String> indexes = new WeightedRandomSelect<>();
        double newValue = value;
        for(int i = 0; i < popSize; i++){
        	newValue = newValue-(newValue*0.05);
         	indexes.addEntry(i,  newValue);
        }
      
        // Build the new population already crossed
    	Random random = new Random();
        Population newPop = new Population(popSize);
        for(int i = 0; i < newPop.size(); i++){
        	//Individual indiv1 = popEvolved.getIndividual(random.nextInt((popSize-1) - 0) + 0); // Random selection of the old population
        	Individual indiv1 = popEvolved.getIndividual(indexes.getRandom()); // Weighted selection according to fitness value
        	Individual indiv2 = popEvolved.getIndividual(indexes.getRandom()); // Weighted selection according to fitness value
            Individual newIndiv = crossover(indiv1, indiv2);
        	newPop.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPop.size(); i++) {
        	if(Math.random() <= mutationRate) {
        		mutate(newPop.getIndividual(i));
        	}
        }
        
        // Calculate cross-population fitness values
        newPop.getFittest(); 
        
        if(elitism) {
            if(popEvolved.getIndividual(0).getOnlyFitness() > newPop.getIndividual(0).getOnlyFitness())
            	newPop.saveIndividual(0, popEvolved.getIndividual(0));
        }
        
        // Bubble Sort
    	Individual temp2 = new Individual();
        for(int i = 0; i < newPop.size()-1; i++){
        	for(int j = 0; j < (newPop.size()-i-1); j++) {
        		if(newPop.getIndividual(j+1).getOnlyFitness() > newPop.getIndividual(j).getOnlyFitness()) {
        			temp2 = newPop.getIndividual(j);
        			newPop.saveIndividual(j, newPop.getIndividual(j+1));
        			newPop.saveIndividual(j+1, temp2);
        		}
        	}
        }
        return newPop;
    }
    
    /**
     * It mades the copy of an individual
     * @param fittest
     * @return
     */
    public static Individual copyIndiv(Individual fittest){
    	Individual best = new Individual();
        best.setFitness(fittest.getOnlyFitness());
        for(int i = 0; i < fittest.size(); i++)
        	best.setGeneCopy(i, fittest.getGene(i));
    	return best;
    }

    /**
     * Crossover individuals
     * @param indiv1
     * @param indiv2
     * @return
     */
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate)
                newSol.setGene(i, indiv1.getGene(i));
            else
                newSol.setGene(i, indiv2.getGene(i));
        }
        return newSol;
    }

    /**
     * Mutate an individual
     * @param indiv
     */
    private static void mutate(Individual indiv) {
    	Random random = new Random();
    	int randomNumber = random.nextInt((indiv.size()-1) - 0) + 0;
        indiv.setGene(randomNumber, (1-indiv.getGene(randomNumber)));
    }
}
