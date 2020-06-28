import dataprocess.BarDataLoader;
import lombok.Getter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.num.PrecisionNum;
import strategy.BollingerBandStrategy;
import strategy.IchimokuStrategy;
import strategy.StrategyRule;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

@Getter
public class Runner {

    Properties properties;
    static PrecisionNum size = PrecisionNum.valueOf(1);
    // This is our starting balance

    public static void main(String[] args) {

        Runner runner = new Runner();
        String filePath = Objects.requireNonNull(runner.getClass().getClassLoader().getResource("config.properties")).getPath();
        runner.loadProps(filePath);

        Stream.of(BollingerBandStrategy.class)
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


            // calculate drawdown for each trade
            // % drawdown for each trade
            // also R&R
            TradingRecord longs = seriesManager.run(longStrategy, Order.OrderType.BUY, size);
            TotalProfitCriterion longProfit = new TotalProfitCriterion();
            System.out.println(file.getName() + " - Long Profit :: ");
            calcProfit(longs, "LONGS");
            printTrades(longs, series);

            TradingRecord shorts = seriesManager.run(shortStrategy, Order.OrderType.SELL, size);
            TotalProfitCriterion shortProfit = new TotalProfitCriterion();
            System.out.println(file.getName() + " - Short Profit :: ");
            calcProfit(shorts, "SHORTS");
            printTrades(shorts, series);
            System.out.println("---------------");

            int totalTrades = longs.getTrades().size() + shorts.getTrades().size();
            System.out.println("Total number of trades: " + totalTrades);

            // positive # of trades (long or short) // and average % gain for the same
            // negative # of trades (long or short) // and average % loss for the same
                /* whenever these is loss fetch stop loss as well. SL is factor of risk per position */


            // returns %
            // total gain in value


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calcProfit(TradingRecord tradingRecord, String side) {
        PrecisionNum profit = PrecisionNum.valueOf(0);
        if (side.equals("LONGS")) {
            for (Trade trade : tradingRecord.getTrades()) {
                profit = PrecisionNum.valueOf((PrecisionNum) profit.plus(trade.getExit().getNetPrice().minus(trade.getEntry().getNetPrice()).multipliedBy(size)));
            }
        } else {
            for (Trade trade : tradingRecord.getTrades()) {
                profit = PrecisionNum.valueOf((PrecisionNum) profit.plus(trade.getEntry().getNetPrice().minus(trade.getExit().getNetPrice())));
            }
        }
        System.out.println("Profit :: " + profit);
    }

    private static void printTrades(TradingRecord tradingRecord, BarSeries series) {
        for (Trade trade : tradingRecord.getTrades()) {
            System.out.println("Time :;" + series.getBar(trade.getEntry().getIndex()).getEndTime());
            System.out.println("Entry :: " + trade.getEntry());
            System.out.println("Exit :: " + trade.getExit());
            System.out.println("Time :;" + series.getBar(trade.getExit().getIndex()).getEndTime());
            System.out.println("-------------");
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
