package tuner;

import java.io.*;
import java.util.*;

class CodeChanger {
    private static final boolean isWindows = System.getProperty("os.name").contains("Win");

    private static final String templates_folder = "c_template_code/";
    private static final String includes_global = templates_folder + "includes_global.c";
    private static final String includes_linux = templates_folder + "includes_linux.c";
    private static final String includes_windows = templates_folder + "includes_windows.c";
    private static final String pragma_footer1_linux = templates_folder + "pragma_footer1_linux.c";
    private static final String pragma_footer1_windows = templates_folder + "pragma_footer1_windows.c";
    private static final String pragma_footer2 = templates_folder + "pragma_footer2.c";
    private static final String pragma_header1_linux = templates_folder + "pragma_header1_linux.c";
    private static final String pragma_header1_windows = templates_folder + "pragma_header1_windows.c";
    private static final String pragma_header2 = templates_folder + "pragma_header2.c";

    private final String testCodeFile = "_TUNER_FILE_WITH_COMPLETE_SOURCE_CODE_TO_TEST.c";
    private ArrayList<String> codeLines;
    private ArrayList<PragmaScope> pragmaScopes;
    private ArrayList<Node> HIRs;

    private ArrayList<Pragma> all_pragmas = new ArrayList<>();

    CodeChanger(ArrayList<String> codeLines, ArrayList<PragmaScope> pragmaScopes, ArrayList<Node> HIRs) {
        this.codeLines = codeLines;
        this.pragmaScopes = pragmaScopes;
        this.HIRs = HIRs;
    }

    void codeVariantsTest() throws Exception {
        checkIfAllPragmaVarsAreDifferent();
        ArrayList<String> codeChanged = changeCCode();
        generateFileWithCode(codeChanged);
        CodeExecutor codeExecutor = new CodeExecutor(testCodeFile);
        if (codeExecutor.compile())
            codeExecutor.exec(all_pragmas);
        else
            System.out.println("No tests will be performed. Fix any warning or error given by the compiler.");
        codeExecutor.delete();
    }

    private ArrayList<String> changeCCode() throws IOException {
        HashMap<Integer, ArrayList<String>> codeToAddByIndex = new HashMap<>();

        for (int i = 0; i < HIRs.size(); i++) {
            int scopeBegin = pragmaScopes.get(i).getStartIndex() + 1;
            int scopeEnd = pragmaScopes.get(i).getEndIndex();
            ArrayList<Node> children = HIRs.get(i).getChildren();

            if (children.size() >= 2) {
                Node n1 = children.get(0);
                Node n2 = children.get(1);
                if ((n1.getInfo().equals("explore") || n1.getInfo().equals("random")) && n2.getInfo().equals("max_abs_error")) {
                    ArrayList<Node> exploreChildren = n1.getChildren();
                    String exploreVarName = exploreChildren.get(0).getInfo();
                    ArrayList<Node> varChildren = exploreChildren.get(0).getChildren();
                    String startValue = varChildren.get(0).getInfo();
                    String endValue = varChildren.get(1).getInfo();
                    String inc = "1";
                    if(varChildren.size() == 3)
                        inc = varChildren.get(2).getInfo();


                    ArrayList<Node> max_abs_errorChildren = n2.getChildren();
                    String max_abs_errorVarName = max_abs_errorChildren.get(0).getInfo();
                    String max_abs_errorValue = max_abs_errorChildren.get(1).getInfo();

                    ArrayList<Node> referenceChildren = exploreChildren.get(1).getChildren();
                    String referenceExecution = referenceChildren.get(1).getInfo();

                    Pragma p = new Pragma(n1.getInfo(), exploreVarName, startValue, endValue, inc, max_abs_errorVarName, max_abs_errorValue, referenceExecution);
                    all_pragmas.add(p);

                    ArrayList<String> newEndCode = loadScopeEnd();
                    adjustCode(newEndCode, p);
                    ArrayList<String> newStartCode = loadScopeBegin();
                    adjustCode(newStartCode, p);

                    codeToAddByIndex.put(scopeEnd, newEndCode);
                    codeToAddByIndex.put(scopeBegin, newStartCode);
                }
            }
        }
        codeToAddByIndex.put(0, loadIncludes());

        ArrayList<String> codeChanged = codeLines;
        ArrayList<Integer> keys = new ArrayList<>(codeToAddByIndex.keySet());
        Collections.sort(keys);
        for (int i = keys.size() - 1; i >= 0; i--) {
            int index = keys.get(i);
            codeChanged.addAll(index, codeToAddByIndex.get(index));
        }

        return codeChanged;
    }

