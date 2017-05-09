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
		Command command = new Command("gcc", "-Wall", "-o" + executableName, filePath);
		command.run();
	}

	/**
	 * Executa o ficheiro c.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void exec() throws IOException, InterruptedException {
		Command command = new Command(executableName + ".exe");
		command.run();
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
