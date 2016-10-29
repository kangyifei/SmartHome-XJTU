package com.kyf.futurespace.smarthome.utils;
/******************************************************************************
 * int Wordtoi.converse(String mandarins);��һ�����ڵĺ�������/�����ַ�ת��Ϊint�� �ų��ַ���ǰ��ĸ���
 * ex: Wordtoi.converse("������ʮ��Ŷ")�� ���56
 * @author Fang
 *****************************************************************************/
public class Wordtoi {
	static char[] number={'零','一','二','三','四','五','六','七','八','九','十'};
	
	public static int distinguish(char a){                        //�ֱ���ַ����ĸ�����
		int num = -1;
		for(int i=0;i<=10;i++){
			if(a==number[i]||a-'0'==i){
				num=i;
				break;
			}
		}
	 return num;
	}
	
	public static int converse(String madanrins){
		try{
			while(distinguish(madanrins.charAt(0))==-1){              //�ų�ǰ׺�з����ֵ��ַ�
				madanrins=madanrins.substring(1);
			}	
		}catch(Exception e){
			return 0;                                             //�ַ����в��������־ͷ���0
		}
		int last=madanrins.length()-1;                            //���һ��Ԫ�ص���
		while(distinguish(madanrins.charAt(last--))==-1){         //�ų���׺�еķ������ַ�
			madanrins=madanrins.substring(0, last+1);
		}
		if(madanrins.length()==1){
			return distinguish(madanrins.charAt(0));                                     //��
		}
		if(madanrins.length()==2){
			if('0'<madanrins.charAt(0)&&madanrins.charAt(0)<='9'){return                 //15��26
				distinguish(madanrins.charAt(0))*10+distinguish(madanrins.charAt(1));
			}
			else if(madanrins.charAt(0)=='ʮ')return 10+distinguish(madanrins.charAt(1));		//ʮ��		
			     else return 10*distinguish(madanrins.charAt(0));				             //��ʮ
		}
		return distinguish(madanrins.charAt(0))*10+distinguish(madanrins.charAt(2));      //��ʮ��
	}
}
