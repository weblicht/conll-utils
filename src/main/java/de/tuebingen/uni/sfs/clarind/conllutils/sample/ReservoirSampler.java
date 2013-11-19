package de.tuebingen.uni.sfs.clarind.conllutils.sample;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class implements a reservoir sampler. A reservoir sampler samples
 * efficiently from a number of items that is large or unknown. If <i>n</i>
 * is the size of the sample and <i>m</i> the total number of items, memory
 * complexity is <i>O(n)</i> and time complexity <i>O(m)</i>.
 *
 * @param <T>
 */
public class ReservoirSampler<T> {
    // The index of the next element that is added.
    private int idx;

    // The current sample.
    private final List<T> sample;

    // The random number generator used for sampling.
    private final Random rng;

    // The size of the sample.
    private final int sampleSize;

    /**
     * Construct a reservoir sampler of the given sample size. Calling this
     * constructor is equivalent to calling {@link #ReservoirSampler(java.util.Random, int)}
     * with {@link Random#Random()} as the random number generator.
     *
     * @param sampleSize
     */
    public ReservoirSampler(int sampleSize) {
        this(new Random(), sampleSize);
    }

    /**
     * Construct a reservoir sampler of the given sample size. The provided
     * random number generator will be used for sampling.
     *
     * @param rng
     * @param sampleSize
     */
    public ReservoirSampler(Random rng, int sampleSize) {
        idx = 0;
        sample = new ArrayList<>(sampleSize);
        this.rng = rng;
        this.sampleSize = sampleSize;
    }

    /**
     * Add the provided instance to the sampler.
     *
     * @param instance The instance.
     */
    public void add(T instance) {
        if (sample.size() < sampleSize)
            sample.add(instance);
        else {
            int randomIdx = rng.nextInt(idx + 1);

            if (randomIdx < sampleSize)
                sample.set(randomIdx, instance);
        }

        ++idx;
    }

    /**
     * Get the current sample.
     *
     * @return The sample.
     */
    public List<T> getSample() {
        return ImmutableList.copyOf(sample);
    }

    /**
     * Get the number of items seen be the sampler.
     * @return The number of items.
     */
    public int getNItems() {
        return idx;
    }

    /**
     * Get the sample size.
     * @return The sample size.
     */
    public int getSampleSize() {
        return sampleSize;
    }
}
