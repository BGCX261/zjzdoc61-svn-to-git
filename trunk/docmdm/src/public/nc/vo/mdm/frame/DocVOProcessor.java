package nc.vo.mdm.frame;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ProcessorUtils;
import nc.pub.mdm.frame.tool.Toolkit;

/**
 * HashVO数据库结果集处理类
 * @author 周海茂
 * @since 2011-03-22
 */
public class DocVOProcessor extends BaseProcessor {

	private static final long serialVersionUID = -7179478916801658996L;
	
	private String PKField;
	
	private String TableName;

	public String getTableName() {
		return TableName;
	}

	public void setTableName(String tableName) {
		TableName = tableName;
	}

	public DocVOProcessor(String strTable, String strPKField) {
		this.PKField = strPKField;
		this.TableName = strTable;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public Object processResultSet(ResultSet rs) throws SQLException {
		List results = new ArrayList();
		while (rs.next()) {
			ResultSetMetaData metaData = rs.getMetaData();
			int cols = metaData.getColumnCount();
			DocVO vo = new DocVO();
			vo.setPrimaryKeyField(PKField);

			String strTableName = metaData.getTableName(0);
			if( !Toolkit.isNull(strTableName) ){
				vo.setTableCode(strTableName);
			}else if( !Toolkit.isNull(TableName) ){
				vo.setTableCode(TableName);
			}
			for (int i = 1; i <= cols; i++) {
//				metaData.getColumnType(i);
				Object value = ProcessorUtils.getColumnValue(metaData.getColumnType(i), rs, i);
				vo.setAttributeValue(metaData.getColumnName(i).toLowerCase(), value);
			}
			results.add(vo);
		}
		return results;
	}

}