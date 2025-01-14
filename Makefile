# Variables
JAVAC = javac
JAVA = java
JAVAFX_PATH = /usr/share/openjfx/lib
MAIN = Host
BIS = Client

# Cibles
all:
	echo "Votre adresse IP locale est : $(ifconfig | grep -oP 'inet \K[\d.]+')"
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(MAIN)
	rm *.class
	clear

make:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml *.java

run:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(MAIN)
	clear

runb:
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml -Dprism.order=sw $(BIS)
	clear

clean:
	rm -f *.class
