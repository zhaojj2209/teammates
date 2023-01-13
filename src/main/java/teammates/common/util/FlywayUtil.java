package teammates.common.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class FlywayUtil {
    private static final FluentConfiguration flywayBaseConfig;

    static {
        flywayBaseConfig = Flyway.configure()
                .baselineVersion(Config.APP_FLYWAY_BASELINEVERSION)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration");
    }

    private FlywayUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    public static Flyway getFlywayInst(String url, String username, String password) {
        return flywayBaseConfig.dataSource(url, username, password).load();
    }
}
