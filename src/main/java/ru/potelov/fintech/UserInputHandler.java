package ru.potelov.fintech;

import ru.potelov.fintech.util.AppConstants;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class UserInputHandler {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> scheduledFuture;

    private List<String> codes;

    public UserInputHandler(List<String> codes) {
        this.codes = codes;
    }

    public String[] startProcessingInput() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("Добро пожаловать в приложение \"Конвертер валют!\"\n");
            System.out.println("При необходимости вы можете остановить приложение напечатав - \"exit\"\n");
            System.out.println("У меня есть:");

            String currencyFirst = scanner.nextLine().toUpperCase().trim();

            if (currencyFirst.equals(AppConstants.EXIT)) {
                break;
            }

            System.out.println("Хочу приобрести:");

            String currencySecond = scanner.nextLine().toUpperCase().trim();

            if (currencySecond.equals(AppConstants.EXIT)) {
                break;
            }

            if (codes == null) {
                System.out.println("Не удалось получить список доступных валют");
                break;
            }

            boolean firstIsExist = codes.contains(currencyFirst);
            boolean secondIsExist = codes.contains(currencySecond);

            if (!firstIsExist || !secondIsExist) {
                System.out.println("-----------------------------------------------");
                System.out.println("Пожалуйста, убедитесь, что Вы корректно набрали валюту, ниже приведен список доступных для использования\n");
                printListAvailableCurrencies();
                continue;
            }
            return new String[]{currencyFirst, currencySecond};
        }
        return new String[0];
    }

    public void showProgressBar(Future<?> future) {
        scheduledFuture = scheduler.scheduleAtFixedRate(() ->
                System.out.print("."), 0, 50, TimeUnit.MILLISECONDS);
        try {
            future.get(30, TimeUnit.SECONDS);
            future.cancel(true);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            hideProgressBar();
        }
    }

    public void hideProgressBar() {
        scheduledFuture.cancel(true);
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private void printListAvailableCurrencies() {
        String codesList = codes.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        System.out.println(codesList);
    }
}
