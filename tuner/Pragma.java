package tuner;

class Pragma {
    String varName;
    String startValue;
    String endValue;
    String inc;
    String max_abs_errorVarName;
    Double max_abs_error;

    Double referenceExecution;
    Double referenceValue;

    String bestExecution;
    Double bestExecutionTime;

    /**
     * Pragma constructor with a reference value.
     *
     * @param varName              Pragma name
     * @param startValue           Pragma initial value
     * @param endValue             Pragma final value
     * @param max_abs_errorVarName Error variable
     * @param max_abs_error        Error value
     * @param referenceExecution   Pragma n execution
     */
    Pragma(String type, String varName, String startValue, String endValue, String inc, String max_abs_errorVarName, String max_abs_error, String referenceExecution) {
        this.type = type;
        this.varName = varName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.inc = inc;
        this.max_abs_errorVarName = max_abs_errorVarName;
        this.max_abs_error = Double.parseDouble(max_abs_error);
        this.referenceExecution = Double.parseDouble(referenceExecution);
    }

    /**
     * Checks if execution time is abs(execution - referenceValue) <= max_abs_error.
     * If true stores the value.
     *
     * @param execution      Pragma n execution
     * @param execution_time Pragma execution time
     */
    void validTime(String execution, Double execution_time) {
        if (Math.abs(execution_time - referenceValue) <= max_abs_error) {
            bestExecution = execution;
            bestExecutionTime = execution_time;
        }
    }
}
