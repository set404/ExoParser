package tf;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.OffersArray;
import config.Property;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class TrafficFactoryAuth {
    //Campaign id from tf for search by name
    public static final String[] TF_GROUP_ID = OffersArray.TrafficFactory.GROUP;

    public static List<String> getInfoFromTF() throws IOException {
        //Parse csrf token
        List<String> stat = new ArrayList<>();
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

        for (String offer : TF_GROUP_ID) {
            stat.add(parse(response, offer));
        }
        return stat;
    }

    private static String parse(Connection.Response response, String offerId) throws IOException {
        System.out.println("Parse offer - " + offerId);
        LocalDate date = LocalDate.now().minusDays(1);
        response = Jsoup
                .connect("https://main.trafficfactory.biz/stats/campaigns/" + date + "-00-00/" + date + "-23-59?campaign_name=" + offerId)
                .cookies(response.cookies())
                .execute();

        Element elem = response.parse().select("[class=\"hg-admin-row hg-admin-row-total\"]").first();
        assert elem != null;
        String clicks = elem.getElementsByClass("hg-admin-list-td-deliveries").html().replace("&nbsp;", "");
        String price = elem.getElementsByClass("hg-admin-list-td-total").html().replace("&nbsp;", "").replace("$", "");
        return clicks + "\t" + price;
    }
}



