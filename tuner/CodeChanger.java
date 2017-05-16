package tuner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CodeChanger {
    private final String testCodeFile = "_TUNER_FILE_WITH_COMPLETE_SOURCE_CODE_TO_TEST.c";
    private ArrayList<String> codeLines;
    private ArrayList<Integer> pragmaIndexes;
    private ArrayList<Node> HIRs;

    public CodeChanger(ArrayList<String> codeLines, ArrayList<Integer> pragmaIndexes, ArrayList<Node> HIRs) throws Exception {
        this.codeLines = codeLines;
        this.pragmaIndexes = pragmaIndexes;
        this.HIRs = HIRs;
    }


    public void codeVariantsTest() throws IOException, InterruptedException {
        ArrayList<String> codeChanged = changeCCode();
        generateFileWithCode(codeChanged);
        CodeExecutor codeExecutor = new CodeExecutor(testCodeFile);
        codeExecutor.compile();
        long runTime = run(codeExecutor);
        System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");
        codeExecutor.delete();
    }

    private ArrayList<String> changeCCode() {
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
                    String varName = exploreChildren.get(0).getInfo();
                    ArrayList<Node> varChildren = exploreChildren.get(0).getChildren();
                    String startValue = varChildren.get(0).getInfo();
                    String endValue = varChildren.get(1).getInfo();
                    String tunerVarName = "_TUNER_VAR_" + varName;
                    String tunerRepeatVarName = "_TUNER_REPEAT_VAR_" + varName;

                    ArrayList<String> newStartCode = new ArrayList<>();
                    newStartCode.add("for (float " + tunerVarName + " = " + startValue + "; " + tunerVarName + " < " + endValue + "; " + tunerVarName + "++" + ") {");
                    newStartCode.add("for (int " + tunerRepeatVarName + " = 1; " + tunerRepeatVarName + " < 5; " + tunerRepeatVarName + "++) {");

                    String time = "1";  // to remove
                    ArrayList<String> newEndCode = new ArrayList<>();
                    newEndCode.add("}");
                    newEndCode.add("printf(\"" + varName + "_%lf_%lf\\n\", " + tunerVarName + ", " + time + " / 5.0" + ");");
                    newEndCode.add("}");

                    codeChanged.addAll(scopeEnd, newEndCode);
                    codeChanged.addAll(scopeBegin, newStartCode);
                }
            }
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

    private long run(CodeExecutor codeExecutor) throws IOException, InterruptedException {
        long iniTime = System.nanoTime();
        codeExecutor.exec();
        long endTime = System.nanoTime();
        return endTime - iniTime;   // nanoseconds
    }

}
