package org.specrunner.sql.util;

import java.util.StringTokenizer;

/**
 * String utils.
 * 
 * @author Thiago Santos
 * 
 */
public final class StringUtil {

    /**
     * Default constructor.
     */
    private StringUtil() {
        // nothing
    }

    /**
     * Tokenize a string.
     * 
     * @param str
     *            The string.
     * @param tokens
     *            The tokens.
     * @return The tokens, using 'separator' as reference.
     */
    public static String[] parts(String str, String tokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, tokens);
        String[] result = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken();
        }
        return result;
    }
}
