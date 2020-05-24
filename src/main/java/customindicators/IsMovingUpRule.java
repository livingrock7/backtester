package customindicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.AbstractRule;

public class IsMovingUpRule extends AbstractRule {

    private Indicator<Num> ref;
    private int barCount;

    public IsMovingUpRule(Indicator<Num> ref, int barCount) {
        this.ref = ref;
        this.barCount = barCount;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        if (index < barCount)
            return false;
        int fromIndex = index - barCount;
        for (int i = fromIndex; i <= index; i++) {
            if (ref.getValue(i).minus(ref.getValue(i + 1)).isGreaterThan(PrecisionNum.valueOf(0)))
                return true;
        }
        return false;
    }
}
