package nc.bs.mdm.frame;

import java.util.ArrayList;

import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.proxy.IBaseBusiQuery;
import nc.vo.mdm.frame.DocVO;
import nc.vo.mdm.frame.DocVOProcessor;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 事务处理后查询更新服务，主要针对聚合VO表体数据。
 * @author 周海茂
 * @since 2011-12-26
 */
public class BaseBusiQueryImpl implements IBaseBusiQuery {
	private AggregatedValueObject billVO;
	private String where;

	public void setWhere(String strWhere) {
		this.where = strWhere;
	}

	public BaseBusiQueryImpl() {
	}

	/*
	 * @see nc.bs.trade.comsave.BillSave.saveBodyVOs(AggregatedValueObject billVO, boolean isQueryData)
	 * @see nc.bs.trade.comsave.IQueryAfterSave#queryBodyVOsAfterSave()
	 */
	@SuppressWarnings("unchecked")
	public CircularlyAccessibleValueObject[] queryBodyVOsAfterSave() throws Exception {
		if (billVO == null) {
			return null;
		}
		DocVO[] items = (DocVO[]) billVO.getChildrenVO();
		CircularlyAccessibleValueObject[] retVOs = null;
		if (items!=null && items.length > 0){
			DocVO dvo = items[0];
			if ( dvo.getParentKeyField()!= null) {
				String strWhere = dvo.getParentKeyField() + "='" + dvo.getParentPK() + "' and isnull(dr,0)=0";
				retVOs = BaseService.queryByCondition(items[0].getClass(), strWhere);

			} else {
				if (where == null) {
					where = "isnull(dr,0)=0";
				}
				
				if( items[0] instanceof DocVO){
					String strPKField = items[0].getPKFieldName();
					String strTableName = items[0].getTableName();
					String strSQL = "select * from "+strTableName ;
					if( where.trim().toLowerCase().startsWith("where")){
						strSQL += where;
					}else{
						strSQL = strSQL + " where " + where;
					}
					ArrayList<DocVO> alVOs = (ArrayList<DocVO>)BaseService.getServiceParam().executeQuery(strSQL, new DocVOProcessor(strTableName, strPKField));
					if( alVOs.size()>0 ){
						retVOs = new DocVO[ alVOs.size() ];
						alVOs.toArray( retVOs );
					}
				}else{
				
					retVOs = BaseService.queryByCondition(items[0].getClass(), where);
				}
			}
		}
		return retVOs;
	}

	public void setAggVO(AggregatedValueObject aggVO) {
		this.billVO = aggVO;
	}
}
