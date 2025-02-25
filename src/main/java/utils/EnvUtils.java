package utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}
