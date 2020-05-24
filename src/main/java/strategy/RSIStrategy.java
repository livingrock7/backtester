package strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.StopGainRule;
import org.ta4j.core.trading.rules.StopLossRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

public class RSIStrategy implements StrategyRule {

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);

        Rule entry = new UnderIndicatorRule(rsi, 30);

        Rule exit = new OverIndicatorRule(rsi, 50)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));

        return new BaseStrategy(entry, exit);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);

        Rule entry = new OverIndicatorRule(rsi, 70);

        Rule exit = new UnderIndicatorRule(rsi, 50)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));

        return new BaseStrategy(entry, exit);
    }
}
