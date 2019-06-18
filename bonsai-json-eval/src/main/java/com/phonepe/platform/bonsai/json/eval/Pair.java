package com.phonepe.platform.bonsai.json.eval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tushar.naik
 * @version 1.0  26/05/17 - 7:02 PM
 */
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Pair<K, V> {
  private K key;
  private V value;
}
