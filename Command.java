import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {

    public static void exec(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str;
        while ((str = br.readLine()) != null)
            System.out.println(str);
        p.waitFor();
    }

}
