package org.glebchanskiy.kek.templater;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.glebchanskiy.kek.templater.patterns.AbstractPattern;
import org.glebchanskiy.kek.templater.patterns.impl.LoopPattern;
import org.glebchanskiy.kek.templater.patterns.impl.PrimaryPattern;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;

public class Templater {

    @Getter
    private final Model model;
    private final List<AbstractPattern> patternList;

    public Templater(Model model) {
        this.model = model;
        this.patternList = List.of(
                new LoopPattern(model),
                new PrimaryPattern(model)
        );
    }

    private String transform(String expression) {
        System.out.println("EXPRESSION -> [" + expression + "]");

        int minIndex = Integer.MIN_VALUE;
        AbstractPattern currentPattern = null;

        for (var pattern : patternList) {
            Matcher matcher = pattern.matcher(expression);

            if (matcher.find() && minIndex < matcher.group().length()) {
                minIndex = matcher.group().length();
                currentPattern = pattern;
            }
        }
        if (currentPattern != null)
            return currentPattern.transform();

        return expression;
    }

    private String transformIfExpression(String line) {
        return transform(line);
    }

    public String template(String template) {
        // several round of templating for nested expressions
        return round(round(template));
    }

    private String round(String template) {
        StringBuilder content = new StringBuilder();

        for (String line : splitByLines(template)) {
            content.append(transformIfExpression(line)).append('\n');
        }
        return content.toString();
    }

    private List<String> splitByLines(String template) {
        return IOUtils.readLines(new StringReader(template));
    }
}
