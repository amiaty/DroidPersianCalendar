package com.byagowi.persiancalendar.view.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byagowi.persiancalendar.Adapter.CalendarAdapter;
import com.byagowi.persiancalendar.R;
import com.byagowi.persiancalendar.Utils;
import com.github.praytimes.Clock;
import com.github.praytimes.Coordinate;
import com.github.praytimes.PrayTime;
import com.github.praytimes.PrayTimesCalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import calendar.CivilDate;
import calendar.DateConverter;
import calendar.PersianDate;

public class CalendarMainFragment extends Fragment implements View.OnClickListener {

    public static final int MONTHS_LIMIT = 1200;
    private ViewPager viewPager;
    private final Utils utils = Utils.getInstance();

    private Calendar c = Calendar.getInstance();
    private char[] digits;
    private boolean clockIn24;

    private Coordinate coord;

    private PrayTimesCalculator ptc;
    private TextView azan1;
    private TextView azan2;
    private TextView azan3;
    private TextView azan4;
    private TextView azan5;
    private TextView aftab1;
    private TextView aftab2;
    private TextView aftab3;

    private TextView weekDayName;
    private TextView miladiDate;
    private TextView ghamariDate;
    private TextView shamsiDate;
    private TextView eventTitle;
    private TextView today;

    private CardView owghat;
    private CardView event;

    private LinearLayoutCompat owghat1;
    private LinearLayoutCompat owghat2;
    private LinearLayoutCompat owghat3;
    private LinearLayoutCompat owghat4;
    private LinearLayoutCompat owghat5;
    private LinearLayoutCompat owghat6;
    private LinearLayoutCompat owghat7;
    private LinearLayoutCompat owghat8;

