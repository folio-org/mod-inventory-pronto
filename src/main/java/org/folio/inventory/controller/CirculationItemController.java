package org.folio.inventory.controller;

import org.folio.inventory.domain.dto.CirculationItem;
import org.folio.inventory.domain.dto.CirculationItems;
import org.folio.inventory.rest.resource.CirculationItemsApi;
import org.folio.inventory.service.CirculationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/")
public class CirculationItemController implements CirculationItemsApi {

  @Autowired
  private final CirculationItemService circulationItemService;

  @Override
  public ResponseEntity<CirculationItem> getCirculationItemByBarcode(String barcode) {
    return circulationItemService.getCirculationItem(barcode)
      .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<CirculationItems> getCirculationItems(String barcode) {
    return CirculationItemsApi.super.getCirculationItems(barcode);
  }
}
