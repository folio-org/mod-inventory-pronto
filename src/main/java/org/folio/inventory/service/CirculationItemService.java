package org.folio.inventory.service;

import org.folio.inventory.domain.dto.CirculationItem;

public interface CirculationItemService {

  CirculationItem getCirculationItem(String barcode);
}
