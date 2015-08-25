package nc.jdbc.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;

/**
 *
 * User: 贺扬<br>
 * Date: 2005-1-14<br>
 * Time: 15:31:45 <br>
 * 值对象集合处理器，返回一个ArrayList集合，集合中的每一个元素是一个javaBean,每个javaBean对应结果集合中一行数据，其中每个JavaBean中的数据映射关系和BeanProcess同理
 * 
 * @author 周海茂
 * @since 2012-08-30
 * 因nc.bs.trade.comsave.BillSave.saveBillWhenAdd()方法，保存后查询<br>
 * 需要对processResultSet()方法做修改。<br>
 * 修改此处代价最低！！！
 */
public class BeanListProcessor extends  BaseProcessor {
    /**
	 * <code>serialVersionUID</code> 的注释
	 */
	private static final long serialVersionUID = 2260963403278654726L;
	@SuppressWarnings("rawtypes")
	private Class type = null;

    @SuppressWarnings("rawtypes")
	public BeanListProcessor(Class type) {
        this.type = type;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {
    	if( type == DocVO.class ){
    		DocVO mdVO = new DocVO();
    		String strTableName = mdVO.getTableName();
    		String strPKField = mdVO.getPKFieldName();
    		
    		DocVOProcessor prc = new DocVOProcessor(strTableName, strPKField);
    		return prc.processResultSet(rs);
    	}
        return ProcessorUtils.toBeanList(rs, type);
    }
}
