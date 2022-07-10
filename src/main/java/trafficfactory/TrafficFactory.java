package trafficfactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import config.OffersArray;
import config.Property;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class TrafficFactory {
    //Campaign id from tf for search by name
    public static final String[] TF_GROUP_ID = OffersArray.TrafficFactory.GROUP;
    private static final int[] TF_ADCO_CAMPAIGN_ID = OffersArray.TrafficFactory.CAMPAIGN;


    public static Map<Integer, String> getStat(LocalDate dateStart, LocalDate dateEnd) throws IOException {
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

        for (int i = 0; i < TF_GROUP_ID.length; i++) {
            stat.put(TF_ADCO_CAMPAIGN_ID[i], parse(response, TF_GROUP_ID[i], dateStart, dateEnd));

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
        assert elem != null;
        String clicks = elem.getElementsByClass("hg-admin-list-td-deliveries").html().replace("&nbsp;", "");
        String price = elem.getElementsByClass("hg-admin-list-td-total").html().replace("&nbsp;", "").replace("$", "");
        return clicks + "\t" + price;
    }
}



