Plugin de Libertya para generar archivos TXT según RG 3685 de AFIP (Régimen de información compras y ventas).

La versión base de Libertya en la que se hizo el desarrollo es 13.01. Está implementado como un plugin que extiende las clases c_invoice y c_tax. Agrega, además, una columna a cada tabla.
Se deben clasificar los impuestos configurados en el sistema en alguna de las categorías en las que AFIP exige agrupar los montos involucrados en cada comprobante. 
Esto se hace una sola vez al instalar el plugin, y a tal fin, se creó una lista de validación con los tipos de impuestos requeridos.
La configuración se hace desde la venta de "Categoría de impuestos".

Para la determinación del tipo de comprobante, se debe especificar al momento de cargar el documento qué tipo de comprobante se trata, según el listado que establece AFIP.
Esto se debe hacer solament para comprobantes de compras; los de ventas se determinan según lo configurado en la ventana "Tipo de documento", perfil "Configuración de la compañía".
El combo está configurado para poder modificarse aún después de completada la factura.

Los comprobantes anteriores a la fecha de instalación del plugin no se actualizan.

Los archivos install.xml, postinstall.xml y preinstal.sql contienen las modificaciones a las tablas y diccionario de datos necesarios, tal como los genera el exportador de de plugins 
de Libertya.

Juan Manuel Martínez - 2015 jmmartinezsf@gmail.com