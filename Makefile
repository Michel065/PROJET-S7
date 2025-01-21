# Variables
JAVAC = javac
JAVA = java
JAVAFX_PATH = /usr/share/openjfx/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml
DPRISM = -Dprism.order

# Cibles
all:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java

host:
	$(JAVA) Host

client:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw Client

ui:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI

ui:
	$(JAVA) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI

gen:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw generateur $(ARGS)

clean:
	del /Q *.class
