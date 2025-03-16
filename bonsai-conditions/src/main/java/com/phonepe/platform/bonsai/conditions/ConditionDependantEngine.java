package com.phonepe.platform.bonsai.conditions;

public abstract class ConditionDependantEngine<C extends Condition> extends ConditionEngine<Void, C> {
    public abstract Boolean match(C c);

    @Override
    public Boolean match(Void entity, C c) {
        return match(c);
    }
}
