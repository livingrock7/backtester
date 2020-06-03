package dataprocess;

import com.opencsv.CSVWriter;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.PrecisionNum;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitmexTimeFrameConverter {

    public static void main(String[] args) throws IOException {
        BarDataLoader dataLoader = new BarDataLoader();
        BarSeries series = dataLoader.createBarSeriesBitMex("bitmex_XBTUSD_60m_MARCH_6_MARCH_14.csv", "XBTUSD", Integer.MAX_VALUE);
        new BitmexTimeFrameConverter().convertTo(series, 60, 240);
    }

    private void convertTo(BarSeries series, int seriesDuration, int targetDuration) throws IOException {

        int len = series.getBarCount();
        int converter = targetDuration / seriesDuration;
        List<String[]> data = new ArrayList<String[]>();

        ZonedDateTime startTime = series.getFirstBar().getEndTime();
        ZonedDateTime lastTime = series.getLastBar().getEndTime();
        Path source = Paths.get(System.getProperty("user.home") + "/Downloads/bitmex_" + series.getName() + "_" + targetDuration + "m_"
                + startTime.getMonth() + "_" + startTime.getDayOfMonth() + "_"
                + lastTime.getMonth() + "_" + lastTime.getDayOfMonth() + ".csv");

        File file = new File(source.toUri());
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile, '\t',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        for (int i = 0; i < len; i += converter) {
            PrecisionNum open = (PrecisionNum) series.getBar(i).getOpenPrice();
            PrecisionNum high = (PrecisionNum) series.getBar(i).getHighPrice();
            PrecisionNum low = (PrecisionNum) series.getBar(i).getLowPrice();
            PrecisionNum volume = PrecisionNum.valueOf(0);
            int j = i;
            if (j + converter > len) {
                break;
            }
            for (; j < i + converter; j++) {
                if (high.isLessThan(series.getBar(j).getHighPrice())) {
                    high = (PrecisionNum) series.getBar(j).getHighPrice();
                }
                if (series.getBar(j).getLowPrice().isLessThan(low)) {
                    low = (PrecisionNum) series.getBar(j).getLowPrice();
                }
                volume = (PrecisionNum) volume.plus(series.getBar(j).getVolume());
            }
            PrecisionNum close = (PrecisionNum) series.getBar(j - 1).getClosePrice();
            try {
                data.add(new String[]{series.getBar(j).getEndTime().toEpochSecond() + "", open.toString(), high.toString(), low.toString(), close.toString(), volume.toString()});
            }catch (Exception e){
                System.out.println("Skipped last bar");
            }
        }

        writer.writeAll(data);
        writer.close();

    }

}
