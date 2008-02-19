//TODO L.m()
/*
* Dateiname: TrafoDialogParameters.java
* Projekt  : WollMux
* Funktion : Speichert diverse Parameter f�r den Aufruf von TrafoDialogen.
* 
* Copyright: Landeshauptstadt M�nchen
*
* �nderungshistorie:
* Datum      | Wer | �nderungsgrund
* -------------------------------------------------------------------
* 01.02.2008 | BNK | Erstellung
* -------------------------------------------------------------------
*
* @author Matthias Benkmann (D-III-ITD 5.1)
* @version 1.0
* 
*/
package de.muenchen.allg.itd51.wollmux.dialog.trafo;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import de.muenchen.allg.itd51.parser.ConfigThingy;

/**
 * Speichert diverse Parameter f�r den Aufruf von TrafoDialogen.
 *
 * @author Matthias Benkmann (D-III-ITD 5.1)
 */
public class TrafoDialogParameters
{
  /**
   * Gibt an, ob der Inhalt dieses Objekts g�ltig ist. Dieser Parameter hat nur eine
   * Bedeutung, wenn ein Dialog die TrafoDialogParameters bei seiner Beendigung an 
   * den wartenden {@link #closeAction} �bergibt. Wurde der Dialog abgebrochen ohne dass
   * g�ltige �nderungen vorgenommen wurden, ist dieses Feld auf false gesetzt und
   * die Daten d�rfen nicht �bernommen werden.
   */
  public boolean isValid = true;
  
  /**
   * Die Beschreibung der Funktion, mit der der Dialog vorbelegt werden soll. Oberster Knoten
   * ist ein beliebiger Bezeichner (typischerweise der Funktionsname). Das ConfigThingy kann
   * also direkt aus einem Funktionen-Abschnitt eines Formulars �bernommen werden.
   */
  public ConfigThingy conf;
  
  /**
   * F�r Dialoge, die Feldnamen zur Auswahl stellen gibt diese Liste an, welche Namen
   * angeboten werden sollen.
   */
  public List fieldNames;
  
  /**
   * Falls nicht null, so wird bei Beendigung des Dialogs dieser Listener aufgerufen,
   * wobei als source der TrafoDialog �bergeben wird. 
   */
  public ActionListener closeAction;
  
  public String toString()
  {
    StringBuilder buffy = new StringBuilder();
    buffy.append("isValid: ");
    buffy.append(isValid);
    buffy.append('\n');
    buffy.append("conf: ");
    if (conf == null) 
      buffy.append("null");
    else
      buffy.append(conf.stringRepresentation());
    
    buffy.append("\nfieldNames: ");
    
    if (fieldNames == null)
      buffy.append("null");
    else 
    {
      Iterator iter = fieldNames.iterator();
      while (iter.hasNext())
      {
        buffy.append(iter.next().toString());
        if (iter.hasNext()) buffy.append(", ");
      }
    }
    return buffy.toString();
  }
}