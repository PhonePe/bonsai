package com.phonepe.platform.bonsai.core.vital.provided;

/**
 * A store for Trees or Tree pointers
 * In Bonsai, this represents keys that actually map to full trees.
 * So basically, all keys here, would ideally point to Knots and edges
 *
 * @param <K> Key for association
 * @param <T> Some form that represents a Tree or pointers to the Tree
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:24 AM
 */
public interface KeyTreeStore<K, T> {
    /**
     * checks if k is present
     *
     * @param k key
     * @return true if present
     */
    boolean containsKey(K k);

    /**
     * to create association between a key
     *
     * @param k key
     * @param t tree to map the key with
     * @return the previous tree associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    T createKeyTree(K k, T t);

    /**
     * @param k key
     * @return tree or ref of tree that is mapped to the key
     */
    T getKeyTree(K k);

    /**
     * remove KeyTree
     *
     * @param k key
     * @return the previous tree associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    T removeKeyTree(K k);
}
