package strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

public class HMABBStart implements StrategyRule {
    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {

        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator sma20 = new SMAIndicator(close, 20);
        BollingerBandsMiddleIndicator bbMiddle = new BollingerBandsMiddleIndicator(sma20);
        //BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(bbMiddle, new StandardDeviationIndicator(bbMiddle, 20), PrecisionNum.valueOf(2));
        RSIIndicator rsiIndicator = new RSIIndicator(close, 14);

        HMAIndicator hma20 = new HMAIndicator(close, 20);

        Rule entry = new CrossedUpIndicatorRule(hma20, bbMiddle)
                .and(new UnderIndicatorRule(rsiIndicator, 59))
                .and(new OverIndicatorRule(close, hma20));

        Rule exit = new CrossedDownIndicatorRule(hma20, bbMiddle)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));


        return new BaseStrategy(entry, exit);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        BollingerBandsMiddleIndicator bbMiddle = new BollingerBandsMiddleIndicator(close);

        HMAIndicator hma20 = new HMAIndicator(close, 20);

        Rule entry = new CrossedDownIndicatorRule(hma20, bbMiddle)
                .and(new UnderIndicatorRule(close, hma20));

        Rule exit = new CrossedUpIndicatorRule(hma20, bbMiddle)
                .or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));


        return new BaseStrategy(entry, exit);
    }
}
