package trafficfactory;

import adcombo.AdcomboStats;
import com.google.gson.Gson;
import config.OffersArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrafficFactoryStats {

    private static final int[] TF_ADCO_CAMPAIGN_ID = OffersArray.TrafficFactory.CAMPAIGN;
    static List<String> offerStats = new ArrayList<>();

    public static StringBuilder parseTf() throws IOException {

        List<String> trafficStats = TrafficFactoryAuth.getInfoFromTF();
        Gson gson = new Gson();

        TrafficFactoryStatsEntity entity = gson.fromJson(AdcomboStats.getStat("trafficfactory"),
                TrafficFactoryStatsEntity.class);

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

