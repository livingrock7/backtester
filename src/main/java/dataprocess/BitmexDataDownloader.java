package dataprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BitmexDataDownloader {

    public static void main(String[] args) {

        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date fromDate = simpleDateFormat.parse("01/05/2020");
            Date toDate = simpleDateFormat.parse("21/05/2020");
            String symbol = "XBTUSD";

            Path source = Paths.get(System.getProperty("user.home") + "/Downloads/bitmex_" + symbol + "_" + Month.of(fromDate.getMonth() + 1) + "_" + fromDate.getDate() + "_" +
                    Month.of(toDate.getMonth() + 1) + "_" + toDate.getDate() + ".csv");
            File file = new File(source.toUri());

            StringBuilder builder = getBitmexData(fromDate, toDate, symbol);

            JSONObject jsonObject = JSON.parseObject(builder.toString());

            writeToCSV(file, jsonObject);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    private static StringBuilder getBitmexData(Date fromDate, Date toDate, String symbol) throws IOException {
        String s = "60";
        URL url = new URL("https://www.bitmex.com/api/udf/history?symbol=" + symbol + "&resolution=" + s + "&from=" +
                ((fromDate.getTime() / 1000)+300) + "&to=" + ((toDate.getTime() / 1000) - 300));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        StringBuilder builder = new StringBuilder();
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }
        conn.disconnect();
        return builder;
    }

    private static void writeToCSV(File file, JSONObject jsonObject) throws IOException {
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile, '\t',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        System.out.println(jsonObject);

        JSONArray time = jsonObject.getJSONArray("t");
        JSONArray open = jsonObject.getJSONArray("o");
        JSONArray high = jsonObject.getJSONArray("h");
        JSONArray low = jsonObject.getJSONArray("l");
        JSONArray close = jsonObject.getJSONArray("c");
        JSONArray volume = jsonObject.getJSONArray("v");

        int i = 0;
        int len = time.size();
        List<String[]> data = new ArrayList<String[]>();
        for (i = 0; i < len; i++) {
            data.add(new String[]{time.getString(i), open.getString(i), high.getString(i), low.getString(i), close.getString(i), volume.getString(i)});
        }

        writer.writeAll(data);
        writer.close();
    }


}
