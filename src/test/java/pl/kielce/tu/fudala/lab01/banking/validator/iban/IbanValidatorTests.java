package pl.kielce.tu.fudala.lab01.banking.validator.iban;

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

@DisplayName("IbanValidator Unit Tests")
class IbanValidatorTests {

    private IbanValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new IbanValidator();
        context = null;
    }

    @Nested
    @DisplayName("Valid IBAN Numbers")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidIbanTests {
        Stream<String> provideValidIbans() {
            return Stream.of("AD1200012030200359100100", "AE070331234567890123456", "AL47212110090000000235698741",
                    "AT611904300234573201", "AZ21NABZ00000000137010001944", "BA391290079401028494", "BE68539007547034"
                    , "BG80BNBG96611020345678", "BH67BMAG00001299123456", "BR1800360305000010009795493C1",
                    "CH9300762011623852957", "CY17002001280000001200527600", "CZ6508000000192000145399",
                    "DE89370400440532013000", "DK5000400440116243", "DO28BAGR00000001212453611324",
                    "EE382200221020145685", "ES9121000418450200051332", "FI2112345600000785", "FO6264600001631634",
                    "FR1420041010050500013M02606", "GB29NWBK60161331926819", "GE29NB0000000101904917",
                    "GI75NWBK000000007099453", "GL8964710001000206", "GR1601101250000000012300695",
                    "GT82TRAJ01020000001210029690", "HR1210010051863000160", "HU42117730161111101800000000",
                    "IE29AIBK93115212345678", "IL620108000000099999999", "IS140159260076545510730339",
                    "IT60X0542811101000000123456", "JO94CBJO0010000000000131000302", "KW81CBKU0000000000001234560101"
                    , "KZ86125KZT5004100100", "LB62099900000001001901229114", "LI21088100002324013AA",
                    "LT121000011101001000", "LU280019400644750000", "LV80BANK0000435195001",
                    "MC5811222000010123456789030", "MD24AG000225100013104168", "ME25505000012345678951",
                    "MK07250120000058984", "MR1300020001010000123456753", "MT84MALT011000012345MTLCAST001S",
                    "MU17BOMM0101101030300200000MUR", "NL91ABNA0417164300", "NO9386011117947",
                    "PK36SCBL0000001123456702", "PL61109010140000071219812874", "PS92PALS000000000400123456702",
                    "PT50000201231234567890154", "QA58DOHB00001234567890ABCDEFG", "RO49AAAA1B31007593840000",
                    "RS35260005601001611379", "SA0380000000608010167519", "SE4550000000058398257466",
                    "SI56263300012039086", "SK3112000000198742637541", "SM86U0322509800000000270100",
                    "TN5910006035183598478831", "TR330006100519786457841326", "VG96VPVG0000012345678901");
        }

        @ParameterizedTest(name = "Valid IBAN: {0}")
        @MethodSource("provideValidIbans")
        @DisplayName("Should accept valid IBAN numbers from various countries")
        void shouldAcceptValidIbans(String iban) {
            boolean result = validator.isValid(iban, context);
            assertTrue(result, "Expected valid IBAN \"" + iban + "\" to be accepted.");
        }
    }

    @Nested
    @DisplayName("Invalid IBAN Numbers")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvalidIbanTests {
        Stream<String> provideInvalidIbans() {
            return Stream.of("DE89370400440532013001", "ZZ89370400440532013000", "DE89", "DE89$70400440532013000",
                    "DE8937040044053201300112313123122319038312893", "", " ", null);
        }

        @ParameterizedTest(name = "Invalid IBAN: \"{0}\"")
        @MethodSource("provideInvalidIbans")
        @DisplayName("Should reject invalid IBAN numbers including improperly formatted ones")
        void shouldRejectInvalidIbans(String iban) {
            boolean result = validator.isValid(iban, context);
            assertFalse(result, "Expected invalid IBAN \"" + iban + "\" to be rejected.");
        }
    }
}
