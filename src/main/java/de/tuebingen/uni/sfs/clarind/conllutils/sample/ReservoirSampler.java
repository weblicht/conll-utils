package de.tuebingen.uni.sfs.clarind.conllutils.sample;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * A reservoir sampler.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class ReservoirSampler<T> implements Sampler<T> {
    private final RandomGenerator random;

    private final List<T> sample;

    private final int sampleSize;

    private int itemsSampled;

    public ReservoirSampler(int sampleSize, RandomGenerator random) {
        Preconditions.checkArgument(sampleSize > 0);
        Preconditions.checkNotNull(random);

        this.sampleSize = sampleSize;
        this.random = random;

        itemsSampled = 0;
        sample = new ArrayList<>();
    }

    @Override
    public Optional<T> add(T item) {
        Preconditions.checkNotNull(item);

        ++itemsSampled;

        // If the sample does not yet contain the maximum number of items,
        // we can simply add the item to the sample.
        if (itemsSampled <= sampleSize) {
            sample.add(item);
            return Optional.absent();
        }

        int idx = random.nextInt(itemsSampled);
        if (idx < sample.size()) {
            T oldItem = sample.get(idx);
            sample.set(idx, item);
            return Optional.of(oldItem);
        }

        return Optional.of(item);
    }

    @Override
    public int maxSize() {
        return sampleSize;
    }

    @Override
    public Iterable<T> sample() {
        return sample;
    }
}
