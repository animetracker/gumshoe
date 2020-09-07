package com.alexzamurca.animetrackersprint2.algorithms;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToMillis;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateSortSeriesList
{
    private static final String TAG = "DateSortSeriesList";
    private List<Long> unsortedMillisDateList;
    private List<Series> unsortedSeriesList;
    private ConvertDateToMillis convertDateToMillis;

    boolean doneSorting;
    boolean swap;

    public DateSortSeriesList(List<Series> unsortedSeriesList) 
    {
        this.unsortedSeriesList = unsortedSeriesList;
        convertDateToMillis = new ConvertDateToMillis();
        initMillisDateList();
    }
    
    private void initMillisDateList()
    {
        unsortedMillisDateList = new ArrayList<>();
        for(Series series:unsortedSeriesList)
        {
            unsortedMillisDateList.add(convertDateToMillis.getDate(series.getAir_date()));
        }
    }

    private void swapElementsByIndexs(int index1, int index2)
    {
        // Swap previous element with the next element
        Collections.swap(unsortedMillisDateList, index1, index2);
        Collections.swap(unsortedSeriesList, index1, index2);
    }

    // Function that checks ranking of numbers (if the previous is greater than the next) in the forward direction(from first to last element); returns a boolean value that signifies if any swapping of elements happened
    private boolean swapInForwardDirection()
    {
        // Originally swap is set to false and will be changed to true if any swap happens in the for loop
        swap = false;

        // Loop for all elements in random_int_list
        for (int i = 0; i <= unsortedMillisDateList.size() -2; i++)
        {
            // If the previous element is greater than the next
            if (unsortedMillisDateList.get(i) > unsortedMillisDateList.get(i+1))
            {
                swapElementsByIndexs(i, i+1);

                // Set swap to true so that we know a swap has taken place
                swap = true;
            }
        }
        return swap;
    }

    // Function that checks ranking of numbers (if the previous is greater than the next) in the backward direction(from last to first element); returns a boolean value that signifies if any swapping of elements happened
    private boolean swapInBackwardDirection()
    {
        // Originally swap is set to false and will be changed to true if any swap happens in the for loop
        swap = false;

        for (int i = unsortedMillisDateList.size() - 2; i >= 0; i--)
        {
            if (unsortedMillisDateList.get(i) > unsortedMillisDateList.get(i+1))
            {
                swapElementsByIndexs(i, i+1);

                // Set swap to true so that we know a swap has taken place
                swap = true;
            }
        }
        return swap;
    }



    private void printList()
    {
        Log.d(TAG, "printList: " + unsortedMillisDateList.toString());
        String irrelevant = "[";
        for(Series s: unsortedSeriesList)
        {
            irrelevant += s.getTitle();
            irrelevant += ",";
        }
        irrelevant += "]";
        Log.d(TAG, "printList: " + irrelevant);
    }

    private void sortList()
    {
        doneSorting = false;
        do
        {
            // Swap with random_int_list index going from 0 to the length of the random_int_list and return the opposite of the boolean value indicating if any swapping was done
            // I.e. if any swapping was done, that probably means the algorithm is not done sorting
            doneSorting = !(swapInForwardDirection());

            // If no swaps happened while random_int_list was indexed in the forward direction it means that the random_int_list is in correct order, so no further swapping is necessery
            if (!doneSorting)
            {
                doneSorting = !(swapInBackwardDirection());
            }
            else{
                break;
            }
        } while(!doneSorting);

    }

    public List<Series> sortMostRecent()
    {
        printList();
        sortList();
        printList();
        return unsortedSeriesList;
    }

    public List<Series> sortLeastRecent()
    {
        sortMostRecent();
        List<Series> backup= unsortedSeriesList;
        Collections.reverse(unsortedSeriesList);
        printList();
        List<Series> reverse= unsortedSeriesList;
        unsortedSeriesList = backup;
        return reverse;
    }
}
