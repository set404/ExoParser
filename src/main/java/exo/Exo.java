package exo;

import adcombo.AuthAdcombo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import tf.Entity;
import config.Property;

import java.io.IOException;
import java.time.LocalDate;


public class Exo {

    public static final String[] EXO_GROUP_ID = {"247790", "249238", "253608", "253608", "248772"};
    public static final int[] EXO_ADCO_CAMPAIGN_ID = {1781, 6519, 23313, 16172, 17341};

    static LocalDate date = LocalDate.now();

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
        Entity entity = gson.fromJson(AuthAdcombo.getStat("exo"), Entity.class);


        String token = getAuthToken();
        StringBuilder groups = new StringBuilder();
        for (int i = 0; i < EXO_GROUP_ID.length; i++) {
            for (int j = 0; j < entity.objects.size(); j++) {
                if (entity.objects.get(j).group_by == EXO_ADCO_CAMPAIGN_ID[i]) {
                    groups.append(parse(EXO_GROUP_ID[i], token)).append("\t").append(entity.objects.get(j).toString()).append("\n");
                }
            }
        }
        return groups;
    }

    private static String parse(String group, String token) throws IOException {
        System.out.println("Parse group - " + group);
        Connection.Response response = Jsoup
                .connect("https://api.exoclick.com/v2/statistics/a/campaign?groupid=" + group + "&date-to=" + date + "&date-from=" + date.minusDays(1) + "&include=totals&detailed=false")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .execute();

        Gson gson = new Gson();
        Statistic statistic = gson.fromJson(response.body(), Statistic.class);
        return statistic.resultTotal.toString();
    }
}
