package exo;

public class Statistic {

    public Campaign resultTotal = null;

    public class Campaign {
        public int clicks = 0;
        public double cost = 0;


        public String toString() {
            return String.valueOf(clicks).replace(".",",") + "\t" +
                    String.valueOf(cost).replace(".",",");
        }
    }

}
