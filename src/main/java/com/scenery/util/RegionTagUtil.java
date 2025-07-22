package com.scenery.util;

public class RegionTagUtil {

    public static String determineRegionTag(String address) {
        if (address == null) return null;

        if (address.contains("台北") || address.contains("新北") || address.contains("基隆") ||
            address.contains("桃園") || address.contains("新竹")) {
            return "北部";
        } else if (address.contains("苗栗") || address.contains("台中") || address.contains("彰化") ||
                   address.contains("南投") || address.contains("雲林")) {
            return "中部";
        } else if (address.contains("嘉義") || address.contains("台南") || address.contains("高雄") ||
                   address.contains("屏東")) {
            return "南部";
        } else if (address.contains("宜蘭") || address.contains("花蓮") || address.contains("台東")) {
            return "東部";
        } else if (address.contains("澎湖") || address.contains("金門") || address.contains("馬祖") ||
                   address.contains("綠島") || address.contains("蘭嶼")) {
            return "外島";
        }
        return null;
    }
}

