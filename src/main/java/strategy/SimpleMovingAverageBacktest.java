package strategy;

import customindicators.AveragePriceIndicator;
import org.ta4j.core.*;
import org.ta4j.core.BarSeries;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
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

public class SimpleMovingAverageBacktest {

    public static void main(String[] args) throws IOException {
        BarSeries series = createBarSeries();

        Strategy strategy3DaySma = create3DaySmaStrategy(series);

        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord3DaySma = seriesManager.run(strategy3DaySma, Order.OrderType.BUY,
                PrecisionNum.valueOf(1));
        System.out.println(tradingRecord3DaySma);

        Strategy strategy2DaySma = create2DaySmaStrategy(series);
        TradingRecord tradingRecord2DaySma = seriesManager.run(strategy2DaySma, Order.OrderType.BUY,
                PrecisionNum.valueOf(1));
        System.out.println(tradingRecord2DaySma);

        AnalysisCriterion criterion = new TotalProfitCriterion();
        Num calculate3DaySma = criterion.calculate(series, tradingRecord3DaySma);
        Num calculate2DaySma = criterion.calculate(series, tradingRecord2DaySma);

        System.out.println(calculate3DaySma);
        System.out.println(calculate2DaySma);
    }

    private static BarSeries createBarSeries() throws IOException {
        String filePath = Objects.requireNonNull(SimpleMovingAverageBacktest.class.getClassLoader().getResource("BTCUSD_2017_2019_5m_clean.txt")).getPath();
        BarSeries series = new BaseBarSeriesBuilder().withName("BTC_USD").build();
        Duration duration = Duration.ofMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd]-MMM-uu [HH][H]:[mm][m] z");
        series.setMaximumBarCount(2000);

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

    private static BaseBar createBar(ZonedDateTime endTime, Number openPrice, Number highPrice, Number lowPrice,
                                     Number closePrice, Number volume) {
        return BaseBar.builder(PrecisionNum::valueOf, Number.class).timePeriod(Duration.ofDays(1)).endTime(endTime)
                .openPrice(openPrice).highPrice(highPrice).lowPrice(lowPrice).closePrice(closePrice).volume(volume)
                .build();
    }

    private static ZonedDateTime CreateDay(int day) {
        return ZonedDateTime.of(2018, 01, day, 12, 0, 0, 0, ZoneId.systemDefault());
    }

    private static Strategy create3DaySmaStrategy(BarSeries series) {
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(averagePriceIndicator, 24);
        return new BaseStrategy(new UnderIndicatorRule(sma, averagePriceIndicator), new OverIndicatorRule(sma, averagePriceIndicator));
    }

    private static Strategy create2DaySmaStrategy(BarSeries series) {
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(averagePriceIndicator, 56);
        return new BaseStrategy(new UnderIndicatorRule(sma, averagePriceIndicator), new OverIndicatorRule(sma, averagePriceIndicator));
    }
}