# Variables
JAVAC = javac
JAVA = java
JAVAFX_PATH = /usr/share/openjfx/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml
DPRISM = -Dprism.order
MAIN = UI

# Cibles
all:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI
	rm -f *.class

runui:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI
	rm -f *.class

run:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI
	rm -f *.class

clean:
	rm -f *.class