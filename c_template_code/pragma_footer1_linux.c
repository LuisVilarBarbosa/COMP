	}

	clock_gettime(CLOCK_MONOTONIC, &_TUNER_END_exploreVarName);
	long long _TUNER_TIME_exploreVarName = (_TUNER_END_exploreVarName.tv_sec - _TUNER_START_exploreVarName.tv_sec) * pow(10,9);
	_TUNER_TIME_exploreVarName += (_TUNER_END_exploreVarName.tv_nsec - _TUNER_START_exploreVarName.tv_nsec);
