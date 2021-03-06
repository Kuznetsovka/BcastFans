package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class MethodSources {
	/**
	 * 0 - airFlow
	 * 1 - airDrop
	 * 2 - typeMontage
	 * 3 - subType
	 * 4 - dimension
	 * 5 - resultArticle
	 */
	@RepeatedTest(50)
	public static Stream argumentOfTests() {
		return Stream.of(
				Arguments.of(100, 100, TypeMontage.ROUND, SubType.NONE,"","5756"),
				Arguments.of(30000, 200, TypeMontage.ROUND, SubType.NONE,"",""),
				Arguments.of(2000, 500, TypeMontage.RECTANGLE,SubType.NONE,"", "93098"),
				Arguments.of(340, 200, TypeMontage.ROUND,SubType.ON_ROOF,"315", "251015"),
				Arguments.of(100, 100, TypeMontage.ROUND, SubType.NONE,"","5756"),
				Arguments.of(30000, 200, TypeMontage.ROUND, SubType.NONE,"",""),
				Arguments.of(2000, 500, TypeMontage.RECTANGLE,SubType.NONE,"", "93098"),
				Arguments.of(340, 200, TypeMontage.ROUND,SubType.ON_ROOF,"315", "251015"),
				Arguments.of(100, 100, TypeMontage.ROUND, SubType.NONE,"","5756"),
				Arguments.of(30000, 200, TypeMontage.ROUND, SubType.NONE,"",""),
				Arguments.of(2000, 500, TypeMontage.RECTANGLE,SubType.NONE,"", "93098"),
				Arguments.of(340, 200, TypeMontage.ROUND,SubType.ON_ROOF,"315", "251015"),
				Arguments.of(100, 100, TypeMontage.ROUND, SubType.NONE,"","5756"),
				Arguments.of(30000, 200, TypeMontage.ROUND, SubType.NONE,"",""),
				Arguments.of(2000, 500, TypeMontage.RECTANGLE,SubType.NONE,"", "93098"),
				Arguments.of(340, 200, TypeMontage.ROUND,SubType.ON_ROOF,"315", "251015"),
				Arguments.of(100, 100, TypeMontage.ROUND, SubType.NONE,"","5756"),
				Arguments.of(30000, 200, TypeMontage.ROUND, SubType.NONE,"",""),
				Arguments.of(2000, 500, TypeMontage.RECTANGLE,SubType.NONE,"", "93098"),
				Arguments.of(340, 200, TypeMontage.ROUND,SubType.ON_ROOF,"315", "251015"));
	}
}