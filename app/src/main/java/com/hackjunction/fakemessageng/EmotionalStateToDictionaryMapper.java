package com.hackjunction.messageng;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by melanie on 24.11.18.
 */

public class EmotionalStateToDictionaryMapper {
    private final Map<Boolean,String> emotionalDictionary = new HashMap<>();
    private final EmotionalStateInterface state;

    public EmotionalStateToDictionaryMapper(EmotionalStateInterface state){
        this.state = state;
        emotionalDictionary.put(true,"https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        emotionalDictionary.put(false,"https://www.youtube.com/watch?v=s1tAYmMjLdY");
    }

    public String getEmotionalMapping() {
        // TODO
        return "";
        //return emotionalDictionary.get(state.getCurrentEmotionalState(Collections.EMPTY_MAP));
    }
}
