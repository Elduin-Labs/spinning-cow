package dev.spinningcow.mixin;

import dev.spinningcow.Spin;
import dev.spinningcow.SpinningCow;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SpinningCowMixin implements SpinningCow {
	/** Ticks the launch has been running for, or -1 when the entity is not spinning. */
	@Unique
	private int spinningcow$ticks = -1;

	@Unique
	private boolean spinningcow$falling;

	@Override
	public void spinningcow$launch() {
		if (this.spinningcow$ticks < 0) {
			this.spinningcow$ticks = 0;
			this.spinningcow$falling = false;
		}
	}

	@Override
	public boolean spinningcow$isSpinning() {
		return this.spinningcow$ticks >= 0;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void spinningcow$tick(CallbackInfo ci) {
		if (this.spinningcow$ticks < 0) {
			return;
		}

		LivingEntity self = (LivingEntity)(Object)this;
		if (!(self.level() instanceof ServerLevel level) || !(self instanceof AbstractCow)) {
			return;
		}

		if (!self.isAlive()) {
			this.spinningcow$ticks = -1;
			return;
		}

		this.spinningcow$ticks++;

		// Spin faster and faster during the wind-up, then hold top speed.
		float speed = Math.min(this.spinningcow$ticks, Spin.SPIN_UP_TICKS) / (float)Spin.SPIN_UP_TICKS * 45.0F;
		float yRot = self.getYRot() + speed;
		self.setYRot(yRot);
		self.setYHeadRot(yRot);
		self.yBodyRot = yRot;

		// We handle the landing ourselves, so vanilla fall damage never applies.
		self.fallDistance = 0.0;

		if (!this.spinningcow$falling) {
			self.setNoGravity(true);

			if (this.spinningcow$ticks < Spin.SPIN_UP_TICKS) {
				// Winding up in place.
				self.setDeltaMovement(0.0, 0.0, 0.0);
			} else {
				if (this.spinningcow$ticks == Spin.SPIN_UP_TICKS) {
					level.playSound(null, self.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1.0F, 1.0F);
				}
				self.setDeltaMovement(0.0, Spin.ASCENT_SPEED, 0.0);
			}

			if (this.spinningcow$ticks >= Spin.SPIN_UP_TICKS + Spin.ASCENT_TICKS) {
				this.spinningcow$falling = true;
				self.setNoGravity(false);
				self.setDeltaMovement(0.0, -0.1, 0.0);
			}

			return;
		}

		if (self.onGround() || self.isInWater() || this.spinningcow$ticks > Spin.MAX_TICKS) {
			this.spinningcow$ticks = -1;
			self.setNoGravity(false);
			level.playSound(null, self.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
			self.kill(level);
			level.getServer()
				.getPlayerList()
				.broadcastSystemMessage(Component.translatable("death.attack.spinningcow.failed_landing"), false);
		}
	}
}
