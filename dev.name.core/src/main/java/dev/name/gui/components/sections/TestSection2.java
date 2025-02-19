package dev.name.gui.components.sections;

import dev.name.gui.components.Section;
import imgui.ImGui;

public class TestSection2 extends Section {
    @Override
    public String name() {
        return "xd";
    }

    @Override
    public void render() {
        ImGui.menuItem("OK XD");
    }
}