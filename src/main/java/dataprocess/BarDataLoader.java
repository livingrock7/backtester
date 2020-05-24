package dataprocess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class BarDataLoader {

    BarSeries createBarSeriesL1(String filename, int maxBarCount) throws IOException {
        String filePath = Objects.requireNonNull(BarDataLoader.class.getClassLoader().getResource(filename)).getPath();
        BarSeries series = new BaseBarSeriesBuilder().withName("BTC_USD").build();
        Duration duration = Duration.ofMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd]-MMM-uu [HH][H]:[mm][m] z");
        series.setMaximumBarCount(maxBarCount);
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

    public BarSeries createBarSeriesBitMex(String filePath, String symbol, int maxBarCount) throws IOException {
        BarSeries series = new BaseBarSeriesBuilder().withName(symbol).build();
        Duration duration = Duration.ofMinutes(5);
        series.setMaximumBarCount(maxBarCount);
        ZoneId zoneId = ZoneId.of("UTC");
        Files.lines(Paths.get(filePath))
                .map(l -> l.split("\\t"))
                .forEach(g -> series.addBar(duration,
                        ZonedDateTime.ofInstant( Instant.ofEpochSecond(Long.parseLong(g[0])), zoneId),
                        parseDouble(g[1]),
                        parseDouble(g[2]),
                        parseDouble(g[3]),
                        parseDouble(g[4]),
                        parseDouble(g[5])
                ));
        return series;
    }

}
