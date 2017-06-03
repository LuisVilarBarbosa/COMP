for (exploreVarName = startValue; exploreVarName < endValue; exploreVarName++) {
	struct timespec _TUNER_START_exploreVarName, _TUNER_END_exploreVarName;
	clock_gettime(CLOCK_MONOTONIC, &_TUNER_START_exploreVarName);
