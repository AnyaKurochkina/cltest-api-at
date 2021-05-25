package clp.core.testdata;

import clp.core.exception.CustomException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class TestData {

    private static final Logger LOG = LoggerFactory.getLogger(TestData.class);

    private TestData() {
    }

    /**
     * @param countDigits
     * @return
     */
    public static String randNumberAsString(int countDigits) {
        return RandomStringUtils.randomNumeric(countDigits);
    }

    public static String randAlphanumeric(int countDigits) {
        return RandomStringUtils.randomAlphanumeric(countDigits);
    }

    /**
     * Format - is template like $RAND_NUM:12$ or $TODAY:{YYYY-MM-dd}$
     * Templates:
     * 1. $RAND_NUM:<int>$          Example:   $RAND_NUM:5$            Return:64845
     * 2. $TODAY:{<string>}$        Example:   $TODAY:{YYYY-MM-dd}$    Return:2015-12-12
     *
     * @param format
     * @return new generated string
     */
    public static String generateValueByFormat(String format) throws CustomException {
        String generatedString = "";

        if (format.startsWith("$RAND_NUM:")) {
            String patternString = "\\$RAND_NUM:(.*?)\\$";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(format);
            try {
                if (matcher.find()) {
                    int countOfSymbolsForReplace = Integer.parseInt(matcher.group(1));
                    generatedString = randNumberAsString(countOfSymbolsForReplace);
                }
            } catch (Exception ex) {
                LOG.error(ex.getLocalizedMessage());
                throw new CustomException(ex);
            }
        } else if (format.startsWith("$TODAY:")) {
            String patternString = "^(?:\\$TODAY:)(?:.*)\\{([a-zA-Z0..9\\W]+)\\}\\$";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(format);
            try {
                if (matcher.find()) {
                    String dateFormatString = matcher.group(1);
                    DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
                    Date today = new Date();
                    generatedString = dateFormat.format(today);
                }
            } catch (Exception ex) {
                LOG.error(ex.getLocalizedMessage());
                throw new CustomException(ex);
            }
        }
        return generatedString;
    }
}
