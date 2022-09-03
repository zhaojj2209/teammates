package teammates.storage.api;

import org.flywaydb.core.Flyway;
import teammates.common.util.Config;

public class SchemaMigration {

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure().dataSource(
                Config.getDbConnectionUrl(),
                Config.APP_LOCALPOSTGRES_USERNAME,
                Config.APP_LOCALPOSTGRES_PASSWORD)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}
