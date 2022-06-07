import config.Property;
import exo.Exo;
import tf.Tf;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public class AdsParser {
    public static void main(String[] args) throws IOException, URISyntaxException {

        String downloadDir = Property.downloadDir;

        StringBuilder result = new StringBuilder();
        result.append(Tf.parseTf()).append("\n\n\n").append(Exo.parseExo());
        System.out.println("Parse successfully");


        try (FileWriter writer = new FileWriter(downloadDir + "1234.txt", false)) {
            writer.write(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Copying to buffer");
        String ctc = result.toString();
        StringSelection stringSelection = new StringSelection(ctc);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);

        File stats = new File(downloadDir + "stats.json");
        File stats2 = new File(downloadDir + "stats (1).json");

        if (stats.delete() && stats2.delete()) {
            System.out.println("Success!!");
        }
    }
}
