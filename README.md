In dieser Hausaufgabe implementieren Sie ein paar Methoden, um den Inhalt einer einfachen MIDI-Datei zu erstellen. Da wir uns Dateioperationen erst später anschauen, stelle ich eine Hilfsklasse bereit, mit der Sie den Code testen und abspielbare MIDI-Dateien erstellen können.

# Allgemeines Vorgehen

Bitte beachten Sie, dass die gradle-Tests diesmal gleich die ganze Aufgabe umfassen. Wenn Sie also irgendwo einen Programmierfehler haben, funktioniert keiner der Tests mehr und es gibt 0 Punkte. Das soll auch so sein - Fehler im Programm (also fehlschlagende Tests) sind OK, dann gibt es halt nicht die volle Punktzahl. Aber ein komplett nicht lauffähiges Programm ist nicht OK, dann gibt es 0 Punkte. Implementieren Sie also am besten direkt am Anfang nur "Skelett-Methoden" für alle Aufgaben, so dass die Tests zumindest laufen - auch wenn sie fehlschlagen. Danach können Sie sich der Reihe nach an die Ausimplementierung der Methoden machen.

Ein Beispiel: Wenn Sie eine Klasse `MathsStuff` implementieren sollen mit:

* einer Methode `add(int a, int b)` implementieren sollen, die a und b addiert und das Ergebnis als `int` zurückgibt
* einer Methode `isEven(int x)`, die als `boolean` zurückgibt, ob die Zahl x gerade ist oder nicht

sollten Sie zunächst als Skelett implementieren:

```java
public class MathsStuff {
    public static int add(int a, int b) {
        return 0;
    }
    
    public static boolean isEven(int x) {
        return true;
    }
}
```

Das ist schon mal eine korrekte Klasse, die auch kompiliert und getestet werden kann (da alle Methoden drin sind und einen Wert mit dem korrekten Datentyp zurückgeben - auch wenn es vielleicht nicht der richtige Wert ist). Dann können Sie sich daran machen, die Logik zu implementieren.

Die Tests finden Sie ab jetzt unter Gradle->Tasks->verification->test bzw., falls Sie gradle installiert haben:

```bash
gradle test
```

# Aufgabe

Implementieren Sie die folgenden Dinge in der Klasse `MIDItools`:

## Aufgabe 1

