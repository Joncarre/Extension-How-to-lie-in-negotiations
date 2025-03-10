package AGS;

import java.awt.Image;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.print.attribute.standard.MediaSize.Engineering;

import AGI.AGI_Engine;
import AGI.AGI_Individual;
import files.CustomReadFile;
import files.CustomWriteFile;

public class AGS_FitnessCalc {
	private static Vector<Vector<Double>> O_preferences;
	private static Vector<Vector<Double>> M_preferences;
	static int numUsers;
	private int maxValuePreference;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
	
	/**
	 * Constructor
	 * @param numUser
	 * @param maxValuePreference
	 */
	public AGS_FitnessCalc(int _numUser) {
		this.numUsers = _numUser;
		this.O_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.M_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.maxValuePreference = 99;
	}
	
	/**
	 * Read O preferences for each user
	 * @throws IOException
	 */
    public void readPreferences_O() throws IOException {
        for (int i = 0; i < numUsers; i++) {
    		this.readFile = new CustomReadFile("agent" + i + ".txt");
        	this.sc = new Scanner(this.readFile);
        	Vector<Double> newPreference = this.readFile.readVector(sc);
        	// Formalizar las preferencias leidas
            O_preferences.add(i, formalizePreferences(newPreference));
        }
    }
    
    /**
     * Read M preferences for each user
     * @throws IOException
     */
    public void readPreferences_M() throws IOException {
        for (int i = 0; i < numUsers; i++) {
    		this.readFile = new CustomReadFile("agent" + i + ".txt");
        	this.sc = new Scanner(this.readFile);
        	Vector<Double> newPreference = this.readFile.readVector(sc);
        	// Formalizar las preferencias leidas
        	M_preferences.add(i, formalizePreferences(newPreference));
        }
    }
    
    /**
     * Write population on files
     * @param indiv
     * @throws IOException
     */
    public void writePopulation(AGS_Individual indiv) throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("final_indiv_" + i + ".txt");
			for(int j = 0; j < AGS_Individual.defaultGeneLength; j++)
				text += indiv.getGene(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }
    
    /**
     * Write M preference for each user
     * @throws IOException
     */
    public void writePreferences_M() throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("agent" + i + ".txt");
			for(int j = 0; j < AGS_Individual.defaultGeneLength; j++)
				text += M_preferences.get(i).get(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }

    /**
     * Calculate individuals fitness
     * @param individual
     * @return
     * @throws IOException 
     */
    public static double getFitness(AGI_Engine engine, AGS_Individual individual, int indiv_i) throws IOException {
        // We replace the i-th individual (remember that each individual in the EMS is a resource allocation of U_0)
    	M_preferences.set(0, individual.getGenes());
    	engine.setM_Preferences(M_preferences);
    	// The fitness is calculated before so that the first of all (when U_0 = R_0) is not modified
    	AGI_Individual bestAGI = engine.executeIGA(indiv_i, false);
    	individual.setFitness(fitnessAgent0(bestAGI.getGenes())); // Save the AGI fittest but calculated with the actual preferences of Agent 0
    	individual.setFitnessAGI(bestAGI.getFitness()); // Save the AGI fittest
    	individual.setSolution(bestAGI.getGenes());
    	return individual.getOnlyFitness();
    } 
    
    /**
     * Get number of users
     * @return
     */
    public int getNumUsers() {
    	return this.numUsers;
    }
    
	/**
	 * Used to get preferences to send it to AGI
     * @return
     */
    public Vector<Vector<Double>> getM_Preferences(){
    	return this.M_preferences;
    }
    
	/**
	 * Used to get preferences to send it to AGI
     * @return
     */
    public Vector<Vector<Double>> getO_Preferences(){
    	return this.O_preferences;
    }
   
    /**
     * Normalizes the vector by making the absolute value equal to 1000
     */
    public Vector<Double> formalizePreferences(Vector<Double> preference) {
    	double aux = 0;
    	for(int i = 0; i < preference.size(); i++)
    		aux += Math.abs(preference.get(i));
    	aux = 1000/aux;
    	for(int j = 0; j < preference.size(); j++)
    		preference.set(j, preference.get(j)*aux);
    	return preference;
    }
    
    /**
     * Method to add the fitness of agent 0 to the file result_solution.txt
     * @param solution
     * @return
     */
    public static double fitnessAgent0(int[] solution) {
    	double fitness = 0.0;
    	for(int i = 0; i < solution.length; i++) {
    		fitness += (solution[i]*O_preferences.get(0).get(i));
    	}
    	return fitness;
    }
    
    /**
     * Method to add the fitness of agent 0 to the file result_solution.txt
     * @param solution
     * @return
     */
    public double fitnessAgent0_M(int[] solution, AGS_Individual individual) {
    	double fitness = 0.0;
    	for(int i = 0; i < solution.length; i++) {
    		fitness += (solution[i]*individual.getGene(i));
    	}
    	return fitness;
    }
}
