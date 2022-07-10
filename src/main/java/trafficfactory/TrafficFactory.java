package trafficfactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import config.OffersArray;
import config.Property;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class TrafficFactory {
    //Campaign id from tf for search by name
    public static final String[] TF_GROUP_ID = OffersArray.TrafficFactory.GROUP;
    private static final Integer[] TF_ADCO_CAMPAIGN_ID = OffersArray.TrafficFactory.CAMPAIGN;


    public static Map<Integer, String> getStat(LocalDate dateStart, LocalDate dateEnd) throws IOException, InterruptedException {
        //Parse csrf token
        Map<Integer, String> stat = new HashMap<>();
        Connection.Response response;
        System.out.println("Authorization...");
        response = Jsoup
                .connect("https://main.trafficfactory.biz/users/sign_in")
                .execute();

        System.out.println("Authorization complete.");
        Document doc = response.parse();
        Element meta = doc.select("[name=\"signin[_csrf_token]\"]").first();
        assert meta != null;
        String token = meta.attr("value");

        String email = Property.tfEmail;
        String password = Property.tfPassword;
        //Authorization, get cookies
        response = Jsoup
                .connect("https://main.trafficfactory.biz/users/sign_in")
                .method(Connection.Method.POST)
                .data("signin[login]", email)
                .data("signin[password]", password)
                .data("signin[_csrf_token]", token)
                .cookies(response.cookies())
                .execute();

        ExecutorService service = Executors.newFixedThreadPool(50);
        for (int i = 0; i < TF_GROUP_ID.length; i++) {

            Connection.Response responseLoop = response;
            String groupId = TF_GROUP_ID[i];
            int campaignId = TF_ADCO_CAMPAIGN_ID[i];
            service.execute(() -> {
                try {
                    stat.put(campaignId, parse(responseLoop, groupId, dateStart, dateEnd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
        service.shutdown();
        if (!service.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("Parse Traffic Factory timed out error");
        }
        return stat;
    }

    private static String parse(Connection.Response response, String offerId, LocalDate dateStart, LocalDate dateEnd) throws IOException {
        System.out.println("Parse offer - " + offerId);
        response = Jsoup
                .connect("https://main.trafficfactory.biz/stats/campaigns/" + dateStart + "-00-00/" + dateEnd.minusDays(1) + "-23-59?campaign_name=" + offerId)
                .cookies(response.cookies())
                .execute();

        Element elem = response.parse().select("[class=\"hg-admin-row hg-admin-row-total\"]").first();
        String clicks = "0";
        String price = "0";
        if (elem != null) {
            clicks = elem.getElementsByClass("hg-admin-list-td-deliveries").html().replace("&nbsp;", "");
            price = elem.getElementsByClass("hg-admin-list-td-total").html().replace("&nbsp;", "").replace("$", "");
        } else {
            System.out.println("Offer id - " + offerId + " not found");
        }
        return clicks + "\t" + price;
    }
}



