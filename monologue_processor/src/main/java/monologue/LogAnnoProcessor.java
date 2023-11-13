package monologue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.lang.model.element.*;
import java.util.Set;

public class LogAnnoProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // List<? extends TypeElement> annotationList = annotations.stream()
    //     .filter((te) -> {
    //       var str = te.getSimpleName().toString();
    //       return str.equals("LogFile") || str.equals("LogNT");
    //   }).toList();
    // if (annotationList.size() > 1) {
    //   // emit warning
    //   throw new RuntimeException("[Monologue] expected 1 annotation, got " + annotationList.size());
    // } else if (annotationList.size() == 0) {
    //   return false;
    // }

    for (var anno : annotations) {
      var annoName = anno.getSimpleName().toString();
      if (annoName.equals("LogFile") || annoName.equals("LogNT")) {
        roundEnv.getElementsAnnotatedWith(anno).forEach(e -> {
          if (e instanceof VariableElement) {
            var ve = (VariableElement) e;
            var type = ve.asType();
            if (!TypeNames.TYPE_NAMES_SET.contains(type.toString())) {
              // throw new RuntimeException("[Monologue] field " + ve.getSimpleName() + " must be of type int");
              processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "[Monologue] field " + ve.getSimpleName() + " must be of type int",
                ve
              );
            }
          } else if (e instanceof ExecutableElement) {
            var ee = (ExecutableElement) e;
            var type = ee.getReturnType();
            if (!TypeNames.TYPE_NAMES_SET.contains(type.toString())) {
              // throw new RuntimeException("[Monologue] method " + ee.getSimpleName() + " must return int");
              processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "[Monologue] method " + ee.getSimpleName() + " must return int",
                ee
              );
            }
            if (ee.getParameters().size() != 0) {
              processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "[Monologue] method " + ee.getSimpleName() + " must have 0 parameters",
                ee
              );
            }
          }
        });
      }
    }
    return true;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(
      "monologue.Annotations.LogFile",
      "monologue.Annotations.LogNT"
    );
  }
}