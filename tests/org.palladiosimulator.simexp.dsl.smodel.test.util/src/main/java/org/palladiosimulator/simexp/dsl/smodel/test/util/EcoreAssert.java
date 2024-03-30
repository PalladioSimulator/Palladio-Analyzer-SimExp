package org.palladiosimulator.simexp.dsl.smodel.test.util;

import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.RecursiveComparisonAssert;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.eclipse.emf.ecore.EObject;

public class EcoreAssert extends AbstractAssert<EcoreAssert, EObject> {
    private static final RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
        .withIgnoredFieldsMatchingRegexes(".*eContainer", ".*eFlags", ".*eStorage")
        .build();

    public EcoreAssert(EObject actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static RecursiveComparisonAssert<?> assertThat(EObject actual) {
        EcoreAssert ecoreAssert = new EcoreAssert(actual, EcoreAssert.class);
        RecursiveComparisonAssert<?> usingRecursiveComparison = ecoreAssert.usingRecursiveComparison(configuration);
        return usingRecursiveComparison;
    }

    public static ListAssert<EObject> assertThat(List<? extends EObject> actual) {
        ListAssert<EObject> listAssert = AssertionsForInterfaceTypes.assertThat(actual);
        listAssert = listAssert.usingRecursiveFieldByFieldElementComparator(configuration);
        return listAssert;
    }
}