    private View divider1;
    private View divider2;
    private View divider3;
    private View divider4;
    private View divider5;
    private View divider6;
    private View divider7;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_calendar, container, false);

        owghat1 = (LinearLayoutCompat) view.findViewById(R.id.owghat1);
        owghat2 = (LinearLayoutCompat) view.findViewById(R.id.owghat2);
        owghat3 = (LinearLayoutCompat) view.findViewById(R.id.owghat3);
        owghat4 = (LinearLayoutCompat) view.findViewById(R.id.owghat4);
        owghat5 = (LinearLayoutCompat) view.findViewById(R.id.owghat5);
        owghat6 = (LinearLayoutCompat) view.findViewById(R.id.owghat6);
        owghat7 = (LinearLayoutCompat) view.findViewById(R.id.owghat7);
        owghat8 = (LinearLayoutCompat) view.findViewById(R.id.owghat8);

        divider1 = view.findViewById(R.id.div1);
        divider2 = view.findViewById(R.id.div2);
        divider3 = view.findViewById(R.id.div3);
        divider4 = view.findViewById(R.id.div4);
        divider5 = view.findViewById(R.id.div5);
        divider6 = view.findViewById(R.id.div6);
        divider7 = view.findViewById(R.id.div7);

        miladiDate = (TextView) view.findViewById(R.id.miladi_date);
        ghamariDate = (TextView) view.findViewById(R.id.ghamari_date);
        weekDayName = (TextView) view.findViewById(R.id.week_day_name);
        shamsiDate = (TextView) view.findViewById(R.id.shamsi_date);
        miladiDate = (TextView) view.findViewById(R.id.miladi_date);
        ghamariDate = (TextView) view.findViewById(R.id.ghamari_date);
        today = (TextView) view.findViewById(R.id.today);

        azan1 = (TextView) view.findViewById(R.id.azan1);
        azan2 = (TextView) view.findViewById(R.id.azan2);
        azan3 = (TextView) view.findViewById(R.id.azan3);
        azan4 = (TextView) view.findViewById(R.id.azan4);
        azan5 = (TextView) view.findViewById(R.id.azan5);
        aftab1 = (TextView) view.findViewById(R.id.aftab1);
        aftab2 = (TextView) view.findViewById(R.id.aftab2);
        aftab3 = (TextView) view.findViewById(R.id.aftab3);

        eventTitle = (TextView) view.findViewById(R.id.event_title);

        CardView infoDay = (CardView) view.findViewById(R.id.info_day);
        owghat = (CardView) view.findViewById(R.id.owghat);
        event = (CardView) view.findViewById(R.id.event);

        viewPager = (ViewPager) view.findViewById(R.id.calendar_pager);

        utils.loadHolidays(getResources().openRawResource(R.raw.holidays));
        utils.loadLanguageFromSettings(getContext());

        digits = utils.preferredDigits(getContext());
        clockIn24 = utils.clockIn24(getContext());
        coord = utils.getCoordinate(getContext());
        ptc = new PrayTimesCalculator(utils.getCalculationMethod(getContext()));

        viewPager.setAdapter(new CalendarAdapter(getActivity().getSupportFragmentManager()));
        viewPager.setCurrentItem(MONTHS_LIMIT / 2);

        infoDay.setOnClickListener(this);
        owghat.setOnClickListener(this);

        today.setOnClickListener(this);

        return view;
    }

    public void changeMonth(int position) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + position, true);
    }

    public void selectDay(PersianDate persianDate) {
        CivilDate civilDate = DateConverter.persianToCivil(persianDate);
        weekDayName.setText(utils.getWeekDayName(persianDate));
        shamsiDate.setText(utils.dateToString(persianDate, digits));
        miladiDate.setText(utils.dateToString(civilDate, digits));
        ghamariDate.setText(utils.dateToString(DateConverter.civilToIslamic(civilDate), digits));

        if (isToday(civilDate)) {
            today.setVisibility(View.GONE);
        } else {
            today.setVisibility(View.VISIBLE);
        }

        setOwghat(civilDate);
        showEvent(persianDate);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void addEventOnCalendar(PersianDate persianDate) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        CivilDate civil = DateConverter.persianToCivil(persianDate);
        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                utils.dayTitleSummary(persianDate, digits));
        Calendar time = Calendar.getInstance();
        time.set(civil.getYear(), civil.getMonth() - 1, civil.getDayOfMonth());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                time.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                time.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        startActivity(intent);
    }

    private void showEvent(PersianDate persianDate) {
        String holidayTitle = utils.getHolidayTitle(persianDate);

        if (holidayTitle != null) {
            eventTitle.setText(holidayTitle);
            event.setVisibility(View.VISIBLE);

        } else {
            event.setVisibility(View.GONE);

        }
    }

    private void setOwghat(CivilDate civilDate) {
        if (coord == null) {
            return;
        }

        c.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());
        Date date = c.getTime();

        Map<PrayTime, Clock> prayTimes = ptc.calculate(date, coord);

        azan1.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.IMSAK), digits, clockIn24));
        aftab1.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.SUNRISE), digits, clockIn24));
        azan2.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.DHUHR), digits, clockIn24));
        azan3.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.ASR), digits, clockIn24));
        aftab2.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.SUNSET), digits, clockIn24));
        azan4.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.MAGHRIB), digits, clockIn24));
        azan5.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.ISHA), digits, clockIn24));
        aftab3.setText(utils.getPersianFormattedClock(prayTimes.get(PrayTime.MIDNIGHT), digits, clockIn24));

        owghat.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_day:
                miladiDate.setVisibility(View.VISIBLE);
                ghamariDate.setVisibility(View.VISIBLE);
                break;

            case R.id.owghat:
                owghat1.setVisibility(View.VISIBLE);
                owghat2.setVisibility(View.VISIBLE);
                owghat3.setVisibility(View.VISIBLE);
                owghat4.setVisibility(View.VISIBLE);
                owghat5.setVisibility(View.VISIBLE);
                owghat6.setVisibility(View.VISIBLE);
                owghat7.setVisibility(View.VISIBLE);
                owghat8.setVisibility(View.VISIBLE);

                divider1.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                divider3.setVisibility(View.VISIBLE);
                divider4.setVisibility(View.VISIBLE);
                divider5.setVisibility(View.VISIBLE);
                divider6.setVisibility(View.VISIBLE);
                divider7.setVisibility(View.VISIBLE);
                break;

            case R.id.today:
                bringTodayYearMonth();
                break;
        }
    }

    private void bringTodayYearMonth() {
        if (viewPager.getCurrentItem() != MONTHS_LIMIT / 2) {
            viewPager.setCurrentItem(MONTHS_LIMIT / 2);
        }
        selectDay(Utils.getToday());
    }

    private boolean isToday(CivilDate civilDate) {
        CivilDate today = new CivilDate();
        return today.getYear() == civilDate.getYear()
                && today.getMonth() == civilDate.getMonth()
                && today.getDayOfMonth() == civilDate.getDayOfMonth();
    }
}
