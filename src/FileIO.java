import java.io.*;

/**
 * Created by Administrator on 2015/11/17.
 */
public class FileIO {

    public static String read(String url){
        String contents;
        try {
            BufferedReader br = new BufferedReader(new FileReader(url));
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }
            contents = stringBuilder.toString();
        } catch (IOException e1) {
            return "";
        }
        return contents;
    }

    public static boolean write(String file, String content){
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } catch ( IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if ( writer != null)
                    writer.close( );
            } catch ( IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
