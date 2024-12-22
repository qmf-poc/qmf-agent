package s4y.solutions.mp.annotation;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConnectionConfiguredCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (System.getenv("DB2_CONNECTION_STRING") == null && System.getProperty("db2.connection.string") == null) {
            return ConditionEvaluationResult.disabled("Neither DB2_CONNECTION_STRING nor db2.connection.string is set");
        }
        if (System.getenv("DB2_USER") == null && System.getProperty("db2.user") == null) {
            return ConditionEvaluationResult.disabled("Neither DB2_USER nor db2.user is set");
        }
        if (System.getenv("DB2_PASSWORD") == null && System.getProperty("db2.password") == null) {
            return ConditionEvaluationResult.disabled("Neither DB2_PASSWORD nor db2.password is set");
        }

        return ConditionEvaluationResult.enabled("Connection is configured");
    }
}
