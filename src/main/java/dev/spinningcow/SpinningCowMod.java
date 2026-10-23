package dev.spinningcow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.cow.AbstractCow;

public class SpinningCowMod implements ModInitializer {
	private static final Identifier COW_PRIORITY_SEEDING = Identifier.fromNamespaceAndPath("spinningcow", "cow_priority_seeding");

	@Override
	public void onInitialize() {
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (level.isClientSide() || !(entity instanceof AbstractCow cow)) {
				return InteractionResult.PASS;
			}

			SpinningCow spinning = (SpinningCow)cow;
			if (spinning.spinningcow$isSpinning()) {
				// Already airborne, don't restart the launch.
				return InteractionResult.SUCCESS;
			}

			spinning.spinningcow$launch();
			if (player instanceof ServerPlayer serverPlayer) {
				AdvancementHolder advancement = serverPlayer.level().getServer().getAdvancements().get(COW_PRIORITY_SEEDING);
				if (advancement != null) {
					serverPlayer.getAdvancements().award(advancement, "launched");
				}
			}
			// Cancel the vanilla hit so the cow survives long enough to fly.
			return InteractionResult.SUCCESS;
		});
	}
}
