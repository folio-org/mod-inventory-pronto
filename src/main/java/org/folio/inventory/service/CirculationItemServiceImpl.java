package org.folio.inventory.service;

import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.repository.InventoryStorageRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CirculationItemServiceImpl implements CirculationItemService {

  @Autowired
  private final InventoryStorageRepositoryImpl circulationItemRepository;

  @Override
  public CirculationItem getCirculationItem(String barcode) {
    return circulationItemRepository.getCirculationItemByBarcode(barcode);
  }
}