Implementieren Sie eine Methode `getNote(char note, int octave, boolean sharp)`, die als ein `byte` zurückgibt, welches die von dem übergebenen Zeichen repräsentierte MIDI-Note zurückgibt. Die Zuordnungstabelle finden Sie auf [dieser Seite](http://www.music.mcgill.ca/~ich/classes/mumt306/StandardMIDIfileformat.html#BMA1_) unter "Appendix 1.3 - Table of MIDI Note Numbers".

Zur Erklärung: Noten sind in wiederkehrenden Oktaven angeordnet. Jede Oktave enthält in aufsteigender Höhe die Noten C, D, E, F, G, A und B. Zur Verwirrung des Gegners sind die Abstände in Halbnoten unterteilt, wobei zwischen manchen Noten zwei und zwischen manchen Noten eine Halbnote Abstand liegt. Zwischen C und D beispielsweise beträgt der Abstand 2 Halbnoten, zwischen E und F beträgt der Abstand nur eine Halbnote.

Zusätzlich kann zu einer Note angegeben werden, ob sie "sharp" ist. Das bedeutet, dass der Ton eine Halbnote höher ist, als die eigentliche Note. Dies wird durch ein `#` hinter der Note repräsentiert. D# ist also einen Halbton höher als D. Da der Abstand zwischen E und F nur ein Halbton ist, wäre E# der selbe Ton wie F.

Der MIDI-Wert für eine Note repräsentiert nun die Anzahl an Halbtönen, die die Note höher ist, als das tiefste in MIDI darstellbare C (Oktave -1). Ein D# der Oktave 2 wre also laut Tabelle 38, denn:

* Ein D ist zwei Halbtöne über einem C
* Damit ist ein D# drei Halbtöne über einem C
* Die 2. Oktave fängt 36 Halbtöne über der -1. Oktave an

Der Standard-Wert, der zurückgegeben werden soll, ist 0. Dies kann passieren, wenn:

* Der übergebene Notenbuchstabe keine gültige Note ist (z.B. X)
* Das resultierende MIDI-Signal höher wäre, als das maximale zulässige Signal (also über 127)

## Aufgabe 2

Implementieren Sie eine Methode `getHeader(byte speed)`, die ein `byte[]` mit dem MIDI-Header zurückgibt. Dieser soll am Anfang die folgenden Bytes (Hexadezimal-Werte) enthalten:

4D 54 68 64 00 00 00 06 00 00 00 01 00

Dahinter soll das letzte Byte die übergebene Geschwindigkeit sein (`speed`).

Bedenken Sie, dass Sie Hexadezimalzahlen nicht von Hand in Dezimalzahlen umrechnen müssen:

```java
int x = 68; // Das ist der Wert 68 in Dezimal
int y = 0x68; // Durch das 0x davor wird signalisiert, dass der Wert in Hexadezimal angegeben wurde - es ist also der Wert 104 im Dezimalsystem.
```

## Aufgabe 3

Implementieren Sie eine Methode `getNoteEvent(byte delay, boolean noteOn, byte note, byte velocity)`, die als `byte[]` ein MIDI-Event zum An- oder Ausschalten einer Note **im Channel 0** zurückgibt. Ein solches Event ist immer 4 Byte lang und enthält der Reihe nach die folgenden Bytes:

* Die zur vorherigen Note vergangene Zeit (delay)
* Binär 0b1000nnnn für Note aus bzw. binär 0b1001nnnn für Note an. Dabei bezeichnen die letzten 4 bit (hier als n dargestellt) die Nummer des Channels.
* Den MIDI-Code der Note (das Argument `byte note` - das ist hier schon der MIDI-Code, welchen Sie vorher in `getNote` berechnet haben)
* Die Geschwindigkeit, mit der das Event stattfindet (velocity)

## Aufgabe 4

Implementieren Sie eine Methode `addNoteToTrack(byte[] trackdata, byte[] noteEvent)`, die existierende Track-Daten als `trackdata` sowie ein neues Noten-Event als `noteEvent` bekommt und ein neues `byte[]` zurückgibt, welches sowohl die existierenden Daten als auch dahinter angehängt die Daten aus dem Noten-Event enthält. Bedenken Sie, dass Sie die Länge eines Arrays nicht verändern können - Sie benötigen für das Ergebnis ein neues Array!

## Aufgabe 5

Implementieren Sie eine Methode `getTrack(byte instrument, byte[] trackdata)`, welche einen Instrumentcode und Trackdaten erhält und ein `byte[]` mit dem kompletten Track zurückgibt. Der Track ist wie folgt aufgebaut (alle Werte gebe ich hier hexadezimal an):

* 4D 54 72 6B 00 00 00: Header
* Ein Byte Länge (die Anzahl der Byte, die nach dem Header und diesem Längen-Byte noch kommen, ohne die letzten 3 Byte die das Trackende markieren)
* 00 FF 58 04 04 02 18 08 00 FF 51 03 07 A1 20: Geschwindigkeitsinformationen, darum kümmern wir uns zunächst nicht - Sie können das einfach so direkt übernehmen
* 00 C0: Setzen des Instruments auf Channel 0.
* Ein Byte Instrumentcode (das Instrument, welches verwendet werden soll)
* Die Track-Daten (in unserem Fall lauter Noten-Events)
* FF 2F 00: Ende des Tracks (werden bei der Längenangabe im Header nicht mit berücksichtigt)

Hinweis: Sie müssen hier immer wieder Byte-Arrays miteinenader verketten. Eventuell haben Sie schon eine Methode in dieser Hausaufgabe implementiert, die Sie dafür wiederverwenden können?