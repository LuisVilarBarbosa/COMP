package tuner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
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
				p.parse(bufferedReader);
				// change C code
				// generate and run file with new code
				long runTime = run(args[i]);

				System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				System.out.println("Syntactic analysis error on token '" + e.getMessage() + "'. Token number: " + e.getErrorOffset());
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

	private static long run(String filePath) throws IOException, InterruptedException {
		Command cmd = new Command(filePath);
		long iniTime = 0;
		long endTime = 0;

		cmd.compile();

		if(cmd.isValid()){
			iniTime = System.nanoTime();

			cmd.exec();

			endTime = System.nanoTime();
		}

		cmd.delete();

		return endTime - iniTime;   // nanoseconds
	}

}
