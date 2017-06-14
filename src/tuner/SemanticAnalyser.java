package tuner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyser {
    private ArrayList<String> codeLines;
    private ArrayList<PragmaScope> pragmaScopes;
    private ArrayList<AutoNode> HIRs;
    private Pattern number;

    public SemanticAnalyser(ArrayList<String> codeLines, ArrayList<PragmaScope> pragmaScopes, ArrayList<AutoNode> syntacticAnalysisTrees) {
        number = Pattern.compile("([0-9]+(.[0-9]*)?)");
        this.codeLines = codeLines;
        this.pragmaScopes = pragmaScopes;
        this.HIRs = cleanTrees(syntacticAnalysisTrees);

        if (this.pragmaScopes.size() != this.HIRs.size())
            throw new Error("A bug has been found in the code.");

        for (int i = 0; i < this.HIRs.size(); i++) {
            try {
                verifyPragmaDataTypes(this.HIRs.get(i));
                verifyVariablesValuesOrder(this.HIRs.get(i));
                verifyIfPassesReferenceValue(this.HIRs.get(i));
                verifyPragmaInstructionsCompatibility(this.HIRs.get(i));
                verifyEqualVarNames(this.HIRs.get(i));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                this.HIRs.remove(i);
                this.pragmaScopes.remove(i);
                i--;
            }
        }
    }

    public ArrayList<String> getCodeLines() {
        return codeLines;
    }

    public ArrayList<PragmaScope> getPragmaScopes() {
        return pragmaScopes;
    }

    public ArrayList<AutoNode> getHIRs() {
        return HIRs;
    }

    private ArrayList<AutoNode> cleanTrees(ArrayList<AutoNode> syntacticAnalysisTrees) {
        ArrayList<AutoNode> HIRs = syntacticAnalysisTrees;
        for (int i = 0; i < HIRs.size(); i++)
            HIRs.set(i, cleanTree(HIRs.get(i)));
        return HIRs;
    }

    private AutoNode cleanTree(AutoNode root) {
        AutoNode newRoot = new AutoNode(root.getInfo());
        ArrayList<AutoNode> children = root.getChildren();
        for (int i = 1; i < children.size(); i++) {
            AutoNode child1 = children.get(i - 1);
            AutoNode child2 = children.get(i);
            Matcher matcher = number.matcher(child1.getInfo());
            if (child1.getInfo().equals(child2.getInfo()) && !matcher.matches()) {
                child1.getChildren().addAll(child2.getChildren());
                children.remove(i);
                i--;
            }
        }

        for (int i = 0; i < children.size(); i++)
            children.set(i, cleanTree(children.get(i)));
        newRoot.getChildren().addAll(children);

        if (children.size() == 1)
            newRoot = children.get(0);

        return newRoot;
    }

    private void verifyPragmaDataTypes(AutoNode root) throws Exception {
        final String nonDecimals = "([0-9]+)";
        final String decimals = "([0-9]+(.[0-9]*))?";
        final Pattern pattern, pattern2;
        final Matcher matcher, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7, matcher8;

        if (root.getChildren().size() > 0) {
            AutoNode var = root.getChildren().get(0);
            ArrayList<AutoNode> children = var.getChildren();
            if (children.size() == 2) {
                AutoNode child1 = children.get(0);
                AutoNode child2 = children.get(1);
                if (child1.getChildren().size() > 0) {
                    String val1 = child1.getChildren().get(0).getInfo();
                    String val2 = child1.getChildren().get(1).getInfo();
                    String val3 = null;
                    if (child1.getChildren().size() == 3)
                        val3 = child1.getChildren().get(2).getInfo();

                    if (child2.getChildren().size() > 1) {
                        String val4 = child2.getChildren().get(1).getInfo();

                        pattern = Pattern.compile(nonDecimals);
                        matcher = pattern.matcher(val1);
                        matcher2 = pattern.matcher(val2);
                        if (val3 != null)
                            matcher3 = pattern.matcher(val3);
                        else
                            matcher3 = null;
                        matcher4 = pattern.matcher(val4);

                        if (matcher3 != null) {
                            if (!(matcher.matches() && matcher2.matches() && matcher3.matches() && matcher4.matches())) {
                                pattern2 = Pattern.compile(decimals);
                                matcher5 = pattern2.matcher(val1);
                                matcher6 = pattern2.matcher(val2);
                                matcher7 = pattern2.matcher(val3);
                                matcher8 = pattern2.matcher(val4);
                                if (!(matcher5.matches() && matcher6.matches() && matcher7.matches() && matcher8.matches()) || var.getInfo().equals("random"))
                                    throw new Exception("Pragma data types are incompatible.");
                            }
                        } else {
                            if (!(matcher.matches() && matcher2.matches() && matcher4.matches())) {
                                pattern2 = Pattern.compile(decimals);
                                matcher5 = pattern2.matcher(val1);
                                matcher6 = pattern2.matcher(val2);
                                matcher8 = pattern2.matcher(val4);
                                if (!(matcher5.matches() && matcher6.matches() && matcher8.matches()))
                                    throw new Exception("Pragma data types are incompatible.");
                            }
                        }
                    }
                }
            }
        }
    }

    private void verifyVariablesValuesOrder(AutoNode root) throws Exception {
        if (root.getInfo().equals("explore") || root.getInfo().equals("random")) {
            AutoNode var = root.getChildren().get(0);
            ArrayList<AutoNode> children = var.getChildren();
            AutoNode child1 = children.get(0);
            AutoNode child2 = children.get(1);
            if (Double.parseDouble(child1.getInfo()) > Double.parseDouble(child2.getInfo())) {
                children.set(0, child2);
                children.set(1, child1);
                System.out.println(var.getInfo() + ": The interval values were not in the correct order. The order has been changed.");
            }
        }
        for (AutoNode n : root.getChildren())
            verifyVariablesValuesOrder(n);
    }

    private void verifyPragmaInstructionsCompatibility(AutoNode root) throws Exception {
        ArrayList<AutoNode> children = root.getChildren();
        if (children.size() >= 2) {
            if (((children.get(0).getInfo().equals("explore") || children.get(0).getInfo().equals("random")) && !children.get(1).getInfo().equals("max_abs_error")) || children.get(0).getInfo().equals("max_abs_error"))
                throw new Exception("One start pragma is not compatible with the end pragma. They will be ignored.");
        }
        for (AutoNode n : root.getChildren())
            verifyPragmaInstructionsCompatibility(n);
    }

    private void verifyEqualVarNames(AutoNode root) throws Exception {
        ArrayList<AutoNode> children = root.getChildren();
        if (root.getInfo().equals("explore") || root.getInfo().equals("random")) {
            String var1 = children.get(0).getInfo();
            String var2 = children.get(1).getChildren().get(0).getInfo();
            if (!var1.equals(var2))
                throw new Exception("One pragma refers two variables ('" + var1 + "' and '" + var2 + "') when it should refer only one. It will be ignored.");
        }
        for (AutoNode n : root.getChildren())
            verifyEqualVarNames(n);
    }

    private void verifyIfPassesReferenceValue(AutoNode root) throws Exception {
        if (root.getChildren().size() > 0) {
            AutoNode var = root.getChildren().get(0);
            ArrayList<AutoNode> children = var.getChildren();
            if (children.size() == 2) {
                AutoNode child1 = children.get(0);
                AutoNode child2 = children.get(1);
                if (child1.getChildren().size() > 1) {
                    Double val1 = Double.parseDouble(child1.getChildren().get(0).getInfo());
                    Double val2 = Double.parseDouble(child1.getChildren().get(1).getInfo());
                    Double val3 = 1.0;
                    if (child1.getChildren().size() == 3)
                        val3 = Double.parseDouble(child1.getChildren().get(2).getInfo());
                    if (child2.getChildren().size() > 1) {
                        Double val4 = Double.parseDouble(child2.getChildren().get(1).getInfo());

                        boolean valueIsUsed = false;
                        for (double i = val1; i < val2; i += val3) {
                            if (i == val4)
                                valueIsUsed = true;
                        }
                        if (!valueIsUsed)
                            throw new Exception("The pragma variable need to pass through the referenced value");
                    }
                }
            }
        }
    }
}
