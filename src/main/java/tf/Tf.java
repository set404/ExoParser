package tf;

import com.google.gson.Gson;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

public class Tf {
    private static final int[] TF_ADCO_CAMPAIGN_ID = {34393, 14106, 1781, 6519, 24940, 31565, 17341, 5530, 32920,
            18124, 16172, 11846, 31981, 3388, 28586, 31535};
    static List<String> offerStats = new ArrayList<>();

    public static StringBuilder parseTf() throws IOException, URISyntaxException {

        long timeStart = LocalDate.now().minusDays(1).toEpochSecond(LocalTime.MIN, ZoneOffset.of("+03:00"));
        long timeEnd = LocalDate.now().toEpochSecond(LocalTime.MIN, ZoneOffset.of("+03:00")) - 1;
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://my.adcombo.com/api/stats?page=1&count=100&order=desc&sorting=group_by&stat_type=pp_stat&ts=" + timeStart + "&te=" + timeEnd + "&by_last_activity=false&percentage=false&normalize=false&comparing=false&group_by=offer_id&tz_offset=-10800&cols=uniq_traffic&cols=orders_confirmed&cols=orders_hold&cols=orders_rejected&cols=orders_trashed&cols=orders_total&cols=approve_total&cols=cr_uniq&cols=ctr_uniq&cols=user_orders_confirmed_income&cols=user_total_hold_income&cols=user_total_income&utm_source=tf&utm_source=-2&epc_factor=0&force=true"));
        }

        System.out.println("Press Enter...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        List<String> trafficStats = Auth.getInfoFromTF();
        Reader reader = Files.newBufferedReader(Paths.get("/Users/kot/Downloads/stats.json"));
        Gson gson = new Gson();
        Entity entity = gson.fromJson(reader, Entity.class);

        for (int i = 0; i < TF_ADCO_CAMPAIGN_ID.length; i++) {
            for (int j = 0; j < entity.objects.size(); j++) {
                if (entity.objects.get(j).group_by == TF_ADCO_CAMPAIGN_ID[i]) {
                    offerStats.add(trafficStats.get(i) + "\t" + entity.objects.get(j).toString());
                    break;
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String offerStat : offerStats) {
            stringBuilder.append(offerStat).append("\n");
        }
        return stringBuilder;
    }
}

