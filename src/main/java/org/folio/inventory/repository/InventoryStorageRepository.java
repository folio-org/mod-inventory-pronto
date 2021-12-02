package org.folio.inventory.repository;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.sql.ResultSet;
import java.util.Optional;

import org.folio.inventory.domain.dto.Campus;
import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.domain.dto.HoldingsRecord;
import org.folio.inventory.domain.dto.Instance;
import org.folio.inventory.domain.dto.Institution;
import org.folio.inventory.domain.dto.Item;
import org.folio.inventory.domain.dto.Library;
import org.folio.inventory.domain.dto.LoanType;
import org.folio.inventory.domain.dto.Location;
import org.folio.inventory.domain.dto.MaterialType;
import org.folio.inventory.domain.dto.ServicePoint;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Repository
@Log4j2
public class InventoryStorageRepository {
  // TODO: get tenant from headers
  private static final String SCHEMA_NAME = "diku_mod_inventory_storage";
  private static final String SELECT_BY_PROPERTY_QUERY_TEMPLATE = "SELECT jsonb FROM %s.%s where %s = '%s'";

  @Autowired
  private final JdbcTemplate jdbcTemplate;

  public Optional<CirculationItem> getByBarcode(String barcode) {
    long start = System.currentTimeMillis();

//    testPerformance();

    return Optional.of(new ItemContext(barcode))
      .flatMap(this::fetchItem)
      .flatMap(this::fetchHoldingsRecord)
      .flatMap(this::fetchInstance)
      // TODO: copy logic for determining loan type ID
      .flatMap(this::fetchLoanType)
      .flatMap(this::fetchMaterialType)
      .flatMap(this::fetchLocation)
      .flatMap(this::fetchInstitution)
      .flatMap(this::fetchCampus)
      .flatMap(this::fetchLibrary)
      .flatMap(this::fetchServicePoint)
      .map(context -> {
        log.info("Fetching done in " + (System.currentTimeMillis() - start));
        return context;
      })
      .map(this::buildCirculationItem);
  }

  private Optional<ItemContext> fetchItem(ItemContext context) {
    return fetchByProperty("item", "jsonb->>'barcode'", context.getItemBarcode(), Item.class)
      .map(context::withItem);
  }

  private Optional<ItemContext> fetchHoldingsRecord(ItemContext context) {
    return fetchById("holdings_record", context.getItem().getHoldingsRecordId(), HoldingsRecord.class)
      .map(context::withHoldingsRecord);
  }

  private Optional<ItemContext> fetchInstance(ItemContext context) {
    return fetchById("instance", context.getHoldingsRecord().getInstanceId(), Instance.class)
      .map(context::withInstance);
  }

  private Optional<ItemContext> fetchLoanType(ItemContext context) {
    return fetchById("loan_type", determineLoanTypeId(context.getItem()), LoanType.class)
      .map(context::withLoanType);
  }

  private Optional<ItemContext> fetchMaterialType(ItemContext context) {
    return fetchById("material_type", context.getItem().getMaterialTypeId(), MaterialType.class)
      .map(context::withMaterialType);
  }

  private Optional<ItemContext> fetchLocation(ItemContext context) {
    return fetchById("location", context.getItem().getEffectiveLocationId(), Location.class)
      .map(context::withLocation);
  }

  private Optional<ItemContext> fetchInstitution(ItemContext context) {
    return fetchById("locinstitution", context.getLocation().getInstitutionId(), Institution.class)
      .map(context::withInstitution);
  }

  private Optional<ItemContext> fetchCampus(ItemContext context) {
    return fetchById("loccampus", context.getLocation().getCampusId(), Campus.class)
      .map(context::withCampus);
  }

  private Optional<ItemContext> fetchLibrary(ItemContext context) {
    return fetchById("loclibrary", context.getLocation().getLibraryId(), Library.class)
      .map(context::withLibrary);
  }

  private Optional<ItemContext> fetchServicePoint(ItemContext context) {
    return fetchById("service_point", context.getLocation().getPrimaryServicePoint(), ServicePoint.class)
      .map(context::withServicePoint);
  }

  private <T> Optional<T> fetchById(String table, String id, Class<T> objectType) {
    return fetchByProperty(table, "id", id, objectType);
  }

  private <T> Optional<T> fetchByProperty(String table, String property, String value, Class<T> objectType) {
    String query = format(SELECT_BY_PROPERTY_QUERY_TEMPLATE, SCHEMA_NAME, table, property, value);
    return fetchByQuery(query, objectType);
  }

