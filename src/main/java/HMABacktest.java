import org.ta4j.core.*;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

import java.io.IOException;

public class HMABacktest {

    public static void main(String[] args) throws IOException {

        BarDataLoader dataLoader = new BarDataLoader();
        BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_JAN_1_MAY_21.csv", Integer.MAX_VALUE);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        Strategy buyStrategy = hmaBuy(closePriceIndicator, averagePriceIndicator);
        Strategy sellStrategy = hmaSell(closePriceIndicator, averagePriceIndicator);
        Strategy rsiBuyStrategy = rsiBuy(closePriceIndicator, averagePriceIndicator);


        BarSeriesManager seriesManager = new BarSeriesManager(series);

       TradingRecord hmaBuyTradingRecord = seriesManager.run(buyStrategy, Order.OrderType.BUY,
                PrecisionNum.valueOf(0.01));
        PrecisionNum profit = PrecisionNum.valueOf(0);
        for (Trade trade : hmaBuyTradingRecord.getTrades()) {
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }
        System.out.println("Buy Profit hma: " + profit);

        TradingRecord hmaSellTradingRecord = seriesManager.run(sellStrategy, Order.OrderType.SELL,
                PrecisionNum.valueOf(0.01));
        profit = PrecisionNum.valueOf(0);
        for (Trade trade : hmaSellTradingRecord.getTrades()) {
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }
        System.out.println("Sell Profit hma: " + profit);


        //RSI
        TradingRecord rsiBuyTradingRecord =seriesManager.run(rsiBuyStrategy, Order.OrderType.BUY,
                PrecisionNum.valueOf(0.01));
        PrecisionNum extraProfit = PrecisionNum.valueOf(0);
        for (Trade trade : rsiBuyTradingRecord.getTrades()) {
            extraProfit = (PrecisionNum) extraProfit.plus(trade.getProfit());
        }
        System.out.println("Buy Profit rsi: " + extraProfit);

    }

    private static Strategy hmaBuy(ClosePriceIndicator priceIndicator, AveragePriceIndicator averagePriceIndicator) {
        HMAIndicator hmaIndicator = new HMAIndicator(priceIndicator, 100);
        Rule entryRule = new CrossedUpIndicatorRule(priceIndicator, hmaIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(priceIndicator, hmaIndicator)
                .or(new StopGainRule(priceIndicator, PrecisionNum.valueOf(5)))
                .or(new StopLossRule(priceIndicator, PrecisionNum.valueOf(0.5)));
        return new BaseStrategy(entryRule, exitRule);
    }

    private static Strategy hmaSell(ClosePriceIndicator priceIndicator, AveragePriceIndicator averagePriceIndicator) {
        HMAIndicator hmaIndicator = new HMAIndicator(priceIndicator, 100);
        Rule entryRule = new CrossedDownIndicatorRule(priceIndicator, hmaIndicator);
        Rule exitRule = new CrossedUpIndicatorRule(priceIndicator, hmaIndicator)
                .or(new StopGainRule(priceIndicator, PrecisionNum.valueOf(5)))
                .or(new StopLossRule(priceIndicator, PrecisionNum.valueOf(0.5)));
        return new BaseStrategy(entryRule, exitRule);
    }

    private static Strategy rsiBuy(ClosePriceIndicator priceIndicator, AveragePriceIndicator averagePriceIndicator) {
        RSIIndicator rsiIndicator = new RSIIndicator(priceIndicator, 12);
        Rule entryRule = new UnderIndicatorRule(rsiIndicator,30);
        Rule exitRule = new OverIndicatorRule(rsiIndicator,57);
        return new BaseStrategy(entryRule, exitRule);
    }

}