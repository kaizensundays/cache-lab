package com.kaizensundays.eta.cache

import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

/**
 * Created: Saturday 12/7/2024, 9:01 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NodesArchTest {

    private fun classes() = ClassFileImporter().importPackages("com.kaizensundays.eta")

    @Test
    fun test() {

        val classes = classes()

        noClasses()
            .that()
            .resideInAPackage("com.kaizensundays.eta.cache")
            .should()
            .accessClassesThat().resideInAPackage("com.kaizensundays.eta.jgroups")
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("com.kaizensundays.eta.cache")
            .should()
            .accessClassesThat().resideInAPackage("org.jgroups..")
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("com.kaizensundays.eta.raft")
            .should()
            .accessClassesThat().resideInAPackage("org.jgroups..")
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("com.kaizensundays.eta.jgroups")
            .and(not(equivalentTo(CacheCommandHandler::class.java)))
            .should()
            .accessClassesThat().resideInAPackage("com.kaizensundays.eta.cache")
            .check(classes)
    }

}