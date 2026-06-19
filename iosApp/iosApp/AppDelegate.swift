import UIKit
import FirebaseCore
import FirebaseCrashlytics
import FirebaseMessaging
import UserNotifications
import sharedKit

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        
        let crashlytics = Crashlytics.crashlytics()
        crashlytics.setCrashlyticsCollectionEnabled(true)

        // Forward seller identity from shared code to native Crashlytics.
        CrashlyticsBridge.shared.userHandler = { id, name, phone in
            Crashlytics.crashlytics().setUserID(id)
            Crashlytics.crashlytics().setCustomValue(name, forKey: "user_name")
            Crashlytics.crashlytics().setCustomValue(phone, forKey: "user_phone")
        }
        CrashlyticsBridge.shared.clearHandler = {
            Crashlytics.crashlytics().setUserID("")
            Crashlytics.crashlytics().setCustomValue("", forKey: "user_name")
            Crashlytics.crashlytics().setCustomValue("", forKey: "user_phone")
        }

        Messaging.messaging().delegate = self
        
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            }
        }
        
        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        let apnsToken = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        print("APNs token received: \(apnsToken)")
        Messaging.messaging().apnsToken = deviceToken
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        print("FCM token (delegate): \(token)")
        UserDefaults.standard.set(token, forKey: "firebase_fcm_token")
        UserDefaults.standard.synchronize()
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        print("Remote notification received: \(userInfo)")
        Messaging.messaging().appDidReceiveMessage(userInfo)
        completionHandler(.noData)
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        print("Foreground notification received: \(notification.request.content.userInfo)")
        completionHandler([.banner, .badge, .sound])
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        print("User tapped notification: \(response.notification.request.content.userInfo)")
        completionHandler()
    }
}
