package tuner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

class CodeExecutor {
    private String executableName;
    private String filePath;

    private ArrayList<Pragma> all_pragmas;

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
     * @throws IOException          ver run()
     * @throws InterruptedException ver run()
     */
    boolean compile() throws IOException, InterruptedException {
        Command command = new Command("gcc", "-Wall", "-Wno-unknown-pragmas", "-o" + executableName, filePath);
        command.setStoreOutput(true);
        command.run();
        ArrayList<String> outputStreamLines = command.getOutputStreamLines();
        ArrayList<String> errorStreamLines = command.getErrorStreamLines();
        printStrings(outputStreamLines);
        printStrings(errorStreamLines);
        return errorStreamLines.isEmpty();
    }

    /**
     * Executa o ficheiro C.
     * Guarda o valor da melhor execução de cada pragma.
     *
     * @throws IOException          ver run()
     * @throws InterruptedException ver run()
     */
    void exec(ArrayList<Pragma> all_pragmas) throws IOException, InterruptedException {
        this.all_pragmas = all_pragmas;
        ArrayList<String> outputMessages;
        Command command = new Command(executableName);

        command.setStoreOutput(true);
        command.run();

        outputMessages = command.getOutputStreamLines();
        for (String s : outputMessages) {
            String[] message = s.split("_");
            if (!message[0].isEmpty() && Character.isLetter(message[0].charAt(0)))
                checkBetterExecution(message);
        }

        for (Pragma p : all_pragmas)
            System.out.println("Best execution of " + p.varName + ": " + p.best_execution);
    }

    /**
     * Searchs if the pragma with a pragma name exists.
     *
     * @param pragma_name Pragma name to exist
     * @return True if exist, false if not
     */
    private boolean searchExistPragma(String pragma_name) {
        for (Pragma p : all_pragmas)
            if (Objects.equals(p.varName, pragma_name))
                return true;
        return false;
    }

    /**
     * Returns the pragma with var name equal to the param.
     *
     * @param pragma_name Pragma name
     * @return Pragma with param as name
     */
    private Pragma getPragma(String pragma_name) {
        if (searchExistPragma(pragma_name))
            for (Pragma p : all_pragmas)
                if (Objects.equals(p.varName, pragma_name))
                    return p;
        return null;
    }

    /**
     * Compares the previous execution time of a pragma with the new and updates the value if the new is lower.
     *
     * @param message String array with var name, var execution, and var execution time
     */
    private void checkBetterExecution(String[] message) {
        Pragma p = getPragma(message[0]);
        if (p != null) {
            Double new_exec_time = Double.parseDouble(message[2]);
            if (Double.compare(new_exec_time, p.best_execution_time) < 0) {
                p.best_execution = Double.parseDouble(message[1]);
                p.best_execution_time = new_exec_time;
            }
        }
    }

    /**
     * Deletes the Executable and the File.
     */
    void delete() {
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

    private void printStrings(ArrayList<String> strings) {
        for (String str : strings)
            System.out.println(str);
    }

}
