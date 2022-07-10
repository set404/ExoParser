package util;

import adcombo.AdcomboStatsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombineStat {
    public static StringBuilder combine(Map<Integer, String> networkStat, AdcomboStatsEntity adcomboStat, Integer[] offers) {

        List<String> offerStats = new ArrayList<>();
        Map<Integer, AdcomboStatsEntity.Inner> adcomboStatMap = adcomboStat.objects.stream()
                .collect(Collectors.toMap(AdcomboStatsEntity.Inner::getCampaignId, Function.identity()));

        for (Integer key : offers) {
            offerStats.add(networkStat.get(key) + "\t" + adcomboStatMap.get(key));
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String offerStat : offerStats) {
            stringBuilder.append(offerStat).append("\n");
        }

        return stringBuilder;
    }
}
