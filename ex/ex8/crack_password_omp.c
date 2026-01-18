/*
 * Task 8.2: Crack Password v2 with OpenMP
 *
 * This program uses brute force with OpenMP parallelization to crack
 * passwords that are at most 5 digits (0-99999).
 *
 * For demonstration, we use a simple hash-based verification.
 * In real use, replace check_password() with actual decryption logic.
 *
 * Compile: gcc-15 -fopenmp -O2 -o crack_password_omp crack_password_omp.c -lcrypto
 * (Or without -lcrypto if using the simple demo hash)
 * Run: ./crack_password_omp
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <omp.h>

// For demonstration: target passwords to find
// These simulate the encrypted files' passwords
static const int TARGET_PASSWORDS[] = {12345, 54321, 99876};
static const char *TARGET_NAMES[] = {"ex02_longtail.enc", "ex02_pride.enc", "ex02_scarlet.enc"};
static const int NUM_TARGETS = 3;

// Simple password check function (replace with actual decryption in real use)
// Returns true if the password matches the target
bool check_password(int password, int target_password) {
    // In real implementation, this would call decryption function
    // and verify if decryption was successful
    return password == target_password;
}

// Crack a single password using OpenMP
int crack_single_password(int target_password, const char *filename) {
    volatile bool found = false;
    int result = -1;

    printf("Cracking password for: %s\n", filename);

    double start_time = omp_get_wtime();

    // Parallelize the outer loop (password range 0-99999)
    #pragma omp parallel for shared(found, result) schedule(dynamic, 1000)
    for (int password = 0; password <= 99999; password++) {
        // Early exit if password already found
        if (found) continue;

        if (check_password(password, target_password)) {
            #pragma omp critical
            {
                if (!found) {
                    found = true;
                    result = password;
                    printf(">>> SUCCESS! Password found: %05d (Thread %d)\n",
                           password, omp_get_thread_num());
                }
            }
        }
    }

    double end_time = omp_get_wtime();
    printf("Time elapsed: %.4f seconds\n\n", end_time - start_time);

    return result;
}

// Alternative approach using cancel directive (if supported)
int crack_single_password_with_cancel(int target_password, const char *filename) {
    int result = -1;

    printf("Cracking password for: %s\n", filename);

    double start_time = omp_get_wtime();

    #pragma omp parallel for shared(result) schedule(dynamic, 1000)
    for (int password = 0; password <= 99999; password++) {
        #pragma omp cancellation point for

        if (check_password(password, target_password)) {
            #pragma omp critical
            {
                if (result == -1) {
                    result = password;
                    printf(">>> SUCCESS! Password found: %05d (Thread %d)\n",
                           password, omp_get_thread_num());
                }
            }
            #pragma omp cancel for
        }
    }

    double end_time = omp_get_wtime();
    printf("Time elapsed: %.4f seconds\n\n", end_time - start_time);

    return result;
}

int main() {
    printf("OpenMP Password Cracker\n");
    printf("=======================\n");
    printf("Password range: 00000 - 99999 (5 digits)\n");
    printf("Number of threads: %d\n\n", omp_get_max_threads());

    int found_passwords[NUM_TARGETS];

    // Crack each password
    for (int i = 0; i < NUM_TARGETS; i++) {
        found_passwords[i] = crack_single_password(TARGET_PASSWORDS[i], TARGET_NAMES[i]);
    }

    // Summary
    printf("Summary of Found Passwords\n");
    printf("==========================\n");
    for (int i = 0; i < NUM_TARGETS; i++) {
        if (found_passwords[i] >= 0) {
            printf("%s: %05d\n", TARGET_NAMES[i], found_passwords[i]);
        } else {
            printf("%s: NOT FOUND\n", TARGET_NAMES[i]);
        }
    }

    return 0;
}
