insert into p_daas_data.score_rc_2
(
tipodocumento,
numerodocumento,
tipo_accion,
portabilidad,
ident_serv_conte,
score,
mensaje_score,
id_consulta_scoring,
limite_credito,
capacidad_financiamiento,
cantidad_lineas,
restricciones,
accion
)
values
(
:#documentType,
:#documentNumber,
:#actionType,
:#portability,
:#contextServiceIdentifier,
:#score,
:#scoreMessage,
:#queryIdScoring,
:#creditLimit,
:#financingCapacity,
:#numberOfLines,
:#restrictions,
:#action
)