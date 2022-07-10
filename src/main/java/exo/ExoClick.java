package exo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.OffersArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import config.Property;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class ExoClick {

    public static final String[] EXO_GROUP_ID = OffersArray.Exo.GROUP;
    public static final Integer[] EXO_ADCO_CAMPAIGN_ID = OffersArray.Exo.CAMPAIGN;

    private static String getAuthToken() throws IOException {

        String userName = Property.exoUserName;
        String password = Property.exoPassword;

        String jsonBody = """
                {
                "password": "%s",
                "username": "%s"
                }
                """.formatted(password, userName);
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

    public static Map<Integer, String> getStat(LocalDate dateStart, LocalDate dateEnd) throws Exception {
        Map<Integer, String> stat = new HashMap<>();
        String token = getAuthToken();

        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < EXO_GROUP_ID.length; i++) {

            String groupId = EXO_GROUP_ID[i];
            int campaignId = EXO_ADCO_CAMPAIGN_ID[i];
            service.execute(() -> {
                try {
                    stat.put(campaignId, parse(groupId, token, dateStart, dateEnd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        service.shutdown();
        if (!service.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("Parse ExoClick timed out error");
        }
        return stat;
    }

    private static String parse(String group, String token, LocalDate dateStart, LocalDate dateEnd) throws IOException {
        System.out.println("Parse group - " + group);
        Connection.Response response = Jsoup
                .connect("https://api.exoclick.com/v2/statistics/a/campaign?groupid=" + group + "&date-to=" + dateStart + "&date-from=" + dateEnd.minusDays(1) + "&include=totals&detailed=false")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .execute();

        Gson gson = new Gson();
        ExoStatsEntity statistic = gson.fromJson(response.body(), ExoStatsEntity.class);

        if (statistic.resultTotal == null) {
            System.out.println("Group id - " + group + " not found");
            return "0\t0";
        } else {
            return statistic.resultTotal.toString();
        }

    }

}
