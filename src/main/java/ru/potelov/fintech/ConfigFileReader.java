package ru.potelov.fintech;

import ru.potelov.fintech.util.AppConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigFileReader {

    public List<String> getAllCodes() {

        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(AppConstants.CONFIG)) {
            properties.load(input);

            String codes = properties.getProperty("curCodes");

            String[] array = codes.split("\\s*,\\s*");

            return new ArrayList<>(Arrays.asList(array));

        } catch (IOException e) {
            System.out.println("Не удалось прочитать конфиг");
            e.printStackTrace();
        }
        return null;
    }
}
