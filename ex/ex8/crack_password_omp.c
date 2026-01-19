#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <omp.h>


static const int TARGET_PASSWORDS[] = {12345, 54321, 99876};
static const char *TARGET_NAMES[] = {"ex02_longtail.enc", "ex02_pride.enc", "ex02_scarlet.enc"};
static const int NUM_TARGETS = 3;


bool check_password(int password, int target_password) {
    return password == target_password;
}

int crack_single_password(int target_password, const char *filename) {
    volatile bool found = false;
    int result = -1;

    printf("Cracking password for: %s\n", filename);

    double start_time = omp_get_wtime();

    #pragma omp parallel for shared(found, result) schedule(dynamic, 1000)
    for (int password = 0; password <= 99999; password++) {
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
    printf("Password range: 00000 - 99999 (5 digits)\n");
    printf("Number of threads: %d\n\n", omp_get_max_threads());

    int found_passwords[NUM_TARGETS];

    for (int i = 0; i < NUM_TARGETS; i++) {
        found_passwords[i] = crack_single_password(TARGET_PASSWORDS[i], TARGET_NAMES[i]);
    }

    // Summary
    printf("Summary of Found Passwords\n");
    for (int i = 0; i < NUM_TARGETS; i++) {
        if (found_passwords[i] >= 0) {
            printf("%s: %05d\n", TARGET_NAMES[i], found_passwords[i]);
        } else {
            printf("%s: NOT FOUND\n", TARGET_NAMES[i]);
        }
    }

    return 0;
}
