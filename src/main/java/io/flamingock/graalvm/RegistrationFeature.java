package io.flamingock.graalvm;

import io.flamingock.core.api.FlamingockConfiguration;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RegistrationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        ClassLoader classLoader = RegistrationFeature.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(FlamingockConfiguration.FILE_PATH)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String className;
                    while ((className = reader.readLine()) != null) {
                        registerClass(className);
                    }
                }
            } else {
                throw new RuntimeException(String.format("File[%s] not found", FlamingockConfiguration.FILE_PATH));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerClass(String className) {
        try {
            System.out.printf("Flamingock: Registering class[%s]%n", className);
            Class<?> clazz = Class.forName(className);
            RuntimeReflection.register(clazz);
            RuntimeReflection.register(clazz.getDeclaredConstructors());
            RuntimeReflection.register(clazz.getDeclaredMethods());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
