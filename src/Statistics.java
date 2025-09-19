import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int entryCount;

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
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Подсчитываем частоту операционных систем
        String os = entry.getUserAgent().getOperatingSystem();
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);

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
                " getOperatingSystemStatistics " + getOperatingSystemStatistics().toString()
                ;
    }
}