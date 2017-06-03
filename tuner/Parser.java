package tuner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private ArrayList<String> codeLines;
    private ArrayList<PragmaScope> pragmaScopes;
    private ArrayList<Node> syntacticAnalysisTrees;

    public Parser(BufferedReader bufferedReader) throws Exception {
        this.codeLines = getLines(bufferedReader);
        this.pragmaScopes = findPragmasByFunction(this.codeLines);
        this.syntacticAnalysisTrees = generateSyntacticAnalysisTrees(this.codeLines, this.pragmaScopes);
    }

    public ArrayList<String> getCodeLines() {
        return codeLines;
    }

    public ArrayList<PragmaScope> getPragmaScopes() {
        return pragmaScopes;
    }

    public ArrayList<Node> getSyntacticAnalysisTrees() {
        return syntacticAnalysisTrees;
    }

    private ArrayList<String> getLines(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String str;
        while ((str = bufferedReader.readLine()) != null)
            lines.add(str);
        return lines;
    }

    private ArrayList<PragmaScope> findPragmasByFunction(ArrayList<String> codeLines) throws Exception {
        ArrayList<Integer> indexes = new ArrayList<>();

        int indentation = 0;
        boolean ignoreLines = false;
        for (int i = 0; i < codeLines.size(); i++) {
            String line = codeLines.get(i);
            if (ignoreLines) {
                if (line.contains("*/")) {
                    ignoreLines = false;
                    line = line.replaceAll(".*\\*/", "");
                } else
                    continue;
            } else {
                if (line.contains("/*")) {
                    ignoreLines = true;
                    line = line.replaceAll("/\\*.*", "");
                }
            }

            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '{':
                        if (indentation == 0)
                            indexes.add(i);
                        indentation++;
                        break;
                    case '}':
                        indentation--;
                        if (indentation == 0)
                            indexes.add(i);
                        break;
                    default:
                        break;
                }
            }
        }

        ArrayList<PragmaScope> pragmaScopes = new ArrayList<>();
        for (int i = 1; i < indexes.size(); i += 2) {
            ArrayList<Integer> pragmaIndexes = findPragmas(codeLines, indexes.get(i - 1), indexes.get(i));
            for (int j = 0, k = pragmaIndexes.size() - 1; j <= k; j++, k--)
                pragmaScopes.add(new PragmaScope(pragmaIndexes.get(j), pragmaIndexes.get(k)));
        }

        return pragmaScopes;
    }


    // Comments started by "//" are ignored by the regular expression.
    private ArrayList<Integer> findPragmas(ArrayList<String> codeLines, int startIndex, int endIndex) throws Exception {
        if (endIndex >= codeLines.size())
            throw new Error("'endIndex' equal to or greater than codeLines.size()");
        ArrayList<Integer> pragmaIndexes = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?:(?!//).)*#pragma[\t ]+tuner.*");
        boolean ignoreLines = false;
        for (int i = startIndex; i < endIndex; i++) {
            String line = codeLines.get(i);
            if (ignoreLines) {
                // Cannot exist an uncommented pragma.
                if (line.contains("*/"))
                    ignoreLines = false;
            } else {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches())
                    pragmaIndexes.add(i);
                if (line.contains("/*"))
                    ignoreLines = true;
            }
        }
        if (pragmaIndexes.size() % 2 != 0)
            throw new Exception("Odd number of pragmas. For each clause must exist a start pragma and an end pragma.");
        return pragmaIndexes;
    }


    private ArrayList<Node> generateSyntacticAnalysisTrees(ArrayList<String> codeLines, ArrayList<PragmaScope> pragmaScopes) throws Exception {
        ArrayList<Node> pragmaTrees = new ArrayList<>();
        for (int i = 0; i < pragmaScopes.size(); i++) {
            PragmaScope pragmaScope = pragmaScopes.get(i);
            Node root = new Node("");
            buildTree(codeLines.get(pragmaScope.getStartIndex()), root);
            buildTree(codeLines.get(pragmaScope.getEndIndex()), root);
            pragmaTrees.add(root);
        }
        return pragmaTrees;
    }

    private void buildTree(String pragma, Node root) throws Exception {
        Command command = new Command("java", "-cp", "bin", "JJTree.SyntacticAnalyser", pragma);
        command.setStoreOutput(true);
        command.run();

        ArrayList<String> lines = command.getOutputStreamLines();

        if (lines.size() >= 2) {
            if (lines.get(0).startsWith("Encountered") && lines.get(1).startsWith("Was expecting"))
                throw new Exception("Invalid pragma found: " + pragma);
        }

        int indentation = 0;
        Node lastNewNode = root;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            int numTabs = 0;
            while (line.charAt(numTabs) == ' ' && numTabs < line.length())
                numTabs++;

            String info = line.substring(numTabs);

            if (numTabs == indentation) {
                Node parent = root.getParent(lastNewNode);
                if (parent == null)
                    parent = root;
                lastNewNode = addNodeToTree(info, parent);
            } else if (numTabs == indentation - 1) {
                Node parent = root.getParent(lastNewNode);
                Node grandparent = root.getParent(parent);
                lastNewNode = addNodeToTree(info, grandparent);
            } else if (numTabs == indentation + 1) {
                lastNewNode = addNodeToTree(info, lastNewNode);
            } else
                throw new Exception("A problem occurred while loading the tree generated by JJTree.");

            indentation = numTabs;
        }
    }

    private Node addNodeToTree(String info, Node parent) {
        Node n = new Node(info);
        parent.addChild(n);
        return n;
    }
}
