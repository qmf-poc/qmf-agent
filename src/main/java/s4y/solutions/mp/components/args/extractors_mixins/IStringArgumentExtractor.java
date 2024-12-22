package s4y.solutions.mp.components.args.extractors_mixins;

/**
 * <p>IStringArgumentExtractor interface.</p>
 *
 * @author dsa
 */
public interface IStringArgumentExtractor {
    /**
     * <p>extract.</p>
     *
     * @param cli an array of {@link java.lang.String} objects
     * @param shortArg a {@link java.lang.String} object
     * @param longArg a {@link java.lang.String} object
     * @param env a {@link java.lang.String} object
     * @param property a {@link java.lang.String} object
     * @param defaultValue a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    default String extract(String[] cli, String shortArg, String longArg, String env, String property, String defaultValue) {
        if (cli != null) {
            if (shortArg!=null && !shortArg.isEmpty()) {
                String shortArgKey = "-" + shortArg;
                for (int i = 0; i < cli.length - 1; i++) {
                    if (cli[i].equals(shortArgKey)) {
                        return cli[i + 1];
                    }
                }
            }
            if (longArg!=null && !longArg.isEmpty()) {
                String longArgKey = "--" + longArg;
                for (int i = 0; i < cli.length - 1; i++) {
                    if (cli[i].equals(longArgKey)) {
                        return cli[i + 1];
                    }
                }
            }
        }

        if (env != null && !env.isEmpty()) {
            String value = System.getenv(env);
            if (value != null) {
                return value;
            }
        }

        if (property != null && !property.isEmpty()) {
            String value = System.getProperty(property);
            if (value != null) {
                return value;
            }
        }

        return defaultValue;
    }
}
