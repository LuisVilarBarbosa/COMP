#include <stdio.h>

int main() {
	int RANDOM;
	int acc = 2;
	#pragma tuner random RANDOM(1,10,5) reference(RANDOM=1)
	unsigned int i;
	int N = 10;
	int buf[10] = {2};
	for (i = 0; i < N; i = i + RANDOM) {
		acc = acc + buf[i];
	}
	acc = acc + RANDOM;
	#pragma tuner max_abs_error acc 5
	return 0;
}
