package strategy;

import customindicators.AveragePriceIndicator;
import customindicators.IsMovingUpRule;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

public class BollingerBandStrategy implements StrategyRule{

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        EMAIndicator emaIndicator = new EMAIndicator(close,20);
        SMAIndicator sma = new SMAIndicator(close, 20);
        BollingerBandsMiddleIndicator bbMiddleBand = new BollingerBandsMiddleIndicator(sma);
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(close,20);
        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        DifferenceIndicator diffLow = new DifferenceIndicator(bbLowerBand,close);
        DifferenceIndicator diffHigh = new DifferenceIndicator(bbUpperBand,close);
        //Rule entryRule = new CrossedUpIndicatorRule(close,bbLowerBand).and(new IsMovingUpRule(rsi,2).and(new UnderIndicatorRule(rsi,43));
        Rule entryRule = new OverIndicatorRule(diffLow,-5).and(new UnderIndicatorRule(diffLow,5)).and(new IsMovingUpRule(rsi,2)).and(new UnderIndicatorRule(rsi,43));
        Rule exitRule = new StopGainRule(close, PrecisionNum.valueOf(3.5))
                .or(new StopLossRule(close, PrecisionNum.valueOf(0.25)));
        return new BaseStrategy(entryRule,exitRule);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        EMAIndicator emaIndicator = new EMAIndicator(close,20);
        SMAIndicator sma = new SMAIndicator(close, 20);
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(close,20);
        BollingerBandsMiddleIndicator bbMiddleBand = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        DifferenceIndicator diffHigh = new DifferenceIndicator(bbUpperBand,close);
        Rule entryRule = new OverIndicatorRule(diffHigh,-5).and(new UnderIndicatorRule(diffHigh,5)).and(new CrossedDownIndicatorRule(rsi,59.40));
        Rule exitRule = new IsMovingUpRule(bbUpperBand,3).or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }
}
