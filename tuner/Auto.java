package tuner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

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
                Vector<String> c_lines = p.parse(bufferedReader);
                //CodeChanger codeChanger = new CodeChanger(sequence);
                //codeChanger.codeVariantsTest();
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }/* catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.out.println("Syntactic analysis error on token '" + e.getMessage() + "'. Token number: " + e.getErrorOffset());
                e.printStackTrace();
            }*/
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

}
