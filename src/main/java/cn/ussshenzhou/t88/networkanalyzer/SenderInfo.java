package cn.ussshenzhou.t88.networkanalyzer;

/**
 * @author USS_Shenzhou
 */
public record SenderInfo(String modId, String clazz) {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SenderInfo other) {
            return modId.equals(other.modId) && clazz.equals(other.clazz);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return modId.hashCode() + clazz.hashCode();
    }
}
