package io;

public abstract class FsRelated {
    protected boolean highlighted = false;
    protected boolean hidden = false;

    public boolean isHidden() {
        return hidden;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void hide() {
        hidden = true;
    }

    public void highlight() {
        highlighted = true;
    }

    public void resetFlags() {
        hidden = highlighted = false;
    }

}
