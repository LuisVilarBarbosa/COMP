package tuner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {
	private String executableName;
	private String filePath;

	public Command(String filePath){
		this.filePath = filePath;

		if(!isValid(this.filePath))
			throw new IllegalArgumentException("Invalid C file path.");

		prepare();
	}

	/**
	 * Builds the name of the c executable for the file located in filePath.
	 */
	private void prepare(){
		File f = new File(filePath);
		String fileName = f.getName();
		int p = fileName.lastIndexOf(".");

		executableName = fileName.substring(0, p);

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
		ProcessBuilder compile = new ProcessBuilder("gcc", "-Wall", "-o " + executableName, filePath);
		Process comp = compile.start();

		String str;
		InputStream es = comp.getErrorStream();
		InputStreamReader esr = new InputStreamReader(es);
		BufferedReader ebr = new BufferedReader(esr);
		while ((str = ebr.readLine()) != null)
			System.out.println(str);

		comp.waitFor();

		if (comp.exitValue() == -1) {
			System.err.println("ERROR IN COMPILE!");
			System.exit(-1);
		}
	}

	/**
	 * Executa o ficheiro c.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void exec() throws IOException, InterruptedException {
		ProcessBuilder execute = new ProcessBuilder(executableName + ".exe");
		Process exec = execute.start();

		String str;
		InputStream es = exec.getErrorStream();
		InputStreamReader esr = new InputStreamReader(es);
		BufferedReader ebr = new BufferedReader(esr);
		while ((str = ebr.readLine()) != null)
			System.out.println(str);

		exec.waitFor();

		if (exec.exitValue() == -1) {
			System.err.println("ERROR IN EXECUTION!");
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
