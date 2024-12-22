package s4y.solutions.mp.components.args.extractors_mixins;

import s4y.solutions.mp.errorhandler.IErrorHandler;

/**
 * <p>IIntArgumentExtractor interface.</p>
 *
 * @author dsa
 */
public interface IIntArgumentExtractor extends IStringArgumentExtractor {
    /**
     * <p>extract.</p>
     *
     * @param cli an array of {@link java.lang.String} objects
     * @param shortArg a {@link java.lang.String} object
     * @param longArg a {@link java.lang.String} object
     * @param env a {@link java.lang.String} object
     * @param property a {@link java.lang.String} object
     * @param defaultValue a int
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @return a int
     */
    default int extract(String[] cli, String shortArg, String longArg, String env, String property, int defaultValue, IErrorHandler errorHandler) {
        String value = extract(cli, shortArg, longArg, env, property, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            errorHandler.handleError(new Exception("Invalid integer argument: " + value));
            return defaultValue;
        }
    }
}
