package analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Custom extension of the functional Consumer<T> interface.
 * Keep it still as functional interface by providing default implementation to the custom methods.
 * This will enable to use it in lambda expressions in case there will be a bulk processing of the data set
 * @param <T>
 */
public interface AnalyzerFunction<T> extends Consumer<T> {

    /**
     * Calculate and return the final value.
     * @return
     */
    default Map<String, Object> getResult() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("WARNING", "Default (empty) implementation has been invoked.");
        return result;
    }

    /**
     * Key set used by the analyzer to represent result. This allows control display value order.
     * @return
     */
    default String[] getKeySet() {
        return new String[]{};
    }
}
