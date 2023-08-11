package org.matilda.commands.utils;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Package {
    private final List<String> mParts;

    public Package(List<String> parts) {
        mParts = parts.stream()
                .filter(part -> !part.isEmpty())
                .collect(Collectors.toList());
    }

    public Package(String... parts) {
        this(List.of(parts));
    }

    public Package subpackage(String child) {
        return subpackage(fromString(child));
    }

    private Package subpackage(Package child) {
        return joinPackages(this, child);
    }

    public static Package fromString(String packageName) {
        return new Package(packageName.split("\\."));
    }

    public static Package joinPackages(Package... packages) {
        return new Package(Arrays.stream(packages)
                .flatMap(p -> p.mParts.stream())
                .collect(Collectors.toList()));
    }

    public String getPackageName() {
        return String.join(".", mParts);
    }

    public String getLastPart() {
        return mParts.get(mParts.size() - 1);
    }

    public Package withoutLastPart() {
        return new Package(mParts.subList(0, mParts.size() - 1));
    }

    public Package removeCommonPrefixFrom(Package other) {
        int equalParts = 0;
        while (equalParts < mParts.size() && equalParts < other.mParts.size()) {
            if (!mParts.get(equalParts).equals(other.mParts.get(equalParts))) {
                break;
            }
            equalParts++;
        }
        return new Package(mParts.subList(equalParts, mParts.size()));
    }

    public List<String> getParts() {
        return mParts;
    }

    public String toPath() {
        return String.join(FileSystems.getDefault().getSeparator(), mParts);
    }
}
