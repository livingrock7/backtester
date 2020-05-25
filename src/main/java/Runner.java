import dataprocess.BarDataLoader;
import lombok.Getter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.num.PrecisionNum;
import strategy.*;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

@Getter
public class Runner {

    Properties properties;

    public static void main(String[] args) {

        Runner runner = new Runner();
        String filePath = Objects.requireNonNull(runner.getClass().getClassLoader().getResource("config.properties")).getPath();
        runner.loadProps(filePath);

        Stream.of(IchimokuStrategy.class)
                .forEach(c -> {
                    try {
                        StrategyRule rule = c.newInstance();
                        Stream.of(Objects.requireNonNull(
                                new File(runner.getProperties().getProperty("file.url"))
                                        .listFiles((FileFilter) FileFilterUtils.directoryFileFilter())))
                                .forEach(t -> {
                                    System.out.println(t.getName());
                                    for (File file : Objects.requireNonNull(t.listFiles())) {
                                        loadAndTest(rule, file);
                                    }
                                });
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });


    }

    private static void loadAndTest(StrategyRule rule, File file) {
        BarDataLoader loader = new BarDataLoader();
        try {
            BarSeries series = loader.createBarSeriesBitMex(file.getPath(), "XBTUSD", Integer.MAX_VALUE);
            BarSeriesManager seriesManager = new BarSeriesManager(series);

            Strategy longStrategy = rule.getLongStrategy(series);
            Strategy shortStrategy = rule.getShortStrategy(series);

            int winningLongTrades = 0;
            int winningShortTrades = 0;
            int losingLongTrades = 0;
            int losingShortTrades = 0;

            TradingRecord longs = seriesManager.run(longStrategy, Order.OrderType.BUY, PrecisionNum.valueOf(10));
            for (Trade trade : longs.getTrades()) {
                System.out.println(series.getBar(trade.getEntry().getIndex()));
                System.out.println(series.getBar(trade.getExit().getIndex()));
                System.out.println("---------------------------------");
                if(trade.getProfit().isGreaterThan(PrecisionNum.valueOf(0)))
                {
                    winningLongTrades++;
                }
                else
                {
                    losingLongTrades++;
                }
            }
            TotalProfitCriterion longProfit = new TotalProfitCriterion();
            System.out.println("Number of long trades: " + longs.getTrades().size());
            System.out.println(file.getName() + " - Long Profit :: " + longProfit.calculate(series, longs));
            //Double ratio = (double) (winningLongTrades / losingLongTrades);
            System.out.println("Win to lose ratio for long trades:" + winningLongTrades + "/" + losingLongTrades);

            TradingRecord shorts = seriesManager.run(shortStrategy, Order.OrderType.SELL, PrecisionNum.valueOf(10));
            TotalProfitCriterion shortProfit = new TotalProfitCriterion();
            System.out.println("Number of short trades: " + shorts.getTrades().size());
            for (Trade trade : shorts.getTrades()) {
                System.out.println(series.getBar(trade.getEntry().getIndex()));
                System.out.println(series.getBar(trade.getExit().getIndex()));
                System.out.println("---------------------------------");
                if(trade.getProfit().isGreaterThan(PrecisionNum.valueOf(0)))
                {
                    winningShortTrades++;
                }
                else
                {
                    losingShortTrades++;
                }
            }
            System.out.println(file.getName() + " - Short Profit :: " + shortProfit.calculate(series, shorts));
            System.out.println("Win to lose ratio for short trades:" + winningShortTrades + "/" + losingShortTrades);
            System.out.println("---------------");
            int total = longs.getTrades().size() + shorts.getTrades().size();
            System.out.println("Total trades: " + total);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProps(String path) {
        try (InputStream input = new FileInputStream(path)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
