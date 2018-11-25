package com.hackjunction.messageng;

import com.choosemuse.libmuse.MuseDataPacketType;

public enum BrainWave {
	ALPHA, BETA, GAMMA, DELTA, THETA;

	public static BrainWave fromMuse(MuseDataPacketType type) {
		switch (type) {
			case ALPHA_RELATIVE:
				return ALPHA;
			case BETA_RELATIVE:
				return BETA;
			case GAMMA_RELATIVE:
				return GAMMA;
			case DELTA_RELATIVE:
				return DELTA;
			default:
				throw new IllegalArgumentException();
		}
	}
}
