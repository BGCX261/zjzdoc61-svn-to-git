package nc.bs.ajaxnc.ncv6.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import nc.bs.ajaxnc.tools.TempletViewTool;
import nc.md.model.IAttribute;
import nc.md.model.IBusinessEntity;
import nc.pub.mdm.frame.tool.LogTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;

public class TempletView {

	protected String templetPK;

	protected HashMap<String, BillTempletBodyVO> templetItemMap = null;

	protected HashMap<String, BillTempletBodyVO[]> templetTabItemMap = null;

	protected BillTabVO[] templetTabVOs = null;

	protected BillTempletVO templetVO = null;

	public TempletView(BillTempletVO templetVO) {
		init(templetVO);
	}

	public TempletView(String pk_billtemplet) {
		this.templetPK = pk_billtemplet;
		BillTempletVO templetVO = TempletViewTool.getTempletVO(pk_billtemplet);
		init(templetVO);
	}

	public BillTempletBodyVO[] getItemsForEdit(BillTabVO tabVO) {
		BillTempletBodyVO[] templetVOs = getItemsInTab(tabVO);
		return getItemsForEdit(templetVOs);
	}

	public BillTempletBodyVO[] getItemsForList(BillTabVO tabVO) {
		BillTempletBodyVO[] templetVOs = getItemsInTab(tabVO);
		return getItemsForEdit(templetVOs);
	}

	public BillTempletBodyVO[] getItemsForEdit(BillTempletBodyVO[] templetVOs) {
		ArrayList<BillTempletBodyVO> altemp = new ArrayList<BillTempletBodyVO>();
		for (int i = 0; templetVOs != null && i < templetVOs.length; i++) {
			// cardflag
			// showflag
			// editflag
			// listflag
			// listshowflag
			if (templetVOs[i].getShowflag() && templetVOs[i].getCardflag()) {
				altemp.add(templetVOs[i]);
			}
		}
		BillTempletBodyVO[] retVOs = new BillTempletBodyVO[altemp.size()];
		altemp.toArray(retVOs);
		return retVOs;
	}
	
	public BillTempletBodyVO[] getSingleListItem(){
		BillTabVO[] tabVOs = getTempletTabVOs();
		BillTabVO headTabVO = tabVOs[0];
		BillTempletBodyVO[] itemVOs = getItemsInTab(headTabVO);
		itemVOs = getItemsForList(itemVOs);
		return itemVOs;
	}

	public BillTempletBodyVO[] getItemsForList(BillTempletBodyVO[] templetVOs) {
		ArrayList<BillTempletBodyVO> altemp = new ArrayList<BillTempletBodyVO>();
		for (int i = 0; templetVOs != null && i < templetVOs.length; i++) {
			// listflag
			// listshowflag
			if (templetVOs[i].getListshowflag() && templetVOs[i].getListflag()) {
				altemp.add(templetVOs[i]);
			}
		}
		BillTempletBodyVO[] retVOs = new BillTempletBodyVO[altemp.size()];
		altemp.toArray(retVOs);
		return retVOs;
	}

	public BillTempletBodyVO[] getItemsInTab(BillTabVO tabVO) {
		String strKey = TempletViewTool.getKeyofTab(tabVO);
		BillTempletBodyVO[] templetVOs = templetTabItemMap.get(strKey);
		return templetVOs;
	}

	public HashMap<String, BillTempletBodyVO> getTempletItemMap() {
		return templetItemMap;
	}

	public String getTempletPK() {
		return templetPK;
	}

	public HashMap<String, BillTempletBodyVO[]> getTempletTabItemMap() {
		return templetTabItemMap;
	}

	public BillTabVO[] getTempletTabVOs() {
		return templetTabVOs;
	}

	public BillTempletVO getTempletVO() {
		return templetVO;
	}

	private void init(BillTempletVO templetVO) {
		this.templetVO = templetVO;
		if( templetVO!=null ){
			this.templetPK = templetVO.getPrimaryKey();
		}else{
			return;
		}
		
		try {
			initMetaData(templetVO);
			initItemMap(templetVO);
			initTabItemMap(templetVO);
			initTabVOs(templetVO);
		} catch (Exception e) {
			LogTool.error(e);
		}
	}

