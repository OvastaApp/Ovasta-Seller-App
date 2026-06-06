import SwiftUI
import sharedKit

@main
struct OvastaSellersApp: App {

    init() {
        // Initialize Koin dependency injection
        Main_iosKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

