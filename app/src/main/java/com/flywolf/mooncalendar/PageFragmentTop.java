package com.flywolf.mooncalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flywolf.mooncalendar.MoonDataArray.MoonFaceType;
import com.flywolf.mooncalendarpro.R;

//http://developer.android.com/training/basics/fragments/communicating.html
public class PageFragmentTop extends Fragment implements OnClickListener {
	final String LOG_TAG = "myLogs";

	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

	int pageNumber = -1;
	Date currentDate = new Date();
	MoonData moonData;
	public static final MoonDataArray moonArray = new MoonDataArray();
	ArrayList<MoonAction> moonActions;
	ImageView nextActivityView, prevActivityView, square1, square2, square3,
			square4, right_arrow, left_arrow, moon, sign;

	static PageFragmentTop newInstance(int page) {
		PageFragmentTop pageFragment = new PageFragmentTop();
		Bundle arguments = new Bundle();
		arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
		pageFragment.setArguments(arguments);
		return pageFragment;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
	}

	public static boolean isSameDay(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date1).equals(sdf.format(date2));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_top, null);
		// colorise
		/*
		 * ((View)
		 * view.findViewById(R.id.left_arrow)).setBackgroundColor(Color.parseColor
		 * ("#000000")); ((View)
		 * view.findViewById(R.id.right_arrow)).setBackgroundColor
		 * (Color.parseColor("#000000")); ((View)
		 * view.findViewById(R.id.next_activity
		 * )).setBackgroundColor(Color.parseColor("#000000")); ((View)
		 * view.findViewById
		 * (R.id.prev_activity)).setBackgroundColor(Color.parseColor
		 * ("#000000"));
		 * 
		 * ((View)
		 * view.findViewById(R.id.squares_top)).setBackgroundColor(Color.
		 * parseColor("#000000")); ((View)
		 * view.findViewById(R.id.squares)).setBackgroundColor
		 * (Color.parseColor("#000000")); ((View)
		 * view.findViewById(R.id.squares2
		 * )).setBackgroundColor(Color.parseColor("#000000"));
		 * 
		 * ((View)
		 * view.findViewById(R.id.square1)).setBackgroundColor(Color.parseColor
		 * ("#fece00")); ((View)
		 * view.findViewById(R.id.square2)).setBackgroundColor
		 * (Color.parseColor("#fece00")); ((View)
		 * view.findViewById(R.id.square3)
		 * ).setBackgroundColor(Color.parseColor("#fece00")); ((View)
		 * view.findViewById
		 * (R.id.square4)).setBackgroundColor(Color.parseColor("#fece00"));
		 * ((View)
		 * view.findViewById(R.id.square4)).setBackgroundColor(Color.parseColor
		 * ("#fece00")); ((View)
		 * view.findViewById(R.id.top_squares)).setBackgroundColor
		 * (Color.parseColor("#de0000")); ((View)
		 * view.findViewById(R.id.moon)).setBackgroundColor
		 * (Color.parseColor("#de0000")); ((View)
		 * view.findViewById(R.id.sign)).setBackgroundColor
		 * (Color.parseColor("#de0000"));
		 */
		right_arrow = (ImageView) view.findViewById(R.id.right_arrow);
		left_arrow = (ImageView) view.findViewById(R.id.left_arrow);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TextView txtDate = (TextView) view.findViewById(R.id.date);
		// set moon day
		moonData = ((MainActivity) getActivity()).getMa().data.get(pageNumber);
		moonData.getMoonFase();

		// date=
		// change color for current date
		Log.d(LOG_TAG, "onPageSelected, position 1= " + pageNumber + " day ="
				+ moonData.getMoonDay());
		if (pageNumber == ((MainActivity) getActivity()).getMa().currentDayInd) {
			txtDate.setTextColor(Color.parseColor("#669933"));
		} else {
			txtDate.setTextColor(Color.parseColor("#424242"));
		}

		if (moonData == null) {
			ImageView day = (ImageView) view.findViewById(R.id.day);
			day.setImageResource(R.drawable.no_data);
			ImageView sign = (ImageView) view.findViewById(R.id.sign);
			sign.setImageResource(R.drawable.square);
			ImageView moon = (ImageView) view.findViewById(R.id.moon);
			moon.setImageResource(R.drawable.square);
		} else {
			dateFormat = new SimpleDateFormat("dd/MM/yyyy E");
			MoonData moonDataNext = null;
			if (pageNumber == ((MainActivity) getActivity()).getMa().currentDayInd) {
				try {
					moonDataNext = ((MainActivity) getActivity()).getMa().data
							.get(pageNumber + 1);
				} catch (Exception ex) {
					Log.e(LOG_TAG, ex.getMessage());
				}
				if (moonDataNext != null) {
					moonDataNext.calculateMoonRise();
					SimpleDateFormat dFormat = new SimpleDateFormat("E");
					txtDate.setText(dateFormat.format(new Date())
							+ " "
							+ getString(R.string.to)
							+ " "
							+ moonDataNext.getMoonRise()
							+ (isSameDay(moonDataNext.getDate(), new Date()) ? ""
									: "("
											+ dFormat.format(moonDataNext
													.getDate()) + ")"));
				} else
					txtDate.setText(dateFormat.format(new Date()) + " "
							+ getString(R.string.from) + " "
							+ moonData.getMoonRise());
			} else
				txtDate.setText(dateFormat.format(moonData.getDate()) + " "
						+ " " + getString(R.string.from) + " "
						+ moonData.getMoonRise());
			ImageView day = (ImageView) view.findViewById(R.id.day);
			String tag = "";
			int id = getResources().getIdentifier(
					MainActivity.PACKAGE_NAME + ":drawable/day"
							+ moonData.getMoonDay(), null, null);

			if (id != 0)
				day.setImageResource(id);
			int moonN = 1;

			// moonData.g
			// ((MainActivity) getActivity()).getMa().moonFaceTime;
			try {
				if (moonData.getMoonFace() == MoonFaceType.GROW) {
					moonN = (moonData.getMoonDay() / 2);
					if (moonN == 8 || moonN == 9)
						moonN = 7;
				}
				// wanning
				if (moonData.getMoonFace() == MoonFaceType.WANNING) {
					moonN = (moonData.getMoonDay() / 2) + 1;
				}
				// full
				if (moonData.getMoonFace() == MoonFaceType.FULL) {
					moonN = 8;
				}
				// new
				if (moonData.getMoonFace() == MoonFaceType.NEW) {
					moonN = 1;
				}
				id = getResources().getIdentifier(
						MainActivity.PACKAGE_NAME + ":drawable/moon" + moonN,
						null, null);
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(LOG_TAG, "moonFaceTime error" + e.toString());
			}
			moon = (ImageView) view.findViewById(R.id.moon);
			// Log.d(LOG_TAG, "moonFaceTime id "+id+
			// " f="+moonData.getMoonFace().name()+" moonN="+moonN);
			if (id != 0)
				moon.setImageResource(id);

			sign = (ImageView) view.findViewById(R.id.sign);
			id = getResources().getIdentifier(
					MainActivity.PACKAGE_NAME + ":drawable/"
							+ moonData.getSign(), null, null);
			if (id != 0)
				sign.setImageResource(id);

			// ******show activity**
			moonActions = createActions(moonData);

			int squareId = 1;
			ImageView nextActivityView = (ImageView) view
					.findViewById(R.id.next_activity);
			ImageView prevActivityView = (ImageView) view
					.findViewById(R.id.prev_activity);

			prevActivityView.setEnabled(false);
			prevActivityView.setImageResource(R.drawable.square);

			if (moonActions.size() > 4) {
				nextActivityView.setEnabled(true);
				nextActivityView.setImageResource(R.drawable.moon_arrow);
			} else {
				nextActivityView.setEnabled(false);
				nextActivityView.setImageResource(R.drawable.square);
			}

			for (int i = 0; i < moonActions.size(); i++) {
				MoonAction s = moonActions.get(i);
				id = getResources().getIdentifier("square" + squareId, "id",
						MainActivity.PACKAGE_NAME);
				ImageView square = (ImageView) view.findViewById(id);
				square.setImageResource(s.getImageId());
				square.setTag(s.getDescription());
				squareId++;
				if (squareId == 5)
					break;

			}
			if (MainActivity.PAGE_COUNT_TOP - 1 == pageNumber) {
				right_arrow.setImageResource(R.drawable.empty);
				// DateFormat dateFormat = new
				// SimpleDateFormat("dd/MM/yyyy");
				String tText = getString(R.string.no_data)
						+ " "
						+ dateFormat
								.format(((MainActivity) getActivity()).getMa().data
										.get(((MainActivity) getActivity())
												.getMa().currentDayInd)
										.getDate());
				Toast.makeText(view.getContext(), tText, Toast.LENGTH_LONG)
						.show();

			} else {
				right_arrow.setImageResource(R.drawable.right_arrow);
			}

		}
		if (pageNumber == 0) {
			left_arrow.setImageResource(R.drawable.empty);
		} else {
			left_arrow.setImageResource(R.drawable.left_arrow);
		}

		nextActivityView = (ImageView) view.findViewById(R.id.next_activity);
		nextActivityView.setOnClickListener(this);
		prevActivityView = (ImageView) view.findViewById(R.id.prev_activity);
		prevActivityView.setOnClickListener(this);
		square1 = (ImageView) view.findViewById(R.id.square1);
		square2 = (ImageView) view.findViewById(R.id.square2);
		square3 = (ImageView) view.findViewById(R.id.square3);
		square4 = (ImageView) view.findViewById(R.id.square4);

		return view;

	}

	public static enum ActionType {
		ManWomen, Clear, Food, Grow, Shower, Aroma, Dream, Body, HairCut, Chacra, Pro
	}

	private static ActionType[] actionTypes = { ActionType.ManWomen,
			ActionType.Clear, ActionType.Food, ActionType.Shower, ActionType.Grow, ActionType.Pro,
			ActionType.Body, ActionType.HairCut, ActionType.Chacra };

	private static ActionType[] actionTypesPro = { ActionType.ManWomen,
			ActionType.Clear, ActionType.Food, ActionType.Shower, ActionType.Grow,
			ActionType.Aroma, ActionType.Dream, ActionType.Body,
			ActionType.HairCut, ActionType.Chacra };
	public static ActionType[] getActionTypes()
	{
		if (MainActivity.extra)
		{return actionTypesPro;}
		else
		{return actionTypes;}
	}
	public ArrayList<MoonAction> createActions(MoonData md) {
		moonActions = new ArrayList<MoonAction>();
		// checkBad(md);
		{
		}
		for (ActionType item : getActionTypes()) {
			MoonAction ma = null;
			switch (item)

			{
			case ManWomen:
				ma = checkManWomen(md);
				break;
			case Clear:
				ma = checkClear(md);
				break;
			case Food:
				ma = checkFood(md);
				break;
			case Grow:
				ma = checkGrow(md);
				break;
			case Shower:
				ma = checkShower(md);
				break;
			case Aroma:
				ma = checkAroma(md, this.getResources());
				break;
			case Dream:
				ma = checkDream(md);
				break;
			case Body:
				ma = checkBody(md, this.getResources());
				break;
			case HairCut:
				ma = checkHairCut(md);
				break;
			case Chacra:
				ma = checkChacra(md);
				break;
			case Pro:
				ma = checkPro(md);
				break;
			default:
				break;
			}
			if (ma != null && ma.getImageId() > 0) {
				moonActions.add(ma);
			}
		}
		/*
		 * checkManWomen(md); MoonAction ma = checkShower(md); if
		 * (ma.getImageId() > 0) { moonActions.add(ma); } checkClear(md);
		 * checkFood(md); checkGrow(md); if (MainActivity.extra) {
		 * checkAroma(md,this); checkDream(md); } else { if (((MainActivity)
		 * getActivity()).checkInternetConnection()) { MoonAction moonAction =
		 * new MoonAction(); moonAction.setImageId(R.drawable.promo);
		 * moonAction.setDescription("gotopro"); moonActions.add(moonAction); }
		 * 
		 * } checkBody(md,this); checkHairCut(md); checkChacra(md); //
		 * checkGrow(md);
		 */
		return moonActions;
	}

	public static MoonAction checkPro(MoonData md) {
		MoonAction moonAction = new MoonAction();
		moonAction.setImageId(R.drawable.promo);
		moonAction.setDescription("gotopro");
		return moonAction;
	}

	public static MoonAction checkGrow(MoonData md) {
		MoonAction moonAction = new MoonAction();
		moonAction.setImageId(moonArray.checkGrowId(md));
		moonAction.setDescription(moonArray.tag);

		return moonAction;
	}

	public static MoonAction checkManWomen(MoonData md) {
		MoonAction moonAction = new MoonAction();
		if (md.getSign().contentEquals("scorpio")) {
			moonAction.setImageId(R.drawable.manwomen_question);
			moonAction.setDescription("manwomen_not_scorpio");
		}
		if (moonArray.manWomenNot.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.manwomen_not);
			moonAction.setDescription("manwomen_not");
		}
		if (moonArray.manWomen.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.manwomen);
			moonAction.setDescription("manwomen");
		}

		return moonAction;
	}

	public static MoonAction checkShower(MoonData md) {
		MoonAction moonAction = new MoonAction();
		if (moonArray.showerNot.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.shower_not);
			moonAction.setDescription("shower_not");
		}
		if (moonArray.shower.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.shower);
			moonAction.setDescription("shower");
		}
		// if (moonAction.getImageId() > 0) {
		// moonActions.add(moonAction);
		// }
		return moonAction;
	}

	public static MoonAction checkClear(MoonData md) {
		MoonAction moonAction = new MoonAction();
		if (moonArray.clear.contains(md.getMoonDay())) {
			moonAction.setDescription("clear");
			moonAction.setImageId(R.drawable.clear);
		}
		return moonAction;
	}

	public static MoonAction checkFood(MoonData md) {
		MoonAction moonAction = new MoonAction();
		moonAction.setImageId(moonArray.checkFood(md));
		moonAction.setDescription(moonArray.tag);

		return moonAction;
	}

	public static MoonAction checkBody(MoonData md, Resources f) {
		MoonAction moonAction = new MoonAction();
		moonAction.setImageId(f.getIdentifier(MainActivity.PACKAGE_NAME
				+ ":drawable/" + md.getSign() + "_body", null, null));
		moonAction.setDescription(md.getSign() + "_body");
		return moonAction;

	}

	public static MoonAction checkHairCut(MoonData md) {
		MoonAction moonAction = new MoonAction();
		if (moonArray.haircut.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.haircut);
			moonAction.setDescription("haircut");
		}
		if (moonArray.haircutNot.contains(md.getMoonDay())) {
			moonAction.setImageId(R.drawable.haircut_not);
			moonAction.setDescription("haircut_not");
		}
		return moonAction;
	}

	public static MoonAction checkChacra(MoonData md) {
		MoonAction moonAction = new MoonAction();
		int id;
		String tag = "";
		switch (md.getMoonDay()) {
		case 2:
		case 3:
		case 28:
		case 29:
		case 2901:
		case 3001:
		case 102:
			tag = "muladhara";
			id = R.drawable.muladhara;
			break;
		case 4:
		case 5:
		case 26:
		case 27:
			tag = "svadhistana";
			id = R.drawable.svadhistana;
			break;
		case 6:
		case 7:
		case 24:
		case 25:
			tag = "manipura";
			id = R.drawable.manipura;
			break;
		case 8:
		case 9:
		case 22:
		case 23:
			tag = "anahata";
			id = R.drawable.anahata;
			break;
		case 10:
		case 11:
		case 20:
		case 21:
			tag = "vishudha";
			id = R.drawable.vishudha;
			break;
		case 12:
		case 13:
		case 18:
		case 19:
			tag = "adgana";
			id = R.drawable.adgana;
			break;
		case 14:
		case 15:
		case 16:
		case 17:
			tag = "sahasrara";
			id = R.drawable.sahasrara;
			break;
		default:
			id = R.drawable.no_data;
		}
		if (id != R.drawable.no_data) {
			if (md.getMoonDay() < 16) {
				tag = tag + "_fill";
			} else
				tag = tag + "_realize";
		}

		if (id != R.drawable.no_data) {
			moonAction.setImageId(id);
			moonAction.setDescription(tag);
		}
		return moonAction;

	}

	public static MoonAction checkAroma(MoonData md, Resources f) {
		MoonAction moonAction = new MoonAction();
		moonAction.setImageId(f.getIdentifier(MainActivity.PACKAGE_NAME
				+ ":drawable/" + moonArray.aromaData.get(md.getMoonDay()),
				null, null));
		moonAction.setDescription(moonArray.aromaData.get(md.getMoonDay()));
		return moonAction;
	}

	public static MoonAction checkDream(MoonData md) {
		MoonAction moonAction = new MoonAction();
		int id;
		switch (md.getMoonDay()) {
		case 1:
		case 4:
		case 6:
		case 7:
		case 20:
		case 25:
		case 30:
		case 3001:
		case 102:
			id = R.drawable.dream_prophetic;
			break;
		case 2:
		case 3:
		case 5:
		case 8:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 22:
		case 23:
		case 24:
		case 27:
		case 28:
			id = R.drawable.dream_sign;
			break;
		default:
			id = R.drawable.dream_usual;
		}

		if (id != R.drawable.no_data) {
			moonAction.setImageId(id);
			moonAction.setDescription("dream" + md.getMoonDay());
		}
		return moonAction;
	}

	public static class MoonAction {
		int imageId;
		String description;

		public int getImageId() {
			return imageId;
		}

		public void setImageId(int imageId) {
			this.imageId = imageId;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.next_activity: {
			int squareId = 1;
			square1.setImageResource(R.drawable.square);
			square1.setTag("");
			square2.setImageResource(R.drawable.square);
			square2.setTag("");
			square3.setImageResource(R.drawable.square);
			square3.setTag("");
			square4.setImageResource(R.drawable.square);
			square4.setTag("");

			nextActivityView.setEnabled(false);
			nextActivityView.setImageResource(R.drawable.square);
			prevActivityView.setEnabled(true);
			prevActivityView.setImageResource(R.drawable.moon_arrow);
			for (int i = 4; i < moonActions.size(); i++) {

				MoonAction s = moonActions.get(i);
				switch (i - 3) {
				case 1: {
					square1.setImageResource(s.getImageId());
					square1.setTag(s.getDescription());
					break;
				}
				case 2: {
					square2.setImageResource(s.getImageId());
					square2.setTag(s.getDescription());
					break;
				}
				case 3: {
					square3.setImageResource(s.getImageId());
					square3.setTag(s.getDescription());
					break;
				}
				case 4: {
					square4.setImageResource(s.getImageId());
					square4.setTag(s.getDescription());
					break;
				}
				}

				squareId++;
				if (squareId == 5)
					break;

			}

			break;
		}
		case R.id.prev_activity: {
			int squareId = 1;
			square1.setImageResource(R.drawable.square);
			square2.setImageResource(R.drawable.square);
			square3.setImageResource(R.drawable.square);
			square4.setImageResource(R.drawable.square);

			prevActivityView.setEnabled(false);
			prevActivityView.setImageResource(R.drawable.square);

			if (moonActions.size() > 4) {
				nextActivityView.setEnabled(true);
				nextActivityView.setImageResource(R.drawable.moon_arrow);
			} else {
				nextActivityView.setEnabled(false);
				nextActivityView.setImageResource(R.drawable.square);
			}
			for (int i = 0; i < moonActions.size(); i++) {

				MoonAction s = moonActions.get(i);
				switch (i + 1) {
				case 1: {
					square1.setImageResource(s.getImageId());
					square1.setTag(s.getDescription());
					break;
				}
				case 2: {
					square2.setImageResource(s.getImageId());
					square2.setTag(s.getDescription());
					break;
				}
				case 3: {
					square3.setImageResource(s.getImageId());
					square3.setTag(s.getDescription());
					break;
				}
				case 4: {
					square4.setImageResource(s.getImageId());
					square4.setTag(s.getDescription());
					break;
				}
				}

				squareId++;
				if (squareId == 5)
					break;

			}

			break;
		}
		} // TODO Auto-generated method stub

	}
}