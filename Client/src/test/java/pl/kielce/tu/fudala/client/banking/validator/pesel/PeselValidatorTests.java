package pl.kielce.tu.fudala.client.banking.validator.pesel;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PeselValidator Unit Tests")
class PeselValidatorTests {

    private PeselValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PeselValidator();
        context = null;
    }

    @Nested
    @DisplayName("Valid PESEL Numbers")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidPeselNumbersTests {
        Stream<String> provideValidPesels() {
            return Stream.of("44051401458", "02070803628", "01301599921");
        }

        @ParameterizedTest(name = "Valid PESEL: {0}")
        @MethodSource("provideValidPesels")
        @DisplayName("Should accept valid PESEL numbers")
        void shouldAcceptValidPesels(String pesel) {
            boolean result = validator.isValid(pesel, context);
            assertTrue(result, "Expected valid PESEL \"" + pesel + "\" to be accepted.");
        }
    }

    @Nested
    @DisplayName("Invalid PESEL Numbers")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvalidPeselNumbersTests {
        Stream<String> provideInvalidPesels() {
            return Stream.of("4405140145", "440514014580", "abcdefghijk", "4405140145a", "12345678901", null, "", " "
                    , "          ", "44051401457", "02070803627", "80010112374");
        }

        @ParameterizedTest(name = "Invalid PESEL: \"{0}\"")
        @MethodSource("provideInvalidPesels")
        @DisplayName("Should reject invalid PESEL numbers including edge cases and incorrect control sum")
        void shouldRejectInvalidPesels(String pesel) {
            boolean result = validator.isValid(pesel, context);
            assertFalse(result, "Expected invalid PESEL \"" + pesel + "\" to be rejected.");
        }
    }
}
