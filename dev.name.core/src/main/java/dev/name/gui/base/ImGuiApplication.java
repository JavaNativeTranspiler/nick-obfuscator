package dev.name.gui.base;

import imgui.app.Configuration;

public abstract class ImGuiApplication extends ImGuiWindow {
    protected abstract void configure(final Configuration config);
    protected abstract void start();
    protected abstract void exit();

    public static void launch(final ImGuiApplication app) {
        initialize(app);
        app.start();
        app.run();
        app.exit();
        app.destroy();
    }

    private static void initialize(final ImGuiApplication app) {
        final Configuration config = new Configuration();
        app.configure(config);
        app.init(config);
    }
}