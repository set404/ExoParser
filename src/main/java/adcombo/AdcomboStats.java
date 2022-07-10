package adcombo;

import com.google.gson.Gson;
import config.Property;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class AdcomboStats {
    public static Connection.Response responseConnection;

    public static void getAuthResponse() throws IOException {
        System.out.println("Authorization...");
        Connection.Response response = Jsoup
                .connect("https://my.adcombo.com/auth/login")
                .method(Connection.Method.POST)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute();
        System.out.println("Cookies get");

        String token = response.cookies().get("X-CSRF-Token");
        String jsonBody = """
        {
        "email": "%s",
        "password": "%s"
        }
        """.formatted(Property.adEmail, Property.adPassword);

        response = Jsoup
                .connect("https://my.adcombo.com/auth/login")
                .method(Connection.Method.POST)
                .header("x-csrf-token", token)
                .header("authority", "my.adcombo.com")
                .header("method", "POST")
                .header("path", "/auth/login")
                .header("scheme", "https")
                .header("accept", "application/json, text/plain, */*")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7")
                .header("cache-control", "no-cache")
                .header("content-length", "51")
                .header("content-type", "application/json")
                // .header("cookie", "_ga=GA1.2.327662190.1642284206; _ym_uid=1642284206912514576; _ym_d=1642284206; _fbp=fb.1.1644163207452.1677197677; __adroll_fpc=ddb74401bf194ebac4d896e82ee45cc8-1644235453361; _gid=GA1.2.638802206.1653901785; __ar_v4=UXGB2QLMHBFBJLLKI3SCDJ%3A20220520%3A11%7CEHABHXTFZ5ES7E3W4RZWGV%3A20220520%3A11%7CWMQILGKTXJGLVA3FNKPRTK%3A20220520%3A6%7CAQ4OGGXKNZBQVAWA22ID76%3A20220603%3A5; session=ea68f4dcc9ca47aba68f6824981d1bbd; _ym_isad=2; X-CSRF-Token=IjcwZjIwNzI4ZjU4Njk4MjYyNzZiOThjYzIzYzlhMWE2ZTJjNmM4YTUi.FYDd3A.-3VC5pRcx1It23E_9n-xKz2V7Kw")
                .header("origin", "https://my.adcombo.com")
                .header("pragma", "no-cache")
                .header("referer", "https://my.adcombo.com/login")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"101\", \"Google Chrome\";v=\"101\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"macOS\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-origin")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36")
                .header("x-csrf-token", token)
                .header("x-requested-with", "XMLHttpRequest")

                .requestBody(jsonBody)
                .cookies(response.cookies())
                //.cookie("X-CSRF-Token", token)

                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute();
        responseConnection = response;
    }

    public static AdcomboStatsEntity getStat(String cpaNetwork, LocalDate dateStart, LocalDate dateEnd) throws IOException {

        if (responseConnection == null) {
            getAuthResponse();
        }

        String timeZone = "+03:00";

        if (cpaNetwork.equals("exo")) {
            timeZone = "-04:00";
        }
        long timeStart = dateStart.toEpochSecond(LocalTime.MIN, ZoneOffset.of(timeZone));
        long timeEnd = dateEnd.toEpochSecond(LocalTime.MIN, ZoneOffset.of(timeZone)) - 1;
        responseConnection = Jsoup
                .connect("https://my.adcombo.com/api/stats?page=1&count=100&order=desc&sorting=group_by&stat_type=pp_stat&ts=" + timeStart + "&te=" + timeEnd + "&by_last_activity=false&percentage=false&normalize=false&comparing=false&group_by=offer_id&tz_offset=-10800&cols=uniq_traffic&cols=orders_confirmed&cols=orders_hold&cols=orders_rejected&cols=orders_trashed&cols=orders_total&cols=approve_total&cols=cr_uniq&cols=ctr_uniq&cols=user_orders_confirmed_income&cols=user_total_hold_income&cols=user_total_income&utm_source=" + cpaNetwork + "&utm_source=-2&epc_factor=0&force=true")
                .method(Connection.Method.GET)
                .cookies(responseConnection.cookies())
                .ignoreContentType(true)
                .maxBodySize(0)
                .execute();
        Gson gson = new Gson();

        return gson.fromJson(responseConnection.body(),
                AdcomboStatsEntity.class);

    }
}
