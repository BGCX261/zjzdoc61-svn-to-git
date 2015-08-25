package nc.vo.mdm.frame;

import java.util.HashMap;

import nc.mddb.constant.ElementConstant;
import nc.pub.mdm.frame.BaseService;
import nc.vo.bd.meta.IBDObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;

/**
 * 主数据核心VO<br>
 * 改动getStatus()是为了配合前后台判断数据标识<br>
 * 改动IBDObject接口，是为了数据锁的NCObject.newInstance()方法<br>
 * 并特意注册了一个元数据，其对应VO类型即为本类nc.vo.mdm.frame.DocVO<br>
 * @author 周海茂
 * @since 2012-03-29
 */
public class DocVO extends SuperVO implements IBDObject{

	public static String KEY_IMPORT_VOS = "_IMPORT_VOS";

	public static String KEY_SUB_TEMPLETS = "_SUB_TEMPLETS";

	public static String KEY_SUB_VOS = "_SUB_VOS";

	public static String KEY_IMP_PARENT_CODE = "_IMP_PC";

	//public static String KEY_PK = "_VO_PK_FIELD";

	private static ThreadLocal<String> mainDataLocal = new ThreadLocal<String>();


	private static final long serialVersionUID = -2863794227977311688L;

	private HashMap<String, Object> map = new HashMap<String, Object>();

	public String parentKeyField;

	public String primaryKeyField;

	public String tableCode;

	@Override
	public Object clone() {
		DocVO obj = (DocVO) super.clone();
		obj.setTableCode(this.getTableCode());
		obj.setPrimaryKeyField(this.getPrimaryKeyField());
		obj.setParentKeyField( this.getParentKeyField() );
		return obj;
	}

	@Override
	public String[] getAttributeNames() {
		if (getPrimaryKeyField() != null) {
			if (!map.keySet().contains(getPrimaryKeyField())) {
				setAttributeValue(getPrimaryKeyField(), getPrimaryKey());
			}
		}
		return map.keySet().toArray(new String[0]);
	}

	@Override
	public Object getAttributeValue(String strKey) {
		Object objRet = null;
		if (strKey != null) {
			objRet = map.get(strKey.trim());
		}
		return objRet;
	}

	@Override
	public Object getCode() {
		return getAttributeValue("vcode");
	}

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Object getId() {
		return getPrimaryKey();
	}

	@Override
	public Object getName() {
		return getAttributeValue("vname");
	}

	/**
	 * 如果是自身树表结构，该值应该为 pk_parent<br>
	 * 如果是主子表的子表，该值应该为子表的外键。
	 * @return
	 */
	public String getParentKeyField() {
		
		return parentKeyField;
	}

	public String getParentPK() {
		String fk = null;
		if (parentKeyField != null) {
			fk = (String) getAttributeValue( getParentKeyField() );
		}
		return fk;
	}

	@Override
	public Object getPId() {
		return getParentPK();
	}

	@Override
	public Object getPk_group() {
		return null;
	}

	@Override
	public Object getPk_org() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return getPrimaryKeyField();
	}

	@Override
	public String getPrimaryKey() {
		String pk = null;
		if (primaryKeyField != null) {
			pk = (String) getAttributeValue(primaryKeyField);
		}
		return pk;
	}

	public String getPrimaryKeyField() {
		if (primaryKeyField == null) {
			String tableCode = getTableCode();
			if( tableCode!=null ){
				primaryKeyField = BaseService.getTablePKField(tableCode, null);
			}
		}
		return primaryKeyField;
	}
	
	@Override
	public int getStatus() {
		Integer iStatus = (Integer)getAttributeValue( ElementConstant.KEY_VOSTATUS );
		if ( iStatus!=null ){
			return iStatus;
		}
		return super.getStatus();
	}
	
	public String getTableCode() {
		if (tableCode == null) {
			String temp = mainDataLocal.get();
			if (temp != null) {
				tableCode = temp;
			}
		} else {
			mainDataLocal.set(tableCode);
		}
		return tableCode;
	}

	@Override
	public String getTableName() {
		return getTableCode();
	}

	/**
	 * @see nc.vo.pub.CircularlyAccessibleValueObject#setAttributeValue(java.lang.String, java.lang.Object)
	 */
	public void setAttributeValue(String attrKey, Object obj) {
		if (attrKey != null && attrKey.trim().length() > 0) {
			map.put(attrKey.trim(), obj);
		}
	}

	public void setParentKeyField(String parentField) {
		this.parentKeyField = parentField;
	}

	@Override
	public void setPrimaryKey(String key) {
		//setAttributeValue(KEY_PK, key);
		
		if (getPrimaryKeyField() != null) {
			setAttributeValue(getPrimaryKeyField(), key);
			if (getTableCode() != null) {
				mainDataLocal.set(getTableCode());
			}
		}
	}

	public void setPrimaryKeyField(String primaryKeyField) {
		this.primaryKeyField = primaryKeyField;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}
	
	@Override
	public void validate() throws ValidationException {
	}
}
