package tuner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class CodeChanger {
    private Vector<String> c_lines;
    private String testCodeFile = "test.c";
    private Vector<Integer> pragmaIndexes;

    public CodeChanger(Vector<String> c_lines) throws Exception {
        this.c_lines = c_lines;
        this.pragmaIndexes = new Vector<>();
        findPragmas();
    }

    /**
     * Procura o token #pragma na string.
     * Guarda o index da string.
     */
    private void findPragmas() throws Exception {
        for (int i = 0; i < c_lines.size(); i++) {
            if (c_lines.get(i).contains("#pragma"))
                pragmaIndexes.add(i);
        }
        if (pragmaIndexes.size() % 2 != 0)
            throw new Exception("Odd number of pragmas. For each clause must exist a start pragma and an end pragma.");
    }

    /**
     * Testa diferentes versoes do codigo.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void codeVariantsTest() throws IOException, InterruptedException {
        Vector<String> codeChanged;
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
    private Vector<String> changeCCode() {
        Vector<String> codeChanged = c_lines;
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

    private void generateFileWithCode(Vector<String> code) throws IOException {
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
