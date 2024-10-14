package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApi {
    private static final String API_URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final String API_KEY = "API_KEY";

    public static void main(String[] args) {
        double lat = -27.114410;
        double lon = -109.425270;
        int limit = 7;

        try {
            String jsonResponse = fetchWeatherData(lat, lon, limit);
            printWeatherData(jsonResponse);
            int[] temperatures = extractTemperaturesFromJson(jsonResponse, limit);
            double averageTemp = calculateAverageTemperature(temperatures);
            System.out.println("Средняя температура за " + limit + " дней: " + averageTemp + "°C");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String fetchWeatherData(double lat, double lon, int limit) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "?lat=" + lat + "&lon=" + lon + "&limit=" + limit))
                .header("X-Yandex-Weather-Key", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static void printWeatherData(String jsonResponse) {
        System.out.println("Полные данные о погоде:");
        System.out.println(jsonResponse);

        String tempKey = "\"temp\":";
        int tempIndex = jsonResponse.indexOf(tempKey);
        if (tempIndex != -1) {
            int start = tempIndex + tempKey.length();
            int end = jsonResponse.indexOf(',', start);
            String temperature = jsonResponse.substring(start, end).trim();
            System.out.println("Текущая температура: " + temperature + "°C");
        } else {
            System.out.println("Температура не найдена в ответе.");
        }
    }

    private static int[] extractTemperaturesFromJson(String jsonResponse, int limit) {
        int[] temperatures = new int[limit];
        String dayTempKey = "\"temp_avg\":";

        int startIndex = 0;
        int count = 0;
        while (count < limit) {
            int tempIndex = jsonResponse.indexOf(dayTempKey, startIndex);
            if (tempIndex != -1) {
                int start = tempIndex + dayTempKey.length();
                int end = jsonResponse.indexOf(',', start);
                String dayTemperature = jsonResponse.substring(start, end).trim();
                temperatures[count] = Integer.parseInt(dayTemperature);
                count++;
                startIndex = end;
            } else {
                break;
            }
        }
        return temperatures;
    }

    private static double calculateAverageTemperature(int[] temperatures) {
        if (temperatures.length == 0) {
            return 0;
        }

        double sumTemperature = 0;
        for (int temperature : temperatures) {
            sumTemperature += temperature;
        }

        return sumTemperature / temperatures.length;
    }
}