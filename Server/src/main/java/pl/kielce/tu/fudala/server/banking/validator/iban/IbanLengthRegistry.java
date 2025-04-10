package pl.kielce.tu.fudala.server.banking.validator.iban;

import java.util.Map;


public final class IbanLengthRegistry {

    private static final Map<String, Integer> IBAN_LENGTHS = Map.<String, Integer>ofEntries(Map.entry("AL", 28),
            Map.entry("AD", 24), Map.entry("AT", 20), Map.entry("AZ", 28), Map.entry("BH", 22), Map.entry("BE", 16),
            Map.entry("BA", 20), Map.entry("BR", 29), Map.entry("BG", 22), Map.entry("CR", 21), Map.entry("HR", 21),
            Map.entry("CY", 28), Map.entry("CZ", 24), Map.entry("DK", 18), Map.entry("DO", 28), Map.entry("EE", 20),
            Map.entry("FO", 18), Map.entry("FI", 18), Map.entry("FR", 27), Map.entry("GE", 22), Map.entry("DE", 22),
            Map.entry("GI", 23), Map.entry("GR", 27), Map.entry("GL", 18), Map.entry("GT", 28), Map.entry("HU", 28),
            Map.entry("IS", 26), Map.entry("IE", 22), Map.entry("IL", 23), Map.entry("IT", 27), Map.entry("JO", 30),
            Map.entry("KZ", 20), Map.entry("KW", 30), Map.entry("LV", 21), Map.entry("LB", 28), Map.entry("LI", 21),
            Map.entry("LT", 20), Map.entry("LU", 20), Map.entry("MK", 19), Map.entry("MT", 31), Map.entry("MR", 27),
            Map.entry("MU", 30), Map.entry("MC", 27), Map.entry("MD", 24), Map.entry("ME", 22), Map.entry("NL", 18),
            Map.entry("NO", 15), Map.entry("PK", 24), Map.entry("PS", 29), Map.entry("PL", 28), Map.entry("PT", 25),
            Map.entry("QA", 29), Map.entry("RO", 24), Map.entry("SM", 27), Map.entry("SA", 24), Map.entry("RS", 22),
            Map.entry("SK", 24), Map.entry("SI", 19), Map.entry("ES", 24), Map.entry("SE", 24), Map.entry("CH", 21),
            Map.entry("TN", 24), Map.entry("TR", 26), Map.entry("AE", 23), Map.entry("GB", 22), Map.entry("VG", 24));

    public static Map<String, Integer> getIbanLengths() {
        return IBAN_LENGTHS;
    }

    private IbanLengthRegistry() {
    }
}
