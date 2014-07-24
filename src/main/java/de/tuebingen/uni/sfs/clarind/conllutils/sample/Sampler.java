package de.tuebingen.uni.sfs.clarind.conllutils.sample;

import com.google.common.base.Optional;

/**
 * Interface for samplers.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public interface Sampler<T> {
    /**
     * 'Add' an item for the sample. The result is one of the following:
     *
     * <ul>
     *     <li>If the item was added to the sample without replacing an existing item, the result is absent.</li>
     *     <li>If the item was added to the sample, replacing an existing item, the result contains the replaced item.</li>
     *     <li>If the item was not added to the sample, the result contains that item.</li>
     * </ul>
     *
     * If the item replaced an existing
     * item in the sample, the item that is replaced is returned.
     */
    public Optional<T> add(T item);

    /**
     * Return the maximum number of items the sample may contain.
     */
    public int maxSize();

    /**
     * Return the items in the sample.
     */
    public Iterable<T> sample();
}
