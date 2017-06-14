package tuner;

import java.io.*;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

class CodeChanger {
    private static final boolean isJar = setIsJar();
    private static final boolean isWindows = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("os.name")).contains("Win");

    private static final String templates_folder = "c_template_code/";
    private static String includes_global = "includes_global.c";
    private static String includes_linux = "includes_linux.c";
    private static String includes_windows = "includes_windows.c";
    private static String pragma_footer1_linux = "pragma_footer1_linux.c";
    private static String pragma_footer1_windows = "pragma_footer1_windows.c";
    private static String pragma_footer2 = "pragma_footer2.c";
    private static String pragma_header1_linux = "pragma_header1_linux.c";
    private static String pragma_header1_windows = "pragma_header1_windows.c";
    private static String pragma_header2 = "pragma_header2.c";

    private final String testCodeFile = "_TUNER_FILE_WITH_COMPLETE_SOURCE_CODE_TO_TEST.c";
    private ArrayList<String> codeLines;
    private ArrayList<PragmaScope> pragmaScopes;
    private ArrayList<AutoNode> HIRs;
    private ArrayList<Pragma> allPragmas;

    CodeChanger(ArrayList<String> codeLines, ArrayList<PragmaScope> pragmaScopes, ArrayList<AutoNode> HIRs) {
        this.codeLines = codeLines;
        this.pragmaScopes = pragmaScopes;
        this.HIRs = HIRs;
        this.allPragmas = new ArrayList<>();
    }

    void codeVariantsTest() throws Exception {
        checkIfAllPragmaVarsAreDifferent();
        ArrayList<String> codeChanged = changeCCode();
        generateFileWithCode(codeChanged);
        CodeExecutor codeExecutor = new CodeExecutor(testCodeFile);
        if (codeExecutor.compile())
            codeExecutor.exec(allPragmas);
        else
            System.out.println("No tests will be performed. Fix any warning or error given by the compiler.");
        codeExecutor.delete();
    }

    private ArrayList<String> changeCCode() throws IOException {
        HashMap<Integer, ArrayList<String>> codeToAddByIndex = new HashMap<>();

        for (int i = 0; i < HIRs.size(); i++) {
            int scopeBegin = pragmaScopes.get(i).getStartIndex() + 1;
            int scopeEnd = pragmaScopes.get(i).getEndIndex();
            ArrayList<AutoNode> children = HIRs.get(i).getChildren();

            if (children.size() >= 2) {
                AutoNode n1 = children.get(0);
                AutoNode n2 = children.get(1);
                if ((n1.getInfo().equals("explore") || n1.getInfo().equals("random")) && n2.getInfo().equals("max_abs_error")) {
                    ArrayList<AutoNode> exploreChildren = n1.getChildren();
                    String exploreVarName = exploreChildren.get(0).getInfo();
                    ArrayList<AutoNode> varChildren = exploreChildren.get(0).getChildren();
                    String startValue = varChildren.get(0).getInfo();
                    String endValue = varChildren.get(1).getInfo();
                    String inc = "1";
                    if (varChildren.size() == 3)
                        inc = varChildren.get(2).getInfo();

                    ArrayList<AutoNode> max_abs_errorChildren = n2.getChildren();
                    String max_abs_errorVarName = max_abs_errorChildren.get(0).getInfo();
                    String max_abs_errorValue = max_abs_errorChildren.get(1).getInfo();

                    ArrayList<AutoNode> referenceChildren = exploreChildren.get(1).getChildren();
                    String referenceExecution = referenceChildren.get(1).getInfo();

                    Pragma p = new Pragma(n1.getInfo(), exploreVarName, startValue, endValue, inc, max_abs_errorVarName, max_abs_errorValue, referenceExecution);
                    allPragmas.add(p);

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
        InputStream is = CodeChanger.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            lines.add(line);
        bufferedReader.close();
        return lines;
    }

    private void adjustCode(ArrayList<String> code, Pragma p) {
        String stmt1 = "";
        String stmt2 = "";
        String stmt3 = "";
        if (p.type.equals("explore")) {
            stmt1 = p.varName + " = " + p.startValue;
            stmt2 = p.varName + " <= " + p.endValue;
            stmt3 = p.varName + " += " + p.controlValue;
        } else if (p.type.equals("random")) {
            stmt1 = p.varName + " = " + p.startValue + " + rand() % (" + p.endValue + " - " + p.startValue + " + 1), _TUNER_ITERATOR_" + p.varName + " = 0";
            stmt2 = "_TUNER_ITERATOR_" + p.varName + " < " + p.controlValue;
            stmt3 = p.varName + " = " + p.startValue + " + rand() % (" + p.endValue + " - " + p.startValue + " + 1)," + "_TUNER_ITERATOR_" + p.varName + "++";
        }

        for (int i = 0; i < code.size(); i++) {
            String line = code.get(i);
            if (p.type.equals("explore"))
                line = line.replaceAll("iteratorForRandom", "");
            else if (p.type.equals("random"))
                line = line.replaceAll("iteratorForRandom", "int _TUNER_ITERATOR_" + p.varName + ";");
            line = line.replaceAll("statement1", stmt1);
            line = line.replaceAll("statement2", stmt2);
            line = line.replaceAll("statement3", stmt3);
            line = line.replaceAll("exploreVarName", p.varName);
            line = line.replaceAll("max_abs_errorVarName", p.max_abs_errorVarName);
            code.set(i, line);
        }
    }

    private void checkIfAllPragmaVarsAreDifferent() throws Exception {
        ArrayList<String> allVariables = new ArrayList<>();
        for (AutoNode root : HIRs) {
            if (root.getChildren().size() > 0) {
                AutoNode child = root.getChildren().get(0);
                if (child.getChildren().size() > 0) {
                    String grandchild = child.getChildren().get(0).getInfo();
                    allVariables.add(grandchild);
                }
            }
        }
        HashSet<String> variablesSet = new HashSet<>(allVariables);
        if (variablesSet.size() != allVariables.size())
            throw new Exception("The variables of different pragmas cannot have the same name.");
    }

    private static void updateTemplates() {
        includes_global = templates_folder + "includes_global.c";
        includes_linux = templates_folder + "includes_linux.c";
        includes_windows = templates_folder + "includes_windows.c";
        pragma_footer1_linux = templates_folder + "pragma_footer1_linux.c";
        pragma_footer1_windows = templates_folder + "pragma_footer1_windows.c";
        pragma_footer2 = templates_folder + "pragma_footer2.c";
        pragma_header1_linux = templates_folder + "pragma_header1_linux.c";
        pragma_header1_windows = templates_folder + "pragma_header1_windows.c";
        pragma_header2 = templates_folder + "pragma_header2.c";
    }

    private static boolean setIsJar() {
        URL path = CodeChanger.class.getResource("CodeChanger.class");
        if (path.toString().startsWith("jar:"))
            return true;
        else
            updateTemplates();
        return false;
    }
}
