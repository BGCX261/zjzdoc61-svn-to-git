package nc.pub.mdm.frame.tool;
/**
 * ����������ʽ
 * @author �ܺ�ï
 * @since 2010-6-25
 */
public class RegTool {
	public static String REG_NUMBER_FFZZ="^\\d+$";  //�Ǹ������������� + 0��
	public static String REG_NUMBER_ZZS="^[0-9]*[1-9][0-9]*$"; //������
	public static String REG_NUMBER_FZZS="^((-\\d+)|(0+))$"; //���������������� + 0��
	public static String REG_NUMBER_FZS="^-[0-9]*[1-9][0-9]*$"; //������
	public static String REG_NUMBER_ZS="^-?\\d+$";//����
	public static String REG_NUMBER_FFFDS="^\\d+(\\.\\d+)?$";//�Ǹ����������������� + 0��
	public static String REG_NUMBER_ZFDS="^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$";//�������� 
	public static String REG_NUMBER_FZFDS="^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";	//�������������������� + 0��
	public static String REG_NUMBER_FFDS="^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))$";//�������� 
	public static String REG_NUMBER_FDS="^(-?\\d+)(\\.\\d+)?$";//������
	public static String REG_NUMBER_EN="^[A-Za-z]+$";//��26��Ӣ����ĸ��ɵ��ַ���
	public static String REG_NUMBER_EN_UP="^[A-Z]+$";//��26��Ӣ����ĸ�Ĵ�д��ɵ��ַ���
	public static String REG_NUMBER_EN_LOW="^[a-z]+$";//��26��Ӣ����ĸ��Сд��ɵ��ַ���
	public static String REG_NUMBER_WORD="^[A-Za-z0-9]+$";//�����ֺ�26��Ӣ����ĸ��ɵ��ַ���
	public static String REG_NUMBER_AF="^[A-Fa-f0-9]+$";//�����ֺ�26��Ӣ����ĸ��ɵ��ַ���
	public static String REG_EMAIL="^[a-zA-Z][\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"; // �����ʼ�
	public static String REG_EMAIL_CHARSET_STRING="^=?[Q|B]\\??=$";//=?gb2312?B?vbvB98nnx/g=?= 

	public static boolean isEmail(String addr){
		return addr!=null && addr.matches(REG_EMAIL);
	}
	
	public static boolean isMath(String strSrc, String strREG){
		return strSrc!=null && strSrc.matches(strREG);
	}
}
