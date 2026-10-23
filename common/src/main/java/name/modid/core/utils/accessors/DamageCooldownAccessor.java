package name.modid.core.utils.accessors;

// Implemented on LivingEntity by a platform mixin, gives access to the protected i-frame damage value
public interface DamageCooldownAccessor {
  float gemstones$getLastDamageTaken();

  void gemstones$setLastDamageTaken(float value);
}
