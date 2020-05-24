import dataprocess.BarDataLoader;
import lombok.Getter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.num.PrecisionNum;
import strategy.RSIStrategy;
import strategy.StrategyRule;

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

        Stream.of(RSIStrategy.class)
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

            TradingRecord longs = seriesManager.run(longStrategy, Order.OrderType.BUY, PrecisionNum.valueOf(1));
            TotalProfitCriterion longProfit = new TotalProfitCriterion();
            System.out.println(file.getName() + " - Long Profit :: " + longProfit.calculate(series, longs));

            TradingRecord shorts = seriesManager.run(shortStrategy, Order.OrderType.SELL, PrecisionNum.valueOf(1));
            TotalProfitCriterion shortProfit = new TotalProfitCriterion();
            System.out.println(file.getName() + " - Short Profit :: " + shortProfit.calculate(series, shorts));
            System.out.println("---------------");

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
