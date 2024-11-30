package ca.wescook.wateringcans;

public class Util {
    public static float colorFloatFromDecimal(int decimal, int offset)
    {
        String hex = Integer.toHexString(decimal);

        int rgbInt = Integer.valueOf(hex.substring(offset, offset + 2), 16);

        return (float) rgbInt / 255;
    }
}
