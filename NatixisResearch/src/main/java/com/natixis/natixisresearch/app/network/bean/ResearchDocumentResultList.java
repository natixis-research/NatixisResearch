package com.natixis.natixisresearch.app.network.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Thibaud on 08/04/2017.
 */
public class ResearchDocumentResultList extends ArrayList<ResearchDocument> {


    public static class ResearchDocumentComparator implements Comparator<ResearchDocument> {
        @Override
        public int compare(ResearchDocument o1, ResearchDocument o2) {
            return o2.getDatetime().compareTo(o1.getDatetime());
        }
    }
}
