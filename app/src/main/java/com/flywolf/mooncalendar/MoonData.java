package com.flywolf.mooncalendar;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.flywolf.mooncalendar.MoonDataArray.MoonFaceType;

public class MoonData {
	private int id;
	private int moonDay;
	private String sign;
	private String moonRise;
	private Date date;
	private MoonFaceType moonFace;
	private Date moonFaceT;
	final String LOG_TAG = "myLogs";
	public int getId() {
		return id;
	}

	public MoonFaceType getMoonFace() {
		return moonFace;
	}

	public void setMoonFace(MoonFaceType moonFace) {
		this.moonFace = moonFace;
	}

	public Date getMoonFaceT() {
		return moonFaceT;
	}

	public void setMoonFaceT(Date moonFaceT) {
		this.moonFaceT = moonFaceT;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getMoonRise() {
		return moonRise;
	}
	public void setMoonRise(String moonRise) {
		this.moonRise = moonRise;
	}
	public int getMoonDay() {
		return moonDay;
	}
	public void setMoonDay(int moonDay) {
		this.moonDay = moonDay;
	}
	public void calculateMoonRise() {
		if (MainActivity.lat != 0 && MainActivity.lng != 0) {
			// Date date = new Date(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int h = 12;
			int m = 0;
			int s = 0;
			// Double obsLon = 36.15 * SunMoonCalculator.DEG_TO_RAD;//харьков
			// Double obsLat = 50 * SunMoonCalculator.DEG_TO_RAD; //широта
			// харьков
			// Double obsLon = 30.3 * SunMoonCalculator.DEG_TO_RAD; //kiev
			// Double obsLat = 50.27 * SunMoonCalculator.DEG_TO_RAD; //широта
			// kiev
			// Double obsLon = 37.36 * SunMoonCalculator.DEG_TO_RAD;//moskov
			// Double obsLat = 55.45 * SunMoonCalculator.DEG_TO_RAD; // широта
			// Широта: 50° 27' с.ш.
			Double obsLon = MainActivity.lng * SunMoonCalculator.DEG_TO_RAD;// харьков
			Double obsLat = MainActivity.lat * SunMoonCalculator.DEG_TO_RAD; // широта

			// Долгота: 30° 30' в.д.
			try {
				SunMoonCalculator smc = new SunMoonCalculator(year, month, day,
						h, m, s, obsLon, obsLat);
				smc.calcSunAndMoon();
				String oldMoonRise = moonRise;

				moonRise = SunMoonCalculator.getDateAsStringTmz(smc.moonRise);
				int mr = Integer.parseInt(moonRise.substring(0, moonRise.indexOf(":"))) * 60
						+ Integer.parseInt(moonRise.substring(moonRise.indexOf(":")+1));
				int oldmr = Integer.parseInt(oldMoonRise.substring(0, oldMoonRise.indexOf(":"))) * 60
						+ Integer.parseInt(oldMoonRise.substring(moonRise.indexOf(":")+1));
				if (Math.abs(oldmr - mr) > 12 * 60) {
					date = oldmr > mr?addDays(date, 1):addDays(date, -1);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}
	public void getMoonFase() {
		// Log.d(LOG_TAG, "moonFaceTime "+getMoonDay()+" moonFaceTime="+MoonDataArray.moonFaceTime.size());
		if (MoonDataArray.moonFaceTime != null)
			for (int i = (getId() - 3)<1?1:(getId() - 3); i <= (getId() + 3); i++) {
				if (MoonDataArray.moonFaceTime.containsKey(i)) {
					//set full moon
					if (getMoonDay() >= 15
							&& getMoonDay() <= 17) {
						setMoonFaceT(MoonDataArray.moonFaceTime.get(i));
						if (i>getId())
						{
							setMoonFace(MoonFaceType.GROW);
						//	Log.d(LOG_TAG, "moonFaceTime GROW");
							return;
							//return current;
						}
						if (i==getId())
						{
						//	Log.d(LOG_TAG, "moonFaceTime FULL");
							setMoonFace(MoonFaceType.FULL);
							return;
							//return current;
						}

					}
					else
						if (getMoonDay() >= 29
						|| getMoonDay() == 1) 
					{
							//set new moon time
							setMoonFaceT(MoonDataArray.moonFaceTime.get(i));}
					break;
				}

			}
		//else {
			// Log.d(LOG_TAG, "moonFaceTime null");
		//}

		if (getMoonDay() >= 2 && getMoonDay() < 15) {
			setMoonFace(MoonFaceType.GROW);
		}
		// wanning
		if (getMoonDay() > 15 && getMoonDay() <= 29) {
			setMoonFace(MoonFaceType.WANNING);
		}
		// full
		if (getMoonDay() == 15) {
			setMoonFace(MoonFaceType.FULL);
		}
		// new
		if (getMoonDay() == 1 || getMoonDay() > 29) {
			setMoonFace(MoonFaceType.NEW);
		}
		calculateMoonRise();
	}	
}
