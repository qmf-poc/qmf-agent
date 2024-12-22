package s4y.solutions.mp.components.args;

import s4y.solutions.mp.components.args.extractors_mixins.IStringArgumentExtractor;

import java.util.function.Consumer;

/**
 * <p>ArgumentString class.</p>
 *
 * @author dsa
 */
public class ArgumentString extends Argument<String> implements IStringArgumentExtractor {
    public String defaultValue;

    private String memo;
    /**
     * <p>use.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String use() {
        if (memo == null) {
            memo = extract(cli, shortArg, longArg, env, property, defaultValue);
        }
        return memo;
    }

    /**
     * <p>define.</p>
     *
     * @param modifier a {@link java.util.function.Consumer} object
     * @return a {@link s4y.solutions.mp.components.args.ArgumentString} object
     */
    public static ArgumentString define(Consumer<DSL> modifier) {
        final ArgumentString definition = new ArgumentString();
        modifier.accept(definition.dsl);
        return definition;
    }

    public final DSL dsl = new DSL();

    public class DSL extends Argument<String>.DSL {
        public void defaultValue(String defaultValue) {
            ArgumentString.this.defaultValue = defaultValue;
        }
    }
}
