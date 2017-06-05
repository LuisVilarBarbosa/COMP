package tuner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyser {
    private ArrayList<String> codeLines;
    private ArrayList<PragmaScope> pragmaScopes;
    private ArrayList<Node> HIRs;
    private Pattern number;

    public SemanticAnalyser(ArrayList<String> codeLines, ArrayList<PragmaScope> pragmaScopes, ArrayList<Node> syntacticAnalysisTrees) {
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
                verifyIfPassesReferencedValue(this.HIRs.get(i));
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

    public ArrayList<Node> getHIRs() {
        return HIRs;
    }

    private ArrayList<Node> cleanTrees(ArrayList<Node> syntacticAnalysisTrees) {
        ArrayList<Node> HIRs = syntacticAnalysisTrees;
        for (int i = 0; i < HIRs.size(); i++)
            HIRs.set(i, cleanTree(HIRs.get(i)));
        return HIRs;
    }

    private Node cleanTree(Node root) {
        Node newRoot = new Node(root.getInfo());
        ArrayList<Node> children = root.getChildren();
        for (int i = 1; i < children.size(); i++) {
            Node child1 = children.get(i - 1);
            Node child2 = children.get(i);
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

    private void verifyPragmaDataTypes(Node root) throws Exception {
        final String nonDecimals = "\\d+";
        final String decimals = "\\d+\\.\\d+";
        final Pattern pattern, pattern2;
        final Matcher matcher, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7, matcher8;

        if (root.getChildren().size() > 0) {
            Node var = root.getChildren().get(0);
            ArrayList<Node> children = var.getChildren();
            if (children.size() == 2) {
                Node child1 = children.get(0);
                Node child2 = children.get(1);
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

    private void verifyVariablesValuesOrder(Node root) throws Exception {
        if (root.getInfo().equals("explore") || root.getInfo().equals("random")) {
            Node var = root.getChildren().get(0);
            ArrayList<Node> children = var.getChildren();
            Node child1 = children.get(0);
            Node child2 = children.get(1);
            if (Double.parseDouble(child1.getInfo()) > Double.parseDouble(child2.getInfo())) {
                children.set(0, child2);
                children.set(1, child1);
                System.out.println(var.getInfo() + ": The interval values were not in the correct order. The order has been changed.");
            }
        }
        for (Node n : root.getChildren())
            verifyVariablesValuesOrder(n);
    }

    private void verifyPragmaInstructionsCompatibility(Node root) throws Exception {
        ArrayList<Node> children = root.getChildren();
        if (children.size() >= 2 && (children.get(0).equals("explore") || children.get(0).equals("random")) && !children.get(1).equals("max_abs_error"))
            throw new Exception("One start pragma is not compatible with the end pragma. They will be ignored.");
        for (Node n : root.getChildren())
            verifyPragmaInstructionsCompatibility(n);
    }

    private void verifyEqualVarNames(Node root) throws Exception {
        ArrayList<Node> children = root.getChildren();
        if (root.getInfo().equals("explore") || root.getInfo().equals("random")) {
            String var1 = children.get(0).getInfo();
            String var2 = children.get(1).getChildren().get(0).getInfo();
            if (!var1.equals(var2))
                throw new Exception("One pragma refers two variables ('" + var1 + "' and '" + var2 + "') when it should refer only one. It will be ignored.");
        }
        for (Node n : root.getChildren())
            verifyEqualVarNames(n);
    }

    private void verifyIfPassesReferencedValue(Node root) throws Exception {
        if (root.getChildren().size() > 0) {
            Node var = root.getChildren().get(0);
            ArrayList<Node> children = var.getChildren();
            if (children.size() == 2) {
                Node child1 = children.get(0);
                Node child2 = children.get(1);
                if (child1.getChildren().size() > 0) {
                    Double val1 = Double.parseDouble(child1.getChildren().get(0).getInfo());
                    Double val2 = Double.parseDouble(child1.getChildren().get(1).getInfo());
                    Double val3 = 1.0;
                    if (child1.getChildren().size() == 3)
                        val3 = Double.parseDouble(child1.getChildren().get(2).getInfo());
                    if (child2.getChildren().size() > 1) {
                        Double val4 = Double.parseDouble(child2.getChildren().get(1).getInfo());

                        ArrayList<Double> usedValues = new ArrayList<>();

                        for (double i = val1; i < val2; i += val3) {
                            usedValues.add(i);
                        }
                        if (!usedValues.contains(val4))
                            throw new Exception("The pragma variable need to pass through the referenced value");
                    }
                }
            }
        }
    }
}
