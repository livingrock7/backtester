package customindicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

public class AdjustedIchimokuSpanAIndicator extends CachedIndicator<Num> {

    /**
     * The Tenkan-sen indicator
     */
    private final IchimokuTenkanSenIndicator conversionLine;

    /**
     * The Kijun-sen indicator
     */
    private final IchimokuKijunSenIndicator baseLine;

    /**
     * Constructor.
     *
     * @param series the series
     */
    public AdjustedIchimokuSpanAIndicator(BarSeries series) {
        this(series, new IchimokuTenkanSenIndicator(series), new IchimokuKijunSenIndicator(series));
    }

    /**
     * Constructor.
     *
     * @param series                 the series
     * @param barCountConversionLine the time frame for the conversion line (usually
     *                               9)
     * @param barCountBaseLine       the time frame for the base line (usually 26)
     */
    public AdjustedIchimokuSpanAIndicator(BarSeries series, int barCountConversionLine, int barCountBaseLine) {
        this(series, new IchimokuTenkanSenIndicator(series, barCountConversionLine),
                new IchimokuKijunSenIndicator(series, barCountBaseLine));
    }

    /**
     * Constructor.
     *
     * @param series         the series
     * @param conversionLine the conversion line
     * @param baseLine       the base line
     */
    public AdjustedIchimokuSpanAIndicator(BarSeries series, IchimokuTenkanSenIndicator conversionLine,
                                          IchimokuKijunSenIndicator baseLine) {
        super(series);
        this.conversionLine = conversionLine;
        this.baseLine = baseLine;
    }

    protected Num calculate(int index) {
        if (index < 26)
            return PrecisionNum.valueOf(0);
        int pastIndex = index - 26;
        return conversionLine.getValue(pastIndex).plus(baseLine.getValue(pastIndex)).dividedBy(PrecisionNum.valueOf(2));
    }
}
