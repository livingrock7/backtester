package strategy;

import customindicators.IsMovingUpRule;
import dataprocess.BarDataLoader;
import org.ta4j.core.*;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.InSlopeRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import java.io.IOException;

public class HMASlopeTest {


    public static void main(String[] args) throws IOException {
        BarDataLoader dataLoader = new BarDataLoader();
        BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_MAY_20_MAY_21.csv", Integer.MAX_VALUE);
        //BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_JAN_1_MAY_21.csv", Integer.MAX_VALUE);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

        HMAIndicator hma = new HMAIndicator(closePriceIndicator, 20);
        RSIIndicator rsi = new RSIIndicator(closePriceIndicator, 14);

        Rule buyEntry = new IsMovingUpRule(hma, 5);
        Rule buyExit = new InSlopeRule(hma, PrecisionNum.valueOf(-5), PrecisionNum.valueOf(0))
                .or(new OverIndicatorRule(rsi, PrecisionNum.valueOf(60)));
        Strategy buyStartegy = new BaseStrategy(buyEntry, buyExit);

        BarSeriesManager seriesManager = new BarSeriesManager(series);

        TradingRecord buyRecord = seriesManager.run(buyStartegy, Order.OrderType.BUY, PrecisionNum.valueOf(0.01));

        PrecisionNum buyProfit = PrecisionNum.valueOf(0);
        for (Trade trade : buyRecord.getTrades()) {
            //if(trade.getProfit().isGreaterThan(PrecisionNum.valueOf(0))){
           // System.out.println(series.getBar(trade.getEntry().getIndex()));
           // System.out.println(series.getBar(trade.getExit().getIndex()));
            //System.out.println("---------------------------------");
            //}
            buyProfit = (PrecisionNum) buyProfit.plus(trade.getProfit());
        }

        System.out.println("Buy Profit :: " + buyProfit);


        Rule sellEntry = new InSlopeRule(hma, PrecisionNum.valueOf(-5), PrecisionNum.valueOf(0));
        Rule sellExit = new InSlopeRule(hma, PrecisionNum.valueOf(0), PrecisionNum.valueOf(5))
                .or(new UnderIndicatorRule(rsi, PrecisionNum.valueOf(40)));
        Strategy sellStrategy = new BaseStrategy(sellEntry, sellExit);

        TradingRecord sellRecord = seriesManager.run(sellStrategy, Order.OrderType.SELL, PrecisionNum.valueOf(0.01));

        PrecisionNum sellProfit = PrecisionNum.valueOf(0);
        for (Trade trade : sellRecord.getTrades()) {
            System.out.println(series.getBar(trade.getEntry().getIndex()));
            System.out.println(series.getBar(trade.getExit().getIndex()));
            System.out.println("---------------------");
            sellProfit = (PrecisionNum) sellProfit.plus(trade.getProfit());
        }
        System.out.println("Sell Profit :: " + sellProfit);


    }
}
