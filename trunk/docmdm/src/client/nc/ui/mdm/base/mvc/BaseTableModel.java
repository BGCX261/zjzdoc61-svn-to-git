package nc.ui.mdm.base.mvc;

import nc.ui.pub.beans.table.VOTableModel;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.ValueObject;

@SuppressWarnings("unchecked")
public class BaseTableModel extends VOTableModel {

	private static final long serialVersionUID = 4130460196444806145L;

	private String[] columnCode = null;

	public String[] getColumnCode() {
		return columnCode;
	}

	public String getColumnCode(int iCol) {
		if( columnCode!=null && columnCode.length>iCol){
			return columnCode[iCol];
		}
		return null;
	}


	public void setColumnCode(String[] columnCode) {
		this.columnCode = columnCode;
	}


	public String[] getColumnName() {
		return columnName;
	}
	
	public String getColumnName(int iCol){
		if( columnName!=null && columnName.length>iCol){
			return columnName[iCol];
		}
		return null;
	}


	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}


	private String[] columnName = null;

	@SuppressWarnings("rawtypes")
	public BaseTableModel(Class c) {
		super(c);
	}


	@Override
	public Object getValueAt(int iRow, int iCol) {
		ValueObject vo = getVO(iRow);
		if( vo instanceof DocVO){
			DocVO mdvo = (DocVO)vo;
			String field = getColumnCode(iCol);
			return mdvo.getAttributeValue( field );
		}
		return null;
	}


	@Override
	public void setValueAt(Object obj, int iRow, int iCol) {
		ValueObject vo = getVO(iRow);
		if( vo instanceof DocVO){
			DocVO mdvo = (DocVO)vo;
			String field = getColumnCode(iCol);
			mdvo.setAttributeValue( field, obj );
		}
	}

	@Override
	public int getColumnCount() {
		return columnCode.length;
	}
}
