package nc.pub.mdm.frame.tool;
/**
 * 常用正则表达式
 * @author 周海茂
 * @since 2010-6-25
 */
public class RegTool {
	public static String REG_NUMBER_FFZZ="^\\d+$";  //非负整数（正整数 + 0）
	public static String REG_NUMBER_ZZS="^[0-9]*[1-9][0-9]*$"; //正整数
	public static String REG_NUMBER_FZZS="^((-\\d+)|(0+))$"; //非正整数（负整数 + 0）
	public static String REG_NUMBER_FZS="^-[0-9]*[1-9][0-9]*$"; //负整数
	public static String REG_NUMBER_ZS="^-?\\d+$";//整数
	public static String REG_NUMBER_FFFDS="^\\d+(\\.\\d+)?$";//非负浮点数（正浮点数 + 0）
	public static String REG_NUMBER_ZFDS="^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$";//正浮点数 
	public static String REG_NUMBER_FZFDS="^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";	//非正浮点数（负浮点数 + 0）
	public static String REG_NUMBER_FFDS="^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))$";//负浮点数 
	public static String REG_NUMBER_FDS="^(-?\\d+)(\\.\\d+)?$";//浮点数
	public static String REG_NUMBER_EN="^[A-Za-z]+$";//由26个英文字母组成的字符串
	public static String REG_NUMBER_EN_UP="^[A-Z]+$";//由26个英文字母的大写组成的字符串
	public static String REG_NUMBER_EN_LOW="^[a-z]+$";//由26个英文字母的小写组成的字符串
	public static String REG_NUMBER_WORD="^[A-Za-z0-9]+$";//由数字和26个英文字母组成的字符串
	public static String REG_NUMBER_AF="^[A-Fa-f0-9]+$";//由数字和26个英文字母组成的字符串
	public static String REG_EMAIL="^[a-zA-Z][\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"; // 电子邮件
	public static String REG_EMAIL_CHARSET_STRING="^=?[Q|B]\\??=$";//=?gb2312?B?vbvB98nnx/g=?= 

	public static boolean isEmail(String addr){
		return addr!=null && addr.matches(REG_EMAIL);
	}
	
	public static boolean isMath(String strSrc, String strREG){
		return strSrc!=null && strSrc.matches(strREG);
	}
}
