CC=gcc
CFLAGS=-fopenmp -Wall -O2

all: array_sum

array_sum: array_sum.c
	$(CC) $(CFLAGS) -o array_sum array_sum.c

clean:
	rm -f array_sum 