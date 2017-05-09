package tuner;

import java.io.File;
import java.io.IOException;

public class CodeExecutor {
	private String executableName;
	private String filePath;

	public CodeExecutor(String filePath){
		if(!isValid(filePath))
			throw new IllegalArgumentException("Invalid C file path.");

		this.filePath = filePath;

		prepare();
	}

	/**
	 * Builds the name of the c executable for the file located in filePath.
	 */
	private void prepare(){
		File f = new File(filePath);
		String fileName = f.getName();
		int p = fileName.lastIndexOf(".");

		executableName = fileName.substring(0, p).trim();

		if(p == -1 || !executableName.matches("\\w+") || !f.isFile())
			executableName = "";
	}

	/**
	 * Checks if the file is valid.
	 * @return true if valid, else false
	 */
	public boolean isValid(String filePath){
		File file = new File(filePath);
		return file.canRead();
	}

	/**
	 * Compiles the c file located in filePath.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void compile() throws IOException, InterruptedException {
		ProcessBuilder compile = new ProcessBuilder("gcc", "-Wall", "-o" + executableName, filePath);

		run(compile);
	}

	/**
	 * Executa o ficheiro c.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void exec() throws IOException, InterruptedException {
		ProcessBuilder execute = new ProcessBuilder(executableName + ".exe");

		run(execute);
	}

	/**
	 * Runs the desired command.
	 * @param pb
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void run(ProcessBuilder pb) throws IOException, InterruptedException{
		pb.inheritIO();

		Process runner = pb.start();

		runner.waitFor();

		if (runner.exitValue() == -1) {
			System.err.println("ERROR IN RUN!");
			System.exit(-1);
		}
	}

	/**
	 * Deletes the Executable and the File.
	 */
	public void delete(){
		deleteCompiled();
		deleteFile();
	}

	/**
	 * Deletes the executable.
	 */
	public void deleteCompiled(){
		new File(executableName).delete();
	}

	/**
	 * Deletes the file.
	 */
	public void deleteFile(){
		new File(filePath).delete();
	}

}
