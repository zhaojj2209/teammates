package teammates.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import teammates.common.util.Config;
import teammates.common.util.StringHelper;

/**
 * Settings for component tests.
 */
public final class TestProperties {

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

    /** The value of "test.localdatastore.port" in test.properties file. */
    public static final int TEST_LOCALDATASTORE_PORT;

    /** The value of "test.localpostgres.port" in test.properties file. */
    public static final int TEST_LOCALPOSTGRES_PORT;

    /** The value of "test.localpostgres.username" in test.properties file. */
    public static final String TEST_LOCALPOSTGRES_USERNAME;

    /** The value of "test.localpostgres.password" in test.properties file. */
    public static final String TEST_LOCALPOSTGRES_PASSWORD;

    /** The value of "test.localpostgres.DB" in test.properties file. */
    public static final String TEST_LOCALPOSTGRES_DB;

    /** Indicates whether auto-update snapshot mode is activated. */
    public static final boolean IS_SNAPSHOT_UPDATE;

    /** The value of "test.search.service.host" in test.search.service.host file. */
    public static final String SEARCH_SERVICE_HOST;

    private TestProperties() {
        // access static fields directly
    }

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/test/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            IS_SNAPSHOT_UPDATE = Boolean.parseBoolean(prop.getProperty("test.snapshot.update", "false"));
            TEST_LOCALDATASTORE_PORT = Integer.parseInt(prop.getProperty("test.localdatastore.port"));
            TEST_LOCALPOSTGRES_PORT = Integer.parseInt(prop.getProperty("test.localpostgres.port", "5433"));
            TEST_LOCALPOSTGRES_USERNAME = prop.getProperty("test.localpostgres.username", "teammates-test");
            TEST_LOCALPOSTGRES_PASSWORD = prop.getProperty("test.localpostgres.password", "teammates-test");
            TEST_LOCALPOSTGRES_DB = prop.getProperty("test.localpostgres.db", "teammates-test");
            SEARCH_SERVICE_HOST = prop.getProperty("test.search.service.host");

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTestPostgresConnectionUrl() {
        return "jdbc:postgresql://localhost:"
                + TestProperties.TEST_LOCALPOSTGRES_PORT + "/" + TestProperties.TEST_LOCALPOSTGRES_DB;
    }

    public static boolean isSearchServiceActive() {
        return !StringHelper.isEmpty(SEARCH_SERVICE_HOST);
    }

}
