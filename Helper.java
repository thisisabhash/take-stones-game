public class Helper {

    /**
     * Class constructor.
     */
    private Helper() {
    }

    /**
     * This method is used to check if a number is prime or not
     *
     * @param x A positive integer number
     * @return boolean True if x is prime; Otherwise, false
     */
    public static boolean isPrime(int x) {
        if (x <= 3) {
            return x > 1;
        } else if (x % 2 == 0 || x % 3 == 0) {
            return false;
        }

        for (int i = 5; i * i <= x; i = i + 6) {
            if (x % i == 0 || x % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method is used to get the largest prime factor
     *
     * @param x A positive integer number
     * @return int The largest prime factor of x
     */
    public static int getLargestPrimeFactor(int x) {

        int maxPrime = -1;

        // remove all powers of 2
        while (x % 2 == 0) {
            maxPrime = 2;
            x >>= 1;
        }

        // iterate till square root of x and keep on dividing all the odd multiples
        for (int i = 3; i*i <= x; i += 2) {
            while (x % i == 0) {
                maxPrime = i;
                x = x / i;
            }
        }

        // if x itself is prime, return it
        if (x > 2) {
            maxPrime = x;
        }

        return maxPrime;
    }
}