package strategy;

import customindicators.AveragePriceIndicator;
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
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

public class BollingerBandStrategy implements StrategyRule{

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        EMAIndicator emaIndicator = new EMAIndicator(close,20);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(averagePriceIndicator, 200);
        BollingerBandsMiddleIndicator bbMiddleBand = new BollingerBandsMiddleIndicator(close);
        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        Rule entryRule = new OverIndicatorRule(bbLowerBand,5).and(new CrossedUpIndicatorRule(sma,10));
        Rule exitRule = new CrossedUpIndicatorRule(bbMiddleBand,2).or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        EMAIndicator emaIndicator = new EMAIndicator(close,20);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(averagePriceIndicator, 200);
        BollingerBandsMiddleIndicator bbMiddleBand = new BollingerBandsMiddleIndicator(close);
        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        Rule entryRule = new InSlopeRule(bbUpperBand,PrecisionNum.valueOf(-5),PrecisionNum.valueOf(0)).and(new CrossedDownIndicatorRule(sma,10));
        Rule exitRule = new CrossedDownIndicatorRule(bbMiddleBand,2).or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }
}
