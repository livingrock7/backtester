import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.ProfitLossPercentageCriterion;
import org.ta4j.core.analysis.criteria.TotalLossCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class ExponentialMovingAverageBacktest {

    public static void main(String[] args) throws IOException {
        BarSeries series = createBarSeries();

        ClosePriceIndicator averagePriceIndicator = new ClosePriceIndicator(series);
        Strategy ema1428Startegy = ema1428Startegy(series, averagePriceIndicator);

        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord3DaySma = seriesManager.run(ema1428Startegy, Order.OrderType.SELL,
                PrecisionNum.valueOf(1));
        System.out.println(tradingRecord3DaySma);

        PrecisionNum profit= PrecisionNum.valueOf(0);

        for(Trade trade: tradingRecord3DaySma.getTrades()){
            profit = (PrecisionNum) profit.plus(trade.getProfit());
        }

        System.out.println(profit);

        AnalysisCriterion profitCriterion = new TotalProfitCriterion();
        Num calculate1428EmaProfit = profitCriterion.calculate(series, tradingRecord3DaySma);
        System.out.println(calculate1428EmaProfit);
    }

    private static BarSeries createBarSeries() throws IOException {
        String filePath = Objects.requireNonNull(CleanUp.class.getClassLoader().getResource("BTCUSD_2017_2019_5m_clean.txt")).getPath();
        BarSeries series = new BaseBarSeriesBuilder().withName("BTC_USD").build();
        Duration duration = Duration.ofMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd]-MMM-uu [HH][H]:[mm][m] z");
        series.setMaximumBarCount(8640);
        Files.lines(Paths.get(filePath))
                .map(l -> l.split("\\t"))
                .forEach(g -> series.addBar(duration,
                        ZonedDateTime.parse(g[1] + " " + g[7] + " GMT", formatter),
                        parseDouble(g[2]),
                        parseDouble(g[3]),
                        parseDouble(g[4]),
                        parseDouble(g[5]),
                        parseDouble(g[6])
                ));
        return series;
    }

    private static Strategy ema1428Startegy(BarSeries series, PriceIndicator priceIndicator) {
        RSIIndicator rsiIndicator = new RSIIndicator(priceIndicator, 14);

        MACDIndicator macdIndicator = new MACDIndicator(priceIndicator, 12, 26);

        EMAIndicator ema14 = new EMAIndicator(priceIndicator, 14);
        EMAIndicator ema28 = new EMAIndicator(priceIndicator, 28);
        return new BaseStrategy(new CrossedUpIndicatorRule(ema14, ema28), new CrossedDownIndicatorRule(ema14, ema28));
    }

}