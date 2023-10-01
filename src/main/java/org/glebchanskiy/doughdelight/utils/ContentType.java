package org.glebchanskiy.doughdelight.utils;

public enum ContentType {
    APPLICATION_JSON ("application/json");

    private final String name;

    private ContentType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
