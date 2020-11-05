/*-
 * #%L
 * WollMux
 * %%
 * Copyright (C) 2005 - 2020 Landeshauptstadt München
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */
package de.muenchen.allg.itd51.wollmux.former.insertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.muenchen.allg.afid.UnoHelperException;
import de.muenchen.allg.itd51.wollmux.config.ConfigThingy;
import de.muenchen.allg.itd51.wollmux.former.FormularMax4kController;
import de.muenchen.allg.itd51.wollmux.former.function.FunctionSelection;
import de.muenchen.allg.itd51.wollmux.former.function.FunctionSelectionAccess;
import de.muenchen.allg.itd51.wollmux.former.function.ParamValue;

public abstract class InsertionModel
{
  protected static final String FM4000AUTO_GENERATED_TRAFO =
    "FM4000AutoGeneratedTrafo";

  /**
   * Die TRAFO für diese Einfügung.
   */
  protected FunctionSelection trafo;

  /**
   * Der FormularMax4000 zu dem dieses Model gehört.
   */
  protected FormularMax4kController formularMax4000;

  /**
   * Die {@link ModelChangeListener}, die über Änderungen dieses Models informiert
   * werden wollen.
   */
  private List<ModelChangeListener> listeners = new ArrayList<>(1);

  /**
   * Entfernt die Einfügestelle komplett aus dem Dokument, d,h, sowohl das eventuell vorhandene
   * WollMux-Bookmark als auch den Feldbefehl.
   * 
   * @throws UnoHelperException
   *           Can't get the view cursor.
   */
  public abstract void removeFromDocument() throws UnoHelperException;

  /**
   * Liefert den "Namen" der Einfügestelle. Dies kann z.B. der Name des Bookmarks sein, das die
   * Einfügestelle umschließt.
   */
  public abstract String getName();

  /**
   * Setzt den ViewCursor auf die Einfügestelle.
   * 
   * @throws UnoHelperException
   *           Can't get the view cursor.
   */
  public abstract void selectWithViewCursor() throws UnoHelperException;

  /**
   * Lässt dieses {@link InsertionModel} sein zugehöriges Bookmark bzw, sonstige
   * Daten updaten, die die TRAFO betreffen.
   *
   * @param mapFunctionNameToConfigThingy
   *          bildet einen Funktionsnamen auf ein ConfigThingy ab, dessen Wurzel der
   *          Funktionsname ist und dessen Inhalt eine Funktionsdefinition. Wenn
   *          diese Einfügung mit einer TRAFO versehen ist, wird für das
   *          Aktualisieren des Bookmarks ein Funktionsname generiert, der noch nicht
   *          in dieser Map vorkommt und ein Mapping für diese Funktion wird in die
   *          Map eingefügt.
   * @return false, wenn ein update nicht möglich ist. In dem Fall wird das
   *         entsprechende Bookmark entfernt und dieses InsertionModel sollte nicht
   *         weiter verwendet werden.
   */
  public abstract boolean updateDocument(
      Map<String, ConfigThingy> mapFunctionNameToConfigThingy);

  /**
   * Liefert den FormularMax4000 zu dem dieses Model gehört.
   */
  public FormularMax4kController getFormularMax4000()
  {
    return formularMax4000;
  }

  /**
   * Setzt die TRAFO auf trafo, wobei das Objekt direkt übernommen (nicht kopiert)
   * wird. ACHTUNG! Derzeit verständigt diese Funktion keine ModelChangeListener,
   * d.h. Änderungen an diesem Attribut werden nicht im FM4000 propagiert. Diese
   * Funktion kann also derzeit nur sinnvoll auf einem frischen InsertionModel
   * verwendet werden, bevor es zur insertionModelList hinzugefügt wird.
   */
  public void setTrafo(FunctionSelection trafo)
  {
    this.trafo = trafo;
    formularMax4000.documentNeedsUpdating();
  }

  /**
   * Liefert true gdw dieses InsertionModel eine TRAFO gesetzt hat.
   */
  public boolean hasTrafo()
  {
    return !trafo.isNone();
  }

  /**
   * Liefert ein Interface zum Zugriff auf die TRAFO dieses Objekts.
   */
  public FunctionSelectionAccess getTrafoAccess()
  {
    return new MyTrafoAccess();
  }

