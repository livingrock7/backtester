package customindicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

public class AdjustedIchimokuSpanBIndicator extends CachedIndicator<Num> {

    /**
     * The period high
     */
    private final Indicator<Num> periodHigh;

    /**
     * The period low
     */
    private final Indicator<Num> periodLow;

    public AdjustedIchimokuSpanBIndicator(BarSeries series) {
        this(series, 52);
    }

    /**
     * Contructor.
     *
     * @param series   the series
     * @param barCount the time frame
     */
    public AdjustedIchimokuSpanBIndicator(BarSeries series, int barCount) {
        super(series);
        periodHigh = new HighestValueIndicator(new HighPriceIndicator(series), barCount);
        periodLow = new LowestValueIndicator(new LowPriceIndicator(series), barCount);
    }

    protected Num calculate(int index) {
        if (index < 78)
            return PrecisionNum.valueOf(0);
        index = index - 26;
        return periodHigh.getValue(index).plus(periodLow.getValue(index)).dividedBy(PrecisionNum.valueOf(2));
    }

}
