import SwiftUI
import sharedKit

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.createComposeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
