#include <stdio.h>

void function() {
	int i, VAR, acc = 0;
	#pragma tuner explore VAR(1,10) reference(VAR=1)
	for (i = VAR; i < 10; i++)
		acc += VAR;
	acc = acc + VAR;
	#pragma tuner max_abs_error acc 10
}

int main() {
	int i, N = 10, STEP, VAR2, acc = 0, acc2 = 0;
	int buf[] = {1,2,3,4,5,6,7,8,9,10};
	#pragma tuner explore STEP(1,10) reference(STEP=1)
	#pragma tuner explore VAR2(1,10) reference(VAR2=1)
	for (i = 0; i < N; i = i + STEP) {
		acc += buf[i];
		acc2 += VAR2;
	}
	acc2 = acc2 + VAR2;
	#pragma tuner max_abs_error acc2 10
	acc = acc + STEP;
	#pragma tuner max_abs_error acc 5
	return 0;
}
