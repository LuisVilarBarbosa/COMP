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

    public Command(String... command) {
        this.processBuilder = new ProcessBuilder(command);
        this.storeOutput = true;
        this.errorStreamLines = new ArrayList<>();
        this.outputStreamLines = new ArrayList<>();
    }

    public void run() throws IOException, InterruptedException {
        /* When inheritIO() is called, getErrorStream() and getInputStream() do not return data. */
        if (!this.storeOutput)
            this.processBuilder.inheritIO();

        Process process = this.processBuilder.start();

        if (this.storeOutput) {
            InputStream errorStream = process.getErrorStream();
            InputStream outputStream = process.getInputStream();
            BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(errorStream));
            BufferedReader outputStreamReader = new BufferedReader(new InputStreamReader(outputStream));
            String line;
            while ((line = outputStreamReader.readLine()) != null)
                outputStreamLines.add(line);
            while ((line = errorStreamReader.readLine()) != null)
                errorStreamLines.add(line);
        }

        process.waitFor();
    }

    public boolean isStoreOutput() {
        return storeOutput;
    }

    public void setStoreOutput(boolean storeOutput) {
        this.storeOutput = storeOutput;
    }

    public ArrayList<String> getErrorStreamLines() {
        return errorStreamLines;
    }

    public ArrayList<String> getOutputStreamLines() {
        return outputStreamLines;
    }

}
