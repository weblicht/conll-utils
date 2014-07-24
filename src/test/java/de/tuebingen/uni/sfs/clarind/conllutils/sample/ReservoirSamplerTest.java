package de.tuebingen.uni.sfs.clarind.conllutils.sample;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for {@link de.tuebingen.uni.sfs.clarind.conllutils.sample.ReservoirSampler}.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class ReservoirSamplerTest {

    public static final int N_SAMPLES = 500;

    public static final int RANGE_UPPER_EXCLUSIVE = 5000;

    @Test
    public void sizeTest() {
        Sampler<Integer> sampler = new ReservoirSampler<>(10, new MersenneTwister());
        RandomGenerator rng = new MersenneTwister();
        for (int i = 0; i < 1000; ++i) {
            sampler.add(i);
        }

        Assert.assertEquals(10, Iterables.size(sampler.sample()));
    }

    @Test
    public void sampleTest() {
        Sampler<Integer> sampler = new ReservoirSampler<>(3, new NonRandom());
        Assert.assertEquals(ImmutableList.of(), sampler.sample());

        Assert.assertFalse(sampler.add(1).isPresent());
        Assert.assertEquals(ImmutableList.of(1), sampler.sample());

        Assert.assertFalse(sampler.add(2).isPresent());
        Assert.assertEquals(ImmutableList.of(1, 2), sampler.sample());

        Assert.assertFalse(sampler.add(3).isPresent());
        Assert.assertEquals(ImmutableList.of(1, 2, 3), sampler.sample());

        Assert.assertEquals(Optional.of(4), sampler.add(4));
        Assert.assertEquals(ImmutableList.of(1, 2, 3), sampler.sample());

        Assert.assertEquals(Optional.of(3), sampler.add(5));
        Assert.assertEquals(ImmutableList.of(1, 2, 5), sampler.sample());

        Assert.assertEquals(Optional.of(1), sampler.add(6));
        Assert.assertEquals(ImmutableList.of(6, 2, 5), sampler.sample());

        Assert.assertEquals(Optional.of(2), sampler.add(7));
        Assert.assertEquals(ImmutableList.of(6, 7, 5), sampler.sample());
    }

    @Test
    public void statisticalPropertiesTest() {
        Sampler<Double> sampler = new ReservoirSampler<>(N_SAMPLES, new MersenneTwister());

        for (int i = 0; i < RANGE_UPPER_EXCLUSIVE; ++i) {
            sampler.add((double) i);
        }

        double[] sample = new double[N_SAMPLES];
        Iterator<Double> iter = sampler.sample().iterator();
        for (int i = 0; i < N_SAMPLES; ++i) {
            assert iter.hasNext();
            sample[i] = iter.next();
        }

        KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        double p = test.kolmogorovSmirnovTest(new UniformRealDistribution(0, RANGE_UPPER_EXCLUSIVE), sample);

        Assert.assertTrue("The distribution of samples should not differ from the uniform distribution", p > 0.01);
    }

    private class NonRandom extends AbstractRandomGenerator {
        private final List<Integer> NON_RANDOMS = ImmutableList.of(3, 2, 0, 1);

        private int idx = 0;

        @Override
        public double nextDouble() {
            throw new RuntimeException("nextDouble() should not be called");
        }

        @Override
        public int nextInt(int n) {
            if (idx < NON_RANDOMS.size()) {
                return NON_RANDOMS.get(idx++);
            }

            return 1;
        }

        @Override
        public void setSeed(long seed) {
            return;
        }
    }

}