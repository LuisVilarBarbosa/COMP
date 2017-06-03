package tuner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CodeExecutor {
    private String executableName;
    private String filePath;

    CodeExecutor(String filePath) {
        if (!isValid(filePath))
            throw new IllegalArgumentException("Invalid C file path.");

        this.filePath = filePath;

        prepare();
    }

    /**
     * Builds the name of the c executable for the file located in filePath.
     */
    private void prepare() {
        File f = new File(filePath);
        String fileName = f.getName();
        int p = fileName.lastIndexOf(".");

        executableName = fileName.substring(0, p).trim() + ".exe";

        if (p == -1 || !f.isFile())
            executableName = "";
    }

    /**
     * Checks if the file is valid.
     *
     * @return true if valid, else false
     */
    private boolean isValid(String filePath) {
        File file = new File(filePath);
        return file.canRead();
    }

    /**
     * Compiles the c file located in filePath.
     *
     * @throws IOException ver run()
     * @throws InterruptedException ver run()
     */
    void compile() throws IOException, InterruptedException {
        Command command = new Command("gcc", "-Wall", "-Wno-unknown-pragmas", "-o" + executableName, filePath);
        command.setStoreOutput(true);
        command.run();
    }

    /**
     * Executa o ficheiro c.
     *
     * @param var Variável que está a ser alterada
     * @throws IOException ver run()
     * @throws InterruptedException ver run()
     */
    void exec(String var) throws IOException, InterruptedException {
        ArrayList<String> outputMessages;
        String best_execution = null;
        long best_execution_time = 999999999;
        Command command = new Command(executableName);

        //command.setStoreOutput();
        command.run();

        outputMessages = command.getOutputStreamLines();
        for (String s : outputMessages) {
            String[] temp = s.split("_");
            if (var.equals(temp[0]) && Long.valueOf(temp[2]) < best_execution_time) {
                best_execution = temp[1];
                best_execution_time = Long.valueOf(temp[2]);
            }
        }

        System.out.println(var + ": " + best_execution);
    }

    /**
     * Deletes the Executable and the File.
     */
    public void delete() {
        deleteCompiled();
        deleteFile();
    }

    /**
     * Deletes the executable.
     */
    private void deleteCompiled() {
        new File(executableName).delete();
    }

    /**
     * Deletes the file.
     */
    private void deleteFile() {
        new File(filePath).delete();
    }

}
