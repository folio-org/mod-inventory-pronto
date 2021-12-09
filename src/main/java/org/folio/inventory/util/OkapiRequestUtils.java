package org.folio.inventory.util;

import static org.folio.spring.utils.RequestUtils.getHttpHeadersFromRequest;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OkapiRequestUtils {
  public static final String OKAPI_TENANT_HEADER = "x-okapi-tenant";

  public static String getTenantFromHeaders() {
    return getHttpHeadersFromRequest()
      .get(OKAPI_TENANT_HEADER)
      .stream()
      .findFirst()
      .orElse(null);
  }

}
