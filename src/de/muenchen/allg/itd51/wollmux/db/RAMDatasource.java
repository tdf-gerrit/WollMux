/*
 * Dateiname: RAMDatasource.java
 * Projekt  : WollMux
 * Funktion : Oberklasse f�r Datasources, die ihre Daten vollst�ndig
 *            im Speicher halten
 * 
 * Copyright (c) 2008 Landeshauptstadt M�nchen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the European Union Public Licence (EUPL),
 * version 1.0 (or any later version).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 *
 * You should have received a copy of the European Union Public Licence
 * along with this program. If not, see
 * http://ec.europa.eu/idabc/en/document/7330
 *
 * �nderungshistorie:
 * Datum      | Wer | �nderungsgrund
 * -------------------------------------------------------------------
 * 31.10.2005 | BNK | Erstellung
 * 03.11.2005 | BNK | besser kommentiert
 * 10.11.2005 | BNK | Refactoring: DatasetChecker & Co. in eigene Klassen
 * -------------------------------------------------------------------
 *
 * @author Matthias Benkmann (D-III-ITD 5.1)
 * @version 1.0
 * 
 */
package de.muenchen.allg.itd51.wollmux.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import de.muenchen.allg.itd51.wollmux.TimeoutException;
import de.muenchen.allg.itd51.wollmux.db.checker.DatasetChecker;

/**
 * Oberklasse f�r Datasources, die ihre Daten vollst�ndig im Speicher halten
 * 
 * @author Matthias Benkmann (D-III-ITD 5.1)
 */
public class RAMDatasource implements Datasource
{
  /**
   * Das Schema dieser Datenquelle.
   */
  private Set<String> schema;

  /**
   * Liste aller Datasets, die in dieser Datasource gespeichert sind.
   */
  private List<Dataset> data;

  /**
   * Der Name dieser Datenquelle.
   */
  private String name;

  /**
   * Erzeugt eine neue RAMDatasource mit Namen name. data und schema werden direkt
   * als Referenz eingebunden, nicht kopiert.
   * 
   * @param name
   *          der Name der Datenquelle
   * @param schema
   *          das Schema der Datenquelle
   * @param data
   *          die Datens�tze der Datenquelle
   */
  public RAMDatasource(String name, Set<String> schema, List<Dataset> data)
  {
    init(name, schema, data);
  }

  /**
   * F�hrt die Initialisierungsaktionen des Konstruktors mit den gleichen Parametern
   * aus. Diese Methode sollte von abgeleiteten Klassen verwendet werden, wenn sie
   * den Konstruktor ohne Argumente verwenden.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  protected void init(String name, Set<String> schema, List<Dataset> data)
  {
    this.schema = schema;
    this.data = data;
    this.name = name;
  }

  /**
   * Erzeugt eine uninitialisierte RAMDatasource. Eine abgeleitete Klasse, die diesen
   * Konstruktor verwendet sollte init() aufrufen, um die n�tigen Initialisierungen
   * zu erledigen.
   */
  protected RAMDatasource()
  {};

  public Set<String> getSchema()
  { // TESTED
    return new HashSet<String>(schema);
  }

  public QueryResults getDatasetsByKey(Collection<String> keys, long timeout)
      throws TimeoutException
  { // TESTED
    Vector<Dataset> res = new Vector<Dataset>();
    Iterator<Dataset> iter = data.iterator();
    while (iter.hasNext())
    {
      Dataset ds = iter.next();
      if (keys.contains(ds.getKey())) res.add(ds);
    }

    return new QueryResultsList(res);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.muenchen.allg.itd51.wollmux.db.Datasource#find(java.util.List, long)
   */
  public QueryResults find(List<QueryPart> query, long timeout)
      throws TimeoutException
  { // TESTED
    if (query.isEmpty()) return new QueryResultsList(new Vector<Dataset>(0));
    DatasetChecker checker = DatasetChecker.makeChecker(query);

    List<Dataset> results = new Vector<Dataset>();

    Iterator<Dataset> iter = data.iterator();
    while (iter.hasNext())
    {
      Dataset ds = iter.next();
      if (checker.matches(ds)) results.add(ds);
    }
    return new QueryResultsList(results);
  }

  public QueryResults getContents(long timeout) throws TimeoutException
  {
    return new QueryResultsList(new Vector<Dataset>(data));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.muenchen.allg.itd51.wollmux.db.Datasource#getName()
   */
  public String getName()
  {
    return name;
  }

}
