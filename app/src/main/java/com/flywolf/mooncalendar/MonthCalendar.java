package com.flywolf.mooncalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.flywolf.mooncalendar.MainActivity;
import com.flywolf.mooncalendar.MoonData;
import com.flywolf.mooncalendar.PageFragmentTop.ActionType;
import com.flywolf.mooncalendar.SimpleImageArrayAdapter;
import com.flywolf.mooncalendarpro.R;

//code from sample https://abinashandroid.wordpress.com/2013/07/21/how-to-create-custom-calendar-in-android/
@TargetApi(3)
public class MonthCalendar extends Activity implements OnClickListener {
	private static final String tag = "MyCalendarActivity";

	private TextView currentMonth;
	// private Button selectedDayMonthYearButton;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	@SuppressLint("NewApi")
	private int month, year;
	@SuppressWarnings("unused")
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	private ActionType currentActionType;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month_calendar);

		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		// Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " +
		// "Year: "
		// + year);

		// selectedDayMonthYearButton = (Button) this
		// .findViewById(R.id.selectedDayMonthYear);
		// selectedDayMonthYearButton.setText("Selected: ");

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (TextView) this.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialised
		currentActionType = ActionType.ManWomen;
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, month, year, currentActionType);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
		final LinkedHashMap<ActionType, Integer> actions = new LinkedHashMap<ActionType, Integer>();
		Spinner spinner = (Spinner) findViewById(R.id.moon_spinner);

		for (ActionType item : PageFragmentTop.getActionTypes()) {
			switch (item) {
			case ManWomen:
				actions.put(item, R.drawable.manwomen);
				break;
			case Clear:
				actions.put(item, R.drawable.clear);
				break;
			case Food:
				actions.put(item, R.drawable.proteins);
				break;
			case Grow:
				actions.put(item, R.drawable.growflower);
				break;
			case Shower:
				actions.put(item, R.drawable.shower);
				break;
			case Aroma:
				actions.put(item, R.drawable.aroma_bay);
				break;
			case Dream:
				actions.put(item, R.drawable.dream_sign);
				break;
			case Body:
				actions.put(item, R.drawable.sagittarius_body);
				break;
			case HairCut:
				actions.put(item, R.drawable.haircut);
				break;
			case Chacra:
				actions.put(item, R.drawable.anahata);
				break;
			default:
				break;
			}
		}
		final Integer[] items = new Integer[actions.size()];
		int i = 0;
		for (ActionType key : actions.keySet()) {
			items[i] = actions.get(key);
			// Log.d(tag, "Simage: " + actions.get(key));
			i++;
		}

		SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(this,
				items);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				int i = 0;
				for (ActionType key : actions.keySet()) {
					if (i == position) {
						currentActionType = key;
						Log.d(tag, "Simage: " + currentActionType.toString());
						setGridCellAdapterToDate(month, year, currentActionType);
					}
					i++;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
	}

	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year,
			ActionType actionType) {
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, month, year, actionType);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			setGridCellAdapterToDate(month, year, currentActionType);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
			setGridCellAdapterToDate(month, year, currentActionType);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View …");
		super.onDestroy();
	}

	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private final HashMap<Integer, Integer> recomendationsPerMonthMap;
		private final HashMap<Integer, Integer> moonDayPerMonthMap;

		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-M-yyyy");

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year, ActionType actionType) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			// eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
			// findRecomendationsPerMonth(year, month);
			// recomendationsPerMonthMap = findRecomendationsPerMonth(year,
			// month);
			String[] day_color = list.get(0).split("-");
			int theday = Integer.parseInt(day_color[0].toString());
			String themonth = day_color[2];
			String theyear = day_color[3];
			HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> m2 = new HashMap<Integer, Integer>();
			// HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			try {
				Date parsedDate = dateFormatter.parse(theday + "-"
						+ (Integer.parseInt(themonth) + 1) + "-" + theyear);
				int start = MainActivity.ma.getDayNumber(parsedDate) + 1;
				// Log.d(tag, "moon check parsedDate:" + theday + "-" + themonth
				// + "-" + theyear);
				MoonData moonData = null;
				if (MainActivity.ma.data.containsKey(start))
					moonData = MainActivity.ma.data.get(start);

				for (int i = 0; i < list.size(); i++) {
					m.put(i, moonData.getMoonDay());
					PageFragmentTop.MoonAction ma = null;
					switch (actionType) {
					case ManWomen:
						ma = PageFragmentTop.checkManWomen(moonData);
						break;
					case Clear:
						ma = PageFragmentTop.checkClear(moonData);
						break;
					case Food:
						ma = PageFragmentTop.checkFood(moonData);
						break;
					case Grow:
						ma = PageFragmentTop.checkGrow(moonData);
						break;
					case Shower:
						ma = PageFragmentTop.checkShower(moonData);
						break;
					case Aroma:
						ma = PageFragmentTop.checkAroma(moonData,
								this._context.getResources());
						break;
					case Dream:
						ma = PageFragmentTop.checkDream(moonData);
						break;
					case Body:
						ma = PageFragmentTop.checkBody(moonData,
								this._context.getResources());
						break;
					case HairCut:
						ma = PageFragmentTop.checkHairCut(moonData);
						break;
					case Chacra:
						ma = PageFragmentTop.checkChacra(moonData);
						break;
					default:
						break;
					}
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy E");

					// Log.d(tag,
					// "moon check :" + moonData.getMoonDay() + " "
					// + moonData.getSign() + " "
					// + dateFormat.format(moonData.getDate()));
					m2.put(i, ma.getImageId());
					moonData = MainActivity.ma.data.get(start + i + 1);
					moonData.getMoonFase();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			recomendationsPerMonthMap = m2;
			moonDayPerMonthMap = m;

		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			// Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			// String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(tag, "**-> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(tag, "***—> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			// Log.d(tag, "Week Day:" + currentWeekDay + " is "
			// + getWeekDayAsString(currentWeekDay));
			// Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			// Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY" + "-" + prevMonth + "-" + prevYear);
			}
			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-" + currentMonth
							+ "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-" + currentMonth
							+ "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				list.add(String.valueOf(i + 1) + "-GREY" + "-" + nextMonth
						+ "-" + nextYear);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

			// Set the Day GridCell
			if (recomendationsPerMonthMap.containsKey(position)) {
				ImageView activityTypeImage = (ImageView) row
						.findViewById(R.id.activity_type_image);
				activityTypeImage.setImageResource(recomendationsPerMonthMap
						.get(position));
			}
			if (moonDayPerMonthMap.containsKey(position)) {
				ImageView dayImage = (ImageView) row
						.findViewById(R.id.day_symbol_image);
				int id = getResources().getIdentifier(
						MainActivity.PACKAGE_NAME + ":drawable/day"
								+ moonDayPerMonthMap.get(position).toString(),
						null, null);
				dayImage.setImageResource(id);

			}

			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources()
						.getColor(R.color.lightgray));
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));
			}
			if (day_color[1].equals("BLUE")) {
				gridcell.setTextColor(getResources().getColor(R.color.orrange));
			}
			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
			Intent intent = new Intent();
			intent.putExtra("date_month_year", date_month_year);
			setResult(RESULT_OK, intent);
			finish();
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
}
