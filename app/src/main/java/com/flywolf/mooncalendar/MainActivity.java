package com.flywolf.mooncalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.flywolf.mooncalendar.MoonDataArray.NoMoonDataException;
import com.flywolf.mooncalendarpro.R;

public class MainActivity extends FragmentActivity {

	final String LOG_TAG = "myLogs";
	public Date date;
	MoonData moonData;
	int step = 1;
	Date currentDate;
	public static MoonDataArray ma = new MoonDataArray();
	ViewPager pagerTop;
	PagerAdapter pagerTopAdapter;
	static int PAGE_COUNT_TOP;
	public static String PACKAGE_NAME;
	SharedPreferences sPref;
	int openCount;
	protected int openCountTotal;
	boolean estimated;
	final String OPEN_COUNT = "open_count";
	final String OPEN_COUNT_TOTAL = "open_count_total";
	final String ESTIMATED = "estimated";
	public static boolean extra = true;
	public Toast toast = null;
	final String PREFNAME = "MoonCalendar";
	public static double lat;
	public static double lng;

	public Date getCurrentDate() {
		currentDate = new Date();
		// test date

		/*
		 * try { SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); currentDate =
		 * formatter.parse("2014-05-17 11:33:00"); } catch (ParseException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		return currentDate;
	}

	public static Class<?> monthCalendar = com.flywolf.mooncalendarpro.MonthCalendar.class;
	int REQUEST_CALENDAR = 2;

	public void openMonthCalendar(View view) {
		Intent myIntent = new Intent(view.getContext(), monthCalendar);
		startActivityForResult(myIntent, REQUEST_CALENDAR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If the request went well (OK) and the request was
		// PICK_CONTACT_REQUEST
		Log.d(LOG_TAG, "back activity " + requestCode + " " + REQUEST_CALENDAR);

		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CALENDAR) {

			Log.d(LOG_TAG,
					"back activity 1111+++"
							+ data.getStringExtra("date_month_year"));
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-M-yyyy");
			try {
				Date d=dateFormatter.parse(data
						.getStringExtra("date_month_year"));
				Calendar c = Calendar.getInstance(); 
				c.setTime(d); 
				c.add(Calendar.DATE, 1);
				goToDate(c.getTime());
				pagerTop.setCurrentItem(moonData.getId());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}

		}
	}

	public boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			// Log.v(TAG, "Internet Connection Not Present");
			return false;
		}
	}

	public MoonData getMoonData() {
		return moonData;
	}

	public void setMoonData(MoonData moonData) {
		this.moonData = moonData;
	}

	public MoonDataArray getMa() {
		return ma;
	}

	public void setMa(MoonDataArray ma) {
		this.ma = ma;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "try create");
		sPref = getSharedPreferences(PREFNAME, MODE_PRIVATE);
		int totalOpenCount = sPref.getInt("total_open_count", 0);
		Editor ed = sPref.edit();
		ed.putInt("total_open_count", ++totalOpenCount);
		ed.commit();
		lat = sPref.getFloat("lat", 0);// 50);
		lng = sPref.getFloat("lng", 0);// 6.15f);
		if ((lat == 0 || lng == 0) && totalOpenCount % 10 == 0)
			calculateLocation();
		// toast = Toast.makeText(this, "This is a toast.", Toast.LENGTH_SHORT);
		if (!extra)
			checkOpenCount();
		setContentView(R.layout.activity_moon);
		PACKAGE_NAME = getApplicationContext().getPackageName();
		pagerTop = (ViewPager) findViewById(R.id.pager_top);
		pagerTopAdapter = new TopPagerAdapter(getSupportFragmentManager());
		pagerTop.setAdapter(pagerTopAdapter);
		ma.readCsv(getResources());
		PAGE_COUNT_TOP = ma.data.size();
		// colorise
		/*
		 * View ml=(View) findViewById(R.id.top_layout);
		 * ml.setBackgroundColor(Color.parseColor("#ffffff"));
		 */
		pagerTop.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				// set moon day
				moonData = ma.data.get(position);
				moonData.getMoonFase();
				date = moonData.getDate();
				int id = getResources().getIdentifier(
						MainActivity.PACKAGE_NAME + ":string/day"
								+ moonData.getMoonDay(), null, null);
				Log.d(LOG_TAG, "onPageSelected, position = " + position
						+ " day =" + moonData.getMoonDay());
				String text = getString(id);
				// draw toast when day change
				drawToast(text);

				TextView addRequestView = (TextView) findViewById(R.id.add_request);
				addRequestView.setText(text);
				// add Aroma banner

				Button iv = (Button) findViewById(R.id.aroma);
				// TextView tv = (TextView) findViewById(R.id.aroma_text);
				try {
					id = getResources().getIdentifier(
							MainActivity.PACKAGE_NAME + ":drawable/"
									+ ma.aromaData.get(moonData.getMoonDay()),
							null, null);
					Drawable img = getResources().getDrawable(id);
					img.setBounds(0, 0, 70, 50);
					// txtVw.setCompoundDrawables( img, null, null, null );
					iv.setCompoundDrawables(img, null, null, null);

					id = getResources().getIdentifier(
							getPackageName() + ":string/"
									+ ma.aromaData.get(moonData.getMoonDay()),
							null, null);
					text = getString(R.string.aroma_day) + ": " + getString(id)
							+ ". \n" + getString(R.string.buy_discount);
					iv.setText(text);
				} catch (Exception ex) {
					iv.setText(R.string.no_description);
					iv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							R.drawable.no_data);
				}

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		сlickCurrent(null);

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(LOG_TAG, "try onstart");
		//сlickCurrent(null);
	}

	@Override
	public void onResume() {
		//try open in current day
		super.onResume();
		if (!compareDateByDay(currentDate,new Date()))
		сlickCurrent(null);
	}
	private boolean compareDateByDay(Date date1, Date date2) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Log.d(LOG_TAG, "date check "+ fmt.format(date1)+" "+ date2);
		return fmt.format(date1).equals(fmt.format(date2));
	}

	void checkOpenCount() {
		sPref = getSharedPreferences("MoonCalendar2013Pref", MODE_PRIVATE);
		estimated = sPref.getBoolean(ESTIMATED, false);
		openCountTotal = sPref.getInt(OPEN_COUNT_TOTAL, 0);
		openCountTotal++;
		if (!estimated) {
			openCount = sPref.getInt(OPEN_COUNT, 0);
			openCount++;
			if (openCount > 7) {
				openCount = 0;
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(getString(R.string.information))
						.setMessage(getString(R.string.add_estimate))
						.setPositiveButton(getString(R.string.ok_no_problem),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Editor ed = sPref.edit();
										ed.putBoolean(ESTIMATED, true);
										ed.commit();
										Intent browse = new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("https://play.google.com/store/apps/details?id=com.flywolf.mooncalendar2013"));

										startActivity(browse);
									}

								})
						.setNegativeButton(getString(R.string.next_time), null)
						.show();
			}
			Editor ed = sPref.edit();
			ed.putInt(OPEN_COUNT, openCount);
			ed.putInt(OPEN_COUNT_TOTAL, openCountTotal);
			ed.commit();
		}
	}

	private class TopPagerAdapter extends FragmentPagerAdapter {

		public TopPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PageFragmentTop.newInstance(position);
		}

		Drawable myDrawable;

		@Override
		public CharSequence getPageTitle(int position) {
			MoonData md = ma.data.get(position);
			DateFormat dateFormat = new SimpleDateFormat("MMM dd");

			SpannableStringBuilder sb = new SpannableStringBuilder(" "
					+ dateFormat.format(md.getDate())); // space added before
														// text for convenience
			int id = getResources().getIdentifier(
					getPackageName() + ":drawable/day" + md.getMoonDay(), null,
					null);
			myDrawable = getResources().getDrawable(id);
			// myDrawable.s
			myDrawable.setBounds(0, -5, 20, 20);
			ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
			sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// return dateFormat.format(md.getDate());
			return sb;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT_TOP;
		}

	}

	public void сlickCurrent(View view) {
		goToDate(getCurrentDate());
		ma.currentDayInd = ma.dayInd;
		pagerTop.setCurrentItem(ma.currentDayInd);
	}

	protected void goToDate(Date in) {
		date = in;
		try {
			Log.d(LOG_TAG, "goToDate = " + date + " ind " + ma.currentDayInd);
			// ma.setCurrentDayNum(date);
			ma.getDayNum(date);
			moonData = ma.data.get(ma.dayInd);
			date = moonData.getDate();
			// Log.d(LOG_TAG, "goToDate 2 = " + date+" ind "+ma.currentDayInd);
			step = 1;
			int id = getResources().getIdentifier(
					getPackageName() + ":string/day" + moonData.getMoonDay(),
					null, null);
			String text = getString(id) + ". "
					+ getString(R.string.simple_description);

			drawToast(text);

		} catch (NoMoonDataException ex) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			drawToast(getString(R.string.no_data)
					+ " "
					+ dateFormat
							.format(ma.data.get(ma.currentDayInd).getDate()));
		}

	}

	public void goNext(View v) {
		// TODO Auto-generated method stub
		goNext();

	}

	public void goPrev(View v) {
		// TODO Auto-generated method stub
		goPrev();
		// setCurrentItem(1, true);

	}

	public void goNext() {
		// TODO Auto-generated method stub
		pagerTop.setCurrentItem(pagerTop.getCurrentItem() + 1, true);
		// setCurrentItem(1, true);

	}

	public void goPrev() {
		// TODO Auto-generated method stub
		pagerTop.setCurrentItem(pagerTop.getCurrentItem() - 1, true);
		// setCurrentItem(1, true);

	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.icon_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.quit:
			quit();
			return true;
		case R.id.current:
			goToDate(getCurrentDate());
			return true;
		case R.id.next:
			goNext();
			return true;
		case R.id.prev:
			goPrev();
			return true;
		case R.id.about:
			about();
			return true;
		case R.id.about_day:
			aboutDay();
			return true;
		case R.id.location:
			showLoacationDialog();
			// calculateLocation();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void quit() {
		if (toast != null)
			toast.cancel();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (toast != null)
			toast.cancel();
	}

	private void about() {
		Toast.makeText(this,
				"Moon Calendar 2013.\n Author flywolf1978@gmail.com",
				Toast.LENGTH_LONG).show();
	}

	private void aboutDay() {
		int id = getResources().getIdentifier(
				getPackageName() + ":string/day" + moonData.getMoonDay(), null,
				null);
		Toast.makeText(this,
				getString(R.string.day_symbol) + " " + getString(id),
				Toast.LENGTH_LONG).show();
	}

	public void showToast(View view) {
		showText((String) view.getTag());
	}

	public void showText(String imgName) {
		String text = "";
		try {
			int id = 0;
			if (imgName.contentEquals("day")) {
				id = getResources().getIdentifier(
						getPackageName() + ":string/day"
								+ moonData.getMoonDay(), null, null);
				text = getString(R.string.day_symbol) + " " + getString(id);
				drawToast(text);
				return;
			}
			String addText = "";
			if (imgName.contentEquals("moon")) {
				id = getResources().getIdentifier(
						getPackageName() + ":string/"
								+ moonData.getMoonFace().name(), null, null);

				if (moonData.getMoonFaceT() != null) {

					if (moonData.getMoonDay() >= 15
							&& moonData.getMoonDay() <= 17)
						addText += ". " + getString(R.string.FULL);
					if (moonData.getMoonDay() >= 29
							|| moonData.getMoonDay() == 1)
						addText += ". " + getString(R.string.NEW);
					addText += " "
							+ ma.getFormattedDateDay(moonData.getMoonFaceT());
				}
			}
			if (imgName.contentEquals("sign")) {
				id = getResources().getIdentifier(
						getPackageName() + ":string/" + moonData.getSign(),
						null, null);
				addText = ma.getSignStart(moonData.getId()) != null ? "("
						+ getString(R.string.from)
						+ " "
						+ ma.getFormattedDateDay(ma.getSignStart(moonData
								.getId())) + ")" : "";
			}
			text = getString(id) + addText;
		} catch (Exception ex) {
			text = getString(R.string.no_description);
		}
		drawToast(text);
	}

	LayoutInflater tinflater = null;
	View tlayout = null;
	TextView ttv = null;
	ImageView tiv = null;

	public void drawToast(String text) {

		// showDialog(DIALOG_MESSAGE);
		if (toast == null) {
			tinflater = getLayoutInflater();
			tlayout = tinflater.inflate(R.layout.toast,
					(ViewGroup) findViewById(R.id.toast_layout));

			ttv = (TextView) tlayout.findViewById(R.id.toast_text);
			tiv = (ImageView) tlayout.findViewById(R.id.toast_img);

			toast = new CustomToast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 50);
			toast.setDuration(8);
		} else {
			tiv.setImageResource(getResources().getIdentifier(
					getPackageName() + ":drawable/day" + moonData.getMoonDay(),
					null, null));
			ttv.setText(text);
			toast.setView(tlayout);

			toast.show();
		}
	}

	public void onClickMore(View view) {
		String day = (String) (moonData.getMoonDay() > 30 ? "1" : Integer
				.toString(moonData.getMoonDay()));
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(
				R.string.extra_day_url).replace("#day", day)));

		startActivity(browse);
	}

	public void onClickSign(View view) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(
				R.string.extra_sign_url).replace("#sign", moonData.getSign())));

		startActivity(browse);
	}

	public void onClickAroma(View view) {

		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(
				R.string.aroma_url).replace("#key",
				ma.aromaData.get(moonData.getMoonDay()).substring(6))));

		startActivity(browse);
	}

	public void drawSquareToast(String key) {
		String text = "";
		try {
			int id = getResources().getIdentifier(
					getPackageName() + ":string/" + key, null, null);
			text = getString(id);
		} catch (Exception ex) {
			text = getString(R.string.no_description);
		}
		drawToast(text);
	}

	public void showSquareToast(View view) {
		try {
			if (view.getTag().toString().contentEquals("gotopro")) {
				Intent browse = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id=com.flywolf.mooncalendarpro"));

				startActivity(browse);
			} else
				drawSquareToast((String) view.getTag());
		} catch (Exception ex) {
			drawSquareToast("No data");
		}
	}

	int DIALOG_DATE = 1;

	public void onClickDate(View view) {
		showDialog(DIALOG_DATE);
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_DATE) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			DatePickerDialog tpd = new DatePickerDialog(this, myCallBack,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			return tpd;
		}
		return super.onCreateDialog(id);
	}

	OnDateSetListener myCallBack = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			GregorianCalendar gk = new GregorianCalendar(year, monthOfYear,
					dayOfMonth);
			// Log.d(LOG_TAG, "goToDate = " + gk.getTime());
			gk.add(Calendar.DATE, 1);
			goToDate(gk.getTime());
			pagerTop.setCurrentItem(moonData.getId());

		}
	};

	public void calculateLocation() {
		Editor ed = sPref.edit();

		lat = sPref.getFloat("lat", 0);// 50);
		lng = sPref.getFloat("lng", 0);// 6.15f);
		// Log.v(LOG_TAG, "totalOpenCount calculate loc"+totalOpenCount);
		Geocoder geocoder;
		String bestProvider;
		List<Address> user = null;

		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		bestProvider = lm.getBestProvider(criteria, false);
		try {
			Location location = lm.getLastKnownLocation(bestProvider);
			if (location == null) {
				Toast.makeText(this,
						"Location Not found. Try start Wifi or GPS.",
						Toast.LENGTH_LONG).show();
			} else {
				geocoder = new Geocoder(this);

				user = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
				lat = (double) user.get(0).getLatitude();
				lng = (double) user.get(0).getLongitude();
				System.out.println(" DDD lat: " + lat + ",  longitude: " + lng);
				Toast.makeText(this,
						" Location latitude: " + lat + ",  longitude: " + lng,
						Toast.LENGTH_LONG).show();
				ed.putFloat("lng", (float) lng);
				ed.putFloat("lat", (float) lat);
				ed.commit();

			}
		} catch (Exception e) {
			Toast.makeText(this, "Location Not found. Try start Wifi or GPS.",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	public void showLoacationDialog() {
		calculateLocation();
		/* Alert Dialog Code Start */
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.location); // Set Alert dialog title
											// here
		// Set an EditText view to get user input
		final LinearLayout l = new LinearLayout(this);
		final EditText inputLat = new EditText(this);
		inputLat.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		inputLat.setText(Double.toString(lat));
		TextView viewLat = new TextView(this);
		viewLat.setText(R.string.lat);
		final EditText inputLng = new EditText(this);
		inputLng.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		inputLng.setText(Double.toString(lng));
		TextView viewLng = new TextView(this);
		viewLng.setText(R.string.lng);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(viewLat);
		l.addView(inputLat);
		l.addView(viewLng);
		l.addView(inputLng);
		alert.setView(l);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// You will get as string input data in this variable.
				// here we convert the input to a string and show in a toast.
				// String srt = inputLat.getEditableText().toString()
				// + inputLng.getEditableText().toString();
				// Toast.makeText(MainActivity.this, srt, Toast.LENGTH_LONG)
				// .show();
				// Toast.makeText(MainActivity.this, "Please, restart app.",
				// Toast.LENGTH_LONG);

				// .show();
				lat = Double.parseDouble(inputLat.getEditableText().toString());
				lng = Double.parseDouble(inputLng.getEditableText().toString());
				Editor ed = sPref.edit();

				ed.putFloat("lng", (float) lng);
				ed.putFloat("lat", (float) lat);
				ed.commit();
				// Restart APP
				Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(
								getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

			} // End of onClick(DialogInterface dialog, int whichButton)
		}); // End of alert.setPositiveButton
		alert.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.cancel();
					}
				}); // End of alert.setNegativeButton
		AlertDialog alertDialog = alert.create();
		alertDialog.show();
	}

}