  private <T> Optional<T> fetchByQuery(String query, Class<T> objectType) {
    log.info(query);
    return ofNullable(jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapRow(rs, objectType)));
  }

  private <T> T mapRow(ResultSet resultSet, Class<T> objectType) {
    T result = null;
    try {
      String json = resultSet.getString(1);
      json = removeMetadata(json);

      result = new ObjectMapper().readValue(json, objectType);
    } catch (Exception e) {
      log.error("Failed to decode " + objectType.getSimpleName(), e);
    }

    return result;
  }

//  private CirculationItem buildCirculationItem(ItemContext context) {
//    CirculationItem circulationItem = new CirculationItem()
//      .materialType(context.getMaterialType())
//      .loanType(context.getLoanType())
//      .location(context.getLocation())
//      .permanentLocation(context.getPermanentLocation());
//
//    final Item item = context.getItem();
//    if (item != null) {
//      circulationItem = circulationItem
//        .id(item.getId())
//        .barcode(item.getBarcode())
//        .status(item.getStatus())
//        .callNumber(item.getEffectiveCallNumberComponents())
//        .chronology(item.getChronology())
//        .copyNumber(item.getCopyNumber())
//        .volume(item.getVolume())
//        .enumeration(item.getEnumeration())
//        .yearCaption(item.getYearCaption())
//        .numberOfPieces(item.getNumberOfPieces())
//        .descriptionOfPieces(item.getDescriptionOfPieces())
//        .lastCheckIn(item.getLastCheckIn());
//    }
//
//    final Instance instance = context.getInstance();
//    if (instance != null) {
//      circulationItem = circulationItem
//        .title(instance.getTitle());
//    }
//
//    return circulationItem;
//  }

  private CirculationItem buildCirculationItem(ItemContext context) {
    return new CirculationItem()
      .item(context.getItem())
      .instance(context.getInstance())
      .holdingsRecord(context.getHoldingsRecord())
      .materialType(context.getMaterialType())
      .loanType(context.getLoanType())
      .location(context.getLocation())
      .primaryServicePoint(context.getServicePoint())
      .institution(context.getInstitution())
      .campus(context.getCampus())
      .library(context.getLibrary());
  }

  private static String removeMetadata(String json) {
    JSONObject jsonObject = new JSONObject(json);
    jsonObject.remove("metadata");

    return jsonObject.toString();
  }

  private static String determineLoanTypeId(Item item) {
    return defaultIfBlank(item.getTemporaryLoanTypeId(), item.getPermanentLoanTypeId());
  }

  private void testPerformance() {
    long start = System.currentTimeMillis();

    ResultSet result = jdbcTemplate.query(
      "SELECT jsonb FROM diku_mod_inventory_storage.item where id = '63ba67a9-a07d-4ac3-8a8d-f89af0951b8a'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.holdings_record where id = 'e6d7e91a-4dbc-4a70-9b38-e000d2fbdc79'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.instance where id = 'cf23adf0-61ba-4887-bf82-956c4aae2260'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.loan_type where id = '2b94c631-fca9-4892-a730-03ee529ffe27'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.material_type where id = '1a54b431-2e4f-452d-9cae-9cee66c9a892'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.location where id = 'fcd64ce1-6995-48f0-840e-89ffa2288371'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.locinstitution where id = '40ee00ca-a518-4b49-be01-0638d0a4ac57'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.loccampus where id = '62cf76b7-cca5-4d33-9217-edf42ce1a848'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.loclibrary where id = '5d78803e-ca04-4b4a-aeae-2c63b924518b'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.location where id = 'fcd64ce1-6995-48f0-840e-89ffa2288371'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.locinstitution where id = '40ee00ca-a518-4b49-be01-0638d0a4ac57'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.loccampus where id = '62cf76b7-cca5-4d33-9217-edf42ce1a848'\n" +
        "UNION\n" +
        "SELECT jsonb FROM diku_mod_inventory_storage.loclibrary where id = '5d78803e-ca04-4b4a-aeae-2c63b924518b';",
      resultSet -> resultSet);

    log.info("Fetching done in " + (System.currentTimeMillis() - start));
  }

  @RequiredArgsConstructor
  @AllArgsConstructor
  @Getter
  @With
  private static class ItemContext {
    private final String itemBarcode;
    private Item item;
    private HoldingsRecord holdingsRecord;
    private Instance instance;
    private MaterialType materialType;
    private LoanType loanType;
    private Location location;
    private Institution institution;
    private Campus campus;
    private Library library;
    private ServicePoint servicePoint;
  }
}
