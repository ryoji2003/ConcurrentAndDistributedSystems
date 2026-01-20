#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <omp.h>

bool is_prime(long long n) {
    if (n <= 1) return false;
    if (n <= 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;

    long long limit = (long long)sqrt((double)n) + 1;
    for (long long i = 5; i <= limit; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) {
            return false;
        }
    }
    return true;
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        printf("Usage: %s <A> <B>\n", argv[0]);
        printf("Finds all prime numbers in the interval [A, B]\n");
        printf("Example: %s 1 100\n", argv[0]);
        return 1;
    }

    long long A = atoll(argv[1]);
    long long B = atoll(argv[2]);

    if (A > B) {
        printf("Error: A must be <= B\n");
        return 1;
    }

    printf("Finding primes in interval [%lld, %lld]\n", A, B);
    printf("Using %d threads\n\n", omp_get_max_threads());

    double start_time = omp_get_wtime();

    long long count = 0;

    #pragma omp parallel for schedule(dynamic, 1000) reduction(+:count)
    for (long long n = A; n <= B; n++) {
        if (is_prime(n)) {
            #pragma omp critical
            {
                printf("%lld ", n);
            }
            count++;
        }
    }

    double end_time = omp_get_wtime();

    printf("\n\nTotal primes found: %lld\n", count);
    printf("Time elapsed: %.4f seconds\n", end_time - start_time);

    return 0;
}
