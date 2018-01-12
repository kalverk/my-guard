package com.myguard;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by kalver on 12/01/18.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void toolbarExist() throws Exception {
        MainActivity mainActivity = rule.getActivity();
        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
        assertThat(toolbar, notNullValue());
    }

    @Test
    public void menuExist() throws Exception {
        MainActivity mainActivity = rule.getActivity();
        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);

        Menu menu = toolbar.getMenu();
        assertThat(menu, notNullValue());
    }

    @Test
    public void menuItemExist() throws Exception {
        MainActivity mainActivity = rule.getActivity();
        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);

        Menu menu = toolbar.getMenu();
        MenuItem menuItem = menu.getItem(0);
        assertThat(menuItem, notNullValue());
        assertEquals(R.id.action_settings, menuItem.getItemId());
    }

    @Test
    public void lockButtonExist() throws Exception {
        MainActivity mainActivity = rule.getActivity();
        Button button = mainActivity.findViewById(R.id.toggleAlarm);
        assertThat(button, notNullValue());
    }
}
