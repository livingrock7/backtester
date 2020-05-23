import org.ta4j.core.*;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.InSlopeRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.StopGainRule;

import java.io.IOException;

public class HMASlopeTest {


    public static void main(String[] args) throws IOException {
        BarDataLoader dataLoader = new BarDataLoader();
        BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_MAY_20_MAY_21.csv", Integer.MAX_VALUE);
        //BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_JAN_1_MAY_21.csv", Integer.MAX_VALUE);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

        HMAIndicator hma = new HMAIndicator(closePriceIndicator, 20);


        Rule entry = new InSlopeRule(hma, PrecisionNum.valueOf(-0.2), PrecisionNum.valueOf(0.5));
        Rule exit = new InSlopeRule(hma, PrecisionNum.valueOf(2), PrecisionNum.valueOf(-1.5))
                .or(new OverIndicatorRule(new RSIIndicator(closePriceIndicator, 14), 50));
        Strategy strategy = new BaseStrategy(entry, exit);


        BarSeriesManager seriesManager = new BarSeriesManager(series);

        TradingRecord record = seriesManager.run(strategy, Order.OrderType.BUY, PrecisionNum.valueOf(0.01));

        PrecisionNum profit = PrecisionNum.valueOf(0);
        for(Trade trade : record.getTrades()){
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }

        System.out.println("Profit :: "+ profit);



    }
}
