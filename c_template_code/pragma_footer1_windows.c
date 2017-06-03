	}

	QueryPerformanceCounter(&_TUNER_END_exploreVarName);
	_TUNER_TIME_exploreVarName = (_TUNER_END_exploreVarName.QuadPart - _TUNER_START_exploreVarName.QuadPart) * (long long)pow(10,9) / _TUNER_FREQUENCY_exploreVarName.QuadPart;
