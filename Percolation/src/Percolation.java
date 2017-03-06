import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean topConnected;
    private boolean bottomConnected;
    private boolean percolated;
    private int gridSideLength;
    private int openSitesCount;
    private int[] siteStatus;
    private WeightedQuickUnionUF grid;
 
    /**
     * Constructor for a Percolation object. Creates an NxN grid with all sites
     * blocked.
     * 
     * @param n The size of the grid to create.
     * @throws IllegalArgumentException
     */
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("The grid size cannot be less than one.");
        }
        this.gridSideLength = n;
        // Add 1 extra element to the NxN grid for a virtual top node at index 0.
        this.grid = new WeightedQuickUnionUF(n * n + 1);
        // Keep track of each sites's status to stop "backwash" from happening.
        // 0 - Blocked, 1 - Open, 2 - Top connected & open, 3 - Bottom connected & open,
        // 4 - Top connected & Bottom connected & open.
        this.siteStatus = new int[n * n + 1];
        // Always set the virtual top to open so that it never gets perceived as blocked when
        // calculating a path of percolation when a site is opened.
        this.siteStatus[0] = 2;
        this.openSitesCount = 0;
        this.percolated = false;
    }

    /**
     * Sets a blocked site to open if it is not open already.
     * 
     * @param row The row number in the grid of the site.
     * @param col The column number in the grid of the site.
     * @throws IndexOutOfBoundsException
     */
    public void open(int row, int col) {
        checkRowColumnForOutOfBounds(row, col);
//        System.out.println("Process open row: " + row + " col: " + col);

        // If the site is already open, just return early from the method
        // because there is nothing to do.
        int tempIndex = (row - 1) * this.gridSideLength + col;
        if (this.siteStatus[tempIndex] > 0) {
            return;
        }
 
        // Increment by one since a site is being opened.
        this.openSitesCount++;
        
        // Always set the bottom and top connected values to false to ensure that a path of
        // percolation can be checked when a site is opened. Only if both of these values get set
        // to true will the value of percolation be set to true.
        this.bottomConnected = false;
        this.topConnected = false;

        // Always connect any open sites in the top row of the grid with the virtual top site.
        if (row == 1) {
            this.grid.union(col, 0);
            this.topConnected = true;
        }
 
        // Always set any opened sites in the bottom row as connected to the bottom of the grid. Do
        // not use union on the current site or you risk backwash happening.
        if (row == this.gridSideLength) {
            this.bottomConnected = true;
        }

        // Check the site above the current one in the grid.
        if (row - 1 > 0 && this.siteStatus[tempIndex - this.gridSideLength] > 0) {
            this.checkSiteStatus(this.siteStatus[tempIndex - this.gridSideLength]);
            this.grid.union((row - 1) * this.gridSideLength + col,
                    (row - 2) * this.gridSideLength + col);
        }

        // Check the site below the current one in the grid.
        if (row + 1 <= this.gridSideLength &&
                this.siteStatus[tempIndex + this.gridSideLength] > 0) {
            this.checkSiteStatus(this.siteStatus[tempIndex + this.gridSideLength]);
            this.grid.union((row - 1) * this.gridSideLength + col, row * this.gridSideLength + col);
        }

        // Check the site to the left of the current one in the grid.
        if (col - 1 > 0 && this.siteStatus[tempIndex - 1] > 0) {
            this.checkSiteStatus(this.siteStatus[tempIndex - 1]);
            this.grid.union((row - 1) * this.gridSideLength + col,
                    (row - 1) * this.gridSideLength + col - 1);
        }

        // Check the site to the right of the current one in the grid.
        if (col + 1 <= this.gridSideLength && this.siteStatus[tempIndex + 1] > 0) {
             this.checkSiteStatus(this.siteStatus[tempIndex + 1]);
             this.grid.union((row - 1) * this.gridSideLength + col,
                     (row - 1) * this.gridSideLength + col + 1);
         }

        // Check the root value of the site in the siteStatus array and update the booleans for top
        // connected and bottom connected if the root has any matches to the top row or bottom row.
        int rootIndex = this.grid.find(tempIndex);
        this.checkSiteStatus(this.siteStatus[rootIndex]);

        // Actually set the value of the site and the root of the site in the siteStatus array to
        // the value obtained from the above logic.
        int status;
        if (this.topConnected && this.bottomConnected) {
            status = 4;
            // If the site is both top connected and bottom connected, set the value of percolation
            // to true.
            this.percolated = true;
        } else if (this.bottomConnected) {
            status = 3;
        } else if (this.topConnected) {
            status = 2;
        } else {
            status = 1;
        }
        this.siteStatus[tempIndex] = status;
        this.siteStatus[rootIndex] = status;
    }

    /**
     * Checks if the given site is open.
     * 
     * @param row The row number in the grid of the site.
     * @param col The column number in the grid of the site.
     * @return boolean True if open, false otherwise.
     * @throws IndexOutOfBoundsException
     */
    public boolean isOpen(int row, int col) {
        checkRowColumnForOutOfBounds(row, col);
        return this.siteStatus[(row - 1) * this.gridSideLength + col] > 0;
    }

    /**
     * Checks if the given site is full. A full site is an open site that can be
     * connected to an open site in the top row via a chain of neighboring
     * (left, right, up, down) open sites.
     * 
     * @param row The row number in the grid of the site.
     * @param col The column number in the grid of the site.
     * @return boolean True if full, false otherwise.
     * @throws IndexOutOfBoundsException
     */
    public boolean isFull(int row, int col) {
        checkRowColumnForOutOfBounds(row, col);
        // There is no need to check if the current site is open, because a
        // blocked site would not have any connection with the virtual top.
        return this.grid.connected(0, (row - 1) * this.gridSideLength + col);
    }

    /**
     * Calculates and returns the number of open sites in the grid.
     * 
     * @return int The number of open sites in the grid.
     */
    public int numberOfOpenSites() {
        return this.openSitesCount;
    }

    /**
     * Returns whether the system percolates or not.
     * 
     * @return boolean True if it does percolate, false otherwise.
     */
    public boolean percolates() {
        return this.percolated;
    }

    /**
     * If the row or column is not a valid value for the current grid, throw an
     * exception, otherwise do nothing as the values would be valid then.
     * 
     * @param row The row number in the grid of the site.
     * @param col The column number in the grid of the site.
     * @throws IndexOutOfBoundsException
     */
    private void checkRowColumnForOutOfBounds(int row, int col) {
        if (row < 1 || row > this.gridSideLength || col < 1 || col > this.gridSideLength) {
            throw new IndexOutOfBoundsException();
        }
    }
 
    /**
     * Checks if the status of the site is 2, meaning it is connected to the top, or 3 meaning it is
     * connected to the bottom. This will be used later to find if a site has a 2 and a 3 in its
     * neighboring sites to find a path of percolation.
     * @param siteStatus An integer representing the status of the site to check.
     */
    private void checkSiteStatus(int siteStatus) {
        if (siteStatus == 2) {
            this.topConnected = true;
        } else if (siteStatus == 3) {
            this.bottomConnected = true;
        } else if (siteStatus == 4) {
            this.topConnected = true;
            this.bottomConnected = true;
        }
    }

    /**
     * Main method to test client.
     * 
     * @param args The command line arguments to pass to the main function.
     */
    public static void main(String[] args) {
        System.out.println("IN MAIN!");
        Percolation percolation = new Percolation(5);
        percolation.open(1, 1);
        percolation.open(2, 1);
        percolation.open(2, 2);
        percolation.open(3, 2);
        percolation.open(4, 2);
        percolation.open(4, 3);
        percolation.open(5, 5);
        percolation.open(4, 4);
        percolation.open(4, 5);
        // Should have 9 open sites and return true for percolation.
        System.out.println(percolation.numberOfOpenSites());
        System.out.println(percolation.percolates());
    }

}