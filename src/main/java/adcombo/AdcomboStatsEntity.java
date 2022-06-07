package adcombo;

import java.util.List;

public class AdcomboStatsEntity {

    public List<Inner> objects = null;


    public class Inner {
        public double user_total_hold_income = 0;
        public int orders_confirmed = 0;
        public int group_by = 0;
        public double user_orders_confirmed_income = 0;

        public String toString() {
            return String.valueOf(user_total_hold_income).replace(".",",") + "\t" +
                    orders_confirmed + "\t" +
                    String.valueOf(user_orders_confirmed_income).replace(".",",");
        }
    }


}
