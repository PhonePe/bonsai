package com.phonepe.platform.bonsai.core.variation.filter;

import com.phonepe.platform.bonsai.core.variation.filter.general.*;
import com.phonepe.platform.bonsai.core.variation.filter.logical.AndFilter;
import com.phonepe.platform.bonsai.core.variation.filter.logical.OrFilter;
import com.phonepe.platform.bonsai.core.variation.filter.numeric.*;

import java.util.stream.Stream;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 3:27 PM
 */
public class FilterFieldIdentifier implements FilterVisitor<Stream<String>> {
    @Override
    public Stream<String> visit(ContainsFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(LessThanFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(LessEqualFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(GreaterThanFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(BetweenFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(GreaterEqualFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(NotInFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(NotEqualsFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(MissingFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(InFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(ExistsFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(EqualsFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(AnyFilter filter) {
        return Stream.of(filter.getField());
    }

    @Override
    public Stream<String> visit(AndFilter andFilter) {
        return andFilter.getFilters()
                        .stream()
                        .map(k -> k.accept(this))
                        .reduce(Stream::concat)
                        .orElse(Stream.empty());
    }

    @Override
    public Stream<String> visit(OrFilter orFilter) {
        return orFilter.getFilters()
                       .stream()
                       .map(k -> k.accept(this))
                       .reduce(Stream::concat)
                       .orElse(Stream.empty());
    }
}
