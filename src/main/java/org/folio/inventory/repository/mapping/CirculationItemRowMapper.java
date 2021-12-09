package org.folio.inventory.repository.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.domain.dto.CirculationItem.StatusEnum;
import org.springframework.jdbc.core.RowMapper;

public class CirculationItemRowMapper implements RowMapper<CirculationItem> {

  @Override
  public CirculationItem mapRow(ResultSet resultSet, int rowNum) {
    try {
      return new CirculationItem()
        .id(resultSet.getString("id"))
        .barcode(resultSet.getString("barcode"))
        .status(StatusEnum.fromValue(resultSet.getString("status")))
        .holdingsRecordId(resultSet.getString("holdingsRecordId"))
        .instanceId(resultSet.getString("instanceId"))
        .title(resultSet.getString("title"))
        .contributors(arrayToList(resultSet, "contributors"))
        .callNumber(resultSet.getString("callNumber"))
        .callNumberPrefix(resultSet.getString("callNumberPrefix"))
        .callNumberSuffix(resultSet.getString("callNumberSuffix"))
        .enumeration(resultSet.getString("enumeration"))
        .volume(resultSet.getString("volume"))
        .chronology(resultSet.getString("chronology"))
        .copyNumber(resultSet.getString("copyNumber"))
        .numberOfPieces(resultSet.getString("numberOfPieces"))
        .descriptionOfPieces(resultSet.getString("descriptionOfPieces"))
        .yearCaption(new HashSet<>(arrayToList(resultSet, "yearCaption")))
        .permanentLoanTypeId(resultSet.getString("permanentLoanTypeId"))
        .temporaryLoanTypeId(resultSet.getString("temporaryLoanTypeId"))
        .materialTypeName(resultSet.getString("materialTypeName"))
        .materialTypeId(resultSet.getString("materialTypeId"))
        .locationId(resultSet.getString("locationId"))
        .institutionId(resultSet.getString("institutionId"))
        .campusId(resultSet.getString("campusId"))
        .libraryId(resultSet.getString("libraryId"))
        .locationName(resultSet.getString("locationName"));
    } catch (SQLException e) {
      throw new RuntimeException("Failed to build circulation item: " + e.getMessage());
    }
  }

  private static List<String> arrayToList(ResultSet resultSet, String arrayName) throws SQLException {
    return Arrays.asList((String[]) resultSet.getArray(arrayName).getArray());
  }

}
