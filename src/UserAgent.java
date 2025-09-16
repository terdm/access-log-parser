import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class UserAgent {
    private final String browser;
    private final String operatingSystem;

    public UserAgent(String userAgentString) {
        this.browser = parseBrowser(userAgentString);
        this.operatingSystem = parseOperatingSystem(userAgentString);
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    private String parseBrowser(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return "Unknown";
        }

        String ua = userAgentString.toLowerCase();

        if (ua.contains("edge") || ua.contains("edg/")) {
            return "Edge";
        } else if (ua.contains("firefox") || ua.contains("fxios")) {
            return "Firefox";
        } else if (ua.contains("chrome") && !ua.contains("chromium")) {
            return "Chrome";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("opera") || ua.contains("presto")) {
            return "Opera";
        } else if (ua.contains("yandex")) {
            return "Yandex";
        } else if (ua.contains("bingbot")) {
            return "BingBot";
        } else if (ua.contains("googlebot")) {
            return "GoogleBot";
        } else if (ua.contains("yandexbot")) {
            return "YandexBot";
        } else {
            return "Other";
        }
    }

    private String parseOperatingSystem(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return "Unknown";
        }

        String ua = userAgentString.toLowerCase();

        if (ua.contains("windows") || ua.contains("win32") || ua.contains("win64")) {
            return "Windows";
        } else if (ua.contains("mac") || ua.contains("os x") || ua.contains("darwin")) {
            return "macOS";
        } else if (ua.contains("linux") && !ua.contains("android")) {
            return "Linux";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("ios") || ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "UserAgent{browser='" + browser + "', operatingSystem='" + operatingSystem + "'}";
    }
}