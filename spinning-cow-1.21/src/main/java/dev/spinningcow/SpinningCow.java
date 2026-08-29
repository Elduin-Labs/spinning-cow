package dev.spinningcow;

/**
 * Implemented by the LivingEntity mixin so the mod can start a cow spinning
 * without needing its own entity type.
 */
public interface SpinningCow {
	void spinningcow$launch();

	boolean spinningcow$isSpinning();
}
