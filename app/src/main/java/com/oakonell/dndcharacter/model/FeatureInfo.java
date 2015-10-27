package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeatureInfo {
        Feature feature;
        BaseCharacterComponent source;

        public String getName() {
                return feature.getName();
        }

        public String getSourceString() {
                return source.getSourceString();
        }

        public String getShortDescription() {
                return feature.getDescription();
        }
        public String getUsesFormula() {
                return feature.getUsesFormula();
        }


        public Feature getFeature() {
                return feature;
        }
}
