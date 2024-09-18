package io.github.easyretrofit.extension.retry.core;

public class MaxRetriesExceededException extends RuntimeException {
    private final transient String causingRetryName;

    private MaxRetriesExceededException(String causingRetryName, String message, boolean writeableStackTrace) {
        super(message, null, false, writeableStackTrace);
        this.causingRetryName = causingRetryName;
    }

    /**
     * Static method to construct a {@link MaxRetriesExceededException}
     *
     * @param retry the Retry which failed
     */
    public static MaxRetriesExceededException createMaxRetriesExceededException(Retry retry) {
        boolean writeStackTrace = retry.getConfig().isWriteableStackTrace();
        String message = String.format(
                "Retry '%s' has exhausted all attempts (%d)",
                retry.getResourceName(),
                retry.getConfig().getMaxRetries()
        );
        return new MaxRetriesExceededException(retry.getResourceName(), message, writeStackTrace);
    }

    /**
     * @return the name of the {@link Retry} that caused this exception
     */
    public String getCausingRetryName() {
        return causingRetryName;
    }
}
