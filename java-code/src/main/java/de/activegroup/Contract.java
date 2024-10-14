package de.activegroup;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/*
 * 1. einfaches Beispiel
 * Zero-Bond: "Ich bekomme 100€ am 24.12.2024."
 * zero-coupon bond
 *
 * 2. zerlegen in Bausteine, als Richtschnur die Attribute des Beispiels
 * - Währung: "Ich bekomme nen EUR jetzt."
 * - Betrag
 * - Später
 *   ... mit Selbstbezügen
 *
 * 3. von vorn
 * Currency-Swap:
 * Weihnachten:
 * - ich bekomme 100€
 * - ich bezahle 105$
 *
 * - Ich bekomme Weihnachten 100€ -UND-
 * - Ich bezahle Weihnachten 105$.
 */

sealed public interface Contract {
    /* OOP
    // Front-Office
    double getValue();
    // Back-Office
    List<Payment> getPayments();
     */
    // record ZeroCouponBond(double amount, Currency currency, LocalDate date)
    //    implements Contract{}

    // Ich bekomme 1EUR jetzt.
    record One(Currency currency) implements Contract {}

    // Ich bekomme 100EUR jetzt.
    record Product(double amount, Contract contract) implements Contract {};

    record DueDate(LocalDate date, Contract contract) implements Contract {}

    record Combination(Contract contract1, Contract contract2) implements Contract {}

    // record WithDirection(Direction direction, Contract contract) implements Contract {}

    record Inverse(Contract contract) implements Contract {}

    record Zero() implements Contract {}

    static Contract zeroCouponBond(double amount, Currency currency, LocalDate date) {
        return new DueDate(date, new Product(amount, new One(currency)));
    }

    // smart constructor
    static Contract combination(Contract contract1, Contract contract2) {
        return switch (new Tuple2<>(contract1, contract2)) {
            case Tuple2(Zero(), Contract c) -> c;
            case Tuple2(Contract c, Zero()) -> c;
            default -> new Combination(contract1, contract2);
         };
    }

    // Denotation
    // Zahlungen bis now
    static Tuple2<List<Payment>, Contract> denotation(Contract contract, LocalDate now) {
        return switch (contract) {
            case Zero() -> new Tuple2<>(List.of(), new Zero());
            case One(Currency currency) ->
                    new Tuple2<>(List.of(new Payment(Direction.LONG, now, 1, currency)),
                            new Zero());
            case Product(double amount, Contract contract1) -> {
                var tuple = denotation(contract1, now);
                List<Payment> productPayments = tuple.a().stream().map(payment -> payment.scale(amount)).toList();
                yield new Tuple2<>(productPayments, new Product(amount, tuple.b())); // dragons
            }
            case DueDate(LocalDate date, Contract contract1) ->
                now.isAfter(date)
                        ? denotation(contract1, now)
                        : new Tuple2<>(List.of(), contract);
            case Inverse(Contract contract1) -> {
                var tuple = denotation(contract1, now);
                yield new Tuple2<>(tuple.a().stream().map(payment -> payment.invert()).toList(),
                        new Inverse(tuple.b()));
            }

            case Combination(Contract contract1, Contract contract2) -> {
                var tuple1 = denotation(contract1, now);
                var tuple2 = denotation(contract2, now);
                var allPayments = new ArrayList<Payment>();
                allPayments.addAll(tuple1.a());
                allPayments.addAll(tuple2.a());
                yield new Tuple2<>(allPayments, combination(tuple1.b(), tuple2.b()));
            }
        };
    }

    class ContractExamples {
        static LocalDate christmas = LocalDate.of(2024, Month.DECEMBER, 24);
        // Contract zcb1 = new ZeroCouponBond(100, Currency.EUR, LocalDate.of(2024, Month.DECEMBER, 24));
        Contract c1 = new One(Currency.EUR);
        // Ich bekomme 100€ jetzt.
        Contract c2 = new Product(100, c1);
        // Ich bekomme 5000€ jetzt.
        Contract c3 = new Product(50, c2);

        Contract zcb1 = new DueDate(christmas, new Product(100, new One(Currency.EUR)));

        Contract swap1 = new Combination(zcb1,
                new Inverse(zeroCouponBond(105, Currency.USD, christmas)));

        static Contract dragon = new Product(100,
                new Combination(new One(Currency.EUR), new DueDate(christmas, new One(Currency.EUR))));
    }
}
