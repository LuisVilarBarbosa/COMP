package tuner;

import java.io.*;
import java.util.ArrayList;

public class CodeChanger {
    private static final boolean isWindows = System.getProperty("os.name").contains("Win");

    private static final String templates_folder = "c_template_code/";
    private static final String includes_global = templates_folder + "includes_global.c";
    private static final String includes_linux = templates_folder + "includes_linux.c";
    private static final String includes_windows = templates_folder + "includes_windows.c";
    private static final String pragma_footer1_windows = templates_folder + "pragma_footer1_windows.c";
    private static final String pragma_footer2 = templates_folder + "pragma_footer2.c";
    private static final String pragma_header1_windows = templates_folder + "pragma_header1_windows.c";
    private static final String pragma_header2 = templates_folder + "pragma_header2.c";

    private final String testCodeFile = "_TUNER_FILE_WITH_COMPLETE_SOURCE_CODE_TO_TEST.c";
    private ArrayList<String> codeLines;
    private ArrayList<Integer> pragmaIndexes;
    private ArrayList<Node> HIRs;

    private String varName;

    CodeChanger(ArrayList<String> codeLines, ArrayList<Integer> pragmaIndexes, ArrayList<Node> HIRs) {
        this.codeLines = codeLines;
        this.pragmaIndexes = pragmaIndexes;
        this.HIRs = HIRs;
    }

    void codeVariantsTest() throws IOException, InterruptedException {
        ArrayList<String> codeChanged = changeCCode();
        generateFileWithCode(codeChanged);
        CodeExecutor codeExecutor = new CodeExecutor(testCodeFile);
        if (codeExecutor.compile())
            codeExecutor.exec(varName);
        else
            System.out.println("It was not possible to compile the generated file. Verify if your code has any bugs.");
        codeExecutor.delete();
    }

    private ArrayList<String> changeCCode() throws IOException {
        ArrayList<String> codeChanged = codeLines;

        for (int i = 0; i < HIRs.size(); i++) {
            int scopeBegin = pragmaIndexes.get(i) + 1;
            int scopeEnd = pragmaIndexes.get(pragmaIndexes.size() - 1 - i);
            ArrayList<Node> children = HIRs.get(i).getChildren();

            if (children.size() >= 2) {
                Node n1 = children.get(0);
                Node n2 = children.get(1);
                if (n1.getInfo().equals("explore") && n2.getInfo().equals("max_abs_error")) {
                    ArrayList<Node> exploreChildren = n1.getChildren();
                    varName = exploreChildren.get(0).getInfo();
                    ArrayList<Node> varChildren = exploreChildren.get(0).getChildren();
                    String startValue = varChildren.get(0).getInfo();
                    String endValue = varChildren.get(1).getInfo();

                    ArrayList<String> newEndCode = loadScopeEnd();
                    adjustCode(newEndCode, varName, startValue, endValue);
                    ArrayList<String> newStartCode = loadScopeBegin();
                    adjustCode(newStartCode, varName, startValue, endValue);

                    codeChanged.addAll(scopeEnd, newEndCode);
                    codeChanged.addAll(scopeBegin, newStartCode);
                }
            }
        }
        codeChanged.addAll(0, loadIncludes());

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
        /*else
            pragmaHeader.addAll(loadFile());*/
        pragmaHeader.addAll(loadFile(pragma_header2));
        return pragmaHeader;
    }

    private ArrayList<String> loadScopeEnd() throws IOException {
        ArrayList<String> pragmaFooter = new ArrayList<>();
        if (isWindows)
            pragmaFooter.addAll(loadFile(pragma_footer1_windows));
        /*else
            pragmaFooter.addAll(loadFile());*/
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

    private void adjustCode(ArrayList<String> code, String varName, String startValue, String endValue) {
        for (int i = 0; i < code.size(); i++) {
            String line = code.get(i);
            line = line.replaceAll("varType", "double");
            line = line.replaceAll("varName", varName);
            line = line.replaceAll("startValue", startValue);
            line = line.replaceAll("endValue", endValue);
            code.set(i, line);
        }
    }

}
