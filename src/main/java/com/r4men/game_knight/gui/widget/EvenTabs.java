package com.r4men.game_knight.gui.widget;

import net.ethrocky.pane.core.Component;
import net.ethrocky.pane.core.Size;
import net.ethrocky.pane.core.UiEvent;
import net.ethrocky.pane.core.layout.Flex;
import net.ethrocky.pane.core.style.Style;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.render.PaneRenderer;
import net.ethrocky.pane.widget.Panel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class EvenTabs extends Component {
    private final List<String> titles = new ArrayList<>();
    private final List<Component> contents = new ArrayList<>();
    private final Panel bar;
    private int active = -1;
    private IntConsumer onTabChanged = index -> {};

    private EvenTabs() {
        style(Style.empty().padding(0).gap(4));
        align(Flex.Align.STRETCH);

        bar = Panel.row();
        bar.style(Style.empty().padding(0).gap(2));
        bar.justify(Flex.Justify.SPACE_BETWEEN);

        add(bar);
    }

    public static EvenTabs of() {
        return new EvenTabs();
    }

    public EvenTabs tab(String title, Component content) {
        titles.add(title);
        contents.add(content);
        bar.add(new TabButton(this, titles.size() - 1));
        if (active < 0) select(0);
        return this;
    }

    public void select(int index) {
        if (index == active || index < 0 || index >= contents.size()) return;
        if (active >= 0) children.remove(contents.get(active));
        active = index;
        add(contents.get(index));
        invalidate();

        onTabChanged.accept(active);
    }

    public int active() {
        return active;
    }

    public EvenTabs onTabChanged(IntConsumer listener) {
        this.onTabChanged = listener;
        return this;
    }

    private static final class TabButton extends Component {
        private final EvenTabs owner;
        private final int index;

        TabButton(EvenTabs owner, int index) {
            this.owner = owner;
            this.index = index;
        }

        @Override
        public Size measure(TextMeasurer tm) {
            return new Size(tm.width(owner.titles.get(index)) + 12, tm.height() + 6);
        }

        @Override
        protected boolean handleMouse(UiEvent.Mouse e) {
            if (e.kind() == UiEvent.Kind.DOWN && e.inside(bounds)) {
                owner.select(index);
                return true;
            }
            return false;
        }

        @Override
        protected void paintSelf(PaneRenderer r, Theme theme) {
            boolean act = owner.active == index;
            r.fill(bounds, act ? theme.surfaceRaised() : theme.surface());
            r.border(bounds, act ? theme.accent() : (hovered ? theme.textDim() : theme.border()), 1);
            String title = owner.titles.get(index);
            int tx = bounds.x() + (bounds.w() - r.width(title)) / 2;
            int ty = bounds.y() + (bounds.h() - r.height()) / 2;
            r.text(title, tx, ty, act ? theme.text() : theme.textDim());
        }
    }
}
