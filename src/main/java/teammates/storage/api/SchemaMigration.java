package teammates.storage.api;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.info.MigrationInfoDumper;
import teammates.common.util.Config;
import teammates.common.util.FlywayUtil;
import teammates.common.util.Logger;

public class SchemaMigration {

    private static final Logger log = Logger.getLogger();

    public static void main(String[] args) {
        Flyway flyway = FlywayUtil.getFlywayInst(
                Config.getDbConnectionUrl(), Config.getDbUsername(), Config.getDbPassword());
        flyway.migrate();
        log.info(MigrationInfoDumper.dumpToAsciiTable(flyway.info().all()));
    }
}
