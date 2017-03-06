import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] percolationThresholds;
    private int trials;

    /**
     * Constructor for a PercolationStats object. Creates an NxN grid with all
     * sites blocked for the specified gridSize and runs a number of trials to
     * test if the grid will percolate.
     * 
     * @param gridSize The size of the grid to create.
     * @param trials The number of trials to run.
     * @throws IllegalArgumentException
     */
    public PercolationStats(int gridSize, int trials) {
        if (gridSize < 1 || trials < 1) {
            throw new IllegalArgumentException(
                    "Both the grid size and number of trials must be greater" + " than 1.");
        }
        this.trials = trials;
        percolationThresholds = new double[this.trials];

        for (int trial = 0; trial < this.trials; trial++) {
            Percolation percolation = new Percolation(gridSize);
            while (!percolation.percolates()) {
                percolation.open(StdRandom.uniform(gridSize) + 1, StdRandom.uniform(gridSize) + 1);
            }
            percolationThresholds[trial] = percolation.numberOfOpenSites()
                    / (double) (gridSize * gridSize);
        }
    }

    /**
     * Calculates the sample mean of the percolation thresholds from the trials.
     * 
     * @return double The mean value of the thresholds.
     */
    public double mean() {
        return StdStats.mean(this.percolationThresholds);
    }

    /**
     * Calculates the standard deviation of percolation threshold from the
     * trials that are run.
     * 
     * @return double The standard deviation of the threshold.
     */
    public double stddev() {
        return StdStats.stddev(this.percolationThresholds);
    }

    /**
     * Returns the low end point of the 95% confidence interval for percolation
     * to happen.
     * 
     * @return double The low end point value.
     */
    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(this.trials));
    }

    /**
     * Returns the high end point of the 95% confidence interval for percolation
     * to happen.
     * 
     * @return double The high end point value.
     */
    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(this.trials));
    }

    /**
     * Reads in two parameters from standard input. One for the size of the grid
     * and one for the number of trials to run.
     *
     * @param args The command-line arguments.
     * @throws NumberFormatException If the given values are not valid integers.
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            PercolationStats percolationStats = new PercolationStats(Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]));
            System.out.println("mean                    = " + percolationStats.mean());
            System.out.println("stddev                  = " + percolationStats.stddev());
            System.out.println("95% confidence interval = [" + percolationStats.confidenceLo()
                    + ", " + percolationStats.confidenceHi() + "]");
        } else {
            System.out.println("Must pass in grid size and number of trials as arguments.");
        }
    }
}
