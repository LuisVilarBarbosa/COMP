for (exploreVarName = startValue; exploreVarName < endValue; exploreVarName++) {
	LARGE_INTEGER _TUNER_FREQUENCY_exploreVarName;
	LARGE_INTEGER _TUNER_START_exploreVarName;
	LARGE_INTEGER _TUNER_END_exploreVarName;
	long long _TUNER_TIME_exploreVarName;

	QueryPerformanceFrequency(&_TUNER_FREQUENCY_exploreVarName);
	QueryPerformanceCounter(&_TUNER_START_exploreVarName);
