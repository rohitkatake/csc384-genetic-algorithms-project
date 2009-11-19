package ga_testbench;

import ScheduleProblem.Schedule;

/**
 *
 * @param <T> The class representing a Problem instance that can be solved.
 * @author dave
 */
public class GASolver<T extends Individual> implements Solver {

    private String statsStringBest = "";
    private String statsStringMean = "";
    private Population<T> population;
    private final GAParams params;

    public GASolver(GAParams params) {
        this.params = params;
    }

    /**
     * Add a line of data to the verbose report.
     */
    private void addReport() {
        double best = population.getBest().fitness();
        double avg = population.fitnessAverage();
        statsStringBest += numEvaluations() + ", " + best + "\n";
        statsStringMean += numEvaluations() + ", " + avg + "\n";
    }

    /**
     * Close files and do whatever is necessary to complete the stats gathering.
     */
    private void verboseTidyUp() {
        // Nothing to do here. No files are used
    }

    /**
     * Get the stats for this run
     * @return The statistics generated by this GA run
     */
    public String getBestStatsString() {
        return statsStringBest + "END-SERIES";
    }

    public String getMeanStatsString() {
        return statsStringMean + "END-SERIES";
    }

    /**
     * Run the genetic algorithm with the current settings.
     * @return
     */
    public T run() {
        boolean loaded = Schedule.isInitialized();
        if (!loaded) {
            // Load the desired problem instance
            loaded = Schedule.initialize(params.getInstanceDataFile());
        }

        if (!loaded) {
            throw new RuntimeException("Problem instance couldn't be loaded.");
        }
        statsStringBest = "Genetic Algorithm with parameters " + params.proportionString() + "\n";
        statsStringMean = "Genetic Algorithm Population Mean" + "\n";

        population = new GAPopulation(params);

        // Evolve!!!
        addReport();
        while (evolutionNotDone()) {
            population.evolve();
            if (params.isVerbose()) {
                addReport();
            }
        }

        // This is pointless since we didn't end up using files here
        if (params.isVerbose()) {
            verboseTidyUp();
        }
        return population.getBest();
    }

    /**
     * Check if we're done evolving the population yet.
     * @return Boolean representing whether we can stop evolving.
     */
    private boolean evolutionNotDone() {
        if (params.getMaxGenerations() != -1 &&
                population.currentGenerationNumber() >= params.getMaxGenerations()) {
            return false;
        }
        return true;

    }

    /**
     * Get the number of fitness evaluations that have been performed since
     * this Solver was created.
     * @return The number of evaluations that have been performed
     */
    public int numEvaluations() {
        return population.numEvaluations();
    }
}
