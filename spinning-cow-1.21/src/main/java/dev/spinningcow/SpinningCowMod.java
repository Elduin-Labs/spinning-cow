package dev.spinningcow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;

public class SpinningCowMod implements ModInitializer {
	@Override
	public void onInitialize() {
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (level.isClientSide() || !(entity instanceof Cow cow)) {
				return InteractionResult.PASS;
			}

			SpinningCow spinning = (SpinningCow)cow;
			if (spinning.spinningcow$isSpinning()) {
				// Already airborne, don't restart the launch.
				return InteractionResult.SUCCESS;
			}

			spinning.spinningcow$launch();
			// Cancel the vanilla hit so the cow survives long enough to fly.
			return InteractionResult.SUCCESS;
		});
	}
}
