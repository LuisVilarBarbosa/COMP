import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {

    public static void exec(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        String str;

        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader ibr = new BufferedReader(isr);
        while ((str = ibr.readLine()) != null)
            System.out.println(str);

        InputStream es = p.getErrorStream();
        InputStreamReader esr = new InputStreamReader(es);
        BufferedReader ebr = new BufferedReader(esr);
        while ((str = ebr.readLine()) != null)
            System.out.println(str);
        p.waitFor();
    }

}
