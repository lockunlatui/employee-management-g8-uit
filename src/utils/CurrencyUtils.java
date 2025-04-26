package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    public static String formatVND(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        Locale vietnamLocale = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);
        currencyFormatter.setMaximumFractionDigits(0);
        return currencyFormatter.format(amount);
    }

}
