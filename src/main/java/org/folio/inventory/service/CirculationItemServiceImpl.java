package org.folio.inventory.service;

import java.util.Optional;

import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.repository.InventoryStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CirculationItemServiceImpl implements CirculationItemService {

  @Autowired
  private final InventoryStorageRepository circulationItemRepository;

  @Override
  public Optional<CirculationItem> getCirculationItem(String barcode) {
    return circulationItemRepository.getByBarcode(barcode);
  }
}
