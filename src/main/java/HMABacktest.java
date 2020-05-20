import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.ProfitLossPercentageCriterion;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

import java.io.IOException;

public class HMABacktest {

    public static void main(String[] args) throws IOException {

        BarDataLoader dataLoader = new BarDataLoader();
        BarSeries series = dataLoader.createBarSeriesBitMex("Bitmex_1_19_5m.csv", Integer.MAX_VALUE);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        Strategy buyStrategy = hmaBuy(closePriceIndicator, averagePriceIndicator);
        Strategy sellStrategy = hmaSell(closePriceIndicator, averagePriceIndicator);
        Strategy rsiBuyStrategy = rsiBuy(closePriceIndicator, averagePriceIndicator);


        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord hmaBuyTradingRecord = seriesManager.run(buyStrategy, Order.OrderType.BUY,
                PrecisionNum.valueOf(0.01));

        TradingRecord hmaSellTradingRecord = seriesManager.run(sellStrategy, Order.OrderType.SELL,
                PrecisionNum.valueOf(0.01));

        TradingRecord rsiBuyTradingRecord =seriesManager.run(rsiBuyStrategy, Order.OrderType.BUY,
                PrecisionNum.valueOf(0.05));

        System.out.println("Number Of trades : " + hmaBuyTradingRecord.getTrades().size());

        for (Trade trade : hmaBuyTradingRecord.getTrades()) {
            System.out.println("Entry Order :: " + trade.getEntry());
            System.out.println("Exit Order :: " + trade.getExit());
            System.out.println("PNL :: "+ trade.getProfit());
            System.out.println("----------------------\n");
        }


        PrecisionNum profit = PrecisionNum.valueOf(0);
        for (Trade trade : hmaBuyTradingRecord.getTrades()) {
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }
        System.out.println("Buy Profit hma: " + profit);

        System.out.println("Number Of trades : " + rsiBuyTradingRecord.getTrades().size());

        for (Trade trade : rsiBuyTradingRecord.getTrades()) {
            System.out.println("Entry Bar :: " + series.getBar(trade.getEntry().getIndex()));
            System.out.println("Entry Order :: " + trade.getEntry());
            System.out.println("Exit Bar :: " + series.getBar(trade.getExit().getIndex()));
            System.out.println("Exit Order :: " + trade.getExit());
            System.out.println("----------------------\n");
        }


        PrecisionNum extraProfit = PrecisionNum.valueOf(0);
        for (Trade trade : rsiBuyTradingRecord.getTrades()) {
            extraProfit = (PrecisionNum) profit.plus(trade.getProfit());
        }
        System.out.println("Buy Profit rsi: " + extraProfit);


        profit = PrecisionNum.valueOf(0);
        for (Trade trade : hmaSellTradingRecord.getTrades()) {
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }
        System.out.println("Sell Profit : " + profit);

        AnalysisCriterion buyCriterion = new ProfitLossPercentageCriterion();
        Num buyStartegy = buyCriterion.calculate(series, hmaBuyTradingRecord);
        //Num buyStartegy = buyCriterion.calculate(series, rsiBuyTradingRecord);
        System.out.println("Buy Strategy : "+buyStartegy);

        AnalysisCriterion sellCriterion = new ProfitLossPercentageCriterion();
        Num sellStartegy = sellCriterion.calculate(series, hmaSellTradingRecord);
        System.out.println("Sell Strategy : "+sellStartegy);


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
        RSIIndicator rsiIndicator = new RSIIndicator(priceIndicator, 14);
        Rule entryRule = new IsEqualRule(rsiIndicator,30);
        Rule exitRule = new IsEqualRule(rsiIndicator,57);
        return new BaseStrategy(entryRule, exitRule);
    }

}