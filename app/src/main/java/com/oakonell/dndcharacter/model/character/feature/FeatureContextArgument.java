package com.oakonell.dndcharacter.model.character.feature;

import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 2/6/2016.
 */
public class FeatureContextArgument {
    @Element(required = false)
    String argument;
    @Element(required = false)
    FeatureContext context;

    public FeatureContextArgument() {
        // support persistence
    }
    public FeatureContextArgument(FeatureContext context, String argument) {
        this.context = context;

        if (argument != null) {
            if (argument.trim().length() == 0) {
                argument = null;
            } else {
                argument = argument.toUpperCase();
            }
        }
        this.argument = argument;
    }

    public FeatureContextArgument(FeatureContext context) {
        this.context = context;
        this.argument = null;
    }

    public String getArgument() {
        return argument;
    }

    public FeatureContext getContext() {
        return context;
    }
}
