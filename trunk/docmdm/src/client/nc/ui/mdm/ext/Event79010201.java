package nc.ui.mdm.ext;

import java.util.Vector;

import nc.ui.mdm.base.TreeCardUI;
import nc.ui.mdm.base.ext.IBaseUI;
import nc.ui.mdm.base.ext.IEditListener;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;

public class Event79010201 implements IEditListener {
	private IBaseUI baseUI = null;

	public IBaseUI getBaseUI() {
		return baseUI;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		if ("pk_treetype".equals(e.getKey())) {
			TreeCardUI ui = (TreeCardUI) getBaseUI();
			if (ui != null) {
				BillCardPanel cp = ui.getBillForm().getBillCardPanel();
				BillItem item = cp.getHeadTailItem(e.getKey());
				UIRefPane pref = (UIRefPane) item.getComponent();
				String[] pks = pref.getRefPKs();
				String[] shows = pref.getRefNames();

				Vector<DefaultConstEnum> vec = new Vector<DefaultConstEnum>();
				if (pks != null) {
					for (int i = 0; i < pks.length; i++) {
						DefaultConstEnum value = new DefaultConstEnum(pks[i], shows[i]);
						vec.add(value);
					}
				}

				String[] codes = cp.getBillData().getBodyTableCodes();
				BillModel model = cp.getBillData().getBillModel(codes[0]);
				int rows = model.getRowCount();
				for (int row = (rows - 1); row >= 0; row--) {
					DefaultConstEnum obj = (DefaultConstEnum) model.getValueObjectAt(row, "pk_treetype");
					String pkTemp = null;
					if( obj!=null ){
						pkTemp = (String)obj.getValue();
					}
					if (pkTemp == null) {

					} else {
						if (!vec.contains(pkTemp)) {
							model.delLine( new int[]{row});
						} else {
							vec.removeElement(pkTemp);
						}
					}
				}

				for (int i = 0; i < vec.size(); i++) {
					model.addLine();
					int row = model.getRowCount() - 1;
					model.setValueAt(vec.get(i), row, item.getKey());
				}
			}
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBaseUI(IBaseUI ui) {
		this.baseUI = ui;
	}

}
