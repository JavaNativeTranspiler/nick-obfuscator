package dev.name.gui;

import dev.name.gui.base.ImGuiApplication;
import dev.name.gui.components.Section;
import dev.name.gui.components.sections.TestSection;
import dev.name.gui.components.sections.TestSection2;
import dev.name.util.collections.list.FastArrayList;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.app.Configuration;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static imgui.ImGui.*;

public final class GUI extends ImGuiApplication {
    private static final FastArrayList<Section> SECTIONS = new FastArrayList<>(new TestSection(), new TestSection2());

    @Override
    protected void pre() {
        ImGuiStyle style = getStyle();
        style.setWindowRounding(5.0f);
        style.setFrameRounding(5.0f);
        style.setScrollbarRounding(5.0f);
        style.setGrabRounding(5.0f);
        style.setTabRounding(5.0f);
        style.setWindowBorderSize(1.0f);
        style.setFrameBorderSize(1.0f);
        style.setPopupBorderSize(1.0f);
        style.setPopupRounding(5.0f);
        style.setColor(ImGuiCol.Text, 0.10f, 0.10f, 0.10f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.60f, 0.60f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.95f, 0.95f, 0.95f, 1.00f);
        style.setColor(ImGuiCol.ChildBg, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.PopupBg, 0.98f, 0.98f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.Border,0.70f, 0.70f, 0.70f, 1.00f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBg, 0.85f, 0.85f, 0.85f, 1.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.FrameBgActive, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.TitleBg, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.85f, 0.85f, 0.85f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.MenuBarBg, 0.95f, 0.95f, 0.95f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive,0.70f, 0.70f, 0.70f, 1.00f);
        style.setColor(ImGuiCol.CheckMark, 0.55f, 0.65f, 0.55f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.55f, 0.65f, 0.55f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.60f, 0.70f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.Button, 0.85f, 0.85f, 0.85f, 1.00f);
        style.setColor(ImGuiCol.ButtonHovered, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.Header, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.HeaderHovered, 0.70f, 0.70f, 0.70f, 1.00f);
        style.setColor(ImGuiCol.HeaderActive, 0.65f, 0.65f, 0.65f, 1.00f);
        style.setColor(ImGuiCol.Separator, 0.60f, 0.60f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.65f, 0.65f, 0.65f, 1.00f);
        style.setColor(ImGuiCol.SeparatorActive, 0.70f, 0.70f, 0.70f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 0.55f, 0.65f, 0.55f, 1.00f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.60f, 0.70f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.65f, 0.75f, 0.65f, 1.00f);
        style.setColor(ImGuiCol.Tab, 0.85f, 0.85f, 0.85f, 1.00f);
        style.setColor(ImGuiCol.TabHovered, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.TabActive, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocused, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.75f, 0.75f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.DockingPreview, 0.55f, 0.65f, 0.55f, 1.00f);
        style.setColor(ImGuiCol.DockingEmptyBg, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setFramePadding(8.0f, 4.0f);
        style.setItemSpacing(8.0f, 4.0f);
        style.setIndentSpacing(20.0f);
        style.setScrollbarSize(16.0f);
    }

    @Override
    protected void process() {
        ImGuiIO io = getIO();
        setNextWindowPos(0, 0);
        setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY());

        begin("Rizzfuscator", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus);

        beginMainMenuBar();

        for (Section section : SECTIONS) {
            if (!beginMenu(section.name())) continue;
            section.render();
            endMenu();
        }

        endMainMenuBar();

        end();
    }



    @Override
    protected void post() {

    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("rizzfuscator");
        config.setFullScreen(false);
    }

    @Override
    protected void start() {

    }

    @Override
    protected void exit() {

    }
}