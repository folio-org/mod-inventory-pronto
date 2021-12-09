select item.id as id,  
item.jsonb->>'barcode' as barcode, 
item.jsonb->'status'->>'name' as status, 
item.jsonb->>'holdingsRecordId' as holdingsRecordId,  
hr.jsonb->>'instanceId' as instanceId,  
instance.jsonb->>'title' as title,  
array(select jsonb_array_elements(instance.jsonb->'contributors')->>'name') as contributors, 
item.jsonb->'effectiveCallNumberComponents'->>'callNumber' as callNumber,  
item.jsonb->'effectiveCallNumberComponents'->>'prefix' as callNumberPrefix,  
item.jsonb->'effectiveCallNumberComponents'->>'suffix' as callNumberSuffix,  
item.jsonb->>'enumeration' as enumeration,  
item.jsonb->>'volume' as volume,  
item.jsonb->>'chronology' as chronology,  
item.jsonb->>'numberOfPieces' as numberOfPieces,  
item.jsonb->>'descriptionOfPieces' as descriptionOfPieces,  
array(select jsonb_array_elements_text(item.jsonb->'yearCaption')) as yearCaption,  
case when item.jsonb->'copyNumber' is not null  
then item.jsonb->>'copyNumber'  
else hr.jsonb->>'copyNumber'  
end as copyNumber, 
item.jsonb->>'permanentLoanTypeId' as permanentLoanTypeId,  
item.jsonb->>'temporaryLoanTypeId' as temporaryLoanTypeId,  
item.jsonb->>'materialTypeId' as materialTypeId, 
material_type.jsonb->>'name' as materialTypeName,  
item.jsonb->>'effectiveLocationId' as locationId,  
location.jsonb->>'institutionId' as institutionId,  
location.jsonb->>'campusId' as campusId, 
location.jsonb->>'libraryId' as libraryId, 
location.jsonb->>'name' as locationName 
from %1$s.item  
inner join %1$s.holdings_record hr on hr.id = item.holdingsRecordId  
inner join %1$s.instance on instance.id = hr.instanceId  
inner join %1$s.material_type on material_type.id = item.materialTypeId  
inner join %1$s.location on location.id = item.effectiveLocationId  
where lower(item.jsonb->>'barcode') = lower('%2$s');