# Variables
JAVAFX_PATH = C:\Program Files\Java\javafx-sdk-23.0.1\lib
#JAVAFX_PATH = /usr/share/openjfx/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml
DPRISM = -Dprism.order
JAVAC = javac
JAVA = java

# Cibles
all:
	$(JAVAC) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) *.java

host:
	$(JAVA) Host

client:
	$(JAVA) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw Client

ui:
	$(JAVA) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI

gen:
	$(JAVA) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw generateur $(ARGS)

clean:
	del /Q *.class
