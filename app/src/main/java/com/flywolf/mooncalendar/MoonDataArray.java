package com.flywolf.mooncalendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.flywolf.mooncalendarpro.R;

import android.content.res.Resources;
import android.util.Log;

public class MoonDataArray {
	public LinkedHashMap<Integer, MoonData> data;
	public Set<Integer> badDays;
	public Set<Integer> goodDays;
	public Set<Integer> manWomen;
	public Set<Integer> manWomenNot;
	public Set<Integer> shower;
	public Set<Integer> showerNot;
	public Set<Integer> clear;
	public Set<Integer> haircut;
	public Set<Integer> haircutNot;
	final String LOG_TAG = "myLogs";
	public int currentDayInd;
	public int dayInd;

	protected HashMap<Integer, String> dateNames;
	public HashMap<Integer, String> aromaData;
	protected HashMap<Integer, Date> signStartTime;
	public static HashMap<Integer, Date> moonFaceTime;
	String tag = "";
	Date date;

	MoonDataArray() {
		init();
	}

	public static enum MoonFaceType {
		GROW, WANNING, FULL, NEW
	}

	public int getDayInd() {
		return dayInd;
	}

	public void setDayInd(int dayInd) {
		this.dayInd = dayInd;
	}

	public void readCsv(Resources res) {
		data = new LinkedHashMap<Integer, MoonData>();
		signStartTime = new LinkedHashMap<Integer, Date>();
		moonFaceTime = new LinkedHashMap<Integer, Date>();

		try {
			// Resources res = getResources();
			InputStream in_s = res.openRawResource(R.raw.moondata);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in_s));
			String line = reader.readLine();
			line = reader.readLine();
			int count = 0;
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line, ";");

				String dateStr = st.nextToken();
				Date date = stringToDate(dateStr);
				int moonDay = Integer.parseInt(st.nextToken()
						.replaceAll("\\s", "").replaceAll(",", "0"));
				String moonRise;
				try {
					moonRise = st.nextToken().replaceAll("\\s", "");
					if (moonRise.contentEquals(""))
						moonRise = "0:00";
				} catch (Exception ex) {
					moonRise = "0:00";
					Log.d(LOG_TAG, "cant parce date with time");
				}
				String sign = st.nextToken().replaceAll("\\s", "")
						.toLowerCase();
				try {
					String signStart = st.nextToken();
						if (!signStart.contentEquals("null")) {
						signStartTime.put(count, addTime(date, signStart));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					//Log.d(LOG_TAG, "cant parce date with time");
					// st.nextToken();
				}

				try {
					String moonStart = st.nextToken();

					if (!moonStart.contentEquals("null")) {
						moonFaceTime.put(count, addTime(date, moonStart));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.d(LOG_TAG, "cant parce date with time");
				}
				

				MoonData temp = new MoonData();
				temp.setId(count);
				temp.setDate(date);
				temp.setMoonDay(moonDay);
				temp.setMoonRise(moonRise);
				temp.setSign(sign.trim());
				data.put(count, temp);
				line = reader.readLine();
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();

			Log.e(LOG_TAG, "fatal error" + e.getMessage());
		}

	}

	public Date getSignStart(int id) {
		Date res = signStartTime.get(id);
		if (res == null) {
			for (int i = id; (i > 0 && i > id - 5); i--) {
				res = signStartTime.get(i);
				if (res != null)
					break;
			}
		}
		return res;
	}

	public String getFormattedDateDay(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("E HH:mm");

		return dateFormat.format(date);
	}

	public int checkGrowId(MoonData current)

	{
		// current=getMoonFase();
		int res = 0;
		if (current.getMoonFace() == MoonFaceType.NEW
				&& (current.getSign().contentEquals("scorpio")
						|| current.getSign().contentEquals("pisces") || current
						.getSign().contentEquals("cancer"))) {
			res = R.drawable.growsalat;
			tag = "growsalat";
		}
		if (current.getMoonFace() == MoonFaceType.NEW
				&& (current.getSign().contentEquals("gemini")
						|| current.getSign().contentEquals("libra") || current
						.getSign().contentEquals("aquarius"))) {
			res = R.drawable.growflower;
			tag = "growflower";
		}
		if (current.getMoonFace() == MoonFaceType.NEW
				&& (current.getSign().contentEquals("aries")
						|| current.getSign().contentEquals("leo") || current
						.getSign().contentEquals("sagittarius"))) {
			res = R.drawable.growtomato;
			tag = "growtomato";
		}
		if (current.getMoonFace() == MoonFaceType.WANNING
				&& (current.getSign().contentEquals("taurus")
						|| current.getSign().contentEquals("virgo") || current
						.getSign().contentEquals("capricorn"))) {
			res = R.drawable.growpotato;
			tag = "growpotato";
		}
		return res;
	}

	public int checkFood(MoonData current)

	{
		int res = 0;
		if ((current.getSign().contentEquals("scorpio")
				|| current.getSign().contentEquals("pisces") || current
				.getSign().contentEquals("cancer"))) {
			res = R.drawable.carbohydrates;
			tag = "carbohydrates";
		}
		if ((current.getSign().contentEquals("gemini")
				|| current.getSign().contentEquals("libra") || current
				.getSign().contentEquals("aquarius"))) {
			res = R.drawable.fats;
			tag = "fats";
		}
		if ((current.getSign().contentEquals("aries")
				|| current.getSign().contentEquals("leo") || current.getSign()
				.contentEquals("sagittarius"))) {
			res = R.drawable.proteins;
			tag = "proteins";
		}
		if ((current.getSign().contentEquals("taurus")
				|| current.getSign().contentEquals("virgo") || current
				.getSign().contentEquals("capricorn"))) {
			res = R.drawable.salt;
			tag = "salt";
		}

		return res;
	}


	protected void getDayNum(Date inDate) throws NoMoonDataException {
		MoonData m = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int i = 0;
		// try {
		boolean dayFound = false;
		for (Map.Entry<Integer, MoonData> entry : data.entrySet()) {
			m = entry.getValue();
			if (dateFormat.format(m.getDate())
					.equals(dateFormat.format(inDate))) {
				Log.d(LOG_TAG, "date found=" + dateFormat.format(m.getDate()));
				dayFound = true;
				break;
			}
			i++;
		}
		if (m!=null&&!compareDates(inDate, m.getMoonRise())) {
			i--;
			// i--;
		}
		dayInd = i;
		if (!dayFound)
			throw new NoMoonDataException("No data found. Last Date "
					+ dateFormat.format(data.get(currentDayInd).getDate()));

	}
	public int getDayNumber(Date inDate) throws NoMoonDataException {
		MoonData m = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int i = 0;
		for (Map.Entry<Integer, MoonData> entry : data.entrySet()) {
			m = entry.getValue();
			if (dateFormat.format(m.getDate())
					.equals(dateFormat.format(inDate))) {
				break;
			}
			i++;
		}
		if (m!=null&&!compareDates(inDate, m.getMoonRise())) {
			i--;
		}
		return i;
	}

	public static class NoMoonDataException extends Exception {

		NoMoonDataException(String message) {
			super(message);
		}
	}

	public MoonData getDayByDate(Date inDate) {
		MoonData m = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			date = inDate;
			for (Map.Entry<Integer, MoonData> entry : data.entrySet()) {
				m = entry.getValue();
				if (m.getDate().equals(dateFormat.format(inDate))) {
					System.out.println("Event " + entry.getValue()
							+ " is on the specified date");
					break;
				}
			}
			if (!compareDates(inDate, m.getMoonRise())) {
				// Log.d(LOG_TAG, "heyaaaaaaaa not compare" + m.getMoonDay());
				date = addDays(inDate, -1);
				m = data.get(dateFormat.parse(dateFormat.format(date)));

				// Log.d(LOG_TAG, "heyaaaaaaaa not compare!" + m.getMoonDay());
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	public Date addTime(Date date, String time) throws ParseException {
		String res = "";

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder nowYYYYMMDD = new StringBuilder(dateformat.format(date));
		res = nowYYYYMMDD.toString() + " " + time;
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setLenient(false);
		return (Date) formatter.parse(res);


	}

	public boolean compareDates(Date date, String time) {
		String res = "";
		Date dateWithRise = null;
		if (date != null) {
			try {
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				StringBuilder nowYYYYMMDD = new StringBuilder(
						dateformat.format(date));
				res = nowYYYYMMDD.toString() + " " + time;
				DateFormat formatter;
				// Date date;
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				formatter.setLenient(false);
				dateWithRise = (Date) formatter.parse(res);
				Log.d(LOG_TAG, "heyaaaaaaaa222!!" + dateWithRise.getTime() + " " + date.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Log.d(LOG_TAG, "heyaaaaaaaa222!!" + date + " " + res + " ");

		}
		Log.d(LOG_TAG, "heyaaaaaaaa222!!" + dateWithRise.getTime() + " " + date.getTime());
		long diff = (dateWithRise.getTime() + (24 * 60 * 60 * 1000))
				- date.getTime();
		if (diff > 0 && diff < 24 * 60 * 60 * 1000) {
			return true;
		} else {
			return false;
		}
	}

	public static Date stringToDate(String strDate) {

		try {
			DateFormat formatter;
			Date date;
			formatter = new SimpleDateFormat("MM/dd/yy");
			date = (Date) formatter.parse(strDate);
			return date;
		} catch (Exception e) {
			Log.e("log_tag", e.toString());
			return null;
		}
	}

	private void init()

	{
		dateNames = new HashMap<Integer, String>();
		dateNames.put(1, "светильник");
		dateNames.put(2, "рог изобилия");
		dateNames.put(3, "леопард");
		dateNames.put(4, "райское дерево");
		dateNames.put(5, "единорог");
		dateNames.put(6, "птица");
		dateNames.put(7, "роза ветров");
		dateNames.put(8, "пожар");
		dateNames.put(9, "летучая мышь");
		dateNames.put(10, "фонтан");
		dateNames.put(11, "корона");
		dateNames.put(12, "сердце");
		dateNames.put(13, "кольцо");
		dateNames.put(14, "труба");
		dateNames.put(15, "змей");
		dateNames.put(16, "голубь");
		dateNames.put(17, "гроздь винограда");
		dateNames.put(18, "зеркало");
		dateNames.put(19, "паук");
		dateNames.put(20, "орёл");
		dateNames.put(21, "конь");
		dateNames.put(22, "слон");
		dateNames.put(23, "крокодил");
		dateNames.put(24, "медведь");
		dateNames.put(25, "черепаха");
		dateNames.put(26, "жаба");
		dateNames.put(27, "трезубец");
		dateNames.put(28, "лотос");
		dateNames.put(29, "спрут");
		dateNames.put(30, "золотой лебедь");
		dateNames.put(291, "спрут, светильник");
		dateNames.put(301, "золотой лебедь, светильник");
		dateNames.put(102, "светильник, рог изобилия");
		badDays = new HashSet<Integer>();
		badDays.add(9);
		badDays.add(15);
		badDays.add(19);
		badDays.add(26);
		badDays.add(29);
		goodDays = new HashSet<Integer>();
		goodDays.add(1);
		goodDays.add(13);
		goodDays.add(14);
		goodDays.add(17);
		goodDays.add(22);
		goodDays.add(28);
		manWomen = new HashSet<Integer>();
		manWomen.add(1);
		manWomen.add(2);
		manWomen.add(6);
		manWomen.add(11);
		manWomen.add(17);
		manWomen.add(21);
		manWomen.add(24);
		manWomenNot = new HashSet<Integer>();
		manWomenNot.add(9);
		manWomenNot.add(13);
		manWomenNot.add(15);
		manWomenNot.add(19);
		manWomenNot.add(23);
		manWomenNot.add(26);
		shower = new HashSet<Integer>();
		shower.add(13);
		shower.add(28);
		shower.add(25);
		showerNot = new HashSet<Integer>();
		showerNot.add(8);
		// showerNot.add(27);
		clear = new HashSet<Integer>();
		clear.add(7);
		clear.add(9);
		clear.add(16);
		clear.add(20);
		clear.add(28);
		haircut = new HashSet<Integer>();
		haircut.add(14);
		haircut.add(16);
		haircut.add(17);
		haircutNot = new HashSet<Integer>();
		haircutNot.add(9);
		haircutNot.add(15);
		haircutNot.add(23);
		haircutNot.add(29);
		aromaData = new HashMap<Integer, String>();
		aromaData.put(1, "aroma_mirra");
		aromaData.put(2, "aroma_jasmine");
		aromaData.put(3, "aroma_bay");
		aromaData.put(4, "aroma_mandarin");
		aromaData.put(5, "aroma_lavender");
		aromaData.put(6, "aroma_cedar");
		aromaData.put(7, "aroma_cinnamon");
		aromaData.put(8, "aroma_rose");
		aromaData.put(9, "aroma_incense");
		aromaData.put(10, "aroma_rosemary");
		aromaData.put(11, "aroma_camomile");
		aromaData.put(12, "aroma_clove");
		aromaData.put(13, "aroma_neroli");
		aromaData.put(14, "aroma_lavender");
		aromaData.put(15, "aroma_cinnamon");
		aromaData.put(16, "aroma_grapefruit");
		aromaData.put(17, "aroma_lavender");
		aromaData.put(18, "aroma_ylang");
		aromaData.put(19, "aroma_litsea_kubeba");
		aromaData.put(20, "aroma_melissa");
		aromaData.put(21, "aroma_mirra");
		aromaData.put(22, "aroma_marjoram");
		aromaData.put(23, "aroma_orchid");
		aromaData.put(24, "aroma_heather");
		aromaData.put(25, "aroma_gardenia");
		aromaData.put(26, "aroma_jasmine");
		aromaData.put(27, "aroma_lemon");
		aromaData.put(28, "aroma_sandal");
		aromaData.put(29, "aroma_orange");
		aromaData.put(30, "aroma_basil");
		aromaData.put(291, "aroma_orange");
		aromaData.put(301, "aroma_mirra");
		aromaData.put(102, "aroma_jasmine");
	}
}
