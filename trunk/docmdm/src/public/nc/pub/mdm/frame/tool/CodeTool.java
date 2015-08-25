package nc.pub.mdm.frame.tool;

public class CodeTool {

	/**
	 * @param strParentCode
	 * @param strMaxCode
	 * @return
	 */
	public static String makeNextCode(String strParentCode, String strMaxCode) {
		String strNextCode = null;
		if (Toolkit.isNull(strParentCode)) {
			if (Toolkit.isNull(strMaxCode)) {
				strNextCode = "01";
	
			} else {
				strNextCode = makeNextCodeTail(strParentCode, strMaxCode);
			}
		} else if (Toolkit.isNull(strMaxCode)) {
			strNextCode = strParentCode + "01";
	
		} else if (strMaxCode.length() > strParentCode.length()) {
			String strTail = strMaxCode.substring(strParentCode.length());
			strNextCode = makeNextCodeTail(strParentCode, strTail);
	
		} else {
			strNextCode = makeNextCodeTail(null, strMaxCode);
		}
		return strNextCode;
	}

	/**
	 * @param strParentCode
	 * @param strTail
	 * @return
	 */
	public static String makeNextCodeTail(String strParentCode, String strTail) {
		StringBuffer sbTemp = new StringBuffer();
		int i = 0;
		for (; i < strTail.length(); i++) {
			char cTemp = strTail.charAt(strTail.length() - i - 1);
			int iTemp = (int) cTemp;
			iTemp++;
			String strTemp = "" + (char) iTemp;
			if (RegTool.isMath(strTemp, RegTool.REG_NUMBER_WORD)) {
				sbTemp.insert(0, strTemp);
				break;
			} else if (iTemp > 122) {
				sbTemp.insert(0, "a");
			} else if (iTemp > 90) {
				sbTemp.insert(0, "A");
			} else if (iTemp > 57) {
				sbTemp.insert(0, "0");
			}
		}
		if (i == strTail.length()) {
			sbTemp.append("1");
	
		} else {
			String strPreSub = strTail.substring(0, strTail.length() - sbTemp.length());
			sbTemp.insert(0, strPreSub);
		}
		if (!Toolkit.isNull(strParentCode)) {
			sbTemp.insert(0, strParentCode);
		}
	
		return sbTemp.toString();
	}
	
	public static String makeShortCode(String strCode, String strTreeRule, boolean isDelZero){
		if( strCode==null )return null;
		String strRet = null;
		if( strTreeRule==null || strTreeRule.length()<0 ){
			if( strCode.length()==1 ){
				return null;
			}else{
				strRet =  strCode.substring(0, strCode.length()-1 );
			}
			
		}else{
			String[] rules = strTreeRule.split("/");
			if( rules!=null && rules.length>0 ){
				String strTemp = strCode;
				for (int i = 0; i < rules.length; i++) {
					int iLen = Integer.parseInt( rules[i] );
					if( iLen>strTemp.length() ){
						strRet = (strRet==null?"":strRet)+strTemp;
					}else{
						strRet = (strRet==null?"":strRet)+strTemp.substring(0, iLen);
						strTemp = strTemp.substring(iLen);
						if( isDelZero ){
							if( strTemp.replaceAll("0", "").length()==0 ){
								return strRet;
							}
						}
					}
				}
			}
		}
		return strRet;
	}


	/**
	 * 50000(2/2/2):null=null <br>
	 * 500100(2/2/2):50=50 <br>
	 * 500101(2/2/2):5001=5001 <br>
	 * 50010101(3/3/2):500101=500101 <br>
	 */
	public static String makeParentCode(String strCode, String strTreeRule, boolean isDelZero){
		if( strCode==null )return null;
		String strRet = null;
		if( isDelZero ){
			while( strCode.endsWith("0") && strCode.length()>1 ){
				strCode = strCode.substring(0, strCode.length()-1 );
			}
		}
		
		if( strTreeRule==null || strTreeRule.length()<0 ){
			if( strCode.length()==1 ){
				return null;
			}else{
				strRet =  strCode.substring(0, strCode.length()-1 );
			}
			
		}else{
			String[] rules = strTreeRule.split("/");
			if( rules!=null & rules.length>0 ){
				String strTempCode = strCode;
				for (int i = 0; i < rules.length; i++) {
					int iLen = Integer.parseInt( rules[i] );
					if( iLen>=strTempCode.length() ){
						return strRet;
					}else{
						if( (i+1)<rules.length){
							int iNext = Integer.parseInt( rules[i+1] );
							if( strTempCode.length()- iLen <= iNext ){
								String strTemp = strTempCode.substring(0, iLen); // strTemp=51
								strRet = (strRet==null?"":strRet)+strTemp;	// strRet=51
								return strRet;
							}else{
								String strTemp = strTempCode.substring(0, iLen); // strTemp=51
								strRet = (strRet==null?"":strRet)+strTemp;	// strRet=51
								strTempCode = strTempCode.substring(iLen); // strTempCode=5101=01
							}
						}
					}
				}
			}
		}
		
		return strRet;
	}

	public static void main(String[] args){
		CodeTool t = new CodeTool();
		t.testFindParentCode();
	}
	
	public void testFindParentCode(){
		String strTreeRule = "2/2/2";
		String strCode = "50000";
		String strParent = CodeTool.makeParentCode(strCode, strTreeRule, true);
		System.out.println(strCode + "("+strTreeRule+"):"+strParent+"=null");
		
		strCode = "500100";
		strParent = CodeTool.makeParentCode(strCode, strTreeRule, true);
		System.out.println(strCode + "("+strTreeRule+"):"+strParent+"=50");
		
		strCode = "500101";
		strParent = CodeTool.makeParentCode(strCode, strTreeRule, true);
		System.out.println(strCode + "("+strTreeRule+"):"+strParent+"=5001");

		strCode = "50010101";
		strTreeRule = "3/3/2";
		strParent = CodeTool.makeParentCode(strCode, strTreeRule, true);
		System.out.println(strCode + "("+strTreeRule+"):"+strParent+"=500101");
	}

}
