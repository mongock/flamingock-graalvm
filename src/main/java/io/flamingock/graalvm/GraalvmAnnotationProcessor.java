package io.flamingock.graalvm;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("io.flamingock.graalvm.FlamingockGraalVM")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GraalvmAnnotationProcessor extends AbstractProcessor {

    private static final String ANNOTATED_CLASSES_PACKAGE = "io.flamingock.graalvm";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return false;
        }

        String logPrefix = "Flamingock Graalvm annotation processor: ";
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, logPrefix + "starting");

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(FlamingockGraalVM.class);


        try (FileWriter writer = new FileWriter("build/annotated-classes.txt")) {
            for (Element element : annotatedElements) {
                if (element.getKind() == ElementKind.CLASS) {
                    String className = ((TypeElement) element).getQualifiedName().toString();
                    writer.write(className + "\n");
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, logPrefix + "Processed class: " + className);
                }
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, logPrefix + "Successfully finished Flamingock annotation processor");

        return true;
    }
}
