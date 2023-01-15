package teammates.storage.api;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.info.MigrationInfoDumper;
import teammates.common.util.Config;
import teammates.common.util.Logger;

public class SchemaMigration {

    private static final Logger log = Logger.getLogger();

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure().dataSource(
                Config.getDbConnectionUrl(),
                Config.APP_LOCALPOSTGRES_USERNAME,
                Config.APP_LOCALPOSTGRES_PASSWORD)
                .baselineVersion(Config.APP_FLYWAY_BASELINEVERSION)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();

        log.info(MigrationInfoDumper.dumpToAsciiTable(flyway.info().all()));
    }
}
