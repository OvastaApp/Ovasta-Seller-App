import SwiftUI

@main
struct OvastaSellersApp: App {

    init() {
        // Force Arabic as the default app language
        UserDefaults.standard.set(["ar"], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
