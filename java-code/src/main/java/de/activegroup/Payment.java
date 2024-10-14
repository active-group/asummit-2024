package de.activegroup;

import java.time.LocalDate;

public record Payment(Direction direction, LocalDate date, double amount, Currency currency) {
    Payment scale(double factor) {
        return new Payment(this.direction, this.date, this.amount * factor, this.currency);
    }

    Payment invert() {
        var newDirection = this.direction == Direction.LONG ? Direction.SHORT : Direction.LONG;
        return new Payment(newDirection, this.date, this.amount, this.currency);
    }
}
