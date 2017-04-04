package tuner;

import java.io.File;
import java.io.IOException;

public class Command {
	private String executableName = "";
	private String filePath;

	public Command(String filePath){
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

		executableName = fileName.substring(0, p);

		if(p == -1 || !executableName.matches("\\w+") || !f.isFile())
			executableName = "";
	}

	/**
	 * Checks if the file was valid.
	 * @return true if valid, else false
	 */
	public boolean isValid(){		
		return !"".equals(executableName);	
	}

	/**
	 * Compiles the c file located in filePath.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void compile() throws IOException, InterruptedException {
		ProcessBuilder compile = new ProcessBuilder("gcc", "-Wall", "-o" + executableName, filePath);
		Process comp = compile.start();
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
		ProcessBuilder execute = new ProcessBuilder("./" + executableName);
		Process exec = execute.start();
		exec.waitFor();

		if (exec.exitValue() == -1) {
			// that means something was written to stderr, and you can do something like
			System.err.println("ERROR IN EXECUTION!");
			System.exit(-1);
		}
	}

	/**
	 * Deletes the executable.
	 */
	public void delete() {
		new File(executableName).delete();
	}

}
