package mensaje;


public enum TipoMensaje {
	MENSAJE_CONEXION,
	MENSAJE_CONFIRMACION_CONEXION,
	MENSAJE_RECHAZO_CONEXION,
	MENSAJE_LISTA_USUARIOS,
	MENSAJE_CONFIRMACION_LISTA_USUARIOS,
    MENSAJE_PEDIR_FICHERO,
    MENSAJE_EMITIR_FICHERO,
    MENSAJE_PREPARADO_CLIENTE_SERVIDOR,
    MENSAJE_PREPARADO_SERVIDOR_CLIENTE,
	MENSAJE_CERRAR_CONEXION,
	MENSAJE_CONFIRMACION_CERRAR_CONEXION,
	MENSAJE_FICHERO_NO_ENCONTRADO, // enviado de servidor a un cliente que ha pedido un fichero que no está compartido
	MENSAJE_TIPO_INESPERADO  //enviado en cualquiera de las dos direcciones cuando no se recibe ninguno de los tipos de mensaje esperados
}
