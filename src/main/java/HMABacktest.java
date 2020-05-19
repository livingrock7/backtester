import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.ProfitLossCriterion;
import org.ta4j.core.analysis.criteria.ProfitLossPercentageCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
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
        Strategy strategy = hma(closePriceIndicator, averagePriceIndicator);

        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord3DaySma = seriesManager.run(strategy, Order.OrderType.BUY,
                PrecisionNum.valueOf(0.01));

        System.out.println("Number Of trades : " + tradingRecord3DaySma.getTrades().size());

        for (Trade trade : tradingRecord3DaySma.getTrades()) {
            System.out.println("Entry Bar :: " + series.getBar(trade.getEntry().getIndex()));
            System.out.println("Entry Order :: " + trade.getEntry());
            System.out.println("Exit Bar :: " + series.getBar(trade.getExit().getIndex()));
            System.out.println("Exit Order :: " + trade.getExit());
            System.out.println("----------------------\n");
        }


        PrecisionNum profit = PrecisionNum.valueOf(0);

        for (Trade trade : tradingRecord3DaySma.getTrades()) {
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }

        System.out.println(profit);

        AnalysisCriterion profitCriterion = new ProfitLossPercentageCriterion();
        Num calculate1428EmaProfit = profitCriterion.calculate(series, tradingRecord3DaySma);
        System.out.println(calculate1428EmaProfit);
    }

    private static Strategy hma(ClosePriceIndicator priceIndicator, AveragePriceIndicator averagePriceIndicator) {
        HMAIndicator hmaIndicator = new HMAIndicator(priceIndicator, 100);
        Rule entryRule = new CrossedUpIndicatorRule(averagePriceIndicator, hmaIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(hmaIndicator, priceIndicator)
                .or(new StopGainRule(priceIndicator, PrecisionNum.valueOf(1)))
                .or(new StopLossRule(priceIndicator, PrecisionNum.valueOf(0.5)));
        return new BaseStrategy(entryRule, exitRule);
    }

}