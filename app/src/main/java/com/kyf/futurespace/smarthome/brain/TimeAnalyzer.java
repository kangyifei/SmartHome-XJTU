package com.kyf.futurespace.smarthome.brain;

import android.util.Log;
import com.kyf.futurespace.smarthome.utils.Wordtoi;

import java.util.Calendar;

/******************************************************************************
 * ��ȡ����е�ʱ����Ϣ��Сʱ�����룩
 * 	�÷���	Textanalyzer mTar=new Textanalyzer();
 mTar.update("ʮ���Ӻ�򿪵��");
 mTar.date=mTar.getTimeCondition();
 boolean changeTime()�ж��Ƿ���ʱ��ʣ��з���true���޷���false
 * ��ʮ��Сʱ�ƶ�
 * @author Fang
 *****************************************************************************/
public class TimeAnalyzer {
    private String vInput;
    public Calendar date;
    private int change;


    public boolean isChangeTime() {
        if (change == 0) return false;
        else return true;
    }

    public void update(String input) {
        vInput = input;
    }

    private Calendar addTime(int signal, int i, Calendar time) {
        if (i < 3) time.add(signal, Wordtoi.converse(vInput.substring(0, i)));//��Сʱwhat...ʮ��Сʱwhat...
        else time.add(signal, Wordtoi.converse(vInput.substring(i - 3, i)));//��ʮ��Сʱwhat...��������Сʱwhat...
        return time;
    }


    public Calendar getTimeCondition() {
        Calendar time = Calendar.getInstance();
        change = 0;
        outer:
        for (int i = 1; i < vInput.length(); i++) {
            switch (vInput.charAt(i)) {
                case '点': {
                    if (i < 3)
                        time.set(Calendar.HOUR_OF_DAY, Wordtoi.converse(vInput.substring(0, i)));//����what...ʮ����what...
                    else
                        time.set(Calendar.HOUR_OF_DAY, Wordtoi.converse(vInput.substring(i - 3, i)));//��ʮ����what...����������what...
                    time.set(Calendar.MINUTE, Wordtoi.converse(vInput.substring(i + 1, i + 4)));
                    if (vInput.charAt(i + 1) == '半') time.set(Calendar.MINUTE, 30);
                    change = 1;
                    break outer;
                }
                case '小': {
                    if (vInput.charAt(i + 1) == '时') {
                        time = addTime(Calendar.HOUR_OF_DAY, i, time);
                        change = 1;
                    }
                    break;
                }
                case '分': {
                    time = addTime(Calendar.MINUTE, i, time);
                    change = 1;
                    break;
                }
                case '秒': {
                    try {
                        if (vInput.charAt(i - 2) == '分') {
                            time = addTime(Calendar.SECOND, i + 1, time);   //����������������ʽ�Ĵ�����
                            change = 1;
                        } else {
                            time = addTime(Calendar.SECOND, i, time);
                            change = 1;
                        }
                        break;

                    } catch (Exception e) {
                        time = addTime(Calendar.SECOND, i, time);
                        change = 1;
                        break;
                    }
                }
            }
        }
        if (change == 0) {
            time = null;
            Log.e("TimeAnalyzer", "0");
        }else {
            Log.e("TimeAnalyzer", "" + time.getTime());
        }

        return time;                 //����Calendar��
    }


}
