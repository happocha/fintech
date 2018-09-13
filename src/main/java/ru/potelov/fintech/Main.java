package ru.potelov.fintech;

import ru.potelov.fintech.model.ApiResponse;
import ru.potelov.fintech.model.CacheEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws Exception {

        ConfigFileReader configFileReader = new ConfigFileReader();
        List<String> codes = configFileReader.getAllCodes();
        UserInputHandler userInputHandler = new UserInputHandler(codes);
        RatesCache ratesCache = new RatesCache();
        ApiClient apiClient = new ApiClient();
        ratesCache.readCache();

        while (true) {

            String[] currencies = userInputHandler.startProcessingInput();

            if (currencies.length == 0) {
                System.out.println("Программа завершена");
                break;
            }

            String currencyFirst = currencies[0];
            String currencySecond = currencies[1];

            CacheEntry cached = ratesCache.findRateInCache(currencyFirst + currencySecond);

            if (cached != null) {
                printResult(currencyFirst, currencySecond, cached.getRate());
            } else {
                Future<ApiResponse> future = apiClient.requestRates(currencyFirst, currencySecond);
                userInputHandler.showProgressBar(future);

                ApiResponse apiResponse = null;

                if (future.isDone()) {
                    apiResponse = future.get();
                    userInputHandler.hideProgressBar();
                }

                if (apiResponse != null && apiResponse.getRates() != null) {
                    ratesCache.writeObject(apiResponse);
                    printResult(currencyFirst, currencySecond, apiResponse.getRates().getRate());
                } else {
                    System.out.println();
                    System.out.println("Не удалось загрузить данные, пожалуйста, попробуйте еще раз!");
                }
            }
        }
        apiClient.shutdown();
        ratesCache.shutdown();
        userInputHandler.shutdown();
    }

    private static void printResult(String currencyFirst, String currencySecond, BigDecimal rate) {
        String result = String.format("%s => %s : %f", currencyFirst, currencySecond, rate);
        System.out.println();
        System.out.println(result);
    }
}
