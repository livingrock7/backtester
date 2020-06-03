package strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.StopGainRule;
import org.ta4j.core.trading.rules.StopLossRule;

public class HMAStrategy implements StrategyRule {
    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        HMAIndicator hma100 = new HMAIndicator(close, 10);
        HMAIndicator hma200 = new HMAIndicator(close, 20);

        Rule entry = new CrossedUpIndicatorRule(hma100, hma200);

        Rule exit = new CrossedDownIndicatorRule(hma100, hma200)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));

        return new BaseStrategy(entry, exit);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        HMAIndicator hma100 = new HMAIndicator(close, 10);
        HMAIndicator hma200 = new HMAIndicator(close, 20);

        Rule entry = new CrossedDownIndicatorRule(hma100, hma200);

        Rule exit = new CrossedUpIndicatorRule(hma100, hma200)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));

        return new BaseStrategy(entry, exit);
    }
}
