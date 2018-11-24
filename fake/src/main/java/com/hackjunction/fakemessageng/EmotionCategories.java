package com.hackjunction.fakemessageng;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum EmotionCategories {
    SATISFIED(0),CONFIDENT(18),PROUD(36),AMUSED(54),AROUSED(72),INTEREST(90),
    TERRROR(100), AGITATED(110), DISTURBED(120), RESTLESS(130), AFFLICTION(140), CONFUSED(150), CONTEMPT(160), EMBARRASSED(170),
    DISGUST(180), MISERABLE(198), GUILT(216), DEPRESSED(234), BORED(252),
    LETHARGIC(270);
    private int theta;
    EmotionCategories(int i) {
    }

    private static Map<Integer, EmotionCategories> map = new HashMap<Integer, EmotionCategories>();
    static {
        for (EmotionCategories e : EmotionCategories.values()) {
            map.put(e.theta, e);
        }
    }
    public static EmotionCategories valueOf(int t) {
        return map.get(t);
    }
}
