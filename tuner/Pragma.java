package tuner;

class Pragma {
    String type;
    String varName;
    String startValue;
    String endValue;
    String controlValue;    // increment value in "explore" and number of attempts in "random"
    String max_abs_errorVarName;
    Double max_abs_error;

    Double referenceExecution;
    Double referenceValue;

    String bestExecution;
    Double bestExecutionValue;
    Double bestExecutionTime;

    Pragma(String type, String varName, String startValue, String endValue, String controlValue, String max_abs_errorVarName, String max_abs_error, String referenceExecution) {
        this.type = type;
        this.varName = varName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.controlValue = controlValue;
        this.max_abs_errorVarName = max_abs_errorVarName;
        this.max_abs_error = Double.parseDouble(max_abs_error);
        this.referenceExecution = Double.parseDouble(referenceExecution);
    }

    /**
     * Checks if execution time is abs(execution - referenceValue) <= max_abs_error.
     * If true stores the value.
     *
     * @param execution      Pragma n execution
     * @param execution_value Pragma execution value
     * @param execution_time Pragma execution time
     */
    void validTime(String execution, Double execution_value, Double execution_time) {
        if (Math.abs(execution_value - referenceValue) <= max_abs_error) {
            bestExecution = execution;
            bestExecutionValue = execution_value;
            bestExecutionTime = execution_time;
        }
    }
}
