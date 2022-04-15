package teammates.e2e.cases;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 */
public class InstructorNotificationsPageE2ETest extends BaseE2ETestCase {
    private AccountAttributes account;
    private NotificationAttributes notificationToMarkAsRead;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        account = testData.accounts.get("INotifications.instr");
        notificationToMarkAsRead = testData.notifications.get("notification3");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_NOTIFICATIONS_PAGE);
        InstructorNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, InstructorNotificationsPage.class,
                account.getGoogleId());

        ______TS("verify that only the correct notifications are shown");
        NotificationAttributes[] notShownNotifications = {
                testData.notifications.get("notification2"),
                testData.notifications.get("expiredNotification1"),
        };
        NotificationAttributes[] shownNotifications = {
                testData.notifications.get("notification1"),
                testData.notifications.get("notification3"),
                testData.notifications.get("notification4"),
        };

        notificationsPage.verifyNotShownNotifications(notShownNotifications);
        notificationsPage.verifyShownNotifications(shownNotifications, account.getReadNotifications().keySet());

        ______TS("mark notification as read");
        notificationsPage.markNotificationAsRead(notificationToMarkAsRead);
        notificationsPage.verifyStatusMessage("Notification marked as read.");

        // Verify that account's readNotifications attribute is updated
        Map<String, Instant> readNotifications = new HashMap<>();
        readNotifications.put(notificationToMarkAsRead.getNotificationId(), notificationToMarkAsRead.getEndTime());
        readNotifications.putAll(account.getReadNotifications());
        account.setReadNotifications(readNotifications);
        verifyPresentInDatabase(account);

        notificationsPage.verifyNotificationTab(notificationToMarkAsRead, account.getReadNotifications().keySet());
    }
}
