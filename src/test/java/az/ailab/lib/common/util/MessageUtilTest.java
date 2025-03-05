package az.ailab.lib.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

    @ParameterizedTest
    @CsvSource(value = {
            "null, null, null",
            "hello, null, hello",
            "hello, hello, null",
            "hello sir, hello {}, sir",
            "hello kind sir, hello {} {}, kind, sir"},
            nullValues = "null")
    void isValidTest(String expected,
                     String message,
                     @AggregateWith(VarargsAggregator.class) Object... args) {
        var actual = MessageUtil.resolveMessage(message, args);
        assertEquals(expected, actual);
    }

    static class VarargsAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
                throws ArgumentsAggregationException {
            return accessor.toList().stream()
                    .skip(context.getIndex())
                    .toArray(Object[]::new);
        }
    }

}