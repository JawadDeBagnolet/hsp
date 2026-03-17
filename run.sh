#!/bin/bash

# Script de lancement pour JavaFX sur macOS Apple Silicon

JAVA_HOME="/Users/redalakhledj/Library/Java/JavaVirtualMachines/temurin-17.0.18/Contents/Home"
CLASSPATH="/Users/redalakhledj/.m2/repository/org/openjfx/javafx-controls/21.0.1/javafx-controls-21.0.1.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-graphics/21.0.1/javafx-graphics-21.0.1.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-base/21.0.1/javafx-base-21.0.1.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-fxml/21.0.1/javafx-fxml-21.0.1.jar:/Users/redalakhledj/.m2/repository/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar:/Users/redalakhledj/.m2/repository/com/google/protobuf/protobuf-java/4.31.1/protobuf-java-4.31.1.jar"

MODULE_PATH="/Users/redalakhledj/.m2/repository/org/openjfx/javafx-base/21.0.1/javafx-base-21.0.1-mac-aarch64.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-controls/21.0.1/javafx-controls-21.0.1-mac-aarch64.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-fxml/21.0.1/javafx-fxml-21.0.1-mac-aarch64.jar:/Users/redalakhledj/.m2/repository/org/openjfx/javafx-graphics/21.0.1/javafx-graphics-21.0.1-mac-aarch64.jar"

"$JAVA_HOME/bin/java" \
    --add-modules javafx.controls,javafx.fxml \
    --module-path "$MODULE_PATH" \
    -classpath "$CLASSPATH:target/classes" \
    -Dapple.awt.UIElement=true \
    -Djava.awt.headless=false \
    -Dprism.order=sw \
    appli.StartApplication
