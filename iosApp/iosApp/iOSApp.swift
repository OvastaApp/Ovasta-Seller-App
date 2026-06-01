import SwiftUI
import sharedKit

@main
struct OvastaSellersApp: App {

    init() {
        // Force Arabic as the default app language
        UserDefaults.standard.set(["ar"], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()

        // Initialize Koin dependency injection
        Main_iosKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
