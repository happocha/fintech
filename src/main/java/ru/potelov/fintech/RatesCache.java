package ru.potelov.fintech;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.javac.main.Option;
import ru.potelov.fintech.model.ApiResponse;
import ru.potelov.fintech.model.CacheEntry;
import ru.potelov.fintech.util.AppConstants;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatesCache {

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private Map<String, CacheEntry> cache = new HashMap<>();

    public void shutdown() {
        executor.shutdown();
    }

    public void writeObject(ApiResponse apiResponse) {
        executor.submit(() -> {
            try (PrintWriter printWriter = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(AppConstants.FILE_PATH, true)))) {

                String key = apiResponse.getBase() + apiResponse.getRates().getName();

                CacheEntry cacheEntry = new CacheEntry();
                cacheEntry.setDate(LocalDate.parse(apiResponse.getDate()));
                cacheEntry.setRate(apiResponse.getRates().getRate());
                cacheEntry.setKey(key);

                cache.put(key, cacheEntry);

                printWriter.println(new Gson().toJson(cacheEntry));

            } catch (IOException e) {
                System.out.println("Не удалось найти файл");
            }
        });
    }

    public void readCache() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(AppConstants.FILE_PATH))) {

            String currentLine;

            Type itemsType = new TypeToken<CacheEntry>() {}.getType();

            Gson gson = new Gson();

            while ((currentLine = bufferedReader.readLine()) != null) {
                CacheEntry cacheEntry = gson.fromJson(currentLine, itemsType);
                cache.put(cacheEntry.getKey(), cacheEntry);
            }
        } catch (IOException e) {
            System.out.println("Не удалось найти файл");
        }
    }

    public CacheEntry findRateInCache(String key) {
        if (cache.get(key) != null) {
            if (validateDate(key)) {
                return cache.get(key);
            }
        }
        return null;
    }

    private boolean validateDate(String key) {
        LocalDate date = cache.get(key).getDate();
        return LocalDate.now().isEqual(date);
    }
}
