package name.modid.core.api.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class SparkParticleFactory implements ParticleFactory<SimpleParticleType> {
  protected final SpriteProvider spriteProvider;

  public SparkParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y,
      double z, double velocityX, double velocityY, double velocityZ) {
    SparkParticle particle = new SparkParticle(world, x, y, z, velocityX, velocityY, velocityZ);
    particle.setSprite(spriteProvider);

    return particle;
  }

  private static class SparkParticle extends SpriteBillboardParticle {
    protected SparkParticle(ClientWorld world, double x, double y, double z,
        double velocityX, double velocityY, double velocityZ) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.maxAge = 20 + world.random.nextInt(10);
      this.scale = 0.25f;
      this.gravityStrength = 0.0f;
      this.velocityX = velocityX * 0.3;
      this.velocityY = velocityY * 0.3;
      this.velocityZ = velocityZ * 0.3;
      this.alpha = 1.0f;
      this.red = 1.0f;
      this.green = 0.9f;
      this.blue = 0.3f;
    }

    @Override
    public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
      super.tick();

      float lifeFrac = (float) this.age / (float) this.maxAge;

      this.red = 1.0f;
      this.green = Math.max(0.6f, 1.0f - lifeFrac * 0.4f);
      this.blue = 0.3f + lifeFrac * 0.7f;
      this.scale = 0.25f * (1.0f - lifeFrac);

      if (this.age > this.maxAge / 2) {
        float fade = (float) (this.age - this.maxAge / 2)
            / (this.maxAge / 2);
        this.alpha = 1.0f - fade;
      }
    }
  }
}
