# Variables
JAVAC = javac
JAVA = java
JAVAFX_PATH = /usr/share/openjfx/lib
MAIN = DisplayApp
BIS = Client

# Cibles
all:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(MAIN)
	rm *.class

run:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(MAIN)

runb:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(BIS)

clean:
	rm -f *.class
