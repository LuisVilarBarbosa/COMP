varType varName;
for (varName = startValue; varName < endValue; varName++) {
	struct timespec _TUNER_START_varName, _TUNER_END_varName;
	clock_gettime(CLOCK_MONOTONIC, &_TUNER_START_varName);
