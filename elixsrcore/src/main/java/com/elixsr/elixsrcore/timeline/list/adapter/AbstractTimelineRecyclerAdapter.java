package com.elixsr.elixsrcore.timeline.list.adapter;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elixsr.elixsrcore.BuildConfig;
import com.elixsr.elixsrcore.R;
import com.elixsr.elixsrcore.timeline.list.models.TimelineInterface;
import com.elixsr.elixsrcore.common.util.EncryptionHelper;
import com.elixsr.elixsrcore.timeline.list.viewholder.AbstractTimelineViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Niall McShane on 11/06/2016.
 */
public abstract class AbstractTimelineRecyclerAdapter<VH extends AbstractTimelineViewHolder, E
        extends TimelineInterface> extends
        RecyclerView
        .Adapter<VH> {

    private static final int MIN_ITEMS_ADVERTISEMENT_THRESHOLD = 4;
    private static final int MAX_ADS_NUMBER = 1;

    protected List<E> dataset;

    private static final String TAG = "TimelineRecyclerAdapter";
    private Class<VH> viewHolderClass;
    private List<ListItem> listItems;
    private int monthHeadingLayout;
    private int dayHeadingLayout;
    private int contentLayout;
    private int advertisementLayout = R.layout.timeline_advertisement_item;
    private int adCount = 0;

    private Map<Integer, Integer> monthHeadingLookup = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> dayHeadingLookup = new HashMap<Integer, Integer>();

    //number corresponds to dataset item e.g. 11 = 11 in dataset
    private List<DatasetItemDetail> datasetItemDetailLookup = new ArrayList<>();

    public AbstractTimelineRecyclerAdapter(Class<VH> viewHolderClass, int
            contentLayout){
        this(viewHolderClass,contentLayout, R.layout.timeline_month_heading_item, R
                .layout.timeline_day_heading_item);
    }

    // TODO: probably should add a builder
    public AbstractTimelineRecyclerAdapter(Class<VH> viewHolderClass, int contentLayout, int
            monthHeadingLayout, int dayHeadingLayout) {
        this.contentLayout = contentLayout;
        this.monthHeadingLayout = monthHeadingLayout;
        this.dayHeadingLayout = dayHeadingLayout;
        this.viewHolderClass = viewHolderClass;

        this.calculateListItems();
    }

    //region RecyclerView Functions
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        int layoutId;

        switch(viewType){
            case(ListItem.TYPE_MONTH_HEADING):
                layoutId = monthHeadingLayout;
                break;
            case(ListItem.TYPE_DAY_HEADING):
                layoutId = dayHeadingLayout;
                break;
            case(ListItem.TYPE_CONTENT):
                layoutId = contentLayout;
                break;
            case(ListItem.TYPE_ADVERTISEMENT):
                layoutId = advertisementLayout;

//                Log.i(TAG, "onCreateViewHolder: Create advertisement list size " +
//                        advertisementList.size() + " current = " + currentAdvertisement);
//
//                view = advertisementList.get(currentAdvertisement);
//
//                if(currentAdvertisement < advertisementList.size()-1){
//                    currentAdvertisement++;
//                }else{
//                    currentAdvertisement = 0;
//                }

                break;
            default:
                layoutId = contentLayout;
                break;
        }

        if(view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(layoutId, parent, false);
        }

        try {
            Constructor<VH> constructor = viewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        ListItem currentListItem = listItems.get(position);
        E currentModel = listItems.get(position).getModel();

        final int viewType = listItems.get(position).getType();

        switch(viewType){
            case(ListItem.TYPE_MONTH_HEADING):
                holder.setMonthText(currentModel.getMonthHeadingText());
                break;
            case(ListItem.TYPE_DAY_HEADING):
                holder.setDayText(currentModel.getDayHeadingText());
                break;
            case(ListItem.TYPE_ADVERTISEMENT):
//                holder.showAdvertisement();

                View view = advertisementMap.get(position);

                holder.addAdvertisement(view);
                break;
            default:
                //for the items content, call the implemented function
                populateContentItem(holder, currentModel, dataset.indexOf(currentModel));
                break;
        }


        final View itemView = holder.itemView;
        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        boolean mMarginsFixed = true;

        int mHeaderDisplay = LayoutManager.LayoutParams.HEADER_STICKY;

        lp.setColumnWidth(100);

        if (listItems.get(position).getType() == ListItem.TYPE_DAY_HEADING) {
            lp.headerDisplay = mHeaderDisplay;
//            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
//                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            } else {
//                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            }

//            lp.headerEndMarginIsAuto = !mMarginsFixed;
//            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }

        lp.setSlm(LinearSLM.ID);


        //work out first in month dynamically?
        ListItem tempListItem = listItems.get(position);

        if(tempListItem.getType() == ListItem.TYPE_MONTH_HEADING || tempListItem.getType() ==
                ListItem.TYPE_DAY_HEADING || tempListItem.getType() ==
                ListItem.TYPE_ADVERTISEMENT){
            lp.setFirstPosition(position);
        }else {
            lp.setFirstPosition(listItems.indexOf(currentListItem.getFirstInSectionListItem()));
        }
        itemView.setLayoutParams(lp);
    }

    public abstract void populateContentItem(VH holder, E currentModel, int position);

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }
    //endregion

    //region Dataset functions
    private E getItem(int position){
        return dataset.get(position);
    }

    public List<E> getDataset() {
        return dataset;
    }

    public void setDataset(List<E> dataset) {
        this.dataset = dataset;
    }
    //endregion

    //region Mutation Functions
    private void printListItemDetails(){
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
        final String outputFormat = "Item %3d %-20s %30s";

        Log.d(TAG, "printListItemDetails: \nprinting \n");
        
        //dump the whole list structure
        DatasetItemDetail datasetItemDetail;
        String dateString;
        for (int i=0; i<datasetItemDetailLookup.size(); i++) {
            datasetItemDetail = datasetItemDetailLookup.get(i);

            dateString = DATE_FORMAT.format(datasetItemDetail.contentListItem
                            .getModel()
                            .getDate());

            Log.d(TAG, "printListItemDetails: " + String.format(outputFormat, i, dateString, datasetItemDetail
                    .toString()));
        }

        Log.d(TAG, "printListItemDetails: \nending \n");
    }

    public void removeItem(int position){

        Log.d(TAG, "removeItem: starting removal");
//        printListItemDetails();


        //get the relevant datasetItemDetail object
        DatasetItemDetail datasetItemDetail = datasetItemDetailLookup.get(position);
        DatasetItemDetail nextDatasetItemDetail = null;

        //index's
        int monthHeadingIndex = 0;
        int dayHeadingIndex = 0;
        int contentIndex = 0;

        //flags
        boolean deleteMonthHeading = false;
        boolean deleteDayHeading = false;

        Log.d(TAG, "removeItem: current "+position+ " " + datasetItemDetail.toString());

        boolean isLastInDataset = true;
        //check if we are at the last item
        if(position < dataset.size()){
            isLastInDataset = false;
            int nextPosition = position+1;

            nextDatasetItemDetail = datasetItemDetailLookup.get(nextPosition);
            Log.d(TAG, "removeItem: next "+nextPosition+ " " + nextDatasetItemDetail.toString());
        }

        Log.d(TAG, "removeItem: is last? " + isLastInDataset);

        if(datasetItemDetail.containsMonthHeading){

            Log.d(TAG, "removeItem: position " + position + " contains a month heading");

            if(nextDatasetItemDetail != null){

                if(nextDatasetItemDetail.containsMonthHeading){
                    deleteMonthHeading = true;
                }else{
                    //item below doesn't have a month heading
                    //it should take the heading
                    nextDatasetItemDetail.addMonthHeading(datasetItemDetail.monthHeadingListItem);
                }
            }else{
                deleteMonthHeading = true;
            }

            //if the dataset entry below is a different month, remove
            if(isLastInDataset || deleteMonthHeading){

                monthHeadingIndex = listItems.indexOf(datasetItemDetail.monthHeadingListItem);

                Log.d(TAG, "removeItem: Removing month heading at " + monthHeadingIndex);

                if(monthHeadingIndex > 0){
                    listItems.remove(monthHeadingIndex);
                    deleteMonthHeading = false;
                }else{
                    Log.w(TAG, "removeItem: Could not find month heading when it was meant to!");
                }
//                notifyItemRemoved(monthHeadingIndex);


            }
        }

        if(datasetItemDetail.containsDayHeading){

            Log.d(TAG, "removeItem: position " + position + " contains a day heading");

            if(nextDatasetItemDetail != null){
                if(nextDatasetItemDetail.containsDayHeading) {
                    deleteDayHeading = true;
                }else{
                    //item below doesn't have a day heading
                    //it should take the heading
                    nextDatasetItemDetail.addDayHeading(datasetItemDetail.dayHeadingListItem);
                }
            }else{
                deleteDayHeading = true;
            }

            if(isLastInDataset || deleteDayHeading) {

                Log.i(TAG, "removeItem: Removing day heading at " + dayHeadingIndex);

                dayHeadingIndex = listItems.indexOf(datasetItemDetail.dayHeadingListItem);

                listItems.remove(dayHeadingIndex);
//                notifyItemRemoved(dayHeadingIndex);
            }
        }

        //now we should actually delete the actual content - it's always there
        Log.d(TAG, "removeItem: Removing content at " + contentIndex);
        contentIndex = listItems.indexOf(datasetItemDetail.contentListItem);
        listItems.remove(contentIndex);
//        notifyItemRemoved(contentIndex);

        //IMPORTANT: Instead of removing 1 item at a time, we need to remove them all at once to ensure the underlying layout manager rebinds the items correctly.
        int removalStart = contentIndex;
        if(deleteDayHeading){
            removalStart--;
        }
        if(deleteMonthHeading){
            removalStart--;
        }

        //hack, ensure the start value is greater than 0!
        if(removalStart < 0){
            removalStart = 0;
        }

        //the amount should be num - start
        // i.e. position 30, 3 long
        Log.i(TAG, "removeItem: removalStart " + removalStart + " contentIndex " + contentIndex);
//        notifyItemRangeRemoved(removalStart, contentIndex - removalStart);



        //if the next item takes over the headings, we need to update the references
        if(!isLastInDataset){

            //if we didn't remove the heading
            if(!deleteMonthHeading){
                nextDatasetItemDetail.addMonthHeading(datasetItemDetail.monthHeadingListItem);
            }

            if(!deleteDayHeading){
                nextDatasetItemDetail.addDayHeading(datasetItemDetail.dayHeadingListItem);
            }
        }

        //finally remove the item from the dataset lookup
        datasetItemDetailLookup.remove(position);


        //tell the adapter to update every item from the fisrt in this items section to the end
        int firstInSectionPosition = listItems.indexOf(datasetItemDetail.getFirstInSection());

        if(firstInSectionPosition >= 0 && listItems.size() > 0) {
            Log.i(TAG, "removeItem: notify item changed - firstInSection: " + firstInSectionPosition + " distance: " + String.valueOf((getItemCount() - 1) - firstInSectionPosition));
            notifyItemRangeChanged(firstInSectionPosition, (getItemCount() - 1) - firstInSectionPosition);
        }

        Log.i(TAG, "removeItem: finishing remove - new state:");
//        printListItemDetails();
    }

    @Deprecated
    public void addItem(int position){

        //left and right items
        DatasetItemDetail previousDatasetItemDetail = null;
        DatasetItemDetail nextDatasetItemDetail = null;
        DatasetItemDetail currentDatasetItemDetail = new DatasetItemDetail();

        //add a empty datasetiItemDetail object, as a placeholder...
        datasetItemDetailLookup.add(position, currentDatasetItemDetail);

        //neighbour items in dataset
        E previousModel = null;
        E nextModel = null;
        E currentModel = dataset.get(position);

//        Log.i(TAG, "addItem: CONTENT " + ((DreamModel) currentModel).getDreamText());

        //flags
        boolean isFirst = false;
        boolean isLast = false;
        boolean mustCreateNewMonthHeading = false;
        boolean finished = false;

        //possible listItem objects
        ListItem monthHeadingListItem = new ListItem(ListItem.TYPE_MONTH_HEADING, position, null,
                currentModel);
        ListItem dayHeadingListItem = new ListItem(ListItem.TYPE_DAY_HEADING, position, null,
                currentModel);
        ListItem contentListItem = new ListItem(ListItem.TYPE_CONTENT, position, dayHeadingListItem,
                currentModel);

        //location properties
        int newMonthHeadingListItemLocation = 0;
        int newDayHeadingListItemLocation = 0;
        int newContentListItemLocation = 0;

        //if it is not the first item in the dataset
        if(position > 0){
            previousDatasetItemDetail = datasetItemDetailLookup.get(position-1);
            previousModel = dataset.get(position-1);
        }else{
            isFirst = true;
        }

        //if it is not the last item in the dataset
        if(position < datasetItemDetailLookup.size()-1){
            nextDatasetItemDetail = datasetItemDetailLookup.get(position+1);
            nextModel = dataset.get(position+1);
        }else{
            isLast = true;
        }

        //do we attach the previous month/day?
        if(previousDatasetItemDetail != null){

            //if it is the same day - we just add our list item after
            if(isSameDay(previousModel, currentModel)){
                newContentListItemLocation = listItems.indexOf(previousDatasetItemDetail
                        .contentListItem)+1;

                //need to obtain reference
//                contentListItem.firstInSectionListItem = listItems.get(listItems.indexOf
//                        (previousDatasetItemDetail
//                        .contentListItem.firstInSectionListItem));
                contentListItem.firstInSectionListItem = previousDatasetItemDetail
                        .getFirstInSection();
                listItems.add(newContentListItemLocation, contentListItem);
                notifyItemInserted(newContentListItemLocation);
                finished = true;

                Log.d(TAG, "addItem: there was something previous - sameDay " + position);
            }

            //if the previous item is the same month - we add a day heading
            else if(isSameMonth(previousModel, currentModel)){

                //location of new day heading
                newDayHeadingListItemLocation = listItems.indexOf
                        (previousDatasetItemDetail.contentListItem)+1;

                //add the day heading to replace the one that was ther
                listItems.add(newDayHeadingListItemLocation, dayHeadingListItem);
                notifyItemInserted(newDayHeadingListItemLocation);
                currentDatasetItemDetail.addDayHeading(dayHeadingListItem);

                //add the content behind it
                listItems.add(newDayHeadingListItemLocation+1, contentListItem);
                notifyItemInserted(newDayHeadingListItemLocation+1);
                finished = true;


                Log.d(TAG, "addItem: there was something previous - sameMonth " + position);
            }
            mustCreateNewMonthHeading = false;
        }else{
            mustCreateNewMonthHeading = true;
        }

        //check if we can take over the mantle of the next month
        if(!finished && nextDatasetItemDetail != null && (nextDatasetItemDetail
                .containsMonthHeading ||
                nextDatasetItemDetail.containsDayHeading)){

            //does it contain a month?
            if(nextDatasetItemDetail.containsMonthHeading){
                //check if we have the same month?
                if(isSameMonth(currentModel, nextModel)){

                    /*
                    if it is the same month

                    we should:
                    - check if it's the same day - if it is, we can take over its heading mantle
                    -
                     */



                    addRelevantListItemTop(currentModel, nextModel, contentListItem,
                            nextDatasetItemDetail,newContentListItemLocation,
                            newDayHeadingListItemLocation, dayHeadingListItem);

                    //if it is the same month, and same day, we are now a step ahead, so we
                    // should take the month title
                    if(isSameDay(currentModel, nextModel)){

                    }


                    finished = true;
                }
            }else if(nextDatasetItemDetail.containsDayHeading){
                //get the date of the next item
                //check if they are the same date
                addRelevantListItemTop(currentModel, nextModel, contentListItem,
                        nextDatasetItemDetail,newContentListItemLocation,
                        newDayHeadingListItemLocation, dayHeadingListItem);
                finished = true;
            }
            mustCreateNewMonthHeading = false;

        }else{
            mustCreateNewMonthHeading = true;
        }

        if(!finished && mustCreateNewMonthHeading){

            int listItemLocation = 0;

            if(isLast){
                listItems.add(monthHeadingListItem);
                notifyItemInserted(listItems.size()-1);
                listItems.add(dayHeadingListItem);
                notifyItemInserted(listItems.size()-1);
                listItems.add(contentListItem);
                notifyItemInserted(listItems.size()-1);
            }else if(isFirst){
                listItems.add(0, monthHeadingListItem);
                listItems.add(1, dayHeadingListItem);
                listItems.add(2, contentListItem);
                notifyItemInserted(0);
                notifyItemInserted(1);
                notifyItemInserted(2);
            }else{

                //something has to be drawn in the middle of the other 2
                //it is gonna take the place of the next item
                //we know there is a next item
                int newLocation = 0;

                //we just need to find where to start it though
                if(nextDatasetItemDetail.containsMonthHeading){
                    newLocation = listItems.indexOf(nextDatasetItemDetail.monthHeadingListItem);
                }else if(nextDatasetItemDetail.containsDayHeading){
                    newLocation = listItems.indexOf(nextDatasetItemDetail.dayHeadingListItem);
                }else{
                    newLocation = listItems.indexOf(nextDatasetItemDetail.contentListItem);
                }

                listItems.add(newLocation, monthHeadingListItem);
                listItems.add(newLocation + 1, dayHeadingListItem);
                listItems.add(newLocation + 2, contentListItem);
                notifyItemInserted(newLocation);
                notifyItemInserted(newLocation + 1);
                notifyItemInserted(newLocation + 2);

            }

            currentDatasetItemDetail.addMonthHeading(monthHeadingListItem);
            currentDatasetItemDetail.addDayHeading(dayHeadingListItem);
        }

        currentDatasetItemDetail.setContentLocation(contentListItem);

        //fix to ensure the items located below are properly refreshed
        //this logic relies on the fact that the item is not the last item - otherwise it will
        // not need a refresh.
        //it is only from the current item, as the items found after may now have out of date
        // first in section pointers
        if(!isLast){
            int nextItemLocation = listItems.indexOf(currentDatasetItemDetail.getFirstInSection())+1;
            notifyItemRangeChanged(nextItemLocation,
                    (getItemCount())-nextItemLocation);
        }


//        printListItemDetails();

    }

    private void addRelevantListItemTop(E currentModel, E nextModel, ListItem contentListItem,
                                        DatasetItemDetail nextDatasetItemDetail, int newContentListItemLocation, int newDayHeadingListItemLocation, ListItem dayHeadingListItem){
        //its the same day - take its place
        if(isSameDay(currentModel, nextModel)){
            //take over the month
            contentListItem.firstInSectionListItem = nextDatasetItemDetail
                    .dayHeadingListItem;
            newContentListItemLocation = listItems.indexOf(nextDatasetItemDetail.contentListItem);
            listItems.add(newContentListItemLocation, contentListItem);
            notifyItemInserted(newContentListItemLocation);

        }else{
            //its a different day, insert a day heading, and

            //location of new day heading
            newDayHeadingListItemLocation = listItems.indexOf
                    (nextDatasetItemDetail.dayHeadingListItem);

            //add the day heading to replace the one that was ther
            listItems.add(newDayHeadingListItemLocation, dayHeadingListItem);
            notifyItemInserted(newDayHeadingListItemLocation);

            //add the content behind it
            listItems.add(newDayHeadingListItemLocation+1, contentListItem);
            notifyItemInserted(newDayHeadingListItemLocation+1);
        }

        //remove the references to the old headers
        nextDatasetItemDetail.changeToHeaderLess();
    }

    public void changeItem(int position){
        //get the details for that item from the lookup table
        DatasetItemDetail datasetItemDetail = datasetItemDetailLookup.get(position);

        //set the listitem to value found from lookup table - using member as it's quicker than
        // funciton...
        ListItem contentListItem = datasetItemDetail.contentListItem;

        //change the model
        contentListItem.model = dataset.get(position);

        int location = listItems.indexOf(contentListItem);

        Log.i(TAG, "changeItem: update location/index: " + location);

        //notify the recycler view that it has changed
        notifyItemChanged(location);
    }

    public int computeStartPositionForDate(Date date){

        //default to the start
        int foundPosition = 0;

        //TODO: look into efficiency issues
        long minDiff = 0;
        int minDiffLocation = 0;
        int currentPosition;
        for(int i=0; i<listItems.size(); i++){

            currentPosition = listItems.get(i).getPosition();

            //if the date matches
            if(isSameDay(dataset.get(currentPosition).getDate(), date)){
                return i;
            }else{
                //try to find the closest date
                long diff = Math.abs(dataset.get(currentPosition).getDate().getTime() - date
                        .getTime());

                if(diff < minDiff || minDiff == 0){
                    minDiff = diff;
                    minDiffLocation = i;
                }
            }
        }

        return minDiffLocation;
    }

    //helper functions
    protected void calculateListItems(){

        this.listItems = new LinkedList<ListItem>();
        this.datasetItemDetailLookup = new ArrayList<>();

        if(dataset==null || dataset.isEmpty()){
            return;
        }

        //preprocess models, find headings, and date headings
        int firstInSection = 0;
        int numExtraHeadings = 0;
        int count = 0;
        int currentDatePosition = 0;
        DatasetItemDetail datasetItemDetail;
        ListItem tempListItem;
        ListItem currentDayHeading = null;
        for (int i=0; i<dataset.size(); i++) {

            //create an item to help with later lookup
            datasetItemDetail = new DatasetItemDetail();


            if(isItemMonthHeader(i)){
                count = 0;

                //add relevant data to lookup
                monthHeadingLookup.put(i, listItems.size());


                firstInSection = i + numExtraHeadings;
                tempListItem = new ListItem(ListItem.TYPE_MONTH_HEADING, i, null,
                        dataset.get
                        (i));
                listItems.add(tempListItem);
                numExtraHeadings++;
//                count++;

                Log.d(TAG, "calculateListItems: MONTH\ti " + i + "\tstartPosition " +
                        firstInSection );

                datasetItemDetail.addMonthHeading(tempListItem);
            }

            if(isItemDayHeader(i)){
                dayHeadingLookup.put(i, listItems.size());


                firstInSection = i + numExtraHeadings;
                numExtraHeadings++;
                tempListItem = new ListItem(ListItem.TYPE_DAY_HEADING, i, null, dataset.get
                        (i));
                currentDayHeading = tempListItem;
                listItems.add(tempListItem);


                currentDatePosition = firstInSection;

                Log.d(TAG, "calculateListItems: DAY\ti " + i + " \tstartPosition" +
                        firstInSection );

                datasetItemDetail.addDayHeading(tempListItem);

            }

            tempListItem = new ListItem(ListItem.TYPE_CONTENT, i, currentDayHeading, dataset.get
                    (i));
            listItems.add(tempListItem);
            datasetItemDetail.setContentLocation(tempListItem);


            Log.i(TAG, "calculateListItems: "+ i + " current datasetItemDetail " + datasetItemDetail
                    .toString() + " size " + datasetItemDetailLookup.size());

            datasetItemDetailLookup.add(datasetItemDetail);

            Log.d(TAG, "calculateListItems: CONTENT\ti " + i + " \tstartPosition" +
                    currentDatePosition );
        }
    }

    public void addItemV2(int position){

        /*
        Setup
         */

        //left and right items
        DatasetItemDetail previousDatasetItemDetail = null;
        DatasetItemDetail nextDatasetItemDetail = null;
        DatasetItemDetail currentDatasetItemDetail = new DatasetItemDetail();

        //add a empty datasetiItemDetail object, as a placeholder...
        datasetItemDetailLookup.add(position, currentDatasetItemDetail);

        //neighbour items in dataset
        E previousModel = null;
        E nextModel = null;
        E currentModel = dataset.get(position);

        //flags
        boolean isFirst = false;
        boolean isLast = false;
        boolean mustCreateNewMonthHeading = false;
        boolean finished = false;

        //possible listItem objects
        ListItem monthHeadingListItem = new ListItem(ListItem.TYPE_MONTH_HEADING, position, null,
                currentModel);
        ListItem dayHeadingListItem = new ListItem(ListItem.TYPE_DAY_HEADING, position, null,
                currentModel);
        ListItem contentListItem = new ListItem(ListItem.TYPE_CONTENT, position, dayHeadingListItem,
                currentModel);

        //location properties
        int newMonthHeadingListItemLocation = 0;
        int newDayHeadingListItemLocation = 0;
        int newContentListItemLocation = 0;

        //if it is not the first item in the dataset
        if(position > 0){
            previousDatasetItemDetail = datasetItemDetailLookup.get(position-1);
            previousModel = dataset.get(position-1);
        }else{
            isFirst = true;
        }

        //if it is not the last item in the dataset
        if(position < datasetItemDetailLookup.size()-1){
            nextDatasetItemDetail = datasetItemDetailLookup.get(position+1);
            nextModel = dataset.get(position+1);
        }else{
            isLast = true;
        }

        /*
        PREVIOUS ITEM CHECK
        Add item to the same day as previous
         */
        //if we have a previous item
        if(previousDatasetItemDetail != null){

            //at this stage, we know we have a least a previous item

            //we know things are sorted by date

            //lets find out how it compares to our date
            boolean isSameDay = isSameDay(previousModel, currentModel);

            //if we are the same day, we simply slip in below
            if(isSameDay){

                //get the location of the previous item, and add one, as that is our items new
                // position
                newContentListItemLocation = listItems.indexOf(previousDatasetItemDetail
                        .contentListItem)+1;

                //ensure our listItem knows the first in section
                contentListItem.setFirstInSectionListItem(previousDatasetItemDetail
                        .getFirstInSection());

                //add our content to the listItem list.
                listItems.add(newContentListItemLocation, contentListItem);

                //tell the view we have added another item
                notifyItemInserted(newContentListItemLocation);

                //ensure we know that we have finished
                finished = true;

            }

            //NOTE: we should only care about same day here, as we don't know if an item with the
            // same day already exists below

        }


        /*
        NEXT ITEM CHECK
         */

        //if the item was not part of the previous items month, we need to check if it is part of
        // the next items month
        if(!finished && nextDatasetItemDetail != null){
            //at this stage, we know we have a least a next item

            //we know things are sorted by date

            //lets find out how it compares to our date
            boolean isSameDay = isSameDay(currentModel, nextModel);
            boolean isSameMonth = isSameMonth(currentModel, nextModel);

            //NOTE: with the next item, we need to take into account the headings the item
            // currently posses - this item will be slipping in before an item, therefore, it
            // will either create new headings, or take over headings


            //USER CASES:


            if(isSameDay){

                //if the same day as the next item
                //the next item has a day heading
                //the next item has no month heading
                //RESULT - item will take over the day heading, nextItem will loose day heading
                if(!nextDatasetItemDetail.containsMonthHeading && nextDatasetItemDetail
                        .containsDayHeading){

                    //take over day heading

                    //remove heading from next item

                }

                //if the same day as the next item
                //the next item has a day heading
                //the next item has a month heading
                //RESULT - item will take over the day & month heading, nextItem will loose day & month
                // heading
                if(nextDatasetItemDetail.containsMonthHeading && nextDatasetItemDetail.containsDayHeading){

                    //take over month heading

                    //take over day heading

                    //remove month heading from next item

                    //remove day heading from next item

                }


                //AFTER THINKING:

                //store the location of the next items content item - we will take that place
                newContentListItemLocation = listItems.indexOf(nextDatasetItemDetail.contentListItem);

                contentListItem.firstInSectionListItem = nextDatasetItemDetail
                        .dayHeadingListItem;
                listItems.add(newContentListItemLocation, contentListItem);
                notifyItemInserted(newContentListItemLocation);


                //EDIT: above use cases share logic
                if(nextDatasetItemDetail.containsMonthHeading || nextDatasetItemDetail
                        .containsDayHeading){

                    if(nextDatasetItemDetail.containsMonthHeading){
                        currentDatasetItemDetail.addMonthHeading(nextDatasetItemDetail.monthHeadingListItem);
                    }

                    //if the item has a day heading, we will take it over
                    if(nextDatasetItemDetail.containsDayHeading){
                        currentDatasetItemDetail.addDayHeading(nextDatasetItemDetail
                                .dayHeadingListItem);
                        contentListItem.setFirstInSectionListItem(nextDatasetItemDetail.dayHeadingListItem);
                    }

                    //remove the references to the old headers
                    nextDatasetItemDetail.changeToHeaderLess();

                }

                //we have added our item - time to finish up
                finished = true;


            }



            //if the same month as next item, but a different day
            //don't worry about next items day heading - not relevant - HOWEVER - what if it is
            // first in month!!!!?
            //RESULT - item will create it's own day heading
            //NOTE: if item was same as previous, that would have been caught by upward check...
            else if(isSameMonth){

                //a different day
                if(!isSameDay){

                    //get our location - it should take the place of the next items day heading
                    newContentListItemLocation = listItems.indexOf(nextDatasetItemDetail.dayHeadingListItem);

                    //hack: ensure it is at least greater than or equal to 0!
                    if(newContentListItemLocation < 0){
                        newContentListItemLocation = 0;
                    }


                    //create day heading
                    listItems.add(newContentListItemLocation, dayHeadingListItem);
                    notifyItemInserted(newContentListItemLocation);
                    currentDatasetItemDetail.addDayHeading(dayHeadingListItem);

                    //add content item
                    listItems.add(newContentListItemLocation + 1, contentListItem);
                    notifyItemInserted(newContentListItemLocation + 1);


                    //what happens if next item was first in month?
                    if(nextDatasetItemDetail.containsMonthHeading){

                        //take over the month heading
                        currentDatasetItemDetail.addMonthHeading(nextDatasetItemDetail.monthHeadingListItem);

                        //remove the old reference
                        nextDatasetItemDetail.removeMonthHeading();
                    }

                    finished = true;

                }

            }
        }

        /*
        PREVIOUS & NEXT ITEM CHECK
         */
        //if we have access to both items
        if(!finished && previousDatasetItemDetail != null && nextDatasetItemDetail != null){

            //work out date stuff
            boolean isSameDayPrevious = isSameDay(previousModel, currentModel);
            boolean isSameMonthPrevious = isSameMonth(previousModel, currentModel);
            boolean isSameDayNext = isSameDay(currentModel, nextModel);
            boolean isSameMonthNext = isSameMonth(currentModel, nextModel);


            //if same month as previous month, however different to next month
            //RESULT - item will create it's own day heading
            if(isSameMonthPrevious && !isSameMonthNext){

                if(isSameDayPrevious){
                    throw new RuntimeException("Should not have reached this point, same day " +
                            "logic should have been taken care of by the Previous Item check!");
                }

                //add the content - it should take the place of the next items day heading
                newContentListItemLocation = listItems.indexOf(previousDatasetItemDetail
                        .contentListItem)+1;

                //create day heading
                listItems.add(newContentListItemLocation, dayHeadingListItem);
                notifyItemInserted(newContentListItemLocation);
                currentDatasetItemDetail.addDayHeading(dayHeadingListItem);

                //add the actual content
                listItems.add(newContentListItemLocation + 1, contentListItem);
                notifyItemInserted(newContentListItemLocation + 1);

                finished = true;
            }

            //if different month to previous, and different to next
            //RESULT - item will create its own headings
            //@see DEFAULT CASE
//            if(!isSameMonthPrevious && !isSameMonthNext){
//
//                //create own month heading
//
//                //create own day heading
//
//            }
        }

        //now we must consider the two edge cases:
        //if we have an item that is at the start
        //if we have an item that is at the very end

        /*
        USER CASES
         */

        //if the item is the very first item in the list
        //there are no other items
        //RESULT: insert content item, new day heading, new month heading

        //we must ensure this is the last item of an already populated list
        if(!finished && isLast && !isFirst){

            //NOTE: to reach this point, we should technically have a previous model!
            if(previousDatasetItemDetail==null){
                throw new RuntimeException("Previous Item was not found, should not have been " +
                        "able to reach this point!");
            }

            boolean isSameMonthPrevious = isSameMonth(previousModel, currentModel);
            boolean isSameDayPrevious = isSameDay(previousModel, currentModel);

            if(isSameDayPrevious){
                throw new RuntimeException("Should not have reached this point, same day " +
                        "logic should have been taken care of by the Previous Item check!");
            }

            //if the item is the very last item in the list
            //is the same month as previous item
            //RESULT: item will create its own day heading
            if(isSameMonthPrevious && !isSameDayPrevious){
                //add the content - it should take the place of the next items day heading
                newContentListItemLocation = listItems.size();


                //create day heading
                listItems.add(newContentListItemLocation, dayHeadingListItem);
                notifyItemInserted(newContentListItemLocation);
                currentDatasetItemDetail.addDayHeading(dayHeadingListItem);

                listItems.add(newContentListItemLocation + 1, contentListItem);
                notifyItemInserted(newContentListItemLocation + 1);

                finished = true;
            }


            //if the item is the very last item in the list
            //is a different month to the previous item
            //RESULT: item will create its own month and day headings
            //XXX: should this be considered default case?
            //@see default case
//            if(!isSameMonthPrevious){
//
//            }

        }


        /*
        DEFAULT CASE
         */

        /*
        USER CASES
         */

        //if the item is the very last item in the list
        //is a different month to the previous item
        //RESULT: item will create its own month and day headings

        //if different month to previous, and different to next
        //RESULT - item will create its own headings

        boolean createdOwnHeadings = false;

        //if we have reached this point, the item is isolated, it doesn't belong anywhere
        if(!finished){

            //we need to work out the location of the new items

            //by default we are inserting at the start
            int newLocation = 0;

            if(isLast && !isFirst) {
                newLocation = listItems.size();
            } else if(nextDatasetItemDetail != null) {
                if(nextDatasetItemDetail.containsMonthHeading){
                    newLocation = listItems.indexOf(nextDatasetItemDetail.monthHeadingListItem);
                }else if(nextDatasetItemDetail.containsDayHeading){
                    newLocation = listItems.indexOf(nextDatasetItemDetail.dayHeadingListItem);
                }else{
                    newLocation = listItems.indexOf(nextDatasetItemDetail.contentListItem);
                }
            }

            Log.i(TAG, "addItemV2: adding whole new item with all headings at "+ newLocation);

            //add month heading

            listItems.add(newLocation, monthHeadingListItem);
            notifyItemInserted(newLocation);
            currentDatasetItemDetail.addMonthHeading(monthHeadingListItem);


            //add day heading
            listItems.add(newLocation + 1, dayHeadingListItem);
            notifyItemInserted(newLocation + 1);
            currentDatasetItemDetail.addDayHeading(dayHeadingListItem);


            //add content view
            listItems.add(newLocation + 2, contentListItem);
            notifyItemInserted(newLocation + 2);

            createdOwnHeadings = true;
            finished = true;
        }

        //we always add the content list item
        currentDatasetItemDetail.setContentLocation(contentListItem);

        //fix to ensure the items located below are properly refreshed
        //this logic relies on the fact that the item is not the last item - otherwise it will
        // not need a refresh.
        //it is only from the current item, as the items found after may now have out of date
        // first in section pointers

        if(!isLast){

            int firstInSectionPosition = listItems.indexOf(currentDatasetItemDetail.getFirstInSection());
            int contentPosition = listItems.indexOf(currentDatasetItemDetail.contentListItem);

            int startPosition = 0;
            if(createdOwnHeadings){
                startPosition = firstInSectionPosition;
            }else{
                startPosition = firstInSectionPosition;
            }

            if(firstInSectionPosition >= 0 && listItems.size() > 0) {

                notifyItemRangeChanged(firstInSectionPosition,
                        (getItemCount() - firstInSectionPosition)-1);



            }
        }

//        printListItemDetails();

    }
    //endregion

    //region Day Comparison Functions
    //helper functions
    public boolean isItemMonthHeader(int position) {

        //is it the first item, then yes
        if(position == 0){
            return true;
        }

        //check every other
        if(position > 0){

            //get point before
            E previousModel = getItem(position-1);
            E currentModel = getItem(position);

            return !isSameMonth(previousModel, currentModel);


        }else if(position <= 0){
            throw new RuntimeException("Illegal position request. Position should be greater than" +
                    " 0");
        }

        //base case is to show heading
        return true;
    }

    public boolean isItemDayHeader(int position) {

        //is it the first item, then yes
        if(position == 0){
            return true;
        }

        //check every other
        if(position > 0){

            //get point before
            E previousModel = getItem(position-1);
            E currentModel = getItem(position);

            return !isSameDay(previousModel, currentModel);


        }else if(position <= 0){
            throw new RuntimeException("Illegal position request. Position should be greater than" +
                    " 0");
        }

        //base case is to show heading
        return true;
    }

    private boolean isSameDay(E previous, E current){
        return isSameDay(previous.getDate(), current.getDate());
    }

    private boolean isSameDay(Date date1, Date date2){
        //compare the dates
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        return sameDay;
    }

    private boolean isSameMonth(E previous, E current){
        //compare the dates
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(previous.getDate());
        cal2.setTime(current.getDate());
        boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

        return sameMonth;
    }
    //endregion

    //region List Item Logic
    protected class ListItem {

        public static final int TYPE_MONTH_HEADING = 0;
        public static final int TYPE_DAY_HEADING = 1;
        public static final int TYPE_CONTENT = 2;
        public static final int TYPE_ADVERTISEMENT = 3;

        private int type;
        private int position;
        private int firstInSection;
        private ListItem firstInSectionListItem;
        private E model;

//        public ListItem(int type, int position, int firstInSection, E model) {
//            this.type = type;
//            this.position = position;
//            this.firstInSection = firstInSection;
//            this.model = model;
//        }

        public ListItem(int type, int position, ListItem firstInSectionListItem, E model) {
            this.type = type;
            this.position = position;
            this.firstInSectionListItem = firstInSectionListItem;
            this.model = model;
        }

        public int getType() {
            return type;
        }

//        public int getPosition() {
//            return position;
//        }

        public int getPosition() {

            int loc = dataset.indexOf(model);

            Log.i(TAG, "getPosition: loc" + loc + " position " + position);

            return loc;
        }

        public E getModel(){
            return model;
        }

        public int getFirstInSection() {
            return firstInSection;
        }

        public ListItem getFirstInSectionListItem() {
            return firstInSectionListItem;
        }

        public void setFirstInSectionListItem(ListItem firstInSectionListItem) {
            this.firstInSectionListItem = firstInSectionListItem;
        }
    }

    private class DatasetItemDetail {

        private boolean containsMonthHeading = false;
        private boolean containsDayHeading = false;
        private ListItem monthHeadingListItem = null;
        private ListItem dayHeadingListItem = null;
        private ListItem contentListItem = null;

        public DatasetItemDetail(boolean containsMonthHeading, boolean containsDayHeading) {
            this.containsMonthHeading = containsMonthHeading;
            this.containsDayHeading = containsDayHeading;
        }

        public DatasetItemDetail() {
        }

        public void addMonthHeading(ListItem location){
            this.containsMonthHeading = true;
            this.monthHeadingListItem = location;
        }

        public void addDayHeading(ListItem location){
            this.containsDayHeading = true;
            this.dayHeadingListItem = location;
        }

        public void setContentLocation(ListItem location){
            this.contentListItem = location;
        }

        public void changeToHeaderLess(){
            containsMonthHeading = false;
            containsDayHeading = false;
        }

        public void removeMonthHeading(){
            this.containsMonthHeading = false;
        }

        public void removeDayHeading(){
            this.containsDayHeading = false;
        }

        public ListItem getFirstInSection(){
            return contentListItem.getFirstInSectionListItem();
        }

        @Override
        public String toString() {
            return "DatasetItemDetail{" +
                    "containsMonthHeading=" + containsMonthHeading +
                    ", containsDayHeading=" + containsDayHeading +
                    '}';
        }
    }

    public ListItem getListItem(int position){
        return listItems.get(position);
    }

    public Date getListItemDate(int position){
        return listItems.get(position).getModel().getDate();
    }
    //endregion

    //region Advertisements
    /* Advertisements */

    public void addAdvertisementItems(Context context, String adUnitId){
        this.addAdvertisementItems(context, MIN_ITEMS_ADVERTISEMENT_THRESHOLD, MAX_ADS_NUMBER, adUnitId);
    }

    private Map<Integer, NativeExpressAdView> advertisementMap = new HashMap<>();
    private List<NativeExpressAdView> advertisementCache = new ArrayList<>();
//    private List<NativeExpressAdView> advertisementList = new ArrayList<>();
//    private int currentAdvertisement = 0;


    //context.getString(R.string.timeline_banner_ad_unit_id)
    public void addAdvertisementItems(Context context, int advertisementTolerance, int maxAds, String adUnitId){

        Log.d(TAG, "addAdvertisementItems: starting to add advertisements");

        int monthEntryCount = 0;
        ListItem listItem;

        //iterate over the current list items
        for (int i = 0; i < listItems.size(); i++) {
            listItem = listItems.get(i);

            if(listItem.getType() == ListItem.TYPE_ADVERTISEMENT){
                listItems.remove(i);
                notifyItemRemoved(i);
            }
        }

        adCount = 0;
//        currentAdvertisement = 0;

        //clear the map
        advertisementMap.clear();

        NativeExpressAdView nativeExpressAdView;

        for (int i = 0; i < listItems.size(); i++) {
            listItem = listItems.get(i);

            Log.d(TAG, "addAdvertisementItems: on item " + i + " current Count " + monthEntryCount);

            //we are at the start of a month - reset the count
            if(listItem.getType() == ListItem.TYPE_MONTH_HEADING){

                //TODO: check - maybe should be below?
                if(monthEntryCount > advertisementTolerance){
                    listItems.add(i, new ListItem(ListItem.TYPE_ADVERTISEMENT, i, null, listItems
                            .get(i-1).getModel()));

                    //determine if we have a cached add ready for use
                    //TODO: review - values/counts correct?
                    if(adCount >= advertisementCache.size()){
                        //create the native advertisement
                        nativeExpressAdView = new NativeExpressAdView(context);
                        nativeExpressAdView.setAdUnitId(adUnitId);
                        nativeExpressAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 80));
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    lp.setMargins(0, 40, 0, 0);
//                    nativeExpressAdView.setLayoutParams(lp);

                        nativeExpressAdView.loadAd(generateAdRequest(nativeExpressAdView));
                        advertisementCache.add(nativeExpressAdView);
                        Log.d(TAG, "addAdvertisementItems: no advert is cache, creating new view.");
                    }else{
                        Log.d(TAG, "addAdvertisementItems: loaded advert from cache - adCount " + adCount + " advertisementCache.size() " + advertisementCache.size());
                    }

                    nativeExpressAdView = advertisementCache.get(adCount);

                    advertisementMap.put(i, nativeExpressAdView);

                    adCount++;

                    if(adCount > maxAds){
                        return;
                    }

                    Log.d(TAG, "addAdvertisementItems: adding advert at " + i);
                }

                monthEntryCount = 0;
                continue;
            }

            //if the current item is content, increment
            if(listItem.getType() == ListItem.TYPE_CONTENT){
                monthEntryCount++;

                Log.d(TAG, "addAdvertisementItems: found another content item, current month " +
                        "total " + monthEntryCount);
            }
        }
    }

    public void updateAds(){

        NativeExpressAdView tempView;

        //iterate over ads in cache
        for (NativeExpressAdView nativeExpressAdView : advertisementCache) {
            nativeExpressAdView.loadAd(generateAdRequest(nativeExpressAdView));
        }

/*        //iterate over all advertisements in the map
        for (Map.Entry<Integer, NativeExpressAdView> integerNativeExpressAdViewEntry : advertisementMap.entrySet()) {
            tempView = ((NativeExpressAdView)integerNativeExpressAdViewEntry
                    .getValue());

            //load a new ad with a newly generated request
            tempView.loadAd
                    (generateAdRequest(tempView));
        }*/
    }

    private AdRequest generateAdRequest(View view){
        final AdRequest adRequest;
        if(BuildConfig.DEBUG){

            String androidId = Settings.Secure.getString(view.getContext().getContentResolver(), Settings
                    .Secure.ANDROID_ID);
            String deviceId = EncryptionHelper.md5(androidId).toUpperCase();

            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(deviceId)
                    .build();

            Log.i(TAG, "showAdvertisement: device id " + deviceId);
        }else{
            adRequest = new AdRequest.Builder().build();
        }
        return adRequest;
    }

    public int getAdvertisementLayout() {
        return advertisementLayout;
    }

    public void setAdvertisementLayout(int advertisementLayout) {
        this.advertisementLayout = advertisementLayout;
    }
    //endregion
}
