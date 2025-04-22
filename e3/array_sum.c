#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(int argc, char *argv[]) {
    if (argc != 3) {
        printf("Usage: %s <array_size> <num_threads>\n", argv[0]);
        return 1;
    }

    int N = atoi(argv[1]);  // Total array size
    int num_threads = atoi(argv[2]);  // Number of threads/processors

    // Allocate and initialize array with random values
    int *arr = (int *)malloc(N * sizeof(int));
    for (int i = 0; i < N; i++) {
        arr[i] = rand() % 100;  // Random values between 0 and 99
    }

    // Print original array
    printf("Original array: ");
    for (int i = 0; i < N; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n\n");

    // Set number of threads
    omp_set_num_threads(num_threads);

    // Calculate elements per thread
    int elements_per_thread = N / num_threads;
    int total_sum = 0;

    // Parallel region for sum calculation
    #pragma omp parallel
    {
        int thread_id = omp_get_thread_num();
        int start = thread_id * elements_per_thread;
        int end = (thread_id == num_threads - 1) ? N : start + elements_per_thread;
        
        int local_sum = 0;
        
        // Calculate local sum
        for (int i = start; i < end; i++) {
            local_sum += arr[i];
        }

        // Critical section to print intermediate sums and update total
        #pragma omp critical
        {
            printf("Thread %d: Sum of elements %d to %d = %d\n", 
                   thread_id, start, end-1, local_sum);
            total_sum += local_sum;
        }
    }

    printf("\nFinal sum: %d\n", total_sum);

    // Verify with sequential sum
    int sequential_sum = 0;
    for (int i = 0; i < N; i++) {
        sequential_sum += arr[i];
    }
    printf("Sequential sum (verification): %d\n", sequential_sum);

    free(arr);
    return 0;
} 