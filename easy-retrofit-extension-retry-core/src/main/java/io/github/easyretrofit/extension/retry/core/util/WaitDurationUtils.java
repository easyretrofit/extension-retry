package io.github.easyretrofit.extension.retry.core.util;

public class WaitDurationUtils {

    public static long getWaitDuration(String waitDuration) {
        if (waitDuration.contains("ms")) {
            return Long.parseLong(waitDuration.replace("ms", ""));
        } else if (waitDuration.contains("s")) {
            return Long.parseLong(waitDuration.replace("s", "")) * 1000;
        } else if (waitDuration.contains("m")) {
            return Long.parseLong(waitDuration.replace("m", "")) * 1000 * 60;
        } else {
            return Long.parseLong(waitDuration);
        }
    }
}
