for (varType varName = startValue; varName < endValue; varName++) {
	LARGE_INTEGER _TUNER_FREQUENCY_varName;
	LARGE_INTEGER _TUNER_START_varName;
	LARGE_INTEGER _TUNER_END_varName;
	long long _TUNER_TIME_varName;

	QueryPerformanceFrequency(&_TUNER_FREQUENCY_varName);
	QueryPerformanceCounter(&_TUNER_START_varName);
