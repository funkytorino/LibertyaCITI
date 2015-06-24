/*
 * Proceso para generar los archivos exigidos por la RG3685: CITI compras y ventas
 * Diseño de los registros: http://www.afip.gob.ar/comprasyventas/
 * 
 * Autor: Juan Manuel Martínez - jmmartinezsf@gmail.com
 * Versión 0.1 - mayo de 2015
 * 
 */

package com.jmm.ExportCITIRG3685.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

public class ExportaCITI extends SvrProcess {

	/*
	 * Constructor de la clase
	 */
	
	public ExportaCITI() {
		super();

		alic.put(0.00, "3");
		alic.put(10.50, "4");
		alic.put(21.00, "5");
		alic.put(27.00, "6");
		cf = new Double(0);
		p_iva = new Double(0);
		p_iibb = new Double(0);
		p_nac = new Double(0);
		p_mun = new Double(0);
		ot = new Double(0);
		ex = new Double(0);
	}
  
    private Timestamp date_from;
    private Timestamp date_to;
    private String transaction;
    private String secuencia;
    private String directorio="";
    private  ResultSet rs = null;
    Map<Double,String> alic = new HashMap<Double, String>();
	private Double cf, p_iva, p_iibb, p_nac, p_mun, ot, ex; 
	
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equalsIgnoreCase( "FechaDesde" )) {
                date_from = ( Timestamp )para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "Fecha Hasta" )) {
                date_to = ( Timestamp )para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "transaction" )) {
            	transaction = (String)para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "Secuencia" )) {
	            secuencia = para[ i ].getParameter().toString();
            } else if( name.equalsIgnoreCase( "Directorio" )) {
            	directorio = (String)para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

	}
	
	protected String doIt() throws java.lang.Exception {
		
		File targetDir = new File (directorio);
		if (!targetDir.exists())
				targetDir.mkdir();
		
		String cabecera = directorio + "/REGINFO_CV_CABECERA.txt";
		String archivo_cbte = directorio + "/REGINFO_CV_" + (transaction.equalsIgnoreCase("V") ? "COMPRAS":"VENTAS")  + "_CBTE.txt";
		String archivo_alic = directorio + "/REGINFO_CV_" + (transaction.equalsIgnoreCase("V") ? "COMPRAS":"VENTAS")  + "_ALICUOTAS.txt";
		String result ="";
		String lineSeparator = System.getProperty("line.separator");
        String sql = null;
        FileWriter cbtes = null, alic = null;
        PreparedStatement pstmt = null;
 		
        try
 		{
 			long inicia = System.currentTimeMillis();
 			
 			sql = getSql();	        	         	 		
 			pstmt = DB.prepareStatement(sql);			
 			pstmt.setTimestamp(1, date_from);
 			pstmt.setTimestamp(2, date_to);
 			pstmt.setString(3, (transaction.equalsIgnoreCase("V")?"N":"Y"));
 			rs = pstmt.executeQuery();
 			
 			cbtes = new FileWriter(archivo_cbte);
 			alic = new FileWriter(archivo_alic);
 			int Q = creaArchivos(cbtes, alic);//, rs);
 			cbtes.close();
 			alic.close();
		
 			long termina = System.currentTimeMillis();
 			long segundos = (termina-inicia)/1000;
 			log.log(Level.SEVERE,"Generar RG3685 Tardo: "+segundos+" segundos - " + Q + " registros.");
 			rs.close();
 			pstmt.close();
 			pstmt = null;
 		}
 		catch (Exception e)
 		{ 
 			log.saveError("Exportacion CITI RG3685 - Prepare", e);
 			e.printStackTrace();
 			
 			try {
				if(cbtes != null) cbtes.close();
				if(alic != null) alic.close();
				if(pstmt != null) pstmt.close();
				if(rs != null)rs.close();					
			} catch (IOException e1) {
				result = "Error al generar los archivos CITI RG3685 " +lineSeparator + e.getLocalizedMessage();
				e1.printStackTrace();
			}	
 		}	
 		return result;
	}				
	
	private int creaArchivos(FileWriter fw_c , FileWriter fw_a/*, ResultSet rs*/) throws IOException, SQLException
	{
		String lineSeparator = System.getProperty("line.separator");
		int invoice_id = 0;
		int cant = 0;
		int q_alic = 0;
		StringBuffer s; // linea de alícuotas
		StringBuffer c; // línea de comprobantes
		String fecha = new String();
		String tipo_comp = new String();
		String pv = new String();
		String nro = new String();
		String cod_doc = new String();
		String nro_doc = new String();
		String total = new String();
		String moneda = new String();
		String rz = new String();
		/*
		 1 inv.c_invoice_id
		 2 inv.dateacct::date
		 3 inv.dateinvoiced::date
		 4 inv.afipdoctype    - Tipo de comprobante
		 5 inv.documentno
		 6 bp.taxidtype       - Tipo documento, solo para proveedores
		 7 bp.taxid           - Nro de documento, solo para proveedores
		 8 bp.name            - Razón social, solo para proveedores
		 9 inv.grandtotal
		 10 itax.taxbaseamt
		 11 itax.taxamt
		 12 cur.wsfecode
		 13 tax.c_cat_citi_id
		 14 tax.rate"
		 */

		while(rs.next())
		{
			/* 
			 * Primero genero las líneas de las alícuotas y voy generando las sumas totalizadoras del
			 * informe por comprobante, que va en un archivo separado
			*/
			log.log(Level.SEVERE,"Invoice_id" + rs.getInt(1));
			q_alic = 0;
			s = new StringBuffer();
			fecha = getDate(rs.getDate(3));
			tipo_comp = rs.getString(4);
			pv = pad(rs.getString(5).substring(1, 5), 5, true);
			nro = pad(rs.getString(5).substring(6, 13), 20, true);
			cod_doc = rs.getString(6);
			nro_doc = pad(rs.getString(7).replace("-", ""), 20, true);
			total = pad(getCnvAmt(rs.getDouble(9)), 15, true);
			moneda = rs.getString(12);
			rz = pad(rs.getString(8).toUpperCase(), 30, false);

			if (esCreditoFiscal(rs.getString(13))){
 				s.append(tipo_comp);
 				s.append(pv);
 				s.append(nro);
 				// los campos 6 y 7 no van para informes de ventas
 				if (transaction.equalsIgnoreCase("V")){
 					s.append(cod_doc);
 					s.append(nro_doc);
 				}
 				s.append(pad(getCnvAmt(rs.getDouble(10)), 15, true));			// NG
 				s.append(pad(alic.get(rs.getDouble(14)), 4, true));				// Alícuota de IVA
 				s.append(pad(getCnvAmt(rs.getDouble(11)), 15, true));			// IVA liquidado
 				//s.append(lineSeparator);
 				fw_a.write(s.toString());
 				fw_a.write("\r");
 				q_alic++;
 				s = null;
			}
			/*
			 * Si acumulaImportes devuelve falso y no es la primera línea que se procesa, es porque la línea
			 * actual es de otro comprobante por lo que debo generar la línea del comprobante. Lo mismo si es la
			 * última línea del rs
			 */
			if ((!acumulaImportes(invoice_id) && !rs.isFirst()) || rs.isLast()){
				c = new StringBuffer();
				c.append(fecha);
				c.append(tipo_comp);
				c.append(pv);
				c.append(nro);
				// TODO: las importaciones no están soportadas por esta versión.
				c.append("                "); 
				c.append(cod_doc);
				c.append(nro_doc);
				c.append(rz);
				c.append(total);
				// TODO: no se discriminan conceptos no gravados de exentos en esta versión.
				c.append("000000000000000");
				if (rs.getString(5).substring(0, 1).equalsIgnoreCase("C"))
					c.append("000000000000000");
				else
					c.append(pad(getCnvAmt(ex), 15, true));
				c.append(pad(getCnvAmt(p_iva), 15, true));
				c.append(pad(getCnvAmt(p_nac), 15, true));
				c.append(pad(getCnvAmt(p_iibb), 15, true));
				c.append(pad(getCnvAmt(p_mun), 15, true));
				// TODO: dar soporte a impuestos internos
				c.append("000000000000000");
				// TODO: dar soporte multimoneda
				c.append(moneda);
				c.append("0001000000");
				c.append(q_alic);
				if (rs.getString(5).substring(0, 1).equalsIgnoreCase("C"))
					c.append("E");
				else
					c.append("0");
				c.append(pad(getCnvAmt(cf), 15, true));
				c.append(pad(getCnvAmt(ot), 15, true));
				// TODO: dar soporte para comisiones de corredores
				c.append("00000000000                              000000000000000");
 				fw_c.write(c.toString());
 				fw_c.write("\r");
 				c = null;
			}
			
			/*
			 * Guardo el  invoice_id del comprobante por que si en la próxima línea del rs 
			 * el invoice_id tiene el mismo valor entonces tengo que acumular los montos de impuestos.
			 */
			
			invoice_id = rs.getInt(1);
			cant++;
		}
	
		return cant;
			
	}
	
	/*
	 * Retorna la fecha en formato anio mes dia
	 */
    private String getDate(Date fecha) {
        
    	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(fecha);
    }

    /*
     * Retorna el monto como un string sin el punto decimal 
     */
    private String getCnvAmt(Double amt) {
    	
    	DecimalFormat df = new DecimalFormat("#.00");
    	return df.format(amt).replace(".", "").replace(",", "");
    }
    
    /*
     * Agrega la cantidad necesaria de ceros o espacios al inicio del argumento src
     * para completar el largo indicado
     * flag = true: agrega ceros
     * flag = false: agrega espacios
     */
    private String pad(String src, int largo, Boolean flag){
    	
    	StringBuffer ret = new StringBuffer();
    	String r = new String();
    	
    	if (src == null)
    		src = "";
    	
    	int n = largo - src.length();
    	if (n < 0){
    		r = src.substring(0, largo - 1);
    		n = 1;
    	}
    	else
    		r = src;
    	
    	final char[] arr = new char[n];
    	Arrays.fill(arr, flag?'0':' ');
    	
    	ret.append(arr);
    	ret.append(r);
    	return ret.toString();
    }
    
    /*
     * Borra los montos acumulados 
     */
    private void borraImpuestos(){
    	cf = 0.0;
    	p_iva = 0.0;
    	p_iibb = 0.0;
    	p_nac = 0.0;
    	p_mun = 0.0;
    	ot = 0.0;
    	ex = 0.00;
    }
    
    /*
     * Devuelve verdadero si el id del impuesto consultado corresponde a uno configurado como crédito fiscal
     */
    private Boolean esCreditoFiscal(String id){
    	return id.trim().equalsIgnoreCase("CRF");
    }
    
    /*
     * Acumula los importes de las líneas de cada factura.
     * Devuelve:
     *   true si la línea actual corresponde a la misma factura que la anterior
     *   false en caso contrario
     */
    private Boolean acumulaImportes(int id) throws SQLException{
		/*
		 * Chequeo si el invoice_id de ésta línea es el mismo que el que está guardado y reseteo los montos
		 * de impuestos de ser necesario
		 */
    	
    	Boolean ret = true;
    	
		if (id != rs.getInt(1)){
			borraImpuestos();
			ret = false;
		}

		String v = rs.getString(13).trim();
		if (v.equalsIgnoreCase("CRF"))
			cf +=rs.getDouble(11);
		else if (v.equalsIgnoreCase("PIV"))
			p_iva += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PIB"))
			p_iibb += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PNC"))
			p_nac += rs.getDouble(11);
		else if (v.equalsIgnoreCase("PMN"))
			p_mun += rs.getDouble(11);
		else if (v.equalsIgnoreCase("OTR"))
			ot += rs.getDouble(11);
		else if (v.equalsIgnoreCase("EXE"))
			ex += rs.getDouble(10);
			
		return ret;
    }
    
	private String getSql()
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select inv.c_invoice_id, inv.dateacct::date, inv.dateinvoiced::date, inv.afipdoctype, inv.documentno, bp.taxidtype, ");
		sql.append("bp.taxid, bp.name, inv.grandtotal, itax.taxbaseamt, itax.taxamt, cur.wsfecode, tax.catcitirg3685, to_char(tax.rate, '90.00') ");
		sql.append("from libertya.c_invoicetax itax ");
		sql.append("join libertya.c_invoice inv on itax.c_invoice_id = inv.c_invoice_id ");
		sql.append("join libertya.c_doctype dt on inv.c_doctype_id = dt.c_doctype_id ");
		sql.append("join libertya.c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("join libertya.c_currency cur on inv.c_currency_id = cur.c_currency_id ");
		sql.append("join libertya.c_tax tax on itax.c_tax_id = tax.c_tax_id ");
		sql.append("where inv.dateacct between ? and ? and ");
		sql.append("inv.c_doctype_id not in (1010517, 1010518, 1010519, 1010520) ");
		sql.append("and inv.docstatus = 'CO' ");
		sql.append("and inv.issotrx = ? ");
		sql.append("order by ");
		sql.append("inv.dateinvoiced asc, bp.name, inv.documentno");
		return sql.toString();
	}
}
