package org.glebchanskiy.kek.templater.patterns.impl;

import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.templater.patterns.AbstractPattern;

import java.util.regex.Pattern;

public class LoopPattern extends AbstractPattern {

    public LoopPattern(Model model) {
        super(model, Pattern.compile("\\{for (\\w+) in (\\w+) : \\{(.+)\\}\\}"));
    }

    @Override
    public String transform() {
        int ident = matcher.start();
        StringBuilder sb = new StringBuilder();
        String variableName = matcher.group(1);
        String nested = matcher.group(3);

        System.out.println("matcher.group(1): " + matcher.group(1));
        System.out.println("matcher.group(2): " + matcher.group(2));
        System.out.println("matcher.group(3): " + matcher.group(3));

        Iterable<Object> iterables = (Iterable<Object>) model.get(matcher.group(2));
        for (var variable : iterables) {
            model.put(String.valueOf(variable.hashCode()), variable);

            if (!sb.isEmpty())
                sb.append('\n').append(" ".repeat(ident));

            sb.append(nested.replaceAll(variableName, String.valueOf(variable.hashCode())));
        }
        System.out.println("sb: " + sb.toString());
        return matcher.replaceAll(sb.toString());
    }
}
