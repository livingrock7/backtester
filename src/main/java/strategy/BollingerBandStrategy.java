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
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
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
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(close,20);
        //BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        //BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));

        Rule entryRule = new CrossedUpIndicatorRule(bbLowerBand,close).and(new OverIndicatorRule(sma,close));
        Rule exitRule = new CrossedUpIndicatorRule(bbMiddleBand,close).or(new StopGainRule(close, PrecisionNum.valueOf(2)))
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
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(close,20);
        BollingerBandsMiddleIndicator bbMiddleBand = new BollingerBandsMiddleIndicator(close);
        //BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));
        //BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,emaIndicator,PrecisionNum.valueOf(2));

        BollingerBandsUpperIndicator bbUpperBand = new BollingerBandsUpperIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        BollingerBandsLowerIndicator bbLowerBand = new BollingerBandsLowerIndicator(bbMiddleBand,stdDev,PrecisionNum.valueOf(2));
        Rule entryRule = new CrossedDownIndicatorRule(bbUpperBand,close).and(new UnderIndicatorRule(sma,close));
        Rule exitRule = new CrossedDownIndicatorRule(bbMiddleBand,close).or(new StopGainRule(close, PrecisionNum.valueOf(2)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1)));
        return new BaseStrategy(entryRule,exitRule);
    }
}
