package com.example.demo.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.example.demo", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

	@ArchTest
	static final ArchRule domainShouldNotDependOnInfrastructure = noClasses().that().resideInAPackage("..domain..")
			.should().dependOnClassesThat().resideInAPackage("..infrastructure..");

	@ArchTest
	static final ArchRule domainShouldBeFreeOfSpringAndJpa = noClasses().that().resideInAPackage("..domain..").should()
			.dependOnClassesThat()
			.resideInAnyPackage("org.springframework..", "jakarta.persistence..", "javax.persistence..");

	@ArchTest
	static final ArchRule portsShouldBeInterfaces = classes().that().resideInAPackage("..domain.port..").should()
			.beInterfaces();
}