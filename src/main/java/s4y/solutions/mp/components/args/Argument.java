package s4y.solutions.mp.components.args;

abstract class Argument<T> {
    public String[] cli = new String[0];
    public String shortArg;
    public String longArg;
    public String env;
    public String property;

    protected class DSL {

        public void cli(String[] cli) {
            Argument.this.cli = cli;
        }

        public void shortArg(String shortArg) {
            Argument.this.shortArg = shortArg;
        }

        public void longArg(String longArg) {
            Argument.this.longArg = longArg;
        }

        public void env(String env) {
            Argument.this.env = env;
        }

        public void property(String property) {
            Argument.this.property = property;
        }
    }
}
