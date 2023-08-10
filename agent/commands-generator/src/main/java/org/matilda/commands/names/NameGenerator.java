package org.matilda.commands.names;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.StringUtils;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ServiceInfo;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NameGenerator {
    @Inject
    NameGenerator() {}

    public static String joinPackages(String... packages) {
        return joinPackages(Arrays.asList(packages));
    }

    public static String joinPackages(List<String> packages) {
        return packages.stream()
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("."));
    }

    public static List<String> splitPackageName(String packageName) {
        return Arrays.asList(packageName.split("\\."));
    }

    public static final String MAIN_GENERATED_PACKAGE = "org.matilda.generated";
    public static final String COMMANDS_GENERATED_PACKAGE = joinPackages(MAIN_GENERATED_PACKAGE, "commands");
    public static final String RAW_COMMAND_CLASSES_PACKAGE = joinPackages(COMMANDS_GENERATED_PACKAGE, "raw");
    public static final String COMMANDS_MODULE_CLASS_NAME = "CommandsModule";
    public static final TypeName COMMANDS_MODULE_TYPE_NAME = ClassName.get(COMMANDS_GENERATED_PACKAGE,
            COMMANDS_MODULE_CLASS_NAME);
    public static final List<String> ORIGINAL_PACKAGE_PARTS = List.of("org", "matilda", "commands");

    public static class ServiceNameGenerator {
        private final ServiceInfo mServiceInfo;

        public ServiceNameGenerator(ServiceInfo serviceInfo) {
            mServiceInfo = serviceInfo;
        }

        public List<String> getFullNameParts() {
            return splitPackageName(mServiceInfo.fullName());
        }

        public String getServiceClassName() {
            List<String> nameParts = getFullNameParts();
            return nameParts.get(nameParts.size() - 1);
        }

        public List<String> getPackageParts() {
            List<String> nameParts = getFullNameParts();
            return nameParts.subList(0, nameParts.size() - 1);
        }

        private String getServiceRelativePackageName() {
            return joinPackages(getServiceRelativePackageNameParts());
        }

        private List<String> getServiceRelativePackageNameParts() {
            List<String> splitPackageName = getPackageParts();
            int equalParts = 0;
            while (equalParts < splitPackageName.size() && equalParts < ORIGINAL_PACKAGE_PARTS.size()) {
                if (!splitPackageName.get(equalParts).equals(ORIGINAL_PACKAGE_PARTS.get(equalParts))) {
                    break;
                }
                equalParts++;
            }
            return splitPackageName.subList(equalParts, splitPackageName.size());
        }

        public class CommandNameGenerator {
            private final CommandInfo mCommandInfo;

            public CommandNameGenerator(CommandInfo commandInfo) {
                mCommandInfo = commandInfo;
            }

            public String getRawCommandClassName() {
                return getServiceClassName() + StringUtils.capitalize(mCommandInfo.name()) + "Command";
            }

            public String getRawCommandPackageName() {
                return joinPackages(RAW_COMMAND_CLASSES_PACKAGE, getServiceRelativePackageName());
            }

            public TypeName getRawCommandTypeName() {
                return ClassName.get(getRawCommandPackageName(), getRawCommandClassName());
            }

            public String getFullCommandName() {
                return getServiceRelativePackageNameParts().stream()
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining()) + getRawCommandClassName();
            }
        }
    }

    public ServiceNameGenerator.CommandNameGenerator forCommand(CommandInfo commandInfo) {
        return new ServiceNameGenerator(commandInfo.service()).new CommandNameGenerator(commandInfo);
    }
}
