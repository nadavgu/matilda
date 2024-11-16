package org.matilda.commands.di

import com.squareup.kotlinpoet.TypeSpec
import org.matilda.commands.MatildaScope

enum class DiFrameWork {
    Dagger,
    KotlinInject;

    val Provides
        get() = when (this) {
            Dagger -> dagger.Provides::class
            KotlinInject -> me.tatarka.inject.annotations.Provides::class
        }

    val Inject
        get() = when (this) {
            Dagger -> javax.inject.Inject::class
            KotlinInject -> me.tatarka.inject.annotations.Inject::class
        }

    val Singleton
        get() = when (this) {
            Dagger -> javax.inject.Singleton::class
            KotlinInject -> MatildaScope::class
        }

    val Module
        get() = when (this) {
            Dagger -> dagger.Module::class
            KotlinInject -> me.tatarka.inject.annotations.Component::class
        }
}

fun TypeSpec.Builder.staticProvidesFunctionBuilder(diFrameWork: DiFrameWork,
                                                   block: TypeSpec.Builder.() -> Unit): TypeSpec.Builder {
    when (diFrameWork) {
        DiFrameWork.Dagger -> {
            val companionObjectBuilder = TypeSpec.companionObjectBuilder()
            companionObjectBuilder.block()
            addType(companionObjectBuilder.build())
        }
        DiFrameWork.KotlinInject -> block()
    }

    return this
}