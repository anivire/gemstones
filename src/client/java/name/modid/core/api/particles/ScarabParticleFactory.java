package name.modid.core.api.particles;

import name.modid.core.content.particles.ScarabParticleInstance;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ScarabParticleFactory implements ParticleFactory<ScarabParticleInstance> {
  private final SpriteProvider spriteProvider;

  public ScarabParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(ScarabParticleInstance type, ClientWorld world,
      double x, double y, double z,
      double velocityX, double velocityY, double velocityZ) {

    Entity entity = world.getEntityById(type.entityId);
    if (entity == null) {
      return null;
    }

    ScarabParticle particle = new ScarabParticle(world, entity, x, y, z);
    particle.setSprite(spriteProvider);
    return particle;
  }

  private static class ScarabParticle extends SpriteBillboardParticle {
    private double radius;
    private final double baseRadius;
    private final double relativeMinY;
    private final double relativeMaxY;
    private final Entity targetEntity;
    private float angle;
    private final double verticalOffset;
    private final double rotationSpeed;
    private final double radiusVariation;
    private double radiusOffset;

    protected ScarabParticle(ClientWorld world, Entity entity, double x, double y, double z) {
      super(world, x, y, z, 0, 0, 0);

      this.targetEntity = entity;
      this.maxAge = 30 + world.random.nextInt(40);
      this.scale = 0.05f + world.random.nextFloat() * 0.1f;
      this.alpha = 1.0f;
      this.relativeMinY = -0.2;
      this.relativeMaxY = entity.getHeight() + 0.5;
      this.verticalOffset = relativeMinY + world.random.nextDouble() * (relativeMaxY - relativeMinY);
      this.baseRadius = 0.3 + world.random.nextDouble() * 0.9;
      this.radius = baseRadius;
      this.angle = world.random.nextFloat() * 360;
      this.rotationSpeed = 3 + world.random.nextDouble() * 9;
      this.radiusVariation = 0.1 + world.random.nextDouble() * 0.2;
    }

    @Override
    public void tick() {
      super.tick();

      if (targetEntity == null || targetEntity.isRemoved()) {
        this.markDead();
        return;
      }

      angle += rotationSpeed;
      radiusOffset = Math.sin(this.age * 0.3) * radiusVariation;
      radius = baseRadius + radiusOffset;

      double entityX = targetEntity.getX();
      double entityY = targetEntity.getY();
      double entityZ = targetEntity.getZ();
      double verticalWave = Math.sin(this.age * 0.2) * 0.1;
      double newX = entityX + Math.cos(Math.toRadians(angle)) * radius;
      double newZ = entityZ + Math.sin(Math.toRadians(angle)) * radius;
      double newY = entityY + verticalOffset + verticalWave;

      this.setPos(newX, newY, newZ);

      if (this.age > this.maxAge - 10) {
        float fadeFraction = (float) (this.maxAge - this.age) / 10.0f;
        this.alpha = Math.max(0.0f, fadeFraction);
      }

      float sizePulse = 1.0f + (float) (Math.sin(this.age * 0.4) * 0.2);
      this.scale *= sizePulse;
    }

    @Override
    public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
  }
}