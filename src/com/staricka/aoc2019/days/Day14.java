package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.util.*;
import java.util.stream.Collectors;

public class Day14 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day14.txt")) {
            List<ReactionRule> reactionRules = inputStream.lines().map(ReactionRule::new).collect(Collectors.toList());
            Map<String, List<ReactionRule>> reactionRulesByOutput = reactionRules.stream().collect(Collectors.groupingBy(r -> r.getOutput().getSymbol()));
            logInfo("Min ORE needed: %d", findMinOre(reactionRulesByOutput));
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day14s2.txt")) {
            List<ReactionRule> reactionRules = inputStream.lines().map(ReactionRule::new).collect(Collectors.toList());
            Map<String, List<ReactionRule>> reactionRulesByOutput = reactionRules.stream().collect(Collectors.groupingBy(r -> r.getOutput().getSymbol()));
            long ore = 1000000000000L;
            logInfo(findMinOre(reactionRulesByOutput));
            logInfo("Possible fuel: %d", findMaxFuel(reactionRulesByOutput, ore / findMinOre(reactionRulesByOutput), ore));
        }
    }

    private long findMinOre(final Map<String, List<ReactionRule>> reactionRulesByOutput) {
        StateController controller = new StateController("ORE", new ReactionComponent("FUEL", 1), reactionRulesByOutput);

        logInfo(controller.best(Comparator.<State, Long>comparing(s -> s.chemicalsNeeded.getNeeded("ORE")).reversed()).chemicalsNeeded.chemicalsNeeded);
        return controller.best(Comparator.<State, Long>comparing(s -> s.chemicalsNeeded.getNeeded("ORE")).reversed()).chemicalsNeeded.getNeeded("ORE");
    }

    private long findMaxFuel(final Map<String, List<ReactionRule>> reactionRulesByOutput, final long start, final long ore) {
        long max = start;
        boolean done = false;
        while (!done) {
            StateController controller = new StateController("ORE", new ReactionComponent("FUEL", max + 1), reactionRulesByOutput);
            long oreNeeded = controller.best(Comparator.<State, Long>comparing(s -> s.chemicalsNeeded.getNeeded("ORE")).reversed()).chemicalsNeeded.getNeeded("ORE");
            if(oreNeeded <= ore) {
                max++;
            }

            if(oreNeeded >= ore) {
                done = true;
            }
        }
        return max;
    }

    private static class StateController {
        List<State> states = new ArrayList<>();

        private StateController(final String target, final ReactionComponent initial, final Map<String, List<ReactionRule>> reactionRulesByOutput) {
            ChemicalsNeeded chemicalsNeeded = new ChemicalsNeeded(target);
            chemicalsNeeded.add(initial.getSymbol(), initial.getQuantity());
            State state = new State(chemicalsNeeded, reactionRulesByOutput, this);

            states.add(state);
            state.run();
        }

        private void branch(final State state) {
            states.add(state);
            state.run();
        }

        private State best(final Comparator<State> comparator) {
            return states.stream().max(comparator).get();
        }
    }

    private static class State implements Runnable{
        ChemicalsNeeded chemicalsNeeded;
        Map<String, List<ReactionRule>> reactionRulesByOutput;
        ReactionComponent bootstrapNeeded;
        ReactionRule bootstrapRule;
        StateController controller;

        public State(ChemicalsNeeded chemicalsNeeded, Map<String, List<ReactionRule>> reactionRulesByOutput, StateController controller) {
            this.chemicalsNeeded = chemicalsNeeded;
            this.reactionRulesByOutput = reactionRulesByOutput;
            this.controller = controller;
        }

        public State(ChemicalsNeeded chemicalsNeeded, Map<String, List<ReactionRule>> reactionRulesByOutput, ReactionComponent bootstrapNeeded, ReactionRule bootstrapRule, StateController controller) {
            this.chemicalsNeeded = chemicalsNeeded;
            this.reactionRulesByOutput = reactionRulesByOutput;
            this.bootstrapNeeded = bootstrapNeeded;
            this.bootstrapRule = bootstrapRule;
            this.controller = controller;
        }

        @Override
        public void run() {
            if(bootstrapNeeded != null && bootstrapRule != null) {
                applyRule(bootstrapNeeded, bootstrapRule);
            }

            while (!chemicalsNeeded.done()) {
                ReactionComponent needed = chemicalsNeeded.nextNeeded();
                List<ReactionRule> reactionRules = reactionRulesByOutput.get(needed.getSymbol());
                if (reactionRules.size() == 1) {
                    ReactionRule rule = reactionRules.get(0);
                    applyRule(needed, rule);
                } else {
                    List<ReactionRule> extraRules = reactionRules.subList(1, reactionRules.size());
                    extraRules.stream().map(r -> new State(chemicalsNeeded.copy(), reactionRulesByOutput, needed, r, controller)).forEach(controller::branch);
                    ReactionRule rule = reactionRules.get(0);
                    applyRule(needed, rule);
                }
            }
        }

        private void applyRule(final ReactionComponent needed, final ReactionRule rule) {
            chemicalsNeeded.subtract(needed.getSymbol(), rule.getOutput().getQuantity());
            for(ReactionComponent input : rule.getInputs()) {
                chemicalsNeeded.add(input.getSymbol(), input.getQuantity());
            }
        }
    }

    private static class ChemicalsNeeded {
        private final String target;
        private Map<String, Long> chemicalsNeeded = new HashMap<>();

        private ChemicalsNeeded(final String target) {
            this.target = target;
        }

        private ChemicalsNeeded copy() {
            ChemicalsNeeded copy = new ChemicalsNeeded(target);
            copy.chemicalsNeeded = new HashMap<>(chemicalsNeeded);
            return copy;
        }

        private long getNeeded(final String symbol) {
            return chemicalsNeeded.get(symbol);
        }

        private void add(final String symbol, final long quantity) {
            Long current = chemicalsNeeded.get(symbol);
            current = current == null ? 0 : current;
            chemicalsNeeded.put(symbol, current + quantity);
        }

        private void subtract(final String symbol, final long quantity) {
            Long current = chemicalsNeeded.get(symbol);
            current = current == null ? 0 : current;
            chemicalsNeeded.put(symbol, current - quantity);
        }

        private ReactionComponent nextNeeded() {
            Iterator<Map.Entry<String, Long>> iterator = chemicalsNeeded.entrySet().iterator();
            Map.Entry<String, Long> next = iterator.next();
            while (next.getKey().equals(target) || next.getValue() <= 0) {
                next = iterator.next();
            }
            return new ReactionComponent(next.getKey(), next.getValue());
        }

        private boolean done() {
            return chemicalsNeeded.entrySet().stream().noneMatch(e -> !e.getKey().equals(target) && e.getValue() > 0);
        }
    }

    private static class ReactionRule {
        private final List<ReactionComponent> inputs;
        private final ReactionComponent output;

        public ReactionRule(List<ReactionComponent> inputs, ReactionComponent output) {
            this.inputs = inputs;
            this.output = output;
        }

        public ReactionRule(final String string) {
            String[] sides = string.split(" => ");
            String[] inputs = sides[0].split(", ");
            this.inputs = Arrays.stream(inputs).map(ReactionComponent::new).collect(Collectors.toList());
            output = new ReactionComponent(sides[1]);
        }

        public List<ReactionComponent> getInputs() {
            return inputs;
        }

        public ReactionComponent getOutput() {
            return output;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReactionRule that = (ReactionRule) o;
            return Objects.equals(inputs, that.inputs) &&
                    Objects.equals(output, that.output);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inputs, output);
        }

        @Override
        public String toString() {
            return String.format("%s => %s", inputs.stream().map(ReactionComponent::toString).collect(Collectors.joining(", ")), output);
        }
    }

    private static class ReactionComponent {
        private final String symbol;
        private final long quantity;

        public ReactionComponent(String symbol, long quantity) {
            this.symbol = symbol;
            this.quantity = quantity;
        }

        public ReactionComponent(String string) {
            String[] split = string.split(" ");
            symbol = split[1];
            quantity = Integer.parseInt(split[0]);
        }

        public String getSymbol() {
            return symbol;
        }

        public long getQuantity() {
            return quantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReactionComponent that = (ReactionComponent) o;
            return quantity == that.quantity &&
                    Objects.equals(symbol, that.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(symbol, quantity);
        }

        @Override
        public String toString() {
            return String.format("%d %s", quantity, symbol);
        }
    }
}
