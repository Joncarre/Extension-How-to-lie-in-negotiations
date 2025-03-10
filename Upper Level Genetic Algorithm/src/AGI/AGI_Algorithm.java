package AGI;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.ZipEntry;

public class AGI_Algorithm {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.1;
    private static final boolean elitism = true;
    private static final double value = 50;

    /**
     * Evolves population
     * @param popEvolved
     * @param popSize
     * @return
     */
    public static AGI_Population evolvePopulation(AGI_Population popEvolved, int popSize) {
        popEvolved.getFittest(); 
        
        // Is it elitism?
        int elitismOffset;
        if (elitism)
            elitismOffset = 1;
        else
            elitismOffset = 0;
        
        // Bubble Sort
    	AGI_Individual temp1 = new AGI_Individual();
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
        AGI_Population newPop = new AGI_Population(popSize);
        for(int i = 0; i < newPop.size(); i++){
        	//Individual indiv1 = popEvolved.getIndividual(random.nextInt((popSize-1) - 0) + 0); // Random selection of the old population
        	AGI_Individual indiv1 = popEvolved.getIndividual(indexes.getRandom()); // Weighted selection according to fitness value
        	AGI_Individual indiv2 = popEvolved.getIndividual(indexes.getRandom()); // Weighted selection according to fitness value
            AGI_Individual newIndiv = crossover(indiv1, indiv2);
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
    	AGI_Individual temp2 = new AGI_Individual();
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
    public static AGI_Individual copyIndiv(AGI_Individual fittest){
    	AGI_Individual best = new AGI_Individual();
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
    private static AGI_Individual crossover(AGI_Individual indiv1, AGI_Individual indiv2) {
        AGI_Individual newSol = new AGI_Individual();
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
    private static void mutate(AGI_Individual indiv) {
    	Random random = new Random();
    	int randomNumber = random.nextInt((indiv.size()-1) - 0) + 0;
        indiv.setGene(randomNumber, (1-indiv.getGene(randomNumber)));
    }
}
