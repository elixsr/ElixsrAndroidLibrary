package com.elixsr.core.timeline.list.adapter;

import android.util.Log;

import com.elixsr.core.timeline.list.models.TimelineInterface;
import com.elixsr.core.timeline.list.viewholder.AbstractTimelineViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Niall McShane on 16/06/2016.
 */
public abstract class FirebaseTimelineRecyclerAdapter<VH extends AbstractTimelineViewHolder, T
        extends TimelineInterface> extends
        AbstractTimelineRecyclerAdapter<VH, T> {


    private final List<DatabaseReference> references;
    private Query reference;
    private FirebaseArray mSnapshots;
    private Class<T> mModelClass;
    private static final String TAG = "FirebaseTimelineRecy";
    FirebaseArray.OnChangedListener firebaseListener;

    public FirebaseTimelineRecyclerAdapter(List<T> dataset, List<DatabaseReference>
            references, Class<VH> viewHolderClass, int contentLayout) {
        super(viewHolderClass, contentLayout);

        super.dataset = dataset;
        this.references = references;

        super.calculateListItems();
    }


    public FirebaseTimelineRecyclerAdapter(Class<VH> viewHolderClass, int contentLayout, final
    Class<T> modelClass, final Query reference, final boolean animatedChanges, final boolean reverse) {
        super(viewHolderClass, contentLayout);

        super.dataset = new LinkedList<T>();
        references = new LinkedList<DatabaseReference>();
        mModelClass = modelClass;

        firebaseListener =  new FirebaseArray.OnChangedListener() {


            @Override
            public void onChanged(EventType type, int index, int oldIndex) {

                if (animatedChanges) {

                    int tempIndex = 0;
                    int virtualIndex = index;
                    int virtualOldIndex = oldIndex;
                    if(false){
                        virtualIndex = mSnapshots.getCount()-index-1;
                        virtualOldIndex = mSnapshots.getCount()-oldIndex-1;
                    }

                    Log.i(TAG, "onChanged: index " + index);


                    switch (type) {
                        case Added:
                            Log.d(TAG, "onChanged: FirebaseTimeline - Added " + index);

                            references.add(virtualIndex, mSnapshots.getItem(index).getRef());
                            dataset.add(virtualIndex, mSnapshots.getItem(index)
                                    .getValue(modelClass));
                            addItemV2(virtualIndex);

                            break;
                        case Removed:
                            Log.d(TAG, "onChanged: FirebaseTimeline - Removed " + index);
                            references.remove(virtualIndex);
                            dataset.remove(virtualIndex);
                            removeItem(virtualIndex);
                            break;
                        case Changed:
                            Log.d(TAG, "onChanged: FirebaseTimeline - Changed " + index);
                            dataset.set(virtualIndex, mSnapshots.getItem(index)
                                    .getValue(modelClass));
                            changeItem(virtualIndex);
                            break;
                        case Moved:
                            Log.d(TAG, "onChanged: FirebaseTimeline - Moved " + index + " oldIndex " + oldIndex);

                            //remove the old reference
                            references.remove(virtualOldIndex);
                            dataset.remove(virtualOldIndex);
                            removeItem(virtualOldIndex);

                            //add the new reference
                            references.add(virtualIndex, mSnapshots.getItem(index).getRef());
                            dataset.add(virtualIndex, mSnapshots.getItem(index)
                                    .getValue(modelClass));
                            addItemV2(virtualIndex);

                            break;
                        default:
                            throw new IllegalStateException("Incomplete case statement");
                    }

                    datasetChanged(type, dataset, index, oldIndex);
                } else {
                    //clear the old lists
                    FirebaseTimelineRecyclerAdapter.super.dataset.clear();
                    references.clear();

                    //store the value from the snapshot
                    for (int i = 0; i < mSnapshots.getCount(); i++) {

                        if (isDataPointValid(mSnapshots.getItem(i)
                                .getValue(modelClass))) {
                            FirebaseTimelineRecyclerAdapter.super.dataset.add(mSnapshots.getItem(i)
                                    .getValue(modelClass));
                            references.add(mSnapshots.getItem(i).getRef());
                        }
                    }


                    //tell the recyclerview to recalculate
                    calculateListItems();
                    notifyDataSetChanged();
                    datasetChanged(type, FirebaseTimelineRecyclerAdapter.super.dataset, index,
                            oldIndex);
                }

            }
        };

        this.reference = reference;
        mSnapshots = new FirebaseArray(reference);
        mSnapshots.setOnChangedListener(firebaseListener);

        super.calculateListItems();
    }


//    @Override
//    public void populateContentItem(DreamViewHolder holder, DreamModel currentModel, int position) {
//        holder.setText(currentModel.getDreamText());
//    }

    public DatabaseReference getRef(int position) {

        Log.i(TAG, "getRef: position: " + position);

        return references.get(position);
    }

    public void updateListItems(){
        super.calculateListItems();
    }

    public void datasetChanged(FirebaseArray.OnChangedListener.EventType type, List<T>
            dataset, int index, int oldIndex){
        return;
    }


    /*
    Function to process data obtained from firebase. Logic based rules can be applied using this
    function.
     */
    public boolean isDataPointValid(T value){
        return true;
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    public void removeListener(){
        mSnapshots.cleanup();
    }

    public void attachListener(){
        mSnapshots = new FirebaseArray(reference);
        mSnapshots.setOnChangedListener(firebaseListener);
    }
}
