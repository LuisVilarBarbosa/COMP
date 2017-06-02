package tuner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Command {
    private ProcessBuilder processBuilder;
    /**
     * If storeOutput = true, the output will not be automatically shown.
     */
    private boolean storeOutput;
    private ArrayList<String> errorStreamLines;
    private ArrayList<String> outputStreamLines;

    Command(String... command) {
        this.processBuilder = new ProcessBuilder(command);
        this.storeOutput = false;
        this.errorStreamLines = new ArrayList<>();
        this.outputStreamLines = new ArrayList<>();
    }

    void run() throws IOException, InterruptedException {
        /* When inheritIO() is called, getErrorStream() and getInputStream() do not return data. */
        if (!this.storeOutput)
            this.processBuilder.inheritIO();

        Process process = this.processBuilder.start();

        if (this.storeOutput) {
            InputStream errorStream = process.getErrorStream();
            InputStream outputStream = process.getInputStream();
            BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(errorStream));
            BufferedReader outputStreamReader = new BufferedReader(new InputStreamReader(outputStream));
            String err_line;
            String out_line = "";
            while ((err_line = errorStreamReader.readLine()) != null || (out_line = outputStreamReader.readLine()) != null) {
                errorStreamLines.add(err_line);
                outputStreamLines.add(out_line);
            }
        }

        process.waitFor();
    }

    void setStoreOutput() {
        this.storeOutput = true;
    }

    public ArrayList<String> getErrorStreamLines() {
        return errorStreamLines;
    }

    ArrayList<String> getOutputStreamLines() {
        return outputStreamLines;
    }

}
