package exo;

import adcombo.AdcomboStats;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.OffersArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import tf.TrafficFactoryStatsEntity;
import config.Property;

import java.io.IOException;
import java.time.LocalDate;


public class ExoStats {

    public static final String[] EXO_GROUP_ID = OffersArray.Exo.GROUP;
    public static final int[] EXO_ADCO_CAMPAIGN_ID = OffersArray.Exo.CAMPAIGN;
    static LocalDate date = LocalDate.now().minusDays(1);

    private static String getAuthToken() throws IOException {

        String userName = Property.exoUserName;
        String password = Property.exoPassword;

        String jsonBody = "{\"password\": \""+ password +"\",\n" +
                "\"username\": \""+ userName +"\"}";
        Connection.Response response = Jsoup
                .connect("https://api.exoclick.com/v2/login")
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .header("Content-Type", "application/json")
                .requestBody(jsonBody)
                .execute();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.get("token").getAsString();
    }

    public static StringBuilder parseExo() throws IOException {

        Gson gson = new Gson();
        TrafficFactoryStatsEntity groupStats = gson.fromJson(AdcomboStats.getStat("exo"), TrafficFactoryStatsEntity.class);


        String token = getAuthToken();
        StringBuilder groups = new StringBuilder();
        for (int i = 0; i < EXO_GROUP_ID.length; i++) {
            for (int j = 0; j < groupStats.objects.size(); j++) {
                if (groupStats.objects.get(j).group_by == EXO_ADCO_CAMPAIGN_ID[i]) {
                    groups.append(parse(EXO_GROUP_ID[i], token)).append("\t").append(groupStats.objects.get(j).toString()).append("\n");
                }
            }
        }
        return groups;
    }

    private static String parse(String group, String token) throws IOException {
        System.out.println("Parse group - " + group);
        Connection.Response response = Jsoup
                .connect("https://api.exoclick.com/v2/statistics/a/campaign?groupid=" + group + "&date-to=" + date + "&date-from=" + date + "&include=totals&detailed=false")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .execute();

        Gson gson = new Gson();
        ExoStatsEntity statistic = gson.fromJson(response.body(), ExoStatsEntity.class);
        return statistic.resultTotal.toString();
    }
}
