package cn.edu.bjtu.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * �������ͽ����࣬String to Date
 * @author RussWest0
 *
 */
public class ParseDate {
	public static Date parseDate(String date)
	{
		DateFormat dateFormat2=new SimpleDateFormat("yyyy-mm-dd");
		Date myDate2=null;
		try {
			myDate2 = dateFormat2.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("�������ڳ���");
			e.printStackTrace();
			return null;
		}
		return myDate2;
	}
	//@Test
	/*public static void main(String [] args)
	{
		System.out.println(ParseDate.parseDate("2010-09-13"));
	}*/

}