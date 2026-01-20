# Makefile for Exercise 8 - OpenMP Programs
#
# On macOS, use Homebrew's GCC for OpenMP support
# Adjust GCC path if needed

GCC = /opt/homebrew/opt/gcc/bin/gcc-15
CFLAGS = -fopenmp -O2
LDFLAGS = -lm

all: quicksort_omp crack_password_omp find_primes_omp game_of_life_omp

quicksort_omp: quicksort_omp.c
	$(GCC) $(CFLAGS) -o $@ $<

crack_password_omp: crack_password_omp.c
	$(GCC) $(CFLAGS) -o $@ $<

find_primes_omp: find_primes_omp.c
	$(GCC) $(CFLAGS) -o $@ $< $(LDFLAGS)

game_of_life_omp: game_of_life_omp.c
	$(GCC) $(CFLAGS) -o $@ $<

clean:
	rm -f quicksort_omp crack_password_omp find_primes_omp game_of_life_omp

test: all
	@echo "=== Testing quicksort_omp ==="
	./quicksort_omp
	@echo ""
	@echo "=== Testing crack_password_omp ==="
	./crack_password_omp
	@echo ""
	@echo "=== Testing find_primes_omp ==="
	./find_primes_omp 1 100
	@echo ""
	@echo "=== Testing game_of_life_omp ==="
	./game_of_life_omp

.PHONY: all clean test
