#include <stdio.h>

int main() {
	int STEP;
	int acc = 2;
	// #pragma tuner explore STEP(1,10) reference(STEP=1)
	unsigned int i;
	int N = 10;
	int buf[10] = {2}; 
	for (i = 0; i < N; i = i + STEP) {
    printf("%d\n", i);
		acc = acc + buf[i];
	}
	acc = acc + STEP;
	// #pragma tuner max_abs_error acc 5
	return 0;
}
