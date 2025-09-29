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
  public Particle createParticle(
      SimpleParticleType type,
      ClientWorld world,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ) {

    float r = 1.0f;
    float g = 0.5f + world.random.nextFloat() * 0.2f;
    float b = 0.0f;

    SparkParticle particle = new SparkParticle(world, x, y, z, velocityX, velocityY, velocityZ, r, g, b);
    particle.setSprite(spriteProvider);

    return particle;
  }

  private static class SparkParticle extends SpriteBillboardParticle {
    private final float startR;
    private final float startG;
    private final float startB;

    protected SparkParticle(
        ClientWorld world,
        double x,
        double y,
        double z,
        double velocityX,
        double velocityY,
        double velocityZ,
        float r,
        float g,
        float b) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.maxAge = 20 + world.random.nextInt(10);
      this.scale = 0.25f;
      this.gravityStrength = 0.0f;
      this.velocityX = velocityX * 0.3;
      this.velocityY = velocityY * 0.3;
      this.velocityZ = velocityZ * 0.3;
      this.alpha = 1.0f;

      this.startR = r;
      this.startG = g;
      this.startB = b;

      this.red = r;
      this.green = g;
      this.blue = b;
    }

    @Override
    public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
      super.tick();

      float lifeFrac = (float) this.age / (float) this.maxAge;

      if (lifeFrac < 0.7f) {
        float t = lifeFrac / 0.7f;
        this.red = lerp(this.startR, 1.0f, t);
        this.green = lerp(this.startG, 1.0f, t);
        this.blue = lerp(this.startB, 0.0f, t);
      } else {
        float t = (lifeFrac - 0.7f) / 0.3f;
        this.red = lerp(1.0f, 0.3f, t);
        this.green = lerp(1.0f, 0.3f, t);
        this.blue = lerp(0.0f, 0.3f, t);
      }

      this.scale = 0.25f * (1.0f - lifeFrac * 0.8f);

      if (this.age > this.maxAge * 0.6f) {
        float fade = (float) (this.age - this.maxAge * 0.6f) / (this.maxAge * 0.4f);
        this.alpha = 1.0f - fade;
      }
    }

    private static float lerp(float start, float end, float t) {
      return start + (end - start) * t;
    }
  }
}