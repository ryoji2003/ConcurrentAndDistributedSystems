# Task 8.1
1. Environment & Settings
CPU Threads: 14 threads
Array Size: 10,000,000 elements
Parallel Threshold: N/30 (333,333 elements)
Note: Recursion is parallelized only when the partition size is larger than this threshold.

2. Benchmark Results
Sequential Time: 0.4703 seconds
Parallel Time: 0.2330 seconds
Speedup: 2.02x

3. Discussion 

Threshold Strategy: Switching to sequential processing when the array size drops below N/30 effectively reduced the overhead associated with excessive thread creation.

Performance Analysis: Although 14 threads were available, the speedup was limited to about 2x. This is likely due to memory bandwidth bottlenecks and the inherently sequential nature of the partitioning step itself. However, the result clearly demonstrates the benefit of parallel execution for large datasets.