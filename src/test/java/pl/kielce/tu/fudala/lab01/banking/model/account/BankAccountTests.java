package pl.kielce.tu.fudala.lab01.banking.model.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BankAccount Unit Tests")
class BankAccountTests {

    private BankAccount sourceAccount;
    private BankAccount targetAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = new BankAccount();
        sourceAccount.setFirstName("Jan");
        sourceAccount.setLastName("Kowalski");
        sourceAccount.setAccountNumber("PL1234567890");
        sourceAccount.setPesel("44051401458");
        sourceAccount.setEmail("jan.kowalski@example.com");
        sourceAccount.setBalance(new BigDecimal("100.00"));

        targetAccount = new BankAccount();
        targetAccount.setFirstName("Anna");
        targetAccount.setLastName("Nowak");
        targetAccount.setAccountNumber("PL0987654321");
        targetAccount.setPesel("02070803628");
        targetAccount.setEmail("anna.nowak@example.com");
        targetAccount.setBalance(new BigDecimal("50.00"));
    }

    @Nested
    @DisplayName("Domestic Transfer Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DomesticTransferTests {

        @Test
        @DisplayName("Should charge fixed fee of 2.00 PLN for domestic transfer")
        void domesticTransferFixedFee() {
            BigDecimal transferAmount = new BigDecimal("10.00");
            BigDecimal initialSourceBalance = sourceAccount.getBalance();
            BigDecimal initialTargetBalance = targetAccount.getBalance();

            sourceAccount.transfer(targetAccount, transferAmount);

            BigDecimal expectedFee = new BigDecimal("2.00");
            BigDecimal expectedSourceBalance = initialSourceBalance.subtract(transferAmount).subtract(expectedFee);
            BigDecimal expectedTargetBalance = initialTargetBalance.add(transferAmount);
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "Source account balance " +
                    "should be reduced by the transfer amount plus a fixed fee of 2.00 PLN.");
            assertEquals(0, expectedTargetBalance.compareTo(targetAccount.getBalance()), "Target account balance " +
                    "should increase by the transfer amount.");
        }

        @Test
        @DisplayName("Should charge fixed fee for high-value domestic transfer")
        void domesticHighValueTransferFixedFee() {
            BigDecimal transferAmount = new BigDecimal("80.00");
            sourceAccount.setBalance(new BigDecimal("100.00"));
            BigDecimal expectedFee = new BigDecimal("2.00");

            sourceAccount.transfer(targetAccount, transferAmount);

            BigDecimal expectedSourceBalance = new BigDecimal("100.00").subtract(transferAmount).subtract(expectedFee);
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "Even for high-value " +
                    "domestic transfers, the fee should remain fixed at 2.00 PLN.");
        }

        @Test
        @DisplayName("Should allow transfer of maximum possible amount (balance minus fee)")
        void domesticTransferMaximumValue() {
            sourceAccount.setBalance(new BigDecimal("100.00"));
            BigDecimal fee = new BigDecimal("2.00");
            BigDecimal maxTransferable = sourceAccount.getBalance().subtract(fee);

            sourceAccount.transfer(targetAccount, maxTransferable);

            BigDecimal expectedSourceBalance = BigDecimal.ZERO;
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "After transferring the " +
                    "maximum possible amount (balance minus fee), the source account balance " + "should be zero.");
        }
    }

    @Nested
    @DisplayName("International Transfer Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InternationalTransferTests {

        static Stream<Arguments> internationalTransferFeeProvider() {
            return Stream.of(Arguments.of(new BigDecimal("100.00"), new BigDecimal("15.00")),
                    Arguments.of(new BigDecimal("50.00"), new BigDecimal("10.00")),
                    Arguments.of(new BigDecimal("200" + ".00"), new BigDecimal("30.00")));
        }

        @ParameterizedTest(name = "International transfer of {0} should charge fee of {1} PLN")
        @MethodSource("internationalTransferFeeProvider")
        @DisplayName("Should charge fee based on 15% of amount or minimum 10.00 PLN for international transfers")
        void internationalTransferFeeCalculation(BigDecimal transferAmount, BigDecimal expectedFee) {
            targetAccount.setAccountNumber("DE9876543210");
            sourceAccount.setBalance(new BigDecimal("500.00"));
            BigDecimal initialSourceBalance = sourceAccount.getBalance();

            sourceAccount.transfer(targetAccount, transferAmount);

            BigDecimal expectedSourceBalance = initialSourceBalance.subtract(transferAmount).subtract(expectedFee);
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "For international " +
                    "transfers, the source account balance should be reduced by the transfer amount" + " plus the " + "calculated fee.");
        }

        @Test
        @DisplayName("Should charge minimum fee of 10.00 PLN for very low international transfer amount")
        void internationalLowValueTransferMinimumFee() {
            targetAccount.setAccountNumber("US1234567890");
            BigDecimal transferAmount = new BigDecimal("10.00");
            BigDecimal initialSourceBalance = new BigDecimal("50.00");
            sourceAccount.setBalance(initialSourceBalance);

            sourceAccount.transfer(targetAccount, transferAmount);

            BigDecimal expectedFee = new BigDecimal("10.00");
            BigDecimal expectedSourceBalance = initialSourceBalance.subtract(transferAmount).subtract(expectedFee);
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "For very low international "
                    + "transfer amounts, the minimum fee of 10.00 PLN should be applied.");
        }

        @Test
        @DisplayName("Should allow transfer of maximum possible amount for international transfer")
        void internationalTransferMaximumValue() {
            targetAccount.setAccountNumber("US1234567890");
            sourceAccount.setBalance(new BigDecimal("115.00"));
            BigDecimal maxTransferable = new BigDecimal("100.00");

            sourceAccount.transfer(targetAccount, maxTransferable);

            BigDecimal expectedSourceBalance = new BigDecimal("0.00");
            assertEquals(0, expectedSourceBalance.compareTo(sourceAccount.getBalance()), "After transferring the " +
                    "maximum possible amount for an international transfer, the source account" + " balance should " +
                    "be" + " zero.");
        }
    }

    @Nested
    @DisplayName("Transaction History Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TransactionHistoryTests {

        @Test
        @DisplayName("Transaction history should contain entries for transfer and fee")
        void transactionHistoryContainsTransferAndFeeEntries() {
            BigDecimal transferAmount = new BigDecimal("20.00");
            targetAccount.setAccountNumber("PL5555555555");
            sourceAccount.setBalance(new BigDecimal("100.00"));

            sourceAccount.transfer(targetAccount, transferAmount);

            boolean containsOutgoingTransfer =
                    sourceAccount.getTransactionHistory().stream().anyMatch(t -> t.getDescription().contains(
                            "Outgoing transfer"));
            boolean containsTransferFee =
                    sourceAccount.getTransactionHistory().stream().anyMatch(t -> t.getDescription().contains(
                            "Transfer fee"));
            assertTrue(containsOutgoingTransfer, "Transaction history should contain an entry for outgoing transfer.");
            assertTrue(containsTransferFee, "Transaction history should contain an entry for the transfer fee.");
        }
    }

    @Nested
    @DisplayName("Invalid Transfer Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvalidTransferTests {

        static Stream<BigDecimal> invalidAmountsProvider() {
            return Stream.of(BigDecimal.ZERO, new BigDecimal("-10.00"));
        }

        @ParameterizedTest(name = "Invalid transfer amount: {0}")
        @MethodSource("invalidAmountsProvider")
        @DisplayName("Should reject transfers with zero or negative amount")
        void transferWithInvalidAmount(BigDecimal invalidAmount) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> sourceAccount.transfer(targetAccount, invalidAmount), "Expected an exception when " +
                            "transferring a zero or negative amount.");
            assertTrue(exception.getMessage().contains("amount must be greater than zero"), "Exception message " +
                    "should" + " indicate that the transfer amount must be greater than zero.");
        }

        @Test
        @DisplayName("Should reject transfer when target account is null")
        void transferToNullAccount() {
            BigDecimal transferAmount = new BigDecimal("10.00");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> sourceAccount.transfer(null, transferAmount), "Expected an exception when the " + "target " +
                            "account is null.");
            assertTrue(exception.getMessage().contains("Target account must not be null"), "Exception message should "
                    + "indicate that the target account must not be null.");
        }

        @Test
        @DisplayName("Should reject transfer with insufficient funds to cover fee")
        void transferWithInsufficientFundsForFee() {
            sourceAccount.setBalance(new BigDecimal("15.00"));
            BigDecimal transferAmount = new BigDecimal("14.00");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> sourceAccount.transfer(targetAccount, transferAmount),
                    "Expected an exception when there " + "are insufficient funds to cover the transfer amount and " +
                            "fee.");
            assertTrue(exception.getMessage().contains("Insufficient funds"), "Exception message should indicate " +
                    "insufficient funds.");
        }
    }
}