    private void generateFileWithCode(ArrayList<String> code) throws IOException {
        FileOutputStream file = new FileOutputStream(testCodeFile);
        byte[] endLine = "\n".getBytes();
        for (String line : code) {
            file.write(line.getBytes());
            file.write(endLine);
        }
        file.close();
    }

    private ArrayList<String> loadIncludes() throws IOException {
        ArrayList<String> includes = new ArrayList<>();
        includes.addAll(loadFile(includes_global));
        if (isWindows)
            includes.addAll(loadFile(includes_windows));
        else
            includes.addAll(loadFile(includes_linux));
        return includes;
    }

    private ArrayList<String> loadScopeBegin() throws IOException {
        ArrayList<String> pragmaHeader = new ArrayList<>();
        if (isWindows)
            pragmaHeader.addAll(loadFile(pragma_header1_windows));
        else
            pragmaHeader.addAll(loadFile(pragma_header1_linux));
        pragmaHeader.addAll(loadFile(pragma_header2));
        return pragmaHeader;
    }

    private ArrayList<String> loadScopeEnd() throws IOException {
        ArrayList<String> pragmaFooter = new ArrayList<>();
        if (isWindows)
            pragmaFooter.addAll(loadFile(pragma_footer1_windows));
        else
            pragmaFooter.addAll(loadFile(pragma_footer1_linux));
        pragmaFooter.addAll(loadFile(pragma_footer2));
        return pragmaFooter;
    }

    private ArrayList<String> loadFile(String filename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            lines.add(line);
        bufferedReader.close();
        inputStreamReader.close();
        fileInputStream.close();
        return lines;
    }

    private void adjustCode(ArrayList<String> code, Pragma p) {
        for (int i = 0; i < code.size(); i++) {
            String stmt1 = null, stmt2 = null, stmt3 = null;
            String line = code.get(i);
            if(p.type.equals("explore")){
                stmt1 = p.varName + " = " + p.startValue;
                stmt2 = p.varName + " < " + p.endValue;
                stmt3 = p.varName + " += " + p.inc;
                line = line.replaceAll("random", "");
            }
            else if(p.type.equals("random")){
                stmt1 = p.varName + " = " + p.startValue + " + rand() % (" + p.endValue + "-" + p.startValue + "+1), _TUNER_ITERATOR_"+ p.varName + "= 0";
                stmt2 = "_TUNER_ITERATOR_" + p.varName + "< " + p.inc;
                stmt3 = p.varName + " = " + p.startValue + " + rand() % (" + p.endValue + "-" + p.startValue + "+1)," + "_TUNER_ITERATOR_" + p.varName + "++";
                line = line.replaceAll("random", "int _TUNER_ITERATOR_" + p.varName + ";");
            }

            line = line.replaceAll("statement1", stmt1);
            line = line.replaceAll("statement2", stmt2);
            line = line.replaceAll("statement3", stmt3);
            line = line.replaceAll("exploreVarName", p.varName);
            line = line.replaceAll("startValue", p.startValue);
            line = line.replaceAll("endValue", p.endValue);
            line = line.replaceAll("inc", p.inc);
            line = line.replaceAll("max_abs_errorVarName", p.max_abs_errorVarName);
            code.set(i, line);
        }
    }

    private void checkIfAllPragmaVarsAreDifferent() throws Exception {
        ArrayList<String> allVariables = new ArrayList<>();
        for(int i = 0; i < HIRs.size(); i++) {
            Node root = HIRs.get(i);
            if(root.getChildren().size() > 0) {
                Node child = root.getChildren().get(0);
                if(child.getChildren().size() > 0) {
                    String grandchild = child.getChildren().get(0).getInfo();
                    allVariables.add(grandchild);
                }
            }
        }
        HashSet<String> variablesSet = new HashSet<>(allVariables);
        if(variablesSet.size() != allVariables.size())
            throw new Exception("The variables of different pragmas cannot have the same name.");
    }

}
