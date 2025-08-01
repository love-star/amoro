/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.amoro.spark;

import org.apache.amoro.TableFormat;
import org.apache.spark.sql.catalyst.analysis.NoSuchFunctionException;
import org.apache.spark.sql.catalyst.analysis.NoSuchTableException;
import org.apache.spark.sql.connector.catalog.FunctionCatalog;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableCatalog;
import org.apache.spark.sql.connector.catalog.functions.UnboundFunction;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

/**
 * For TableCatalog in spark 3.3 is different with spark 3.2。 so we define it seperately 1、 we
 * support the grammar feature of time travel. 2、 support FunctionCatalog
 */
public class SparkUnifiedSessionCatalog<
        T extends TableCatalog & SupportsNamespaces & FunctionCatalog>
    extends SparkUnifiedSessionCatalogBase<T> {

  @Override
  protected SparkUnifiedCatalogBase createUnifiedCatalog(
      String name, CaseInsensitiveStringMap options) {
    return new SparkUnifiedCatalog();
  }

  @Override
  public Table loadTable(Identifier ident, String version) throws NoSuchTableException {
    try {
      TableCatalog catalog = getTargetCatalog();
      SparkUnifiedCatalogBase unifiedCatalog = (SparkUnifiedCatalogBase) catalog;
      return unifiedCatalog.tableCatalog(TableFormat.ICEBERG).loadTable(ident, version);
    } catch (org.apache.iceberg.exceptions.NoSuchTableException e) {
      return getSessionCatalog().loadTable(ident, version);
    }
  }

  @Override
  public Table loadTable(Identifier ident, long timestamp) throws NoSuchTableException {
    try {
      TableCatalog catalog = getTargetCatalog();
      SparkUnifiedCatalogBase unifiedCatalog = (SparkUnifiedCatalogBase) catalog;
      return unifiedCatalog.tableCatalog(TableFormat.ICEBERG).loadTable(ident, timestamp);
    } catch (org.apache.iceberg.exceptions.NoSuchTableException e) {
      return getSessionCatalog().loadTable(ident, timestamp);
    }
  }

  @Override
  public UnboundFunction loadFunction(Identifier ident) throws NoSuchFunctionException {
    try {
      return super.loadFunction(ident);
    } catch (NoSuchFunctionException e) {
      return getSessionCatalog().loadFunction(ident);
    }
  }

  private static boolean isSystemNamespace(String[] namespace) {
    return namespace.length == 1 && namespace[0].equalsIgnoreCase("system");
  }
}
