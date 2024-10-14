package de.activegroup;

sealed public interface Dosage {
    String format();
    record Tablets(int countMorning, int countNoon, int countEvening)
        implements Dosage {
        @Override
        public String format() {
            return "morgens " + countMorning + " Stück, " +
                    "mittags " + countNoon + ", abends" + countEvening;
        }
    }
    record Infusion(double mlPerMinute, double durationH)
        implements Dosage {
        @Override
        public String format() {
            return mlPerMinute + "ml/min für " + durationH + "h";
        }
    }

    static String format2(Dosage dosage) {
        return switch (dosage) {
            case Infusion(double mlPerMinute, double durationH) ->
                    mlPerMinute + "ml/min für " + durationH + "h";
            case Tablets(int countMorning, int countNoon, int countEvening) ->
                "mittags " + countNoon + ", abends" + countEvening;
        };
    }
}
