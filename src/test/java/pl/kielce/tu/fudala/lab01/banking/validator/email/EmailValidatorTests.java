package pl.kielce.tu.fudala.lab01.banking.validator.email;

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

@DisplayName("EmailValidator Unit Tests")
class EmailValidatorTests {

    private EmailValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
        context = null;
    }

    @Nested
    @DisplayName("Valid Student Emails")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidStudentEmailsTests {

        Stream<String> provideValidEmails() {
            return Stream.of("s123456@student.tu.kielce.pl", "s000001@student.tu.kielce.pl", "s999999@student.tu" +
                    ".kielce.pl");
        }

        @ParameterizedTest(name = "Valid email: {0}")
        @MethodSource("provideValidEmails")
        @DisplayName("Should accept valid student emails")
        void shouldAcceptValidStudentEmails(String email) {
            boolean result = validator.isValid(email, context);
            assertTrue(result, "Expected valid email \"" + email + "\" to be accepted.");
        }
    }

    @Nested
    @DisplayName("Invalid Emails")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvalidEmailsTests {

        Stream<String> provideInvalidEmails() {
            return Stream.of("s12345@student.tu.kielce.pl", "s1234567@student.tu.kielce.pl", "x123456@student.tu" +
                    ".kielce.pl", "s123456@gmail.com", "123456@student.tu.kielce.pl", "s12a456@student.tu" + ".kielce" +
                    ".pl", null, "", " ", "   ");
        }

        @ParameterizedTest(name = "Invalid email: \"{0}\"")
        @MethodSource("provideInvalidEmails")
        @DisplayName("Should reject invalid emails including edge cases")
        void shouldRejectInvalidEmails(String email) {
            boolean result = validator.isValid(email, context);
            assertFalse(result, "Expected invalid email \"" + email + "\" to be rejected.");
        }
    }
}
