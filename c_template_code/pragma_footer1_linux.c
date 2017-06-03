	}

	clock_gettime(CLOCK_MONOTONIC, &_TUNER_END_varName);
	long long _TUNER_TIME_varName = (_TUNER_END_varName.tv_sec - _TUNER_START_varName.tv_sec) * pow(10,9);
	_TUNER_TIME_varName += (_TUNER_END_varName.tv_nsec - _TUNER_START_varName.tv_nsec);
