package ru.testit.utils;

public enum Outcome
{
    PASSED("Passed"), 
    FAILED("Failed"),
    BLOCKED("Blocked"),
    SKIPPED("Skipped");

    private String value;
    
    private Outcome(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public static Outcome getByValue(final String value) {
        for (final Outcome outcome : values()) {
            if (value.equals(outcome.getValue())) {
                return outcome;
            }
        }
        return null;
    }
}
