int main() {
	#pragma tuner explore STEP(1,10) reference(STEP=1)
	unsigned int i;
	int N = 10;
	int STEP = 1;
	int acc = 2;
	int buf[10] = {2}; 
	for (int i = 0; i < N; i += STEP) {
		acc += buf[i];
	}
	acc *= STEP;
	#pragma tuner max_abs_error acc 5
	return 0;
}
