import config.Property;
import exo.ExoStats;
import tf.TrafficFactoryStats;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.io.IOException;

public class AdsParser {
    public static void main(String[] args) throws IOException {

        String downloadDir = Property.downloadDir;

        StringBuilder result = new StringBuilder();
        result.append(TrafficFactoryStats.parseTf()).append("\n\n\n").append(ExoStats.parseExo());
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
        System.out.println("Success");
    }
}

