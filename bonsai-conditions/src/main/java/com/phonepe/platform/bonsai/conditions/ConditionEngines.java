package com.phonepe.platform.bonsai.conditions;

public class ConditionEngines {

    public static class TrueConditionEngine<C extends Condition> extends ConditionEngine<Void, C> {
        @Override
        public Boolean match(Void v1, C c) {
            return true;
        }
    }

    public static <C extends Condition> ConditionEngine<Void, C> trueConditionEngine() {
        return new TrueConditionEngine<>();
    }
}
