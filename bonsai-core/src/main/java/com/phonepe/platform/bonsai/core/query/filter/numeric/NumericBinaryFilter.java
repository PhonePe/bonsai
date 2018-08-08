package com.phonepe.platform.bonsai.core.query.filter.numeric;


import com.phonepe.platform.bonsai.core.query.filter.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:45 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NumericBinaryFilter extends Filter {

    private Number value;

    protected NumericBinaryFilter(final String operator) {
        super(operator);
    }

    protected NumericBinaryFilter(final String operator, String field, Number value) {
        super(operator, field);
        this.value = value;
    }
}
