package s4y.solutions.mp.components.args;

import s4y.solutions.mp.components.args.extractors_mixins.IIntArgumentExtractor;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.util.function.Consumer;

/**
 * <p>ArgumentInt class.</p>
 *
 * @author dsa
 */
public class ArgumentInt extends Argument<Integer> implements IIntArgumentExtractor {
    public final IErrorHandler errorHandler;
    public Integer defaultValue;

    /**
     * <p>Constructor for ArgumentInt.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     */
    public ArgumentInt(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private Integer memo = null;
    /**
     * <p>use.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer use() {
        if (memo == null) {
            memo = extract(cli, shortArg, longArg, env, property, defaultValue, errorHandler);
        }
        return memo;
    }

    /**
     * <p>define.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @param modifier a {@link java.util.function.Consumer} object
     * @return a {@link s4y.solutions.mp.components.args.ArgumentInt} object
     */
    public static ArgumentInt define(IErrorHandler errorHandler, Consumer<DSL> modifier) {
        ArgumentInt definition = new ArgumentInt(errorHandler);
        modifier.accept(definition.dsl);
        return definition;
    }

    public final DSL dsl = new DSL();
    public class DSL extends Argument<Integer>.DSL {
        public void defaultValue(Integer defaultValue) {
            ArgumentInt.this.defaultValue = defaultValue;
        }
    }
}
