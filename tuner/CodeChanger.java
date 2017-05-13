package tuner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CodeChanger {
    private ArrayList<String> codeLines;
    private String testCodeFile = "test.c";
    private ArrayList<Integer> pragmaIndexes;
    private ArrayList<Node> HIRs;

    public CodeChanger(ArrayList<String> codeLines, ArrayList<Integer> pragmaIndexes, ArrayList<Node> HIRs) throws Exception {
        this.codeLines = codeLines;
        this.pragmaIndexes = pragmaIndexes;
        this.HIRs = HIRs;
    }

    /**
     * Testa diferentes versoes do codigo.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void codeVariantsTest() throws IOException, InterruptedException {
        ArrayList<String> codeChanged;
        while ((codeChanged = changeCCode()) != null) {
            generateFileWithCode(codeChanged);
            CodeExecutor codeExecutor = new CodeExecutor(testCodeFile);
            codeExecutor.compile();
            long runTime = run(codeExecutor);
            System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");

            codeExecutor.delete();
        }
    }

    /**
     * TODO
     *
     * @return
     */
    private ArrayList<String> changeCCode() {
        ArrayList<String> codeChanged = codeLines;
        boolean changed = false;

        for (int i = 0, j = pragmaIndexes.size() - 1; i <= j; i++, j--) {
            for (int k = 1; k < codeChanged.size() - 1; k++) {
                // update "changed" and change the code looking to the semantic analysis data
            }
        }

        if (changed)
            return codeChanged;
        return null;
    }

    private void generateFileWithCode(ArrayList<String> code) throws IOException {
        FileOutputStream file = new FileOutputStream(testCodeFile);
        for (String line : code)
            file.write(line.getBytes());
        file.close();
    }

    private long run(CodeExecutor codeExecutor) throws IOException, InterruptedException {
        long iniTime = System.nanoTime();
        codeExecutor.exec();
        long endTime = System.nanoTime();
        return endTime - iniTime;   // nanoseconds
    }

}
