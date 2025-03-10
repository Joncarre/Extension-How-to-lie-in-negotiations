package AGS;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.print.attribute.Size2DSyntax;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import AGI.AGI_Engine;
import AGI.AGI_Individual;
import AGI.AGI_Population;
import AGI.WeightedRandomSelect;

public class AGS_Algorithm {
    private static final double uniformRate = 0.5;
    private static final double uniformRateCrossover = 0.05;
    private static final double mutationRate = 0.1;
    private static final boolean elitism = true;
    private static final double value = 50;

    /**
     * Evolve Population
     * @param pop
     * @return
     * @throws IOException 
     */
    public static AGS_Population evolvePopulation(AGS_Population popEvolved, AGI_Engine engine, int popSize, int generation) throws IOException {
    	popEvolved.getFittest(engine, true);
        
        //System.out.println("Indiv: 		  " + popEvolved.getIndividual(0).toString() + "  |   " + popEvolved.getIndividual(0).getSolution());

        int elitismOffset;
        if (elitism)
            elitismOffset = 1;
        else
            elitismOffset = 0;
  
        // Bubble Sort
    	AGS_Individual temp1 = new AGS_Individual();
        for(int i = 0; i < popSize-1; i++){
        	for(int j = 0; j < (popSize-i-1); j++) {
        		if(popEvolved.getIndividual(j+1).getOnlyFitness() > popEvolved.getIndividual(j).getOnlyFitness()) {
        		    temp1 = copyIndiv(popEvolved.getIndividual(j));
        			popEvolved.sustituteIndividual(j, copyIndiv(popEvolved.getIndividual(j+1)));
        			popEvolved.sustituteIndividual(j+1, temp1);
        		}
        	}
        }
        /*
        if(true) {
            System.out.println("Población de padres con fitness calculado y ordenado");
            for(int i = 0; i < popSize; i++){
            	System.out.println("Ite: " + i + " Fitness: " + popEvolved.getIndividual(i).getOnlyFitness() + " ¦ Genes: " + popEvolved.getIndividual(i).toString());
            }
            System.out.println();
        }
   		*/
        // Almacenar índices y probabilidades asociadas
    	WeightedRandomSelect<String> indexes = new WeightedRandomSelect<>();
        double newValue = value;
        for(int i = 0; i < popSize; i++){
        	newValue = newValue-(newValue*0.05);
         	indexes.addEntry(i,  newValue);
        }
      
        // Construir la nueva población ya cruzada
    	Random random = new Random();
        AGS_Population newPop = new AGS_Population(popSize);
    	AGS_Individual indiv1 = new AGS_Individual();
    	AGS_Individual indiv2 = new AGS_Individual();
        if(elitism) {
        	newPop.saveIndividual(0, copyIndiv(popEvolved.getIndividual(0)));
        }
        for(int i = elitismOffset; i < popEvolved.size(); i++){
        	indiv1 = copyIndiv(popEvolved.getIndividual(indexes.getRandom())); // Selección ponderada según valor fitness
        	indiv2 = copyIndiv(popEvolved.getIndividual(indexes.getRandom())); // Selección ponderada según valor fitness
        	AGS_Individual newIndiv = crossover(indiv1, indiv2);
        	newPop.saveIndividual(i, newIndiv);
        }
        
        // Mutate population
        for (int i = elitismOffset; i < newPop.size(); i++) {
        	if(Math.random() <= mutationRate) {
        		mutate(newPop.getIndividual(i));
        	}
        }

        // Calcular valores fitness de la población cruzada
        newPop.getFittest(engine, false); 
        
        // Bubble Sort
    	AGS_Individual temp2 = new AGS_Individual();
        for(int i = 0; i < newPop.size()-1; i++){
        	for(int j = 0; j < (newPop.size()-i-1); j++) {
        		if(newPop.getIndividual(j+1).getOnlyFitness() > newPop.getIndividual(j).getOnlyFitness()) {
        			temp2 = copyIndiv(newPop.getIndividual(j));
        			newPop.sustituteIndividual(j, copyIndiv(newPop.getIndividual(j+1)));
        			newPop.sustituteIndividual(j+1, temp2);
        		}
        	}
        }
        /*
        if(true) {
            System.out.println("Población de hijos con fitness calculado y ordenado");
            for(int i = 0; i < newPop.size(); i++){
            	System.out.println("Ite: " + i + " Fitness: " + newPop.getIndividual(i).getOnlyFitness() + " ¦ Genes: " + newPop.getIndividual(i).toString());
            }
            System.out.println();
        }
        */
        return newPop;
    }
    
    /**
     * 
     * @param fittest
     * @return
     */
    public static AGS_Individual copyIndiv(AGS_Individual fittest){
    	AGS_Individual best = new AGS_Individual();
        best.setFitness(fittest.getOnlyFitness());
        int[] solution = fittest.getSolution();
        best.setSolution(solution);
        for(int i = 0; i < fittest.size(); i++)
        	best.setGene(i, fittest.getGene(i));
    	return best;
    }

    /**
     * Crossover individuals
     * @param indiv1
     * @param indiv2
     * @return
     */
    private static AGS_Individual crossover(AGS_Individual indiv1, AGS_Individual indiv2) {
    	Random random = new Random();
    	int randomNumber = random.nextInt((indiv1.size()-1) - 0) + 0;
        AGS_Individual newSol = new AGS_Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
        	if(Math.random() <= uniformRateCrossover) {
                if (Math.random() <= uniformRate)
                    newSol.setGene(i, indiv1.getGene(randomNumber));
                else
                    newSol.setGene(i, indiv2.getGene(randomNumber));
            	randomNumber = random.nextInt((indiv1.size()-1) - 0) + 0;  
        	}else {
                if (Math.random() <= uniformRate)
                    newSol.setGene(i, indiv1.getGene(i));
                else
                    newSol.setGene(i, indiv2.getGene(i));
        	}
        }
        newSol.formalizePreferences();
        return newSol;
    }
    
    /**
     * Mutate an individual
     * @param indiv
     */
    private static void mutate(AGS_Individual indiv) {
    	Random random = new Random();
        int randomGene = random.nextInt((indiv.size()-1) - 0) + 0;
        Double gene;
        gene = aproxValue(indiv.getGene(randomGene));
        indiv.changeGene(randomGene, gene);

        indiv.formalizePreferences();
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public static double aproxValue(double value) {
    	double variation;
    	double oldValue = value;
    	do {
        	Random r = new Random();
        	variation = (r.nextInt(2)==0?-1:1)*20*r.nextDouble(); // random interval (-20, 20)
        	value = oldValue + variation;
		} while (value < -999 || value > 1000);
    	return value;
    }
}