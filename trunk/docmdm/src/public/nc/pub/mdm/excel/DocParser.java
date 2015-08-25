package nc.pub.mdm.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import nc.pub.mdm.frame.tool.LogTool;
import nc.pub.mdm.frame.tool.Toolkit;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * NC61 主数据单Excel数据导入解析器<br>
 * @author 周海茂
 * @since 2012-09-13
 * @usage <br>
 * File excelFile = .....<br>
 * String[] voFields = ....<br>
 * DocParser dp = new DocParser(excelFile,voFields,tableCode,tablePK);<br>
 * DocVO[] vos = dp.getVOs();<br>
 */
public class DocParser {
	private String[] fields = null;
	private XSSFSheet childSheetExcel2007 = null;
	private HSSFSheet childSheetExcel2003 = null;
	private boolean isExcel2007 = true;
	private int rowMax = 0;
	private int rowCurrent = 0;
	private String tableCode = null;
	private String tablePK = null;
	private String tableParentPK = null;

	public DocParser(File file, String[] fields){
		this(file, fields, null, null, null);
	}
	
	public DocParser(File file, String[] fields, String tableCode, String tablePK, String parentPK) {
		this.tableCode = tableCode;
		this.tablePK = tablePK;
		this.tableParentPK = parentPK;
		this.fields = fields;
		
		String strFileName = file.getName();
		try {
			if (strFileName.endsWith("xlsx")) {
				XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
				this.isExcel2007 = true;
				this.childSheetExcel2007 = wb.getSheetAt(0);
				this.rowMax = childSheetExcel2007.getLastRowNum();
 
			} else {
				HSSFWorkbook poiWB = new HSSFWorkbook(new FileInputStream(file));
				this.isExcel2007 = false;
				this.childSheetExcel2003 = poiWB.getSheetAt(0);
				this.rowMax = childSheetExcel2003.getLastRowNum();
			}
		} catch (Exception e) {
			LogTool.error(e);
		}

		if (fields == null) {
			Object poiRow = getExcelRow(rowCurrent);
			rowCurrent++;
			
			int iRows = 0;
			if( isExcel2007 ){
				 XSSFRow row = (XSSFRow)poiRow;
				 iRows=row.getLastCellNum();
			}else{
				HSSFRow row = (HSSFRow)poiRow;
				iRows=row.getLastCellNum();
			}
			
			Vector<String> vtemp = new Vector<String>();
			for (int j = 0; j < iRows; j++) {
				String value = (String) getCellValue(poiRow, j);
				if( value!=null && value.trim().length()>0 ){
					vtemp.add( value.trim());
				}
			}
			
			String[] temp = new String[vtemp.size()];
			vtemp.toArray(temp);
			this.fields = temp;
			
		}

	}
	
	private Object getExcelRow(int iRow){
		Object poiRow = null;
		if (isExcel2007) {
			poiRow = childSheetExcel2007.getRow(iRow);
		} else {
			poiRow = childSheetExcel2003.getRow(iRow);
		}
		return poiRow;
	}
	

	public DocVO[] getVOs() throws Exception {

		Vector<DocVO> vec = new Vector<DocVO>();
		while (rowCurrent <= rowMax) {
			Object poiRow = getExcelRow(rowCurrent);
			if( poiRow == null ) {
				rowCurrent++;
				continue;
			}
			DocVO vo = parserExcelRow(poiRow, rowCurrent);
			if (vo != null) {
				vec.add(vo);
			}
			rowCurrent++;
		}

		DocVO[] retVOs = new DocVO[vec.size()];
		vec.toArray(retVOs);
		return retVOs;
	}

	private DocVO parserExcelRow(Object row, int iRow) {
		DocVO vo = new DocVO();
		boolean isNullRow = true;
		for (int i = 0; i < fields.length; i++) {
			Object value = getCellValue(row, i); //可能三种类型UFDouble,UFBoolean,String
			if(!(value instanceof String)){
				LogTool.info("nc.pub.mdm.excel.DocParser.parserExcelRow()::第"+ (iRow+1) +"行的("+fields[i]+")值不是String类型，请注意检查Excel文件!");
			}
			vo.setAttributeValue(fields[i], value);
			if( !Toolkit.isNull(value) ){
				isNullRow = false;
			}
		}
		
		if( isNullRow ){
			return null;
		}
		
		vo.setTableCode(tableCode);
		vo.setPrimaryKeyField(tablePK);
		vo.setParentKeyField(tableParentPK);
		return vo;
	}

	/**
	 * 如果是String类型的，返回值已经被 trim()
	 * @param poiRow
	 * @param index
	 * @return 可能值：Null,UFDouble,UFBoolean,String
	 */
	private Object getCellValue(Object poiRow, int index) {
		int iCol = index;

		if (poiRow instanceof XSSFRow) { // Excel2007
			XSSFRow xssRow = (XSSFRow) poiRow;
			if (iCol > xssRow.getLastCellNum()) {
				return null;
			}
			XSSFCell xssCell = xssRow.getCell(iCol);
			return getCellValue(xssCell);

		} else { // Excel2003
			HSSFRow hssRow = (HSSFRow) poiRow;
			if (iCol > hssRow.getLastCellNum()) {
				return null;
			}
			HSSFCell poiCell = hssRow.getCell(iCol);
			return getCellValue(poiCell);
		}
	}

	private Object getCellValue(XSSFCell xssCell) {
		Object objRet = null;
		if (null != xssCell) {
			switch (xssCell.getCellType()) {
			case XSSFCell.CELL_TYPE_NUMERIC: // 数字
				double dv = xssCell.getNumericCellValue();
				objRet = new UFDouble(dv);
				break;
			case XSSFCell.CELL_TYPE_STRING: // 字符串
				String sv = xssCell.getStringCellValue();
				if(sv!=null){
					objRet = sv.trim();
				}
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN: // Boolean
				boolean bv = xssCell.getBooleanCellValue();
				objRet = new UFBoolean(bv);
				break;
			case XSSFCell.CELL_TYPE_FORMULA: // 公式
				objRet = xssCell.getCellFormula();
				if( objRet!=null ){
					objRet = objRet.toString();
				}
				break;
			case XSSFCell.CELL_TYPE_BLANK: // 空值
				break;
			case XSSFCell.CELL_TYPE_ERROR: // 故障
				break;
			default:
				break;
			}
		}
		return (objRet == null ? null : objRet);
	}

	private Object getCellValue(HSSFCell hssCell) {
		Object objRet = null;
		if (null != hssCell) {
			switch (hssCell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC: // 数字
				double dv = hssCell.getNumericCellValue();
				objRet = new UFDouble(dv);
				break;
			case HSSFCell.CELL_TYPE_STRING: // 字符串
				String sv = hssCell.getStringCellValue();
				if(sv!=null){
					objRet = sv.trim();
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
				boolean bv = hssCell.getBooleanCellValue();
				objRet = new UFBoolean(bv);
				break;
			case HSSFCell.CELL_TYPE_FORMULA: // 公式
				objRet = hssCell.getCellFormula();
				break;
			case HSSFCell.CELL_TYPE_BLANK: // 空值
				break;
			case HSSFCell.CELL_TYPE_ERROR: // 故障
				break;
			default:
				break;
			}
		}
		return (objRet == null ? null : objRet);
	}

}
