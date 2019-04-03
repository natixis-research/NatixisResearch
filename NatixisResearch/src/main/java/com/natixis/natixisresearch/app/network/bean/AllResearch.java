package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

import java.util.List;

/**
 * Created by Thibaud on 07/04/2017.
 */
public class AllResearch {
    @JsonProperty
    private boolean IsEcoPublic;

    @JsonProperty
    private	List<ResearchDocument> DocsEco;

    @JsonProperty
    private	Boolean IsCreditPublic;

    @JsonProperty
    private	List<ResearchDocument> DocsCredit;

    @JsonProperty
    private	boolean IsEquityPublic;

    @JsonProperty
    private List<ResearchDocument> DocsEquity;


    public boolean isEcoPublic() {
        return IsEcoPublic;
    }

    public void setEcoPublic(boolean isEcoPublic) {
        IsEcoPublic = isEcoPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllResearch)) return false;

        AllResearch that = (AllResearch) o;

        if (IsEcoPublic != that.IsEcoPublic) return false;
        if (IsEquityPublic != that.IsEquityPublic) return false;
        if (DocsCredit != null ? !DocsCredit.equals(that.DocsCredit) : that.DocsCredit != null)
            return false;
        if (DocsEco != null ? !DocsEco.equals(that.DocsEco) : that.DocsEco != null) return false;
        if (DocsEquity != null ? !DocsEquity.equals(that.DocsEquity) : that.DocsEquity != null)
            return false;
        if (IsCreditPublic != null ? !IsCreditPublic.equals(that.IsCreditPublic) : that.IsCreditPublic != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (IsEcoPublic ? 1 : 0);
        result = 31 * result + (DocsEco != null ? DocsEco.hashCode() : 0);
        result = 31 * result + (IsCreditPublic != null ? IsCreditPublic.hashCode() : 0);
        result = 31 * result + (DocsCredit != null ? DocsCredit.hashCode() : 0);
        result = 31 * result + (IsEquityPublic ? 1 : 0);
        result = 31 * result + (DocsEquity != null ? DocsEquity.hashCode() : 0);
        return result;
    }
}
