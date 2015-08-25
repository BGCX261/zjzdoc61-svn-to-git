package nc.bs.trade.lock;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * �˴���������˵���� �������ڣ�(2004-5-13 20:49:45)
 * @author �ν�
 * 
 * @since 2012-10-16
 * @author �ܺ�ï
 * @because nc.bs.pub.pflock.VOConsistenceCheck.checkConsistenceAry()������Ȼ��m_billtype��ȡ����<br>
 * �޸���ν�ġ���������ʱ���У�顱������������������Ӧ����m_billtype�������޸ı����������VOConsistenceCheck.checkConsistenceAry()�Ĵ���
 */
public class BDConsistenceCheck extends nc.bs.pub.pflock.VOConsistenceCheck
{
	/**
	 * BDConsistenceCheck ������ע�⡣
	 * 
	 * @param vo
	 *            nc.vo.pub.AggregatedValueObject
	 */
	public BDConsistenceCheck(nc.vo.pub.AggregatedValueObject vo)
	{
		super(vo);
	}

	/** 
	 * ���Ҫ������AggregatedVO�Ƿ������ݿ��д��ڣ������Ƿ�͵�ǰʹ�õİ汾һ��(ts) ��������ڣ�throw
	 * BusinessException "�õ����Ѿ�������ɾ������ˢ�½���" ���ts��һ�£�throw BusinessException
	 * "�õ����Ѿ��������޸ģ���ˢ�½��棬����ҵ��" ���أ�VOID �������ڣ�(2003-5-23 7:49:37) ���ھ�
	 */
	public void checkConsistence() throws BusinessException
	{

		if(this.m_vo instanceof AggregatedValueObject)
		{

			AggregatedValueObject agvo = (AggregatedValueObject) this.m_vo;
			CircularlyAccessibleValueObject headVo = agvo.getParentVO();
			CircularlyAccessibleValueObject[] itemVos = agvo.getChildrenVO();
			if (headVo != null)
				super.checkConsistence();
			else{
				// �ܺ�ï  2012-10-16 begin
				// super.checkConsistenceAry(itemVos);
				if( itemVos!=null && itemVos.length>0 ){
					for (int i = 0; i < itemVos.length; i++) {
						if( itemVos[i]!=null ){
							m_vo = itemVos[i];
							super.checkConsistence();
						}
					}
				}
				// �ܺ�ï  2012-10-16 end
			}
		}

	}
}