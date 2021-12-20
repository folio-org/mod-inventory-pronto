package org.folio.inventory.repository;

import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.repository.mapping.CirculationItemRowMapper;
import org.folio.inventory.util.FileUtils;
import org.folio.inventory.util.OkapiRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Repository
@Log4j2
public class InventoryStorageRepositoryImpl implements InventoryStorageRepository {
  private static final String SCHEMA_NAME_TEMPLATE = "%s_mod_inventory_storage";
  private static final String QUERY_TEMPLATE_FILE_PATH =
    "db/query/select_circulation_item_by_barcode_template.sql";

  private static String queryTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public CirculationItem getCirculationItemByBarcode(String barcode) {
    return fetchObject(buildQuery(barcode), new CirculationItemRowMapper());
  }

  private <T> T fetchObject(String query, RowMapper<T> rowMapper) {
    log.debug(query);
    return jdbcTemplate.queryForObject(query, rowMapper);
  }

  public static String buildQuery(String itemBarcode) {
    return String.format(getQueryTemplate(), getSchemaName(), itemBarcode);
  }

  private static String getSchemaName() {
    return String.format(SCHEMA_NAME_TEMPLATE, OkapiRequestUtils.getTenantFromHeaders());
  }

  private static String getQueryTemplate() {
    if (queryTemplate == null) {
      queryTemplate = FileUtils.readFile(QUERY_TEMPLATE_FILE_PATH);
    }

    return queryTemplate;
  }

}