  /**
   * listener wird über Änderungen des Models informiert.
   */
  public void addListener(ModelChangeListener listener)
  {
    if (!listeners.contains(listener)) listeners.add(listener);
  }

  /**
   * Benachrichtigt alle auf diesem Model registrierten Listener, dass das Model aus
   * seinem Container entfernt wurde. ACHTUNG! Darf nur von einem entsprechenden
   * Container aufgerufen werden, der das Model enthält.
   */
  public void hasBeenRemoved()
  {
    for (ModelChangeListener listener : listeners)
    {
      listener.modelRemoved(this);
    }
    formularMax4000.documentNeedsUpdating();
  }

  /**
   * Ruft für jeden auf diesem Model registrierten {@link ModelChangeListener} die
   * Methode
   * {@link ModelChangeListener#attributeChanged(InsertionModel, int, Object)} auf.
   */
  protected void notifyListeners(int attributeId, Object newValue)
  {
    for (ModelChangeListener listener : listeners)
    {
      listener.attributeChanged(this, attributeId, newValue);
    }
    formularMax4000.documentNeedsUpdating();
  }

  /**
   * Diese Klasse leitet Zugriffe weiter an das Objekt {@link InsertionModel#trafo}.
   * Bei ändernden Zugriffen wird auch noch der FormularMax4000 benachrichtigt, dass
   * das Dokument geupdatet werden muss. Im Prinzip müsste korrekterweise ein
   * ändernder Zugriff auf trafo auch einen Event an die ModelChangeListener
   * schicken. Allerdings ist dies derzeit nicht implementiert, weil es derzeit genau
   * eine View gibt für die Trafo, so dass konkurrierende Änderungen gar nicht
   * möglich sind.
   *
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  private class MyTrafoAccess implements FunctionSelectionAccess
  {
    @Override
    public boolean isReference()
    {
      return trafo.isReference();
    }

    @Override
    public boolean isExpert()
    {
      return trafo.isExpert();
    }

    @Override
    public boolean isNone()
    {
      return trafo.isNone();
    }

    @Override
    public String getFunctionName()
    {
      return trafo.getFunctionName();
    }

    @Override
    public ConfigThingy getExpertFunction()
    {
      return trafo.getExpertFunction();
    }

    @Override
    public void setParameterValues(Map<String, ParamValue> mapNameToParamValue)
    {
      trafo.setParameterValues(mapNameToParamValue);
      formularMax4000.documentNeedsUpdating();
    }

    @Override
    public void setFunction(String functionName, String[] paramNames)
    {
      trafo.setFunction(functionName, paramNames);
      formularMax4000.documentNeedsUpdating();
    }

    @Override
    public void setExpertFunction(ConfigThingy funConf)
    {
      trafo.setExpertFunction(funConf);
      formularMax4000.documentNeedsUpdating();
    }

    @Override
    public void setParameterValue(String paramName, ParamValue paramValue)
    {
      trafo.setParameterValue(paramName, paramValue);
      formularMax4000.documentNeedsUpdating();
    }

    @Override
    public String[] getParameterNames()
    {
      return trafo.getParameterNames();
    }

    @Override
    public boolean hasSpecifiedParameters()
    {
      return trafo.hasSpecifiedParameters();
    }

    @Override
    public ParamValue getParameterValue(String paramName)
    {
      return trafo.getParameterValue(paramName);
    }
  }

  /**
   * Interface für Listener, die über Änderungen eines Models informiert werden
   * wollen.
   *
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  public static interface ModelChangeListener
  {
    /**
     * Wird aufgerufen wenn ein Attribut des Models sich geändert hat.
     *
     * @param model
     *          das InsertionModel, das sich geändert hat.
     * @param attributeId
     *          eine der {@link InsertionModel4InsertXValue#ID_ATTR Attribut-ID-Konstanten}.
     * @param newValue
     *          der neue Wert des Attributs. Numerische Attribute werden als Integer übergeben.
     */
    public void attributeChanged(InsertionModel model, int attributeId,
        Object newValue);

    /**
     * Wird aufgerufen, wenn model aus seinem Container entfernt wird (und damit in
     * keiner View mehr angezeigt werden soll).
     */
    public void modelRemoved(InsertionModel model);
  }

}
