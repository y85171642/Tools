package org.redstone.tools;
public class XRandom
{
	private long seed;

	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;

	private static final double DOUBLE_UNIT = 1.0 / (1L << 53);

	static final String BadBound = "bound must be positive";

	public XRandom()
	{
		this(seedUniquifier() ^ System.nanoTime());
	}

	private static long seedUniquifier()
	{
		for (;;)
		{
			long current = _seedUniquifier;
			long next = current * 181783497276652981L;
			if (_seedUniquifier == current)
			{
				_seedUniquifier = next;
				return next;
			}
		}
	}

	private static long _seedUniquifier = 8682522807148012L;

	public XRandom(long seed)
	{
		if (getClass() == XRandom.class)
			this.seed = initialScramble(seed);
		else
		{
			this.seed = 0L;
			setSeed(seed);
		}
	}

	private static long initialScramble(long seed)
	{
		return (seed ^ multiplier) & mask;
	}

	synchronized public void setSeed(long seed)
	{
		this.seed = initialScramble(seed);
		haveNextNextGaussian = false;
	}

	protected int next(int bits)
	{
		long oldseed, nextseed;
        boolean isSeedEqual = false;
		do
		{
			 oldseed = seed;
             nextseed = (oldseed * multiplier + addend) & mask;
             if (seed == oldseed)
             {
                 seed = nextseed;
                 isSeedEqual = true;
             }
         } while (!isSeedEqual);
		return (int) (nextseed >>> (48 - bits));
	}

	public void nextBytes(byte[] bytes)
	{
		for (int i = 0, len = bytes.length; i < len;)
			for (int rnd = nextInt(), n = Math.min(len - i, Integer.SIZE
					/ Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE)
				bytes[i++] = (byte) rnd;
	}

	public int nextInt()
	{
		return next(32);
	}

	public int nextInt(int bound)
	{
		if (bound <= 0)
			throw new IllegalArgumentException(BadBound);

		int r = next(31);
		int m = bound - 1;
		if ((bound & m) == 0)
			r = (int) ((bound * (long) r) >> 31);
		else
		{
			for (int u = r; u - (r = u % bound) + m < 0; u = next(31))
				;
		}
		return r;
	}

	public long nextLong()
	{
		return ((long) (next(32)) << 32) + next(32);
	}

	public boolean nextBoolean()
	{
		return next(1) != 0;
	}

	public float nextFloat()
	{
		return next(24) / ((float) (1 << 24));
	}

	public double nextDouble()
	{
		return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
	}

	private double nextNextGaussian;
	private boolean haveNextNextGaussian = false;

	synchronized public double nextGaussian()
	{
		if (haveNextNextGaussian)
		{
			haveNextNextGaussian = false;
			return nextNextGaussian;
		} else
		{
			double v1, v2, s;
			do
			{
				v1 = 2 * nextDouble() - 1;
				v2 = 2 * nextDouble() - 1;
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			nextNextGaussian = v2 * multiplier;
			haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}
}
