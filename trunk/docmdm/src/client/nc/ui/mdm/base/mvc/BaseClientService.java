package nc.ui.mdm.base.mvc;

import nc.pub.mdm.frame.BaseService;
import nc.pub.mdm.frame.tool.TempletTool;
import nc.pub.mdm.proxy.BaseUserObject;
import nc.ui.pubapp.uif2app.model.BillListModelService;
import nc.uif.pub.exception.UifException;
import nc.vo.mdm.frame.DocAggVO;
import nc.vo.mdm.frame.DocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

public class BaseClientService extends BillListModelService {

	private LoginContext loginContext = null;

	@Override
	public void delete(Object object) throws Exception {
		// super.delete(object);
		BaseService.getService().delete((DocVO) object);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		DocVO[] vos = null;
		String strBillType = getLoginContext().getNodeCode();
		BillTempletVO tvo = TempletTool.queryTempletByCode(strBillType);//UITool.getTemplet(strBillType);
		String strTable = TempletTool.getTableCode(tvo);
		String strTablePK = TempletTool.getTablePkField(tvo);
		vos = BaseService.queryMainDataByWhere(strTable, strTablePK, "isnull(dr,0)=0");
		return vos;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	@Override
	public Object insert(Object object) throws Exception {
		if (object instanceof DocVO) {
			DocVO vo = (DocVO) object;
			String pk = BaseService.getService().insert(vo);
			vo = BaseService.queryMainDataByPK(vo.getTableCode(), vo.getPrimaryKeyField(), pk);
			return vo;
		}
		return super.insert(object);
	}

	@Override
	public Object update(Object object) throws Exception {
		if (object instanceof DocVO) {
			DocVO vo = (DocVO) object;
			// BaseService.getService().update(vo);
			// vo = BaseService.queryMainDataByPK(vo.getTableCode(), vo.getPrimaryKeyField(), vo.getPrimaryKey());
			return saveDocVO(vo);
			
		}if ( object instanceof DocVO[]){
			DocVO[] vos = (DocVO[])object;
			return saveDocVO(vos);
		}
		return super.update(object);
	}

	private DocVO saveDocVO(DocVO vo) throws UifException {
		DocAggVO aggVO = new DocAggVO();
		aggVO.setParentVO(vo);
		BaseUserObject userObj = new BaseUserObject();
		AggregatedValueObject aggRet = BaseService.getService().saveBD(aggVO, userObj);
		vo = (DocVO) aggRet.getParentVO();
		return vo;
	}

	private DocVO[] saveDocVO(DocVO[] vos) throws UifException {
		DocAggVO aggVO = new DocAggVO();
		aggVO.setParentVO(null);
		aggVO.setChildrenVO(vos);
		BaseUserObject userObj = new BaseUserObject();
		AggregatedValueObject aggRet = BaseService.getService().saveBD(aggVO, userObj);
		vos = (DocVO[]) aggRet.getChildrenVO();
		return vos;
	}
}
