package org.folio.inventory.service;

import java.util.Optional;

import org.folio.inventory.domain.dto.CirculationItem;

public interface CirculationItemService {

  Optional<CirculationItem> getCirculationItem(String barcode);
}
