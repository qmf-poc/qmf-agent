package s4y.solutions.mp.errorhandler;

/**
 * <p>IErrorHandler interface.</p>
 *
 * @author dsa
 */
public interface IErrorHandler {
    /**
     * <p>handleError.</p>
     *
     * @param th a {@link java.lang.Throwable} object
     * @return a {@link s4y.solutions.mp.errorhandler.ErrorType} object
     */
    @SuppressWarnings("UnusedReturnValue")
    ErrorType handleError(Throwable th);
}
