package org.matilda.commands.names;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.StringUtils;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.python.PythonProperties;
import org.matilda.commands.utils.Package;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class NameGenerator {
    @Inject
    NameGenerator() {}

    @Inject
    PythonProperties mPythonProperties;

    public static final Package MAIN_GENERATED_PACKAGE = Package.fromString("org.matilda.generated");
    public static final Package COMMANDS_GENERATED_PACKAGE = MAIN_GENERATED_PACKAGE.subpackage( "commands");
    public static final Package RAW_COMMAND_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage( "raw");
    public static final String COMMANDS_MODULE_CLASS_NAME = "CommandsModule";
    public static final TypeName COMMANDS_MODULE_TYPE_NAME = ClassName.get(COMMANDS_GENERATED_PACKAGE.getPackageName(),
            COMMANDS_MODULE_CLASS_NAME);
    public static final String SERVICES_MODULE_CLASS_NAME = "ServicesModule";
    public static final TypeName SERVICES_MODULE_TYPE_NAME = ClassName.get(COMMANDS_GENERATED_PACKAGE.getPackageName(),
            SERVICES_MODULE_CLASS_NAME);
    public static final Package ORIGINAL_PACKAGE = new Package("org", "matilda", "commands");

    public Package getPythonGeneratedCommandsPackage() {
        return mPythonProperties.pythonGeneratedPackage().subpackage("commands");
    }

    public class ServiceNameGenerator {
        private final ServiceInfo mServiceInfo;

        public ServiceNameGenerator(ServiceInfo serviceInfo) {
            mServiceInfo = serviceInfo;
        }

        private Package getFullNamePackage() {
            return Package.fromString(mServiceInfo.getFullName());
        }

        public String getServiceClassName() {
            return getFullNamePackage().getLastPart();
        }

        public String getServiceSnakeCaseName() {
            String regex = "([a-z])([A-Z]+)";

            // Replacement string
            String replacement = "$1_$2";

            // Replace the given regex
            // with replacement string
            // and convert it to lower case.
            return getServiceClassName()
                    .replaceAll(regex, replacement)
                    .toLowerCase();
        }

        public Package getPythonGeneratedServicePackage() {
            return getPythonGeneratedCommandsPackage().subpackage(getServiceSnakeCaseName());
        }

        public Package getPackage() {
            return getFullNamePackage().withoutLastPart();
        }

        private Package getServiceRelativePackage() {
            return getPackage().removeCommonPrefixFrom(ORIGINAL_PACKAGE);
        }

        public class CommandNameGenerator {
            private final CommandInfo mCommandInfo;

            public CommandNameGenerator(CommandInfo commandInfo) {
                mCommandInfo = commandInfo;
            }

            public String getRawCommandClassName() {
                return getServiceClassName() + StringUtils.capitalize(mCommandInfo.getName()) + "Command";
            }

            public String getRawCommandPackageName() {
                return Package.joinPackages(RAW_COMMAND_CLASSES_PACKAGE, getServiceRelativePackage()).getPackageName();
            }

            public TypeName getRawCommandTypeName() {
                return ClassName.get(getRawCommandPackageName(), getRawCommandClassName());
            }

            public String getFullCommandName() {
                return getServiceRelativePackage().getParts().stream()
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining()) + getRawCommandClassName();
            }
        }
    }

    public ServiceNameGenerator forService(ServiceInfo serviceInfo) {
        return new ServiceNameGenerator(serviceInfo);
    }

    public ServiceNameGenerator.CommandNameGenerator forCommand(CommandInfo commandInfo) {
        return forService(commandInfo.getService()).new CommandNameGenerator(commandInfo);
    }
}
