package nc.pub.mdm.proxy;

import nc.bs.trade.comsave.IQueryAfterSave;
import nc.vo.pub.AggregatedValueObject;

/**
 * �Ž�����̨ר�ýӿڣ���NCV5ϵ����������
 * @author �ܺ�ï
 * @since 2012-8-28
 */
public interface IBaseBusiQuery extends IQueryAfterSave {
	public abstract void setAggVO(AggregatedValueObject aggVO);
	public abstract void setWhere(String strWhere);
}
