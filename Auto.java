import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Auto {

    public static void main(String[] args) {
        if (args.length == 0)
            System.out.println("Usage: Auto <C filename>*");

        try {
            verifyFilesNames(args);
        } catch (IllegalArgumentException e) {
            return;
        }

        // each arg is a filename
        Parser p = new Parser();
        for (int i = 0; i < args.length; i++) {
            try {
                System.out.println("\n" + args[i] + ":");
                FileReader fileReader = new FileReader(args[i]);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ArrayList<String> tokens = p.parse(bufferedReader);
                printStrings(tokens);
                // change C code
                // generate file with new code
                String testCode = args[i];  // to change
                compile(testCode);
                long runTime = run();
                System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");
                deleteCompiled();
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void verifyFilesNames(String[] filesNames) {
        ArrayList<String> errors = new ArrayList<>();
        for (int i = 0; i < filesNames.length; i++)
            if (!filesNames[i].endsWith(".c"))
                errors.add(filesNames[i]);
        if (errors.size() != 0) {
            System.err.println("Invalid C files names:");
            printStrings(errors);
            throw new IllegalArgumentException();
        }
    }

    private static void printStrings(ArrayList<String> tokens) {
        for (int i = 0; i < tokens.size(); i++)
            System.out.println(tokens.get(i));
    }

    private static void compile(String file) throws IOException, InterruptedException {
        Command.exec("gcc -Wall " + file);
    }

    private static long run() throws IOException, InterruptedException {
        long iniTime = System.nanoTime();
        Command.exec("a.exe");
        long endTime = System.nanoTime();
        return endTime - iniTime;   // nanoseconds
    }

    private static void deleteCompiled() throws IOException, InterruptedException {
        Command.exec("del a.exe");
    }

}
