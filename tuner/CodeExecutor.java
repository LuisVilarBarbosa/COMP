package tuner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class CodeExecutor {
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
    void exec() throws IOException, InterruptedException {
        ArrayList<String> outputMessages;
        ArrayList<String> pragmas = new ArrayList<>();
        ArrayList<Double> best_pragma_value = new ArrayList<>();
        ArrayList<Double> best_execution_time = new ArrayList<>();
        Command command = new Command(executableName);

        command.setStoreOutput(true);
        command.run();

        outputMessages = command.getOutputStreamLines();
        for (String s : outputMessages) {
            String[] temp = s.split("_");
            if (!temp[0].isEmpty() && Character.isLetter(temp[0].charAt(0))) {
                if (pragmas.contains(temp[0])) {
                    int index = pragmas.indexOf(temp[0]);
                    Double new_exec_time = Double.parseDouble(temp[2]);
                    if (Double.compare(new_exec_time, best_execution_time.get(index)) < 0) {
                        best_pragma_value.set(index, Double.parseDouble(temp[1]));
                        best_execution_time.set(index, new_exec_time);
                    }
                } else {
                    pragmas.add(temp[0]);
                    best_pragma_value.add(Double.parseDouble(temp[1]));
                    best_execution_time.add(Double.parseDouble(temp[2]));
                }
            }
        }

        for (int i = 0; i < pragmas.size(); i++)
            System.out.println("Best execution of " + pragmas.get(i) + ": " + best_pragma_value.get(i));
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
