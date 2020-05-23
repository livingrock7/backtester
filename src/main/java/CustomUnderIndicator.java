import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

public class CustomUnderIndicator extends UnderIndicatorRule {
    public CustomUnderIndicator(Indicator<Num> indicator, Number threshold) {
        super(indicator, threshold);
    }

    public CustomUnderIndicator(Indicator<Num> indicator, Num threshold) {
        super(indicator, threshold);
    }

    public CustomUnderIndicator(Indicator<Num> first, Indicator<Num> second) {
        super(first, second);
    }

}
