package tuner;

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
        for (int i = 0; i < args.length; i++) {
            try {
                System.out.println("\n" + args[i] + ":");
                FileReader fileReader = new FileReader(args[i]);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                Parser parser = new Parser(bufferedReader);
                // for(Node root : parser.getSyntacticAnalysisTrees()) printTree(root);
                SemanticAnalyser semanticAnalyser = new SemanticAnalyser(parser.getCodeLines(), parser.getSyntacticAnalysisTrees());
                // for(Node root : semanticAnalyser.getHIRs()) printTree(root);
                CodeChanger codeChanger = new CodeChanger(parser.getCodeLines(), parser.getPragmaIndexes());
                codeChanger.codeVariantsTest();
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
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

    private static void printTree(Node root) {
        int indentation = 0;
        StringBuilder sb = new StringBuilder();
        printTreeAux(root, sb, indentation);
        System.out.println(sb);
    }

    private static void printTreeAux(Node node, StringBuilder stringBuilder, int indentation) {
        for (int i = 0; i < indentation; i++)
            stringBuilder.append(" ");
        stringBuilder.append(node.getInfo()).append("\n");

        for (Node n : node.getChildren())
            printTreeAux(n, stringBuilder, indentation + 1);
    }

}
