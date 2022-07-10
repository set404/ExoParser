import adcombo.AdcomboStats;
import adcombo.AdcomboStatsEntity;
import config.OffersArray;
import config.Property;
import exo.ExoClick;
import trafficfactory.TrafficFactory;
import util.CombineStat;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class AdsParser {
    public static void main(String[] args) throws IOException {

        String downloadDir = Property.downloadDir;

        LocalDate dateStart = OffersArray.parseDateStart;
        LocalDate dateEnd = OffersArray.parseDateEnd;
        StringBuilder result = new StringBuilder();

        int[] exoOffers = OffersArray.Exo.CAMPAIGN;
        int[] tfOffers = OffersArray.TrafficFactory.CAMPAIGN;

        AdcomboStatsEntity adcoTF = AdcomboStats.getStat("tf", dateStart, dateEnd);
        AdcomboStatsEntity adcoExo = AdcomboStats.getStat("exo", dateStart, dateEnd);

        Map<Integer, String> tfStats = TrafficFactory.getStat(dateStart, dateEnd);
        Map<Integer, String> exoStats = ExoClick.getStat(dateStart, dateEnd);

        result.append(CombineStat.combine(tfStats, adcoTF, tfOffers)).append("\n\n\n").append(CombineStat.combine(exoStats, adcoExo, exoOffers));
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

