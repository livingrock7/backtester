package strategy;

//import jdk.internal.org.jline.utils.DiffHelper;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanAIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanBIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

public class IchimokuStrategy implements StrategyRule{

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series);
        IchimokuSenkouSpanAIndicator greenLine = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator redLine = new IchimokuSenkouSpanBIndicator(series);
        DifferenceIndicator diff = new DifferenceIndicator(blueLine,redLine);

        Rule entryRule = new OverIndicatorRule(greenLine,50).and(new OverIndicatorRule(redLine,50)).and(new CrossedUpIndicatorRule(blueLine,50));
        Rule exitRule = new UnderIndicatorRule(blueLine,50)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series);
        IchimokuSenkouSpanAIndicator greenLine = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator redLine = new IchimokuSenkouSpanBIndicator(series);
        DifferenceIndicator diff = new DifferenceIndicator(blueLine,redLine);

        Rule entryRule = new UnderIndicatorRule(greenLine,50).and(new UnderIndicatorRule(redLine,50)).and(new CrossedDownIndicatorRule(maroonLine,50));
        Rule exitRule = new UnderIndicatorRule(blueLine,50)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }
}
