package clp.core.testdata;

import clp.core.vars.TestVars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableReplacer {

    private VariableReplacer() {
    }

    public static String getReplacedString(String stringToReplace, TestVars testVars) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)}");
        Matcher matcher = pattern.matcher(stringToReplace);
        StringBuilder builder = new StringBuilder();
        int i=0;
        while (matcher.find()){
            String replacement = testVars.getVariables().get(matcher.group(1));
            builder.append(stringToReplace.substring(i,matcher.start()));
            if (replacement==null){
                builder.append("");
            }else {
                builder.append(replacement);
                i=matcher.end();
            }
        }
        builder.append(stringToReplace.substring(i,stringToReplace.length()));

        return builder.toString();
    }
}
