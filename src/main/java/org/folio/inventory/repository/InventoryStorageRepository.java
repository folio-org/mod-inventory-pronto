package org.folio.inventory.repository;

import org.folio.inventory.domain.dto.CirculationItem;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStorageRepository {

  CirculationItem getCirculationItemByBarcode(String barcode);

}
