package nc.jdbc.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;

/**
 *
 * User: ����<br>
 * Date: 2005-1-14<br>
 * Time: 15:31:45 <br>
 * ֵ���󼯺ϴ�����������һ��ArrayList���ϣ������е�ÿһ��Ԫ����һ��javaBean,ÿ��javaBean��Ӧ���������һ�����ݣ�����ÿ��JavaBean�е�����ӳ���ϵ��BeanProcessͬ��
 * 
 * @author �ܺ�ï
 * @since 2012-08-30
 * ��nc.bs.trade.comsave.BillSave.saveBillWhenAdd()������������ѯ<br>
 * ��Ҫ��processResultSet()�������޸ġ�<br>
 * �޸Ĵ˴�������ͣ�����
 */
public class BeanListProcessor extends  BaseProcessor {
    /**
	 * <code>serialVersionUID</code> ��ע��
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
