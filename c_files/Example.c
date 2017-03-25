#include <stdio.h>

int main() {
	#pragma tuner explore STEP(1,10) reference(STEP=1)
	unsigned int i;
	for (i = 1; i <= 10; i++)
		printf("%u - Working\n", i);
	#pragma tuner max_abs_error acc 5
	return 0;
}
