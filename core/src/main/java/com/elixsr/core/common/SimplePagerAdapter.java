package com.elixsr.core.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * The SimplePagerAdapter class uses an ArrayList to manage a collection of fragments.
 *
 * Created by markmcshane on 08/09/16.
 */
public class SimplePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Item> pageItems = new ArrayList<>();

    public SimplePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return pageItems.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    public void addPage(String name, Fragment fragment){
        pageItems.add(new Item(name, fragment));
    }

    public void removePage(int position){
        pageItems.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageItems.get(position).getName();
    }

    private class Item {
        private String name;
        private Fragment fragment;

        public Item(String name, Fragment fragment) {
            this.name = name;
            this.fragment = fragment;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }
}
