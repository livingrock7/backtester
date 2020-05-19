import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Double.parseDouble;

public class CleanUp {

    public static void main(String[] args) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd]-MMM-uu [HH][H]:[mm][m] z");

        BarSeries series = new BaseBarSeriesBuilder().withName("my_2017_series").build();

        String filePath = Objects.requireNonNull(CleanUp.class.getClassLoader().getResource("BTCUSD_2017_2019_5m.txt")).getPath();

        Duration duration = Duration.ofMinutes(5);

        MutableLong mutableLong = new MutableLong(ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant().toEpochMilli());
        MutableInt mutableInt = new MutableInt(0);

        HashSet<Long> ts = new HashSet<>();
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

        List<String[]> liss = new ArrayList<>();
        List<String[]> liss_1 = new ArrayList<>();


        Files.lines(Paths.get(filePath))
                .map(l -> l.split("\\t"))
                .forEach(g -> {
                    try {
                        long fileDate = ZonedDateTime.parse(g[1] + " " + g[7] + " GMT", formatter).toInstant().toEpochMilli();
                        if (fileDate != mutableLong.getValue()) {

                            System.out.println(g[1] + " " + g[7]);
                            mutableInt.getAndIncrement();
                            String[] e = liss.get(liss.size() - 1).clone();
                            LocalDateTime triggerTime =
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(mutableLong.getValue()),
                                            TimeZone.getTimeZone("UTC").toZoneId());
                            e[7] = triggerTime.getHour() + ":" + triggerTime.getMinute();
                            liss.add(e);

                            mutableLong.add(300000);

                            if (mutableLong.getValue() != fileDate) {

                                while (fileDate > mutableLong.getValue()) {

                                    String[] e1 = liss.get(liss.size() - 1).clone();
                                    LocalDateTime triggerTime1 =
                                            LocalDateTime.ofInstant(Instant.ofEpochMilli(mutableLong.getValue()),
                                                    TimeZone.getTimeZone("UTC").toZoneId());
                                    e1[7] = triggerTime1.getHour() + ":" + triggerTime1.getMinute();
                                    liss.add(e1);
                                    mutableLong.add(300000);

                                }

                            }

                            // mutableLong.setValue(fileDate);
                        }
                        liss.add(g);
                        mutableLong.add(300000);
                        series.addBar(duration, ZonedDateTime.parse(g[1] + " " + g[7] + " GMT", formatter), parseDouble(g[2]), parseDouble(g[3]), parseDouble(g[4]), parseDouble(g[5]), parseDouble(g[6]));
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                });

       /* int k = 0;
        for (int i = 1; i <= 315360; i++) {
            if (!ts.contains(mutableLong.getValue())) {
                System.out.println(simple.format(new Date(mutableLong.getValue())));
                k++;
            }
            mutableLong.add(300000);
        }*/


        System.out.println("liss : " + liss.size());


        MutableLong mutableLong_1 = new MutableLong(ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant().toEpochMilli());

        FileUtils.writeLines(new File("output.txt"), "UTF8", liss);

        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/moonx/moonx/opensource-libs/tabacktest/src/main/resources/BTCUSD_2017_2019_5m_clean.txt", true));

        for (String[] arr : liss) {
            writer.append(String.join("\t", arr));
            writer.append("\n");
        }

        writer.close();

        liss.stream()
                .forEach(g -> {
                    try {
                        long fileDate = ZonedDateTime.parse(g[1] + " " + g[7] + " GMT", formatter).toInstant().toEpochMilli();
                        if (fileDate != mutableLong_1.getValue()) {
                            System.out.println(g[1] + " " + g[7]);
                            mutableLong_1.setValue(fileDate);
                            mutableLong_1.getAndIncrement();
                            liss_1.add(liss_1.get(liss_1.size() - 1));
                        }
                        liss_1.add(g);
                        mutableLong_1.add(300000);
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                });

        System.out.println("liss_1 : " + liss_1.size());


    }

}
