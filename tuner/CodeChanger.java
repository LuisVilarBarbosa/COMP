package tuner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class CodeChanger {
    private Sequence sequence;
    private String testCodeFile = "test.c";
    private Vector<Integer> pragmaIndexes;

    public CodeChanger(Sequence sequence) {
        this.sequence = sequence;
        this.sequence.setTokenIndex(0);
        this.pragmaIndexes = new Vector<>();
        findPragmas();
    }

    private void findPragmas() {
        Token token;
        do {
            while ((token = sequence.nextToken()) != null && !token.getToken().equalsIgnoreCase("#pragma")) ;
            if (token != null)
                pragmaIndexes.add(sequence.getTokenIndex() - 1);
        } while (sequence.getCurrentToken() != null);
        this.sequence.setTokenIndex(0);
    }

    public void codeVariantsTest() throws IOException, InterruptedException {
        boolean codeChanged;
        do {
            codeChanged = changeCCode();
            generateFileWithCode();
            Command command = new Command(testCodeFile);
            command.compile();
            long runTime = run(command);
            System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");
            deleteCompiled(command);
            deleteFile(testCodeFile);
        } while (codeChanged);
    }

    private boolean changeCCode() {
        boolean changed = false;
        Token token;

        // update sequence

        sequence.setTokenIndex(0);
        return changed;
    }

    private void generateFileWithCode() {
        try {
            FileOutputStream file = new FileOutputStream(testCodeFile);
            Token token;
            while ((token = sequence.nextToken()) != null)
                file.write(token.getToken().concat(" ").getBytes());
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sequence.setTokenIndex(0);
    }

    private long run(Command command) throws IOException, InterruptedException {
        long iniTime = System.nanoTime();
        command.exec();
        long endTime = System.nanoTime();
        return endTime - iniTime;   // nanoseconds
    }

    private void deleteCompiled(Command command) {
        command.delete();
    }

    private void deleteFile(String filename) {
        new File(filename).delete();
    }

}