	private void initMetaData(BillTempletVO templetVO) throws BusinessException {
		if (templetVO.getHeadVO() != null) {
			IBusinessEntity ibe = templetVO.getHeadVO().getBillMetaDataBusinessEntity();
			if (ibe != null) {
				HashMap<String, String> nameMap = new HashMap<String, String>();
				List<IAttribute> listAttr = ibe.getAttributes();
				if (listAttr != null && listAttr.size() > 0) {
					for (IAttribute attr : listAttr) {
						String strCode = attr.getName();
						String strName = attr.getDisplayName();

						nameMap.put(strCode, strName);
					}
				}
				
				BillTempletBodyVO[] itemVOs = templetVO.getBodyVO();
				for (int i = 0; i < itemVOs.length; i++) {
					String strCode = itemVOs[i].getItemkey();
					String strName = nameMap.get(strCode);
					itemVOs[i].setDefaultshowname(strName);
				}
			}
		}
	}
	
	private void initItemMap(BillTempletVO templetVO) throws BusinessException {
		templetItemMap = new HashMap<String, BillTempletBodyVO>();
		BillTempletBodyVO[] itemVOs = templetVO.getBodyVO();
		for (int i = 0; i < itemVOs.length; i++) {
			BillTempletBodyVO itemVO = itemVOs[i];
			String strKey = TempletViewTool.getKeyofItem(itemVO);
			templetItemMap.put(strKey, itemVO);
		}
	}

	private void initTabItemMap(BillTempletVO templetVO) throws BusinessException {
		templetTabItemMap = new HashMap<String, BillTempletBodyVO[]>();

		HashMap<String, ArrayList<BillTempletBodyVO>> tempMap = new HashMap<String, ArrayList<BillTempletBodyVO>>();
		BillTempletBodyVO[] bodyVOs = templetVO.getBodyVO();
		for (int i = 0; i < bodyVOs.length; i++) {
			if ("selected".equalsIgnoreCase(bodyVOs[i].getItemkey()) || "Please Select ".equals(bodyVOs[i].getDefaultshowname())) {
				continue;
			}

			String strKey = bodyVOs[i].getPos() + "_" + bodyVOs[i].getTable_code();
			ArrayList<BillTempletBodyVO> alBody = tempMap.get(strKey);
			if (alBody == null) {
				alBody = new ArrayList<BillTempletBodyVO>();
				tempMap.put(strKey, alBody);
			}
			alBody.add(bodyVOs[i]);
		}

		for (String strKey : tempMap.keySet()) {
			ArrayList<BillTempletBodyVO> alTemp = tempMap.get(strKey);
			BillTempletBodyVO[] bodys = alTemp.toArray(new BillTempletBodyVO[alTemp.size()]);
			TempletViewTool.sortBodyVOsByShowOrder(bodys);
			templetTabItemMap.put(strKey, bodys);
		}
	}

	private void initTabVOs(BillTempletVO templetVO) {
		Vector<BillTabVO> vecTemp = new Vector<BillTabVO>();
		BillTabVO[] btVOs = templetVO.getHeadVO().getStructvo().getBillTabVOs();
		for (int i = 0; i < btVOs.length; i++) {
			BillTempletBodyVO[] bodyItemVOs = getItemsForEdit(btVOs[i]);
			if (bodyItemVOs != null && bodyItemVOs.length > 0) {
				vecTemp.add(btVOs[i]);
			}
		}
		templetTabVOs = new BillTabVO[vecTemp.size()];
		vecTemp.toArray(templetTabVOs);
	}

	public void setTempletItemMap(HashMap<String, BillTempletBodyVO> templetItemMap) {
		this.templetItemMap = templetItemMap;
	}

	public void setTempletPK(String templetPK) {
		this.templetPK = templetPK;
	}

	public void setTempletTabItemMap(HashMap<String, BillTempletBodyVO[]> templetTabItemMap) {
		this.templetTabItemMap = templetTabItemMap;
	}

	public void setTempletTabVOs(BillTabVO[] templetTabVOs) {
		this.templetTabVOs = templetTabVOs;
	}

	public void setTempletVO(BillTempletVO templetVO) {
		this.templetVO = templetVO;
	}

}
