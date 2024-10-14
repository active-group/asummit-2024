package de.activegroup;

/*
 * - Seife, da interessiert der pH-Wert
 * - Shampoo, da interessiert der Haartyp
 * - Duschgel bestehend aus Seife und Shampoo (in gleichen Teilen)
 */

sealed interface ShowerProduct {

    record Soap(double pH) implements ShowerProduct {
    }

    // Haartyp:
    // - brittle ODER
    // - dry ODER
    // - bald ODER
    // - oily
    enum Hairtype {
        BRITTLE, DRY, BALD, OILY
    }

    record Shampoo(Hairtype hairtype) implements ShowerProduct {
    }

//    record ShowerGel(Soap soap, Shampoo shampoo) implements ShowerProduct {
//    }

    // Selbstreferenz
    record Mixture(ShowerProduct product1, ShowerProduct product2)
        implements ShowerProduct {}

    static double soapProportion(ShowerProduct product) {
        return switch (product) {
            case Soap(double pH) -> 1.0;
            case Shampoo(Hairtype hairtype) -> 0.0;
            case Mixture(ShowerProduct product1, ShowerProduct product2) ->
                    (soapProportion(product1) + soapProportion(product2))/2.0;
        };
    }

    class ShowerProductExamples {
        Soap soap1 = new Soap(7);
        Soap soap2 = new Soap(5);
        Shampoo shampoo1 = new Shampoo(Hairtype.OILY);
        Shampoo shampoo2 = new Shampoo(Hairtype.BALD);
        ShowerProduct gel1 = new Mixture(soap1, shampoo1);
        ShowerProduct mix1 = new Mixture(gel1, soap2);
        ShowerProduct mix2 = new Mixture(mix1, shampoo2);
    }
}

