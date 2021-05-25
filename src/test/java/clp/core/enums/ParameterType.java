package clp.core.enums;

import clp.core.helpers.Constants;

/**
 *
 */
public enum ParameterType {

    RQUID("RQUID"),
    IGNORED("IGNORED"),
    DIGITAL_SIGNATURE("DigitalSignature"),
    MSGID("MSGID");

    private String name;

    ParameterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getParameter();
    }

    public String getName() {
        return name;
    }

    public String getParameter() {
        return Constants.DELIMITER + name + Constants.DELIMITER;
    }

    public static ParameterType getByName(String name) {
        String nameWithoutDelimiter = name.replaceAll(Constants.DELIMITER, "");
        for (ParameterType type : values()) {
            if (type.getName().equalsIgnoreCase(nameWithoutDelimiter)) {
                return type;
            }
        }
        throw new IllegalArgumentException();

    }
}
