package name.modid.helpers.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class StunnedParticleFactory implements ParticleFactory<SimpleParticleType> {
  protected final SpriteProvider spriteProvider;

  public StunnedParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y,
      double z, double velocityX, double velocityY, double velocityZ) {
    StunnedParticle particle = new StunnedParticle(world, x, y, z, velocityX, velocityY, velocityZ);
    particle.setSprite(spriteProvider);
    return particle;
  }

  private static class StunnedParticle extends SpriteBillboardParticle {
    protected StunnedParticle(ClientWorld world, double x, double y, double z, double velocityX,
        double velocityY, double velocityZ) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.maxAge = 20;
      this.scale = 0.15f;
      this.gravityStrength = 0.7f;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      this.alpha = 1.0f;
    }

    @Override
    public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
      super.tick();
      if (this.age > 10) {
        float fadeFraction = (float) (this.age - 10) / (this.maxAge - 10);
        this.alpha = 1.0f - (float) Math.pow(fadeFraction, 3);
      } else {
        this.alpha = 1.0f;
      }
    }
  }
}
