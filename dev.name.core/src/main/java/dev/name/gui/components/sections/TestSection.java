package dev.name.gui.components.sections;

import dev.name.gui.components.Section;
import imgui.ImGui;

import java.awt.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TestSection extends Section {
    @Override
    public String name() {
        return "sigma";
    }

    @Override
    public void render() {
        if (!ImGui.menuItem("Open File")) return;
        CompletableFuture.supplyAsync(this::showFileDialog).thenAccept(this::handleFileSelection);
    }

    private File showFileDialog() {
        CompletableFuture<File> future = new CompletableFuture<>();
        FileDialog dialog = new FileDialog((Frame) null, "Choose a file", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".jmod") || name.endsWith(".class"));
        dialog.setVisible(true);
        String selectedFile = dialog.getFile();

        if (selectedFile != null) {
            future.complete(new File(dialog.getDirectory(), selectedFile));
        } else {
            future.complete(null);
        }

        return future.join();
    }

    private void handleFileSelection(File file) {
        if (file != null) {
            System.out.println("Selected File: " + file.getAbsolutePath());
        } else {
            System.out.println("No file selected.");
        }
    }
}