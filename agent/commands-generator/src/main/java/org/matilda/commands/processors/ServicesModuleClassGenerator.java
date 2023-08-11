package org.matilda.commands.processors;

import com.squareup.javapoet.*;
import dagger.Module;
import dagger.Provides;
import org.apache.commons.lang3.StringUtils;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.names.CommandIdGenerator;
import org.matilda.commands.names.NameGenerator;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class ServicesModuleClassGenerator implements Processor<ProjectServices> {
    @Inject
    Filer mFiler;

    @Inject
    NameGenerator mNameGenerator;

    @Inject
    CommandIdGenerator mCommandIdGenerator;

    @Inject
    public ServicesModuleClassGenerator() {}

    @Override
    public void process(ProjectServices services) {
        try {
            JavaFile.builder(NameGenerator.COMMANDS_GENERATED_PACKAGE.getPackageName(),
                            createClassSpec(services))
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createClassSpec(ProjectServices services) {
        var builder = TypeSpec.classBuilder(NameGenerator.SERVICES_MODULE_CLASS_NAME)
                .addAnnotation(Module.class)
                .addModifiers(Modifier.PUBLIC);

        services.processEachService(service -> {
            if (!service.hasInjectConstructor()) {
                builder.addMethod(createServiceProvideMethod(service));
            }
        });

        return builder.build();
    }

    private MethodSpec createServiceProvideMethod(ServiceInfo service) {
        return MethodSpec.methodBuilder(getProvideMethodName(service))
                .addAnnotation(Provides.class)
                .addModifiers(Modifier.STATIC)
                .returns(TypeName.get(service.type()))
                .addStatement("return new $T()", service.type())
                .build();
    }

    private String getProvideMethodName(ServiceInfo service) {
        return StringUtils.uncapitalize(mNameGenerator.forService(service).getServiceClassName());
    }
}
