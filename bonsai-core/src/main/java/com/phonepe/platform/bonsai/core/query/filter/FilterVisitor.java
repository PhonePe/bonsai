package com.phonepe.platform.bonsai.core.query.filter;

import com.phonepe.platform.bonsai.core.query.filter.general.*;
import com.phonepe.platform.bonsai.core.query.filter.general.ContainsFilter;
import com.phonepe.platform.bonsai.core.query.filter.logical.AndFilter;
import com.phonepe.platform.bonsai.core.query.filter.logical.OrFilter;
import com.phonepe.platform.bonsai.core.query.filter.numeric.*;

/**
 * A visitor on various types of {@link Filter}s
 *
 * @author tushar.naik
 * @version 1.0  02/05/17 - 6:43 PM
 */
public interface FilterVisitor<T> {

    T visit(ContainsFilter filter);

    T visit(LessThanFilter filter);

    T visit(LessEqualFilter filter);

    T visit(GreaterThanFilter filter);

    T visit(BetweenFilter filter);

    T visit(GreaterEqualFilter filter);

    T visit(NotInFilter filter);

    T visit(NotEqualsFilter filter);

    T visit(MissingFilter filter);

    T visit(InFilter filter);

    T visit(ExistsFilter filter);

    T visit(EqualsFilter filter);

    T visit(AnyFilter filter);

    T visit(AndFilter andFilter);

    T visit(OrFilter orFilter);
}
