package net.sothatsit.heads.menu.ui;

import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;

public final class Bounds {

    public final Position position;
    public final int width;
    public final int height;

    public Bounds(int x, int y, int width, int height) {
        this(new Position(x, y), width, height);
    }

    public Bounds(Position position, int width, int height) {
        Checks.ensureNonNull(position, "position");
        Checks.ensureTrue(width > 0, "width must be greater than 0");
        Checks.ensureTrue(height > 0, "height must be greater than 0");

        this.position = position;
        this.width = width;
        this.height = height;
    }

    public int getVolume() {
        return this.width * this.height;
    }

    public Position[] getCorners() {
        return new Position[] {
                position,
                position.add(0, height - 1),
                position.add(width - 1, 0),
                position.add(width - 1, height - 1)
        };
    }

    public boolean inBounds(Position pos) {
        return pos.x >= position.x && pos.y >= position.y && pos.x < position.x + width && pos.y < position.y + height;
    }

    public boolean inBounds(Bounds other) {
        for(Position corner : other.getCorners()) {
            if(!inBounds(corner))
                return false;
        }
        return true;
    }

    public boolean collides(Bounds other) {
        for(Position corner : other.getCorners()) {
            if(inBounds(corner))
                return true;
        }

        for(Position corner : getCorners()) {
            if(other.inBounds(corner))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("position", position)
                .entry("width", width)
                .entry("height", height).toString();
    }

}
