import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class Starter {

    public static void main(String[] args) throws IOException {

        String filePath = Objects.requireNonNull(CleanUp.class.getClassLoader().getResource("BTCUSD_2017_2019_5m_clean.txt")).getPath();
        BarSeries series = new BaseBarSeriesBuilder().withName("BTC_USD").build();
        Duration duration = Duration.ofMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd]-MMM-uu [HH][H]:[mm][m] z");

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

        BarSeriesManager seriesManager = new BarSeriesManager(series);

        System.out.println(series);


    }
}
