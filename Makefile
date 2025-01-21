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
	rm -f *.class

host:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) Carte.java CoordFloat.java CoordFloatAtomic.java CoordInt.java Host.java LightPlayer.java LightProjectile.java LightRond.java ListeAtomicCoord.java ListePartageThread.java Main.java MatriceCarre.java Obstacle.java Player.java Projectile.java Rond.java ThreadHostConnexion.java ThreadHostGestionPlayer.java ThreadHostToClient.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw Host
	rm -f *.class

client:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw 
	rm -f *.class

ui:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw UI
	rm -f *.class

gen:
	$(JAVAC) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) *.java
	$(JAVA) --module-path $(JAVAFX_PATH) --add-modules $(JAVAFX_MODULES) $(DPRISM)=sw generateur $(ARGS)
	rm -f *.class

clean:
	rm -f *.class