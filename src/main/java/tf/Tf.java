package tf;

import adcombo.AuthAdcombo;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tf {
    private static final int[] TF_ADCO_CAMPAIGN_ID = {34393, 14106, 1781, 6519, 24940, 31565, 17341, 5530, 32920,
            18124, 16172, 11846, 31981, 3388, 28586, 31535};
    static List<String> offerStats = new ArrayList<>();

    public static StringBuilder parseTf() throws IOException {

        List<String> trafficStats = Auth.getInfoFromTF();
        Gson gson = new Gson();

        Entity entity = gson.fromJson(AuthAdcombo.getStat("tf"), Entity.class);

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

