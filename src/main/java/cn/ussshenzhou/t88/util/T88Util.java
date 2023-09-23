package cn.ussshenzhou.t88.util;

/**
 * @author USS_Shenzhou
 */
public class T88Util {

    public static int overrideBlockLight(int packedLight, short blockLight) {
        return packedLight & 0b00000000_11110000_00000000_00000000 | blockLight << 4;
    }
}
