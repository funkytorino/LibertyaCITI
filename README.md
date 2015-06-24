# LibertyaCITI
Plugin de Libertya para generar archivos TXT según RG 3685 de AFIP (Régimen de información compras y ventas).

La versión base de Libertya en la que se hizo el desarrollo es 13.01.
Está implementado como un plugin que extiende las clases c_invoice y c_tax. Agrega, además, una columna a cada tabla.
Se deben clasificar los impuestos configurados en el sistema en alguna de las categorías en las que AFIP exige agrupar los montos involucrados
en cada comprobante. Esto se hace una sola vez al instalar el plugin.

También se deben actualizar las facturas que se hayan cargado antes de la instalación del plugin a fin de incorporar el código del tipo de comprobante
que se trate. A tal fin se provee un proceso que hace la actualización. Todos los c_invoices que se creen después de instalado el plugin incorporarán
el código de forma automática.

Juan Manuel Martínez - 2015
jmmartinezsf@gmail.com
