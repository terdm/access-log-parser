import java.time.LocalDateTime;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int entryCount;
    private List<LogEntry> entries = new ArrayList<>();
    private int errorRequestsCount = 0; // Количество ошибочных запросов

    // Переменная для хранения несуществующих страниц (404)
    private Set<String> nonExistentPages = new HashSet<>();

    // Переменная для подсчета частоты браузеров
    private Map<String, Integer> browserFrequency = new HashMap<>();

    // Переменная для хранения существующих страниц сайта (код ответа 200)
    private Set<String> existingPages = new HashSet<>();

    // Переменная для подсчета частоты операционных систем
    private Map<String, Integer> osFrequency = new HashMap<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
    }

    public void addEntry(LogEntry entry) {
        this.totalTraffic += entry.getResponseSize();
        this.entryCount++;

        LocalDateTime entryTime = entry.getTime();

        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }

        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
        // Добавляем страницу с кодом ответа 200 в список существующих страниц
        if (Objects.equals(entry.getResponseCode(), "200")) {
            existingPages.add(entry.getPath());
        }

        // Подсчитываем частоту операционных систем
        String os = entry.getUserAgent().getOperatingSystem();
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);

        // Проверяем код ответа и добавляем в nonExistentPages если 404
        if (Objects.equals(entry.getResponseCode(), "404")) {
            nonExistentPages.add(entry.getPath());
        }

        // Обновляем статистику браузеров
        String browser = entry.getUserAgent().getBrowser(); // или метод для получения браузера
        browserFrequency.put(browser, browserFrequency.getOrDefault(browser, 0) + 1);

        entries.add(entry);
        // Проверяем на ошибочный код ответа (4xx или 5xx)
        if (isErrorResponse( entry.getResponseCode())) {
            errorRequestsCount++;
        }
    }
    /**
     * Проверяет, является ли код ответа ошибочным (4xx или 5xx)
     */
    private boolean isErrorResponse(String event) {
        if (event == null || event.trim().isEmpty()) {
            return false;
        }

        try {
            // Предполагаем, что event содержит HTTP код ответа
            // Например: "GET /page 404", "POST /api 500", etc.
            String[] parts = event.split(" ");
            for (String part : parts) {
                if (part.matches("^[45]\\d\\d$")) {
                    return true;
                }
            }
        } catch (Exception e) {
            // В случае ошибки парсинга считаем не ошибочным
        }
        return false;
    }
    /**
     * Метод подсчёта среднего количества посещений сайта за час
     * @return среднее количество посещений в час (только не боты)
     */
    public double getAverageVisitsPerHour() {
        if (entries.isEmpty()) {
            return 0.0;
        }

        // Фильтруем только не-боты
        long nonBotVisits = entries.stream()
                .filter(entry -> !entry.getUserAgent().isBot())
                .count();

        if (nonBotVisits == 0) {
            return 0.0;
        }

        // Находим временной диапазон
        LocalDateTime minTime = getMinTime();
        LocalDateTime maxTime = getMaxTime();

        // Вычисляем период в часах
        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            hours = 1; // Минимум 1 час чтобы избежать деления на 0
        }

        return (double) nonBotVisits / hours;
    }

    /**
     * Метод подсчёта среднего количества ошибочных запросов в час
     * @return среднее количество ошибочных запросов в час
     */
    public double getAverageErrorRequestsPerHour() {
        if (entries.isEmpty() || errorRequestsCount == 0) {
            return 0.0;
        }

        // Находим временной диапазон
        LocalDateTime minTime = getMinTime();
        LocalDateTime maxTime = getMaxTime();

        // Вычисляем период в часах
        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            hours = 1;
        }

        return (double) errorRequestsCount / hours;
    }

    /**
     * Метод расчёта средней посещаемости одним пользователем
     * @return средняя посещаемость на одного пользователя (не бота)
     */
    public double getAverageVisitsPerUser() {
        if (entries.isEmpty()) {
            return 0.0;
        }

        // Фильтруем только не-боты
        List<LogEntry> nonBotEntries = entries.stream()
                .filter(entry -> !entry.getUserAgent().isBot())
                .collect(Collectors.toList());

        if (nonBotEntries.isEmpty()) {
            return 0.0;
        }

        // Количество посещений не-ботами
        long nonBotVisits = nonBotEntries.size();

        // Количество уникальных IP-адресов не-ботов
        long uniqueNonBotIps = nonBotEntries.stream()
                .map(LogEntry::getIpAddr)
                .distinct()
                .count();

        if (uniqueNonBotIps == 0) {
            return 0.0;
        }

        return (double) nonBotVisits / uniqueNonBotIps;
    }




    /**
     * Возвращает список всех несуществующих страниц сайта (код ответа 404)
     * @return Set<String> с адресами несуществующих страниц
     */
    public Set<String> getNonExistentPages() {
        return new HashSet<>(nonExistentPages);
    }

    /**
     * Возвращает статистику браузеров в виде Map с долями (0-1)
     * @return Map<String, Double> где ключ - название браузера, значение - доля
     */
    public Map<String, Double> getBrowserStatistics() {
        // Вычисляем общее количество записей браузеров
        int total = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        // Создаем новую Map с долями
        Map<String, Double> browserStatistics = new HashMap<>();

        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
            double proportion = (double) entry.getValue() / total;
            browserStatistics.put(entry.getKey(), proportion);
        }

        return browserStatistics;
    }

    // Альтернативная реализация с использованием Stream API
    public Map<String, Double> getBrowserStatisticsStream() {
        int total = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        return browserFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (double) entry.getValue() / total
                ));
    }


    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0.0;
        }

        Duration duration = Duration.between(minTime, maxTime);
        double hours = duration.toHours();

        if (hours == 0) {
            // Если разница меньше часа, считаем как минимум 1 час
            hours = 1.0;
        }

        return totalTraffic / hours;
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public int getEntryCount() {
        return entryCount;
    }

    /**
     * Возвращает список всех существующих страниц сайта (с кодом ответа 200)
     * @return Set<String> уникальных адресов существующих страниц
     */
    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages); // Возвращаем копию для безопасности
    }

    /**
     * Возвращает статистику операционных систем в виде долей (от 0 до 1)
     * @return Map<String, Double> где ключи - названия ОС, значения - их доли
     */
    public Map<String, Double> getOperatingSystemStatistics() {
        // Вычисляем общее количество записей
        int total = osFrequency.values().stream().mapToInt(Integer::intValue).sum();

        // Создаем новую Map для хранения долей
        Map<String, Double> osStatistics = new HashMap<>();

        // Рассчитываем долю для каждой операционной системы
        for (Map.Entry<String, Integer> entry : osFrequency.entrySet()) {
            double proportion = (double) entry.getValue() / total;
            osStatistics.put(entry.getKey(), proportion);
        }

        return osStatistics;
    }

    // Альтернативная реализация с использованием Stream API
    public Map<String, Double> getOperatingSystemStatisticsStream() {
        int total = osFrequency.values().stream().mapToInt(Integer::intValue).sum();

        return osFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (double) entry.getValue() / total
                ));
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", entryCount=" + entryCount +
                ", trafficRate=" + getTrafficRate() +
                " bytes/hour}" +
                " ExistingPages " + getExistingPages().toString() +
                " OperatingSystemStatistics " + getOperatingSystemStatistics().toString() +
                " NonExistentPages" + getNonExistentPages().toString() +
                " BrowserStatistics" + getBrowserStatistics().toString()
                ;
    }
}