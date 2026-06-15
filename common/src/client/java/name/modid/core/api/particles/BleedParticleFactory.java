package name.modid.core.api.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class BleedParticleFactory implements ParticleFactory<SimpleParticleType> {
  protected final SpriteProvider spriteProvider;

  public BleedParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y,
      double z, double velocityX, double velocityY, double velocityZ) {
    return new BleedParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
  }

  private static class BleedParticle extends SpriteBillboardParticle {
    private final float initialScale;

    protected BleedParticle(ClientWorld world, double x, double y, double z, double velocityX,
        double velocityY, double velocityZ, SpriteProvider spriteProvider) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.setSprite(spriteProvider.getSprite(world.random));
      this.maxAge = 22 + world.random.nextInt(14);
      this.initialScale = 0.055F + world.random.nextFloat() * 0.055F;
      this.scale = this.initialScale;
      this.gravityStrength = 0.9F + world.random.nextFloat() * 0.35F;
      this.velocityX = velocityX * 0.85;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ * 0.85;
      this.alpha = 1.0f;
      this.collidesWithWorld = true;

      float tone = 0.55F + world.random.nextFloat() * 0.25F;
      this.red = tone;
      this.green = world.random.nextFloat() * 0.035F;
      this.blue = world.random.nextFloat() * 0.025F;
    }

    @Override
    public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
      super.tick();

      this.velocityX *= 0.96;
      this.velocityZ *= 0.96;

      if (this.onGround) {
        this.velocityX *= 0.55;
        this.velocityZ *= 0.55;
      }

      float life = (float) this.age / (float) this.maxAge;
      this.scale = this.initialScale * (1.0F - life * 0.35F);

      if (life > 0.6F) {
        float fadeFraction = (life - 0.6F) / 0.4F;
        this.alpha = 1.0F - (float) Math.pow(fadeFraction, 2);
      }
    }
  }
}
