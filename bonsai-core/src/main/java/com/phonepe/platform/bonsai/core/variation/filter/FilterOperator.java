package com.phonepe.platform.bonsai.core.variation.filter;

/**
 * @author tushar.naik
 * @version 1.0  02/05/17 - 6:46 PM
 */
public class FilterOperator {

    /* numeric */
    public static final String GREATER_EQUAL = "GREATER_EQUAL";
    public static final String GREATER_THAN = "GREATER_THAN";
    public static final String LESS_EQUAL = "LESS_EQUAL";
    public static final String LESS_THAN = "LESS_THAN";
    public static final String BETWEEN = "BETWEEN";

    /* general */
    public static final String EQUALS = "EQUALS";
    public static final String IN = "IN";
    public static final String NOT_IN = "NOT_IN";
    public static final String NOT_EQUALS = "NOT_EQUALS";
    public static final String ANY = "ANY";
    public static final String EXISTS = "EXISTS";
    public static final String MISSING = "MISSING";
    public static final String CONTAINS = "CONTAINS";

    /* logical filters */
    public static final String AND = "AND";
    public static final String OR = "OR";
}
