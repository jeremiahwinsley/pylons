package net.permutated.pylons.util;

public record TextureHolder(int progressOffsetX, int progressOffsetY,
                            int textureOffsetX, int textureOffsetY,
                            int textureWidth, int textureHeight) {

    public int getWidthFraction(float fraction) {
        return (int) (textureWidth * fraction);
    }

    public int progressWidthOffset(float fraction) {
        return progressOffsetX + (textureWidth - (int) (textureWidth * fraction));
    }
}
