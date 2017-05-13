package tuner;

import java.util.ArrayList;

public class SemanticAnalyser {
    private ArrayList<String> codeLines;
    private ArrayList<Integer> pragmaIndexes;
    private ArrayList<Node> HIRs;

    public SemanticAnalyser(ArrayList<String> codeLines, ArrayList<Integer> pragmaIndexes, ArrayList<Node> syntacticAnalysisTrees) {
        this.codeLines = codeLines;
        this.pragmaIndexes = pragmaIndexes;
        this.HIRs = cleanTrees(syntacticAnalysisTrees);

        if (this.pragmaIndexes.size() != this.HIRs.size() * 2)
            throw new Error("A bug has been found in the code.");

        for (int i = 0, j = this.pragmaIndexes.size() - 1; i < this.HIRs.size(); i++, j--) {
            try {
                verifyVariablesValuesOrder(this.HIRs.get(i));
                verifyPragmaInstructionsCompatibility(this.HIRs.get(i));
                verifyEqualVarNames(this.HIRs.get(i));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                this.HIRs.remove(i);
                // The removal order is very important.
                this.pragmaIndexes.remove(j);
                this.pragmaIndexes.remove(i);
                i--;
                j++;
            }
        }
    }

    public ArrayList<String> getCodeLines() {
        return codeLines;
    }

    public ArrayList<Integer> getPragmaIndexes() {
        return pragmaIndexes;
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
            if (child1.getInfo().equals(child2.getInfo())) {
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

    private void verifyVariablesExistence() {
        //foreach var do outside her scope
        /*if(codeLines.get(i).indexOf(var) != -1)
            throw new Exception("'"var + "' found outside her scope on line " +  (i + 1));*/
    }

    private void verifyPragmaDataTypes() {

    }

    private void verifyVariablesValuesOrder(Node root) throws Exception {
        if (root.getInfo().equals("explore")) {
            Node var = root.getChildren().get(0);
            ArrayList<Node> children = var.getChildren();
            if (children.size() != 2)
                throw new Exception("The interval indicated in an 'explore' pragma must start and end with different values. The pragma will be ignored.");
            Node child1 = children.get(0);
            Node child2 = children.get(1);
            if (Integer.parseInt(child1.getInfo()) > Integer.parseInt(child2.getInfo())) {
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
        if (children.size() >= 2 && children.get(0).equals("explore") && !children.get(1).equals("max_abs_error"))
            throw new Exception("One start pragma is not compatible with the end pragma. They will be ignored.");
        for (Node n : root.getChildren())
            verifyPragmaInstructionsCompatibility(n);
    }

    private void verifyEqualVarNames(Node root) throws Exception {
        ArrayList<Node> children = root.getChildren();
        if (root.getInfo().equals("explore")) {
            String var1 = children.get(0).getInfo();
            String var2 = children.get(1).getChildren().get(0).getInfo();
            if (!var1.equals(var2))
                throw new Exception("One pragma refers two variables ('" + var1 + "' and '" + var2 + "') when it should refer only one. It will be ignored.");
        }
        for (Node n : root.getChildren())
            verifyEqualVarNames(n);
    }

}
