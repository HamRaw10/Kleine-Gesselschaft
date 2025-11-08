package utilidades.items;

public class StatsModifier {
    private final float speedBonus; // sumatoria: 0.2 = +20%
    public StatsModifier(float speedBonus){ this.speedBonus = speedBonus; }
    public float getSpeedBonus(){ return speedBonus; }
    public static StatsModifier none(){ return new StatsModifier(0f); }
}
