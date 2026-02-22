import java.util.Random;

public class MCSAlgorithms {

    // Counters for the number of times the "core" of each algorithm executes
    public static long countOn3;
    public static long countOn2A;
    public static long countOn2B;
    public static long countOn;

    /**
     * O(n^3) maximum contiguous subsequence algorithm.
     * Direct translation of the triple-nested-loop version.
     */
    public static int mcsOn3(int[] X) {
        int n = X.length;
        int maxSoFar = 0;
        countOn3 = 0;

        for (int low = 0; low < n; low++) {               // [0, n)
            for (int high = low; high < n; high++) {      // [low, n)
                int sum = 0;
                // Match the Python version: range(low, high) => [low, high)
                for (int r = low; r < high; r++) {        // [low, high)
                    sum += X[r];
                    // "Core" operation for O(n^3) algorithm
                    countOn3++;
                }
                if (sum > maxSoFar) {
                    maxSoFar = sum;
                }
            }
        }
        return maxSoFar;
    }

    /**
     * First O(n^2) algorithm: recomputes sums starting at each low index.
     */
    public static int mcsOn2A(int[] X) {
        int n = X.length;
        int maxSoFar = 0;
        countOn2A = 0;

        for (int low = 0; low < n; low++) {               // [0, n)
            int sum = 0;
            for (int r = low; r < n; r++) {               // [low, n)
                sum += X[r];
                // "Core" operation for first O(n^2) algorithm
                countOn2A++;
                if (sum > maxSoFar) {
                    maxSoFar = sum;
                }
            }
        }
        return maxSoFar;
    }

    /**
     * Second O(n^2) algorithm using prefix sums.
     */
    public static int mcsOn2B(int[] X) {
        int n = X.length;
        int[] sumTo = new int[n + 1];
        sumTo[0] = 0;

        // Build prefix sums: sumTo[i] is sum of X[0..i-1]
        for (int i = 1; i <= n; i++) {
            sumTo[i] = sumTo[i - 1] + X[i - 1];
        }

        int maxSoFar = 0;
        countOn2B = 0;

        for (int low = 0; low < n; low++) {               // [0, n)
            for (int high = low; high < n; high++) {      // [low, n)
                // Sum of X[low..high] using prefix sums
                int sum = sumTo[high + 1] - sumTo[low];
                // "Core" operation for second O(n^2) algorithm
                countOn2B++;
                if (sum > maxSoFar) {
                    maxSoFar = sum;
                }
            }
        }
        return maxSoFar;
    }

    /**
     * O(n) algorithm (Kadane's algorithm).
     */
    public static int mcsOn(int[] X) {
        int n = X.length;
        int maxSoFar = 0;
        int maxToHere = 0;
        countOn = 0;

        for (int i = 0; i < n; i++) {                     // [0, n)
            maxToHere = Math.max(maxToHere + X[i], 0);
            // "Core" operation for O(n) algorithm (one per element)
            countOn++;
            if (maxToHere > maxSoFar) {
                maxSoFar = maxToHere;
            }
        }
        return maxSoFar;
    }

    /**
     * Create an array X of length n where roughly one third of the
     * entries are negative, following the Python specification.
     *
     * Python version:
     *   X = [randint(1,n)*(-1)**randint(2,4) for k in range(n)]
     */
    public static int[] randomArrayWithOneThirdNegatives(int n, Random rng) {
        int[] X = new int[n];
        for (int k = 0; k < n; k++) {
            int value = rng.nextInt(n) + 1;   // 1..n
            int exponent = 2 + rng.nextInt(3); // 2, 3, or 4
            if (exponent % 2 == 1) {          // only 3 is odd
                value = -value;
            }
            X[k] = value;
        }
        return X;
    }

    /**
     * Run the four algorithms on a single n and print out the
     * counts in one row. Also checks that all algorithms agree
     * on the maximum sum.
     */
    public static void runSingleExperiment(int n, String label, Random rng) {
        int[] X = randomArrayWithOneThirdNegatives(n, rng);

        int max1 = mcsOn3(X);
        long c3 = countOn3;

        int max2a = mcsOn2A(X);
        long c2a = countOn2A;

        int max2b = mcsOn2B(X);
        long c2b = countOn2B;

        int maxLin = mcsOn(X);
        long c1 = countOn;

        // Sanity check: all algorithms should give the same maximum
        if (!(max1 == max2a && max2a == max2b && max2b == maxLin)) {
            System.out.println("Warning: algorithms disagree on result for n = " + n);
        }

        System.out.printf("%-8s %-8d %-8d %-8d %-8d%n",
                label, c3, c2a, c2b, c1);
    }


    public static void main(String[] args) {
        Random rng = new Random();
        System.out.printf("%-8s %-8s %-8s %-8s %-8s%n",
                "n", "O(n3)", "O(n2)", "O(n2)", "O(n)");

        // Use powers of 10 as in the PDF: 10^2, 10^3, ..., 10^6
        int[] exponents = {2, 3, 4, 5, 6};
        for (int exp : exponents) {
            int n = (int) Math.pow(10, exp);
            String label = "10^" + exp;
            runSingleExperiment(n, label, rng);
        }
    }
}
