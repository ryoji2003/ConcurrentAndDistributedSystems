#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <omp.h>

#define ARRAY_SIZE 10000000  

static int N; 

void swap(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

int partition(int *A, int lo, int hi) {
    int pivot = A[hi];
    int i = lo;
    for (int j = lo; j < hi; j++) {
        if (A[j] < pivot) {
            swap(&A[i], &A[j]);
            i++;
        }
    }
    swap(&A[i], &A[hi]);
    return i;
}

void quicksort_sequential(int *A, int lo, int hi) {
    if (lo < hi) {
        int p = partition(A, lo, hi);
        quicksort_sequential(A, lo, p - 1);
        quicksort_sequential(A, p + 1, hi);
    }
}

void quicksort_parallel(int *A, int lo, int hi) {
    if (lo < hi) {
        int p = partition(A, lo, hi);

        int left_size = p - lo;
        int right_size = hi - p;
        int threshold = N / 30;

        if (left_size >= threshold && right_size >= threshold) {
            #pragma omp parallel sections
            {
                #pragma omp section
                {
                    quicksort_parallel(A, lo, p - 1);
                }
                #pragma omp section
                {
                    quicksort_parallel(A, p + 1, hi);
                }
            }
        } else {
            quicksort_sequential(A, lo, p - 1);
            quicksort_sequential(A, p + 1, hi);
        }
    }
}

int is_sorted(int *A, int n) {
    for (int i = 1; i < n; i++) {
        if (A[i] < A[i - 1]) {
            return 0;
        }
    }
    return 1;
}

void fill_random(int *A, int n) {
    for (int i = 0; i < n; i++) {
        A[i] = rand() % 1000000;
    }
}

int main() {
    N = ARRAY_SIZE;

    int *arr_seq = (int *)malloc(N * sizeof(int));
    int *arr_par = (int *)malloc(N * sizeof(int));

    if (!arr_seq || !arr_par) {
        fprintf(stderr, "Memory allocation failed!\n");
        return 1;
    }

    srand(42); 

    fill_random(arr_seq, N);
    memcpy(arr_par, arr_seq, N * sizeof(int));  

    printf("OpenMP Quicksort Benchmark\n");
    printf("Array size: %d elements\n", N);
    printf("Parallelization threshold: N/30 = %d elements\n", N / 30);
    printf("Number of available threads: %d\n\n", omp_get_max_threads());

    double start_seq = omp_get_wtime();
    quicksort_sequential(arr_seq, 0, N - 1);
    double end_seq = omp_get_wtime();
    double time_seq = end_seq - start_seq;

    if (!is_sorted(arr_seq, N)) {
        fprintf(stderr, "ERROR: Sequential sort failed!\n");
        return 1;
    }
    printf("Sequential time: %.4f seconds\n", time_seq);
    printf("Verified: Array is correctly sorted.\n\n");

    double start_par = omp_get_wtime();

    omp_set_nested(1);
    omp_set_max_active_levels(4); 

    quicksort_parallel(arr_par, 0, N - 1);
    double end_par = omp_get_wtime();
    double time_par = end_par - start_par;

    if (!is_sorted(arr_par, N)) {
        fprintf(stderr, "ERROR: Parallel sort failed!\n");
        return 1;
    }
    printf("Parallel time: %.4f seconds\n", time_par);
    printf("Verified: Array is correctly sorted.\n\n");

    double speedup = time_seq / time_par;
    printf("Results Summary\n");
    printf("Sequential time: %.4f seconds\n", time_seq);
    printf("Parallel time:   %.4f seconds\n", time_par);
    printf("Speedup:         %.2fx\n", speedup);

    free(arr_seq);
    free(arr_par);

    return 0;
}
