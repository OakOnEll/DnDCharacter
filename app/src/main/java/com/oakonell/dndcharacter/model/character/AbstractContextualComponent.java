package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.IContextualComponent;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.ElementList;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rob on 1/4/2016.
 */
public abstract class AbstractContextualComponent extends BaseCharacterComponent implements IContextualComponent {
    @NonNull
    @ElementList(required = false)
    private Set<FeatureContextArgument> contexts = new HashSet<>();

    public void addContext(FeatureContextArgument featureContextArgument) {
        contexts.add(featureContextArgument);
    }

    @Override
    public boolean isInContext(FeatureContextArgument context) {
        for (FeatureContextArgument each : contexts) {
            if (each.getContext() != context.getContext()) continue;
            if (each.getArgument() == null) return true;
            if (each.getArgument().equals(context.getArgument())) return true;
        }
        return false;
    }

    @Override
    public boolean isInContext(@NonNull Set<FeatureContextArgument> filter) {
        for (FeatureContextArgument eachFilter : filter) {
            if (isInContext(eachFilter)) return true;
        }
        return false;
    }

    private static final Pattern PAREN_ARG = Pattern.compile("([^\\(]+)(\\(([^\\)]*)\\))?");

    public static FeatureContextArgument parseFeatureContext(String contextWithPossibleArg) {
        final Matcher argMatcher = PAREN_ARG.matcher(contextWithPossibleArg);
        if (!argMatcher.matches()) {
            throw new RuntimeException("Error matching pattern for " + contextWithPossibleArg);
        }
        String contextName = argMatcher.group(1);
        String argument = argMatcher.group(3);

        FeatureContext context = EnumHelper.stringToEnum(contextName, FeatureContext.class);
        return new FeatureContextArgument(context, argument);
    }
}
