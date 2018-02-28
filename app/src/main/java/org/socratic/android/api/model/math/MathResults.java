package org.socratic.android.api.model.math;

import org.socratic.android.api.model.CardResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by byfieldj on 8/30/17.
 */

public class MathResults {

    public ArrayList<CardResponse> cardResponseList;


    public void setCardResponseList(ArrayList<CardResponse> list){
        this.cardResponseList = list;
    }

    public ArrayList<CardResponse> getCardResponseList(){
        return this.cardResponseList;
    }

}
