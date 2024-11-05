package io.flamingock.graalvm;

import io.flamingock.core.api.FlamingockMetadata;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class RegistrationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        registerClass(FlamingockMetadata.class.getCanonicalName());
        List<String> classesToRegister= fromFile(Constants.GRAALVM_REFLECT_CLASSES_PATH);
        classesToRegister.forEach(RegistrationFeature::registerClass);
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

    public List<String> fromFile(String filePath) {
        ClassLoader classLoader = RegistrationFeature.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream != null) {
                List<String> classesToRegister = new LinkedList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        classesToRegister.add(line);
                    }
                }
                return classesToRegister;

            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
