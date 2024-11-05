package io.flamingock.graalvm;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import io.flamingock.core.api.FlamingockMetadata;
import io.flamingock.core.api.annotations.FlamingockGraalVM;


@SupportedAnnotationTypes("io.flamingock.core.api.annotations.FlamingockGraalVM")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GraalvmAnnotationProcessor extends AbstractProcessor {

    private final String logPrefix = "Flamingock Graalvm annotation processor: ";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return false;
        }

        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, logPrefix + "starting");

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(FlamingockGraalVM.class);


        List<String> classes = new LinkedList<>();
        try {
            for (Element element : annotatedElements) {
                if (element.getKind() == ElementKind.CLASS) {
                    String className = ((TypeElement) element).getQualifiedName().toString();
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, logPrefix + "Processing class: " + className);
                    extractAnnotations(element);
                    classes.add(className);
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, logPrefix + "Processed class: " + className);
                }
            }
            FlamingockMetadata metadata = new FlamingockMetadata(true, classes);
            writeJsonMetadata(metadata);
            writeClassesToRegister(metadata);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
            throw new RuntimeException(logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, logPrefix + "Successfully finished Flamingock annotation processor");

        return true;
    }


    //TODO REFACTOR THIS
    private void writeJsonMetadata(FlamingockMetadata metadata) {
        FileObject file;
        try {
            file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", FlamingockMetadata.FILE_PATH);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to creating flamingock metadata file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try (Writer writer = file.openWriter()) {
            String serialisedObject =new Gson().toJson(metadata);
            writer.write(serialisedObject);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
            throw new RuntimeException(logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
        }
    }

    //TODO REFACTOR THIS
    private void writeClassesToRegister(FlamingockMetadata metadata) {
        FileObject file;
        try {
            file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", Constants.GRAALVM_REFLECT_CLASSES_PATH);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to creating flamingock metadata file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try (Writer writer = file.openWriter()) {
            for(String clazz : metadata.getClasses()) {
                writer.write(clazz);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
            throw new RuntimeException(logPrefix + "Failed to write AnnotatedClasses file: " + e.getMessage());
        }
    }

    private void extractAnnotations(Element element) {
        // Get all annotations on the element
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror annotationMirror : annotationMirrors) {
            // Get the annotation type (e.g., com.yourcompany.annotations.AdditionalAnnotation)
            TypeMirror annotationType = annotationMirror.getAnnotationType();
            String annotationName = annotationType.toString();

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Found annotation: " + annotationName + " on element: " + element.getSimpleName());

            // Extract annotation values if needed
            if (annotationName.equals("io.mongock.api.annotations.ChangeUnit")) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues =
                        annotationMirror.getElementValues();

                for (Map.Entry<? extends ExecutableElement, ? extends javax.lang.model.element.AnnotationValue> entry : elementValues.entrySet()) {
                    String key = entry.getKey().getSimpleName().toString();
                    String value = entry.getValue().getValue().toString();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                            "Annotation value: " + key + " = " + value);
                }
            }
        }
    }

}
