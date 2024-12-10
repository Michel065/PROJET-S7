# Variables
JAVAC = javac
JAVA = java
JAVAFX_PATH = /usr/share/openjfx/lib
MAIN = DisplayApp

# Cibles
all:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml $(MAIN)

run: all
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml $(MAIN)

clean:
	rm -f *.class
