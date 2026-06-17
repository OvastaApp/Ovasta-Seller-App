import SwiftUI
import sharedKit

struct ContentView: View {
    // Toggling this forces SwiftUI to destroy and recreate ComposeView,
    // which calls makeUIViewController again with the updated saved language.
    @State private var composeViewId = UUID()

    var body: some View {
        ComposeView()
            .id(composeViewId)
            .ignoresSafeArea(.all)
            .onReceive(
                NotificationCenter.default.publisher(
                    for: Notification.Name("com.ovasta.sellers.LanguageChanged")
                )
            ) { _ in
                composeViewId = UUID()
            }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.createComposeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
