package com.r4men.game_knight.gui.widget;

import java.util.Objects;
import java.util.function.Function;

import net.ethrocky.pane.core.Component;
import net.ethrocky.pane.core.Rect;
import net.ethrocky.pane.core.Size;
import net.ethrocky.pane.core.State;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.render.PaneRenderer;

public class FloatLabel extends Component {
    private String text;
    private boolean dim;

    private FloatLabel(
            State<Float> state,
            Function<Float, String> formatter
    ) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(formatter, "formatter");

        this.text = formatter.apply(state.get());

        state.onChange(value -> {
            String updated = formatter.apply(value);

            if (!Objects.equals(text, updated)) {
                text = updated;
                invalidate();
            }
        });
    }

    public static FloatLabel of(State<Float> state) {
        return new FloatLabel(state, value -> Float.toString(value));
    }

    public static FloatLabel of(
            State<Float> state,
            Function<Float, String> formatter
    ) {
        return new FloatLabel(state, formatter);
    }

    public FloatLabel dim(boolean dim) {
        this.dim = dim;
        invalidate();
        return this;
    }

    @Override
    public Size measure(TextMeasurer tm) {
        return new Size(tm.width(text), tm.height());
    }

    @Override
    protected void paintSelf(PaneRenderer r, Theme theme) {
        super.paintSelf(r, theme);

        r.border(new Rect(bounds.x() - theme.padding(), bounds.y() - theme.padding(), bounds.w() + theme.padding() * 2, bounds.h() + theme.padding() * 2), theme.border(), 1);

        int color = style.fg != null
                ? style.fg
                : (dim ? theme.textDim() : theme.text());

        int y = bounds.y() + (bounds.h() - r.height()) / 2;

        r.text(text, bounds.x(), y, color);
    }
}