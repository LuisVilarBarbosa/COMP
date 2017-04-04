package tuner;

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

	/**
	 * Procura o token #pragma na sequence.
	 * Coloca o index da sequence a começar no #pragma.
	 */
	private void findPragmas() {
		while(sequence.hasNext()) {
			sequence.nextToken();

			// skip C code
			if("#pragma".equalsIgnoreCase(sequence.getCurrentToken().getToken()))
				pragmaIndexes.add(sequence.getTokenIndex() - 1);
		}
		this.sequence.setTokenIndex(0);
	}

	/**
	 * TODO: Se calhar a parte "do" desta funçao pode ir para o execute do Command.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void codeVariantsTest() throws IOException, InterruptedException {
		boolean codeChanged;
		do {
			codeChanged = changeCCode();
			generateFileWithCode();
			Command command = new Command(testCodeFile);
			command.compile();
			long runTime = run(command);
			System.out.println("Running time: " + runTime / Math.pow(10, 6) + " milliseconds");

			command.delete();
		} while (codeChanged);
	}

	/**
	 * TODO
	 * @return
	 */
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
			Token token = sequence.getCurrentToken();
			while (sequence.hasNext()){
				file.write(token.getToken().concat(" ").getBytes());
				token = sequence.nextToken();
			}
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

}
