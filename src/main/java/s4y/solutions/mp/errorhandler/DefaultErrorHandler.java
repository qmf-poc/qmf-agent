package s4y.solutions.mp.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>DefaultErrorHandler class.</p>
 *
 * @author dsa
 */
public class DefaultErrorHandler implements IErrorHandler {
    /** {@inheritDoc} */
    @Override
    public ErrorType handleError(Throwable th) {
        if (th instanceof Exception) {
            logger.error("Exception handled", th);
            return ErrorType.CONTINUE;
        }
        logger.error("Error handled", th);
        return ErrorType.ABORT;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultErrorHandler.class);
}
