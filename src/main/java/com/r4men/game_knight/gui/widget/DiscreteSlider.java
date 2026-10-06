package com.r4men.game_knight.gui.widget;

import net.ethrocky.pane.core.*;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.render.PaneRenderer;

import java.util.Arrays;
import java.util.Objects;

public class DiscreteSlider extends Component {
    private final State<Float> value;
    private final float[] stops;

    private boolean dragging;

    private DiscreteSlider(State<Float> value, float... stops) {
        this.value = Objects.requireNonNull(value, "value");
        Objects.requireNonNull(stops, "stops");

        if (stops.length < 2) {
            throw new IllegalArgumentException(
                    "A discrete slider needs at least two stops"
            );
        }

        this.stops = stops.clone();
        Arrays.sort(this.stops);

        for (int i = 0; i < this.stops.length; i++) {
            float stop = this.stops[i];

            if (!Float.isFinite(stop)) {
                throw new IllegalArgumentException("Stops must be finite");
            }

            if (i > 0 && stop == this.stops[i - 1]) {
                throw new IllegalArgumentException("Stops must be unique");
            }
        }

        this.value.set(nearestStop(this.value.get()));
    }

    public static DiscreteSlider of(State<Float> value, float... stops) {
        return new DiscreteSlider(value, stops);
    }

    @Override
    public Size measure(TextMeasurer tm) {
        return new Size(
                style.width != null ? style.width : 120,
                tm.height() + 6
        );
    }

    @Override
    protected boolean handleMouse(UiEvent.Mouse e) {
        switch (e.kind()) {
            case DOWN -> {
                if (e.inside(bounds)) {
                    dragging = true;
                    setFromX(e.x());
                    return true;
                }
            }
            case MOVE -> {
                if (dragging) {
                    setFromX(e.x());
                    return true;
                }
            }
            case UP -> {
                if (dragging) {
                    dragging = false;
                    return true;
                }
            }
            default -> {}
        }

        return false;
    }

    private void setFromX(double x) {
        double t = bounds.w() <= 0
                ? 0
                : (x - bounds.x()) / bounds.w();

        t = Math.clamp(t, 0, 1);

        int index = (int) Math.round(t * (stops.length - 1));
        float snapped = stops[index];

        if (Float.compare(value.get(), snapped) != 0) {
            value.set(snapped);
        }
    }

    private int nearestStopIndex(double raw) {
        if (!Double.isFinite(raw)) {
            return 0;
        }

        int nearestIndex = 0;
        double nearestDistance = Math.abs(raw - stops[0]);

        for (int i = 1; i < stops.length; i++) {
            double distance = Math.abs(raw - stops[i]);

            if (distance < nearestDistance) {
                nearestIndex = i;
                nearestDistance = distance;
            }
        }

        return nearestIndex;
    }

    private float nearestStop(double raw) {
        return stops[nearestStopIndex(raw)];
    }

    private float fraction() {
        int index = nearestStopIndex(value.get());
        return index / (float) (stops.length - 1);
    }

    @Override
    protected void paintSelf(PaneRenderer r, Theme theme) {
        int trackY = bounds.y() + bounds.h() / 2 - 1;

        r.fill(
                new Rect(bounds.x(), trackY, bounds.w(), 2),
                theme.border()
        );

        int fillW = Math.round(bounds.w() * fraction());

        if (fillW > 0) {
            r.fill(
                    new Rect(bounds.x(), trackY, fillW, 2),
                    theme.accent()
            );
        }

        int handleX = bounds.x() + Math.max(0, Math.min(bounds.w() - 4, fillW -2));

        r.fill(
                new Rect(handleX, bounds.y(), 4, bounds.h()),
                hovered || dragging ? theme.accent() : theme.text()
        );
    }
}
