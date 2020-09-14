package strategy;

import customindicators.AdjustedIchimokuSpanAIndicator;
import customindicators.AdjustedIchimokuSpanBIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

/**
 * Have to take care of previous candles. 78 bars for SPAN B Indicator
 */
public class IchimokuStrategy implements StrategyRule {

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series, 26);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series, 9);
        AdjustedIchimokuSpanAIndicator greenLine = new AdjustedIchimokuSpanAIndicator(series, blueLine, maroonLine);
        AdjustedIchimokuSpanBIndicator redLine = new AdjustedIchimokuSpanBIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);

        Rule entryRule = new OverIndicatorRule(blueLine, maroonLine)
                .and(new OverIndicatorRule(maroonLine, greenLine))
                .and(new OverIndicatorRule(maroonLine, redLine))
                .and(new OverIndicatorRule(close, blueLine))
                .and(new UnderIndicatorRule(rsi, 70));

        Rule exitRule = new UnderIndicatorRule(close, maroonLine)
                .or(new UnderIndicatorRule(rsi, 50))
                .or(new StopGainRule(close, PrecisionNum.valueOf(25)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(5)));

        return new BaseStrategy(entryRule, exitRule);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series, 26);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series, 9);
        AdjustedIchimokuSpanAIndicator greenLine = new AdjustedIchimokuSpanAIndicator(series, blueLine, maroonLine);
        AdjustedIchimokuSpanBIndicator redLine = new AdjustedIchimokuSpanBIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);

        Rule entryRule = new UnderIndicatorRule(maroonLine, greenLine)
                .and(new UnderIndicatorRule(maroonLine, redLine))
                .and(new UnderIndicatorRule(blueLine, maroonLine))
                .and(new UnderIndicatorRule(close, blueLine))
                .and(new OverIndicatorRule(rsi, 30));

        Rule exitRule = new OverIndicatorRule(close, maroonLine)
                .or(new OverIndicatorRule(blueLine, maroonLine))
                .or(new StopGainRule(close, PrecisionNum.valueOf(11)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(3.5)));

        return new BaseStrategy(entryRule, exitRule);
    }
}
