package tuner;

public class Pragma {
    public String varName;
    public String startValue;
    public String endValue;
    public String max_abs_errorVarName;
    public String max_abs_error;

    public Double best_execution;
    public Double best_execution_time;

    public Pragma(String varName, String startValue, String endValue, String max_abs_errorVarName, String max_abs_error) {
        this.varName = varName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.max_abs_errorVarName = max_abs_errorVarName;
        this.max_abs_error = max_abs_error;
        this.best_execution_time = 9999999d;
    }
}
