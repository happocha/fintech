package ru.potelov.fintech;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.potelov.fintech.model.ApiResponse;
import ru.potelov.fintech.model.RateObject;
import ru.potelov.fintech.util.AppConstants;
import ru.potelov.fintech.util.RatesDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiClient {

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public ApiClient() {
    }

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(RateObject.class, new RatesDeserializer())
            .create();

    private String getJson(URL url) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return builder.toString();
            }

        } catch (IOException e) {
            System.out.println("Не удалось открыть соединение");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private URL createURL(String currencyFirst, String currencySecond) throws MalformedURLException {
        StringBuilder sb = new StringBuilder();
        sb.append(AppConstants.BASE_URL)
                .append("base=")
                .append(currencyFirst)
                .append("&symbols=")
                .append(currencySecond);

        return new URL(sb.toString());
    }

    private ApiResponse convertJsonToGson(String json, Gson gson) {
        return gson.fromJson(json, ApiResponse.class);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public Future<ApiResponse> requestRates(String currencyFirst, String currencySecond) {
        return executor.submit(() -> {
            URL url = createURL(currencyFirst, currencySecond);
            String response = getJson(url);
            return convertJsonToGson(response, gson);
        });
    }
}
