package org.javacs.kt

import org.eclipse.lsp4j.CompletionList
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class InstanceMemberTest : SingleFileTestFixture("completions", "InstanceMember.kt") {
    @Test fun `complete instance members`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 3, 15)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("instanceFoo")))
        assertThat(labels, hasItem(startsWith("extensionFoo")))
        assertThat(labels, hasItem(startsWith("fooVar")))
        assertThat(labels, not(hasItem(startsWith("privateInstanceFoo"))))
        assertThat(labels, not(hasItem(startsWith("getFooVar"))))
        assertThat(labels, not(hasItem(startsWith("setFooVar"))))

        assertThat("Reports instanceFoo only once", completions.items.filter { it.label.startsWith("instanceFoo") }, hasSize(1))
        assertThat("Reports extensionFoo only once", completions.items.filter { it.label.startsWith("extensionFoo") }, hasSize(1))
    }

    @Test fun `find count extension function`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 8, 14)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("count"))
    }

    @Test fun `complete method reference`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 13, 16)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("instanceFoo")))
        // assertThat(labels, not(hasItem("extensionFoo"))) Kotlin will probably implement this later
        assertThat(labels, hasItem(startsWith("fooVar")))
        assertThat(labels, not(hasItem(startsWith("privateInstanceFoo"))))
        assertThat(labels, not(hasItem(startsWith("getFooVar"))))
        assertThat(labels, not(hasItem(startsWith("setFooVar"))))

        assertThat(completions.items.filter { it.label.startsWith("instanceFoo") }.firstOrNull(), hasProperty("insertText", equalTo("instanceFoo")))
    }

    @Test fun `complete unqualified function reference`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 17, 8)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("findFunctionReference")))
    }

    @Test fun `complete a function name within a call`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 22, 27)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("findFunctionReference")))
        assertThat(labels, not(hasItem("instanceFoo")))
    }
}

class InstanceMembersJava : SingleFileTestFixture("completions", "InstanceMembersJava.kt") {
    @Test fun `convert getFileName to fileName`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 4, 14)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("fileName"))
        assertThat(labels, not(hasItem("getFileName")))
    }
}

class FunctionScopeTest : SingleFileTestFixture("completions", "FunctionScope.kt") {
    @Test fun `complete identifiers in function scope`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 4, 10)).get().right!!
        val labels = completions.items.map { it.label }

        println("ok lol"  + labels.toString())
        assertThat(labels, hasItem("anArgument"))
        assertThat(labels, hasItem("aLocal"))
        assertThat(labels, hasItem("aClassVal"))
        assertThat(labels, hasItem(startsWith("aClassFun")))
        assertThat(labels, hasItem("aCompanionVal"))
        assertThat(labels, hasItem(startsWith("aCompanionFun")))

        assertThat("Reports anArgument only once", completions.items.filter { it.label == "anArgument" }, hasSize(1))
        assertThat("Reports aLocal only once", completions.items.filter { it.label == "aLocal" }, hasSize(1))
        assertThat("Reports aClassVal only once", completions.items.filter { it.label == "aClassVal" }, hasSize(1))
        assertThat("Reports aClassFun only once", completions.items.filter { it.label.startsWith("aClassFun") }, hasSize(1))
        assertThat("Reports aCompanionVal only once", completions.items.filter { it.label == "aCompanionVal" }, hasSize(1))
        assertThat("Reports aCompanionFun only once", completions.items.filter { it.label.startsWith("aCompanionFun") }, hasSize(1))
    }
}

class TypesTest : SingleFileTestFixture("completions", "Types.kt") {
    @Test fun `complete a type name`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 25)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("SomeInnerClass"))
        assertThat(labels, hasItem("String"))
        assertThat(labels, hasItem("SomeInnerObject"))
        assertThat(labels, hasItem("SomeAlias"))
    }
}

class FillEmptyBodyTest : SingleFileTestFixture("completions", "FillEmptyBody.kt") {
    @Test fun `fill an empty body`() {
        replace(file, 2, 16, "", """"
            Callee.
""")
        val completions = languageServer.textDocumentService.completion(completionParams(file, 3, 20)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("bar")))
    }
}

class ConstructorTest : SingleFileTestFixture("completions", "Constructor.kt") {
    @Test fun `complete a constructor`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 10)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("SomeConstructor"))
    }
}

class MiddleOfFunctionTest : SingleFileTestFixture("completions", "MiddleOfFunction.kt") {
    @Test fun `complete in the middle of a function`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 3, 11)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("subSequence")))
    }
}

class BackquotedFunctionTest : SingleFileTestFixture("completions", "BackquotedFunction.kt") {
    @Test fun `complete with backquotes`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 7)).get().right!!
        val insertText = completions.items.map { it.insertText }

        assertThat(insertText, hasItem("`fun that needs backquotes`()"))
    }
}

class CompleteStaticsTest : SingleFileTestFixture("completions", "Statics.kt") {
    @Test fun `java static method`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 4, 16)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("isNull")))
    }

    @Test fun `java static method reference`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 7, 17)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("isNull")))
    }

    @Test fun `object method`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 5, 17)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("objectFun")))
    }

    @Test fun `companion object method`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 6, 16)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("companionFun")))
    }
}

class VisibilityTest : SingleFileTestFixture("completions", "Visibility.kt") {
    @Test fun `find tricky visibility members`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 3, 10)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItems(startsWith("privateThisFun"), startsWith("protectedThisFun"), startsWith("publicThisFun"), startsWith("privateThisCompanionFun"), startsWith("protectedThisCompanionFun"), startsWith("publicThisCompanionFun"), startsWith("privateTopLevelFun")))
        assertThat(labels, hasItems(startsWith("protectedSuperFun"), startsWith("publicSuperFun"), startsWith("protectedSuperCompanionFun"), startsWith("publicSuperCompanionFun")))
        assertThat(labels, not(hasItems(startsWith("privateSuperFun"), startsWith("privateSuperCompanionFun"), startsWith("publicExtensionFun"))))
    }
}

class ImportsTest : SingleFileTestFixture("completions", "Imports.kt") {
    @Test fun `complete import from java-nio-path-P`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 1, 23)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItems("Path", "Paths"))
    }

    @Test fun `complete import from java-nio-`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 3, 25)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItems("MethodHandle"))
    }

    @Test fun `complete import from j`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 5, 9)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItems("java"))
    }
}

class DoubleDotTest : SingleFileTestFixture("completions", "DoubleDot.kt") {
    @Test fun `complete nested select`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 15)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, not(hasItem("chars")))
        assertThat(labels, hasItem(startsWith("anyMatch")))
    }
}

class QuestionDotTest : SingleFileTestFixture("completions", "QuestionDot.kt") {
    @Test fun `complete null-safe select`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 8)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("chars")))
    }
}

class OuterDotInnerTest : SingleFileTestFixture("completions", "OuterDotInner.kt") {
    @Test fun `complete as OuterClass-InnerClass`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 24)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("InnerClass"))
    }

    @Test fun `complete static OuterClass-InnerClass`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 6, 19)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem("InnerClass"))
    }
}

class EditCallTest : SingleFileTestFixture("completions", "EditCall.kt") {
    @Test fun `edit existing function`() {
        val completions = languageServer.textDocumentService.completion(completionParams(file, 2, 11)).get().right!!
        val labels = completions.items.map { it.label }

        assertThat(labels, hasItem(startsWith("println")))
        assertThat(completions.items.filter { it.label.startsWith("println") }.firstOrNull(), hasProperty("insertText", equalTo("println")))
    }
}
