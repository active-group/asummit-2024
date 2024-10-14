package de.activegroup;

import java.time.LocalDate;
import java.time.Month;

public class Main {
    public static void main(String[] args) {

        System.out.println(Contract.denotation(Contract.ContractExamples.dragon,
                LocalDate.of(2024, Month.OCTOBER, 14)));
    }
}