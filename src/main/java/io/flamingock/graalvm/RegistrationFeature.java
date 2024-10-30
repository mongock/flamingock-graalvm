package io.flamingock.graalvm;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RegistrationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        File file = new File("build/annotated-classes.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String className;
                while ((className = reader.readLine()) != null) {
                    try {
                        registerClass(Class.forName(className));
                        System.out.println("Registered class: " + className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Failed to register class for reflection: " + className);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Annotated classes file not found: " + file.getAbsolutePath());
        }
    }

    private static void registerClass(Class<?> clazz) {
        RuntimeReflection.register(clazz);
        RuntimeReflection.register(clazz.getDeclaredConstructors());
        RuntimeReflection.register(clazz.getDeclaredMethods());
    }


}
