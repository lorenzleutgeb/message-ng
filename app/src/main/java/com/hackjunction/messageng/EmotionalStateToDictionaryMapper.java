package com.hackjunction.messageng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by melanie on 24.11.18.
 */

public class EmotionalStateToDictionaryMapper {
    private final Map<Boolean,String> emotionalDictionary;
    private final EmotionalStateInterface state;

    public EmotionalStateToDictionaryMapper(EmotionalStateInterface state, Map<Boolean,String> emotionalDictionary){
        this.state = state;
        this.emotionalDictionary = emotionalDictionary;
    }

    public String getEmotionalMapping(){
        return emotionalDictionary.get(state.getCurrentEmotionalState());
    }
}
