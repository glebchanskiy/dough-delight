package org.glebchanskiy.kek.templater.patterns.impl;

import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.templater.patterns.AbstractPattern;

import java.util.regex.Pattern;

public class PrimaryPattern extends AbstractPattern {

    public PrimaryPattern(Model model) {
        super(model, Pattern.compile("\\{.*\\{(.+)\\}.*}"));
    }

    @Override
    public String transform() {
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
                var field = variable.getClass().getDeclaredField(splitedVariable[1]);
                field.setAccessible(true);
                result = field.get(variable).toString();
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
}
