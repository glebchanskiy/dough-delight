package org.glebchanskiy.kek.templater;

import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Templater {

    @Getter
    private final Model model;

    private final Pattern loopPattern = Pattern.compile("\\{for (\\w+) in model\\.(\\w+) : (\\{.+\\})\\}");
    private final Pattern ternaryPatter = Pattern.compile("\\{(\\w+) \\? (\\{.+\\}) : (\\{.+\\}\\})");
    private final Pattern unaryPatter = Pattern.compile("\\{(\\w+) && (\\{.+\\}\\})");
    private final Pattern simplePatter = Pattern.compile("\\{.*\\{(.+)\\}.*}");

    public Templater(Model model) {
        this.model = model;
    }

    private String matches(String expression) {
        System.out.println("EXPRESSION -> [" + expression + "]");
        this.model.forEach((e, v) -> System.out.println(e + " <-> " + v));

        Matcher loopMatcher = loopPattern.matcher(expression);
        Matcher ternaryMatcher = ternaryPatter.matcher(expression);
        Matcher unaryMatcher = unaryPatter.matcher(expression);
        Matcher simpleMatcher = simplePatter.matcher(expression);

        int minIndex = Integer.MAX_VALUE;
        Matcher currentMatcher = null;
        Template currentTemplate = null;

        if (loopMatcher.find() && minIndex > loopMatcher.start()) {
            minIndex = loopMatcher.start();
            currentMatcher = loopMatcher;
            currentTemplate = Template.LOOP;
        }

        if (ternaryMatcher.find() && minIndex > ternaryMatcher.start()) {
            minIndex = ternaryMatcher.start();
            currentMatcher = ternaryMatcher;
            currentTemplate = Template.TERNARY;
        }

        if (unaryMatcher.find() && minIndex > unaryMatcher.start()) {
            minIndex = unaryMatcher.start();
            currentMatcher = unaryMatcher;
            currentTemplate = Template.UNARY;
        }

        if (simpleMatcher.find() && minIndex > simpleMatcher.start()) {
            currentMatcher = simpleMatcher;
            currentTemplate = Template.SIMPLE;
        }

        if (currentMatcher != null) {
            return matches(switchExpression(currentTemplate, currentMatcher));
        }

        return expression;
    }
    
    private String switchExpression(Template template, Matcher matcher) {
        switch (template) {
            case LOOP -> { return loop(matcher); }
            case UNARY -> { return unary(matcher); }
            case SIMPLE -> { return simple(matcher); }
            case TERNARY -> { return ternary(matcher); }
        }
        return null;
    }

    private String loop(Matcher matcher) {
        int ident = matcher.start();

        StringBuilder sb = new StringBuilder();

        String variableName = matcher.group(1);
        String nested = matcher.group(3);

        Iterable iterables = (Iterable) model.get("users");
        for (var variable : iterables) {
            model.put(String.valueOf(variable.hashCode()), variable);

            if (!sb.isEmpty())
                sb.append('\n').append(" ".repeat(ident));

            sb.append(matches(nested.replaceFirst(variableName, String.valueOf(variable.hashCode()))));
        }
        return matcher.replaceAll(sb.toString());
    }

    private String simple(Matcher matcher) {
        String rowObject = matcher.group(1);
        String result = getStringifyValue(rowObject);

        result = matcher.group().replace('{' + matcher.group(1) + '}', result);
        return result.substring(1, result.length() - 1);
    }

    private String getStringifyValue(String objectName) {
        String result = null;
        System.out.println("objectName: " + objectName);
        String[] splitedVariable = objectName.split("\\.");
        if (splitedVariable.length > 1) {
            System.out.println("split");
            try {
                Object variable = model.get(splitedVariable[0]);
//                System.out.println("variable: " + variable);
//                System.out.println("field: [" + splitedVariable[1] + "]");
//                for (var field : variable.getClass().getDeclaredFields()) {
//                    System.out.println("name: " + field.getName());
//                }

                var field = variable.getClass().getDeclaredField(splitedVariable[1]);
//                System.out.println("field: " + field);
                field.setAccessible(true);
                result = field.get(variable).toString();
//                System.out.println("result: " + result);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        } else {
            Object variable = model.get(objectName);
            if (variable != null) {
                result = variable.toString();
            }
        }
        System.out.println("result: " + result);
        return result == null ? "null" : result;
    }

    private String templamize(String line) {
        String searchLine = line.trim();
        if (searchLine.startsWith("{") && searchLine.endsWith("}")) {
            return matches(line);
        } else {
            return line;
        }
    }

    public String template(String template) {
        this.model.forEach((e, v) -> System.out.println(e + " <-> " + v));
        List<String> result = new ArrayList<>();
        List<String> templateAsLines = IOUtils.readLines(new StringReader(template));

        for (String line : templateAsLines) {
            result.add(templamize(line));
        }

        return String.join("\n", result);
    }

    private String unary(Matcher matcher) {
        return matcher.replaceAll("UNARY");
    }
    private String ternary(Matcher matcher) {
        return matcher.replaceAll("TERNARY");
    }
}
