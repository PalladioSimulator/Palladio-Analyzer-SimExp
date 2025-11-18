package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.ext.moea.ParetoFront;
import io.jenetics.util.BaseSeq;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

public class ParetoSetCollector {
    public static Collector<IndividualResult, ?, ISeq<IndividualResult>> create(IPrecisionProvider precisionProvider,
            IAverageProvider averageProvider, Function<String, Comparator<Double>> comparatorFactory,
            Optimize optimize) {
        Comparator<IndividualResult> dominance = new ParetoDominance(precisionProvider, averageProvider,
                comparatorFactory);

        return Collector.of( //
                () -> new Front<>(dominance, optimize) //
                , Front::add //
                , Front::merge //
                , Front::toISeq);
    }

    private static class Front<G extends Gene<?, G>, C extends Comparable<? super C>> {
        private final Comparator<IndividualResult> dominance;
        private final Optimize _optimize;
        private final ParetoFront<IndividualResult> _front;

        public Front(Comparator<IndividualResult> dominance, Optimize optimize) {
            this.dominance = dominance;
            this._optimize = optimize;
            this._front = new ParetoFront<>(this::dominance, this::equals);
        }

        void add(IndividualResult result) {
            final ISeq<IndividualResult> front = front(MSeq.of(result), this::dominance);
            _front.addAll(front.asList());
        }

        /**
         * Return the elements, from the given input {@code set}, which are part of the pareto
         * front.
         * <p>
         * <b>Reference:</b><em> E. Zitzler and L. Thiele. Multiobjective Evolutionary Algorithms: A
         * Comparative Case Study and the Strength Pareto Approach, IEEE Transactions on
         * Evolutionary Computation, vol. 3, no. 4, pp. 257-271, 1999.</em>
         *
         * @param set
         *            the input set
         * @param dominance
         *            the dominance comparator used
         * @param <T>
         *            the element type
         * @return the elements which are part of the pareto set
         * @throws NullPointerException
         *             if one of the arguments is {@code null}
         */
        private ISeq<IndividualResult> front(final BaseSeq<? extends IndividualResult> set,
                final Comparator<IndividualResult> dominance) {
            final MSeq<IndividualResult> front = MSeq.of(set);

            int n = front.size();
            int i = 0;
            while (i < n) {
                int j = i + 1;
                while (j < n) {
                    if (dominance.compare(front.get(i), front.get(j)) > 0) {
                        --n;
                        front.swap(j, n);
                    } else if (dominance.compare(front.get(j), front.get(i)) > 0) {
                        --n;
                        front.swap(i, n);
                        --i;
                        break;
                    } else {
                        ++j;
                    }
                }
                ++i;
            }

            return front.subSeq(0, n)
                .copy()
                .toISeq();
        }

        private int dominance(IndividualResult a, IndividualResult b) {
            return _optimize == Optimize.MAXIMUM ? dominance.compare(a, b) : dominance.compare(b, a);
        }

        private boolean equals(IndividualResult a, IndividualResult b) {
            return Objects.equals(a, b);
        }

        Front<G, C> merge(Front<G, C> front) {
            _front.merge(front._front);
            return this;
        }

        ISeq<IndividualResult> toISeq() {
            return _front != null ? _front.toISeq() : ISeq.empty();
        }
    }
}
