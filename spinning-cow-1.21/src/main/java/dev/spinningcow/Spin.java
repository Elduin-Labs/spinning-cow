package dev.spinningcow;

/** Tuning knobs for the launch sequence. */
public final class Spin {
	/** Ticks spent winding up on the ground before liftoff. */
	public static final int SPIN_UP_TICKS = 20;

	/** Ticks spent climbing after liftoff. */
	public static final int ASCENT_TICKS = 110;

	/** Hard cutoff so a stuck cow can never spin forever. */
	public static final int MAX_TICKS = 1200;

	/** Blocks per tick during the climb. */
	public static final double ASCENT_SPEED = 1.8;

	private Spin() {
	}
}
