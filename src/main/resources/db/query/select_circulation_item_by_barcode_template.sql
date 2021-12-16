select item.id as id,
item.jsonb->>'barcode' as barcode, 
item.jsonb->'status'->>'name' as status, 
item.holdingsRecordId as holdingsRecordId,
hr.instanceId as instanceId,
instance.jsonb->>'title' as title,
array(select jsonb_array_elements(instance.jsonb->'contributors')->>'name') as contributors, 
item.jsonb->'effectiveCallNumberComponents'->>'callNumber' as callNumber,
item.jsonb->'effectiveCallNumberComponents'->>'prefix' as callNumberPrefix,
item.jsonb->'effectiveCallNumberComponents'->>'suffix' as callNumberSuffix,
item.jsonb->>'enumeration' as enumeration,
item.jsonb->>'volume' as volume,
item.jsonb->>'chronology' as chronology,
array(select jsonb_array_elements_text(item.jsonb->'yearCaption')) as yearCaption,
item.jsonb->>'copyNumber' as itemLevelCopyNumber,
hr.jsonb->>'copyNumber' as holdingsLevelCopyNumber,
item.permanentLoanTypeId as permanentLoanTypeId,
item.temporaryLoanTypeId as temporaryLoanTypeId,
item.materialTypeId as materialTypeId,
material_type.jsonb->>'name' as materialTypeName,
item.effectiveLocationId as effectiveLocationId,
location.institutionId as institutionId,
location.campusId as campusId,
location.libraryId as libraryId,
location.jsonb->>'name' as effectiveLocationName
from %1$s.item
left join %1$s.holdings_record hr on hr.id = item.holdingsRecordId
left join %1$s.instance on instance.id = hr.instanceId
left join %1$s.material_type on material_type.id = item.materialTypeId
left join %1$s.location on location.id = item.effectiveLocationId
where lower(item.jsonb->>'barcode') = lower('%2$s');