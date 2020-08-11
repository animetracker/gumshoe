package com.alexzamurca.animetrackersprint2.series.algorithms;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AlphabeticalSortList
{
    private static final String TAG = "AlphabeticalSortList";

    private List<Integer> unsortedIntegerList;
    private List<Series> unsortedSeriesList;
    private HashMap<Character, Integer> characterRankGuide;

    boolean doneSorting;
    boolean swap;

    public AlphabeticalSortList(List<Series> unsortedSeriesList) {
        this.unsortedSeriesList = unsortedSeriesList;
        createRankedHashMap();
        createRankedIntList();
    }

    private List<Character> getCharList()
    {
        List<Character> list = new ArrayList<>();
        for(Series series:unsortedSeriesList)
        {
            // Store the first character of the titles
            list.add(series.getTitle().toLowerCase().toCharArray()[0]);
        }
        return list;
    }

    private void createRankedHashMap()
    {
        characterRankGuide = new HashMap<>();
        characterRankGuide.put('0', 1);
        characterRankGuide.put('1', 2);
        characterRankGuide.put('2', 3);
        characterRankGuide.put('3', 4);
        characterRankGuide.put('4', 5);
        characterRankGuide.put('5', 6);
        characterRankGuide.put('6', 7);
        characterRankGuide.put('7', 8);
        characterRankGuide.put('8', 9);
        characterRankGuide.put('9', 10);
        characterRankGuide.put('a', 11);
        characterRankGuide.put('b', 12);
        characterRankGuide.put('c', 13);
        characterRankGuide.put('d', 14);
        characterRankGuide.put('e', 15);
        characterRankGuide.put('f', 16);
        characterRankGuide.put('g', 17);
        characterRankGuide.put('h', 18);
        characterRankGuide.put('i', 19);
        characterRankGuide.put('j', 20);
        characterRankGuide.put('k', 21);
        characterRankGuide.put('l', 22);
        characterRankGuide.put('m', 23);
        characterRankGuide.put('n', 24);
        characterRankGuide.put('o', 25);
        characterRankGuide.put('p', 26);
        characterRankGuide.put('q', 27);
        characterRankGuide.put('r', 28);
        characterRankGuide.put('s', 29);
        characterRankGuide.put('t', 30);
        characterRankGuide.put('u', 31);
        characterRankGuide.put('v', 32);
        characterRankGuide.put('w', 33);
        characterRankGuide.put('x', 34);
        characterRankGuide.put('y', 35);
        characterRankGuide.put('z', 36);
    }

    private void createRankedIntList()
    {
        List<Character> charList = getCharList();
        unsortedIntegerList = new ArrayList<>();
        for(Character character:charList)
        {
            if(Character.isLetter(character) || Character.isDigit(character))
            {
                Log.d(TAG, "createRankedIntList: trying to get  '" + character+"'");
                unsortedIntegerList.add(characterRankGuide.get(character));
            }
            else
            {
                unsortedIntegerList.add(0);
            }
        }
    }

    private void swapElementsByIndexs(int index1, int index2)
    {
        // Swap previous element with the next element
        Collections.swap(unsortedIntegerList, index1, index2);
        Collections.swap(unsortedSeriesList, index1, index2);
    }

    // Function that checks ranking of numbers (if the previous is greater than the next) in the forward direction(from first to last element); returns a boolean value that signifies if any swapping of elements happened
    private boolean swapInForwardDirection()
    {
        // Originally swap is set to false and will be changed to true if any swap happens in the for loop
        swap = false;

        // Loop for all elements in random_int_list
        for (int i = 0; i <= unsortedIntegerList.size() -2; i++)
        {
            // If the previous element is greater than the next
            if (unsortedIntegerList.get(i) > unsortedIntegerList.get(i+1))
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

        for (int i = unsortedIntegerList.size() - 2; i >= 0; i--)
        {
            if (unsortedIntegerList.get(i) > unsortedIntegerList.get(i+1))
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
        Log.d(TAG, "printList: " + unsortedIntegerList.toString());
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

   public List<Series> sortAlphabetically()
   {
       printList();
       sortList();
       printList();
       return unsortedSeriesList;
   }

   public List<Series> sortReverseAlphabetically()
   {
       sortAlphabetically();
       List<Series> backup= unsortedSeriesList;
       Collections.reverse(unsortedSeriesList);
       printList();
       List<Series> reverse= unsortedSeriesList;
       unsortedSeriesList = backup;
       return reverse;
   }
}
