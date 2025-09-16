import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        this.ipAddr = parseIpAddress(logLine);
        this.time = parseTime(logLine);
        this.method = parseMethod(logLine);
        this.path = parsePath(logLine);
        this.responseCode = parseResponseCode(logLine);
        this.responseSize = parseResponseSize(logLine);
        this.referer = parseReferer(logLine);
        this.userAgent = new UserAgent(parseUserAgentString(logLine));
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    private String parseIpAddress(String logLine) {
        Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }

    private LocalDateTime parseTime(String logLine) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            String timeString = matcher.group(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            return LocalDateTime.parse(timeString, formatter);
        }
        return LocalDateTime.now();
    }

    private HttpMethod parseMethod(String logLine) {
        Pattern pattern = Pattern.compile("\"([A-Z]+)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            try {
                return HttpMethod.valueOf(matcher.group(1));
            } catch (IllegalArgumentException e) {
                return HttpMethod.UNKNOWN;
            }
        }
        return HttpMethod.UNKNOWN;
    }

    private String parsePath(String logLine) {
        Pattern pattern = Pattern.compile("\"[A-Z]+\\s+([^\\s?]+)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private int parseResponseCode(String logLine) {
        Pattern pattern = Pattern.compile("\"\\s+(\\d{3})\\s+");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private int parseResponseSize(String logLine) {
        Pattern pattern = Pattern.compile("\\s+(\\d+)\\s+\"");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            try {
                String vResponseSize = matcher.group(1);
                //System.out.println("vResponseSize " + vResponseSize);
                return Integer.parseInt(vResponseSize);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String parseReferer(String logLine) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s+\"([^\"]*)\"$");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            String referer = matcher.group(1);
            return "-".equals(referer) ? "" : referer;
        }
        return "";
    }

    private String parseUserAgentString(String logLine) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s+\"([^\"]*)\"$");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            String userAgent = matcher.group(2);
            return "-".equals(userAgent) ? "" : userAgent;
        }
        return "";
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddr='" + ipAddr + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", path='" + path + '\'' +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", referer='" + referer + '\'' +
                ", userAgent=" + userAgent +
                '}';
    }
}