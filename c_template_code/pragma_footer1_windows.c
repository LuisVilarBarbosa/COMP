	}

	QueryPerformanceCounter(&_TUNER_END_varName);
	_TUNER_TIME_varName = (_TUNER_END_varName.QuadPart - _TUNER_START_varName.QuadPart) * (long long)pow(10,9) / _TUNER_FREQUENCY_varName.QuadPart;
