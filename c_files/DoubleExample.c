#include <stdio.h>

int main() {
	double STEP;
	double acc = 2;
	#pragma tuner explore STEP(1.2,11.2,1.2) reference(STEP=2.4)
	unsigned int i;
	int N = 10;
	int buf[10] = {2}; 
	for (i = 0; i < N; i = i + STEP) {
		acc = acc + buf[i];
	}
	acc = acc + STEP;
	#pragma tuner max_abs_error acc 5000
	return 0;
}
